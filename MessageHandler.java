import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;

public class MessageHandler implements Runnable{
    private String peerId=null;
    boolean isRunning = true;

    public MessageHandler(String peerId) {
        this.peerId=peerId;
    }

    private static int byteArrayToInt(byte[] b)
    {
        int offset = 0;
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    private static byte[] intToByteArray(int value)
    {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

    public void run(){
        DataMsg dataMessage;
        DataMsgWrapper dataMessageWrapper;
        String messageType;
        String remotePeerId;

        while(isRunning)
        {
            dataMessageWrapper  = peerProcess.popMessageFromQueue();


            while(dataMessageWrapper == null)
            {
                Thread.currentThread();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dataMessageWrapper  = peerProcess.popMessageFromQueue();
            }

            dataMessage = dataMessageWrapper.getDataMsg();

            messageType = dataMessage.getMessageTypeString();
            remotePeerId = dataMessageWrapper.getMyPeerID();
            int state = peerProcess.peerInfoHashMap.get(remotePeerId).state;
            System.out.println("Got msg of type "+Constants.ConstantMap.get(Integer.parseInt(messageType))+" from "+remotePeerId+" with state "+state);

            if(messageType.equals(Constants.HAS_DATA_MSG) && state != 14)
            {
                peerProcess.printLog("Peer "+peerProcess.peerId + " receieved HAVE message from Peer " + remotePeerId);
                if(interestedTrue(dataMessage, remotePeerId))
                {
                    sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.INTERESTED_DATA_MESSAGE);
                    peerProcess.peerInfoHashMap.get(remotePeerId).state = 9;
                }
                else
                {
                    sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.NOTINTERESTED_DATA_MESSAGE);
                    peerProcess.peerInfoHashMap.get(remotePeerId).state = 13;
                }
            }
            else
            {
                switch (state)
                {

                    case 2:
                        if (messageType.equals(Constants.BITFIELD_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " receieved a BITFIELD message from Peer " + remotePeerId);
                            sendBitField(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).state = 3;
                        }
                        break;

                    case 3:
                        if (messageType.equals(Constants.NOTINTERESTED_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " receieved a NOT INTERESTED message from Peer " + remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).interestedTrue = 0;
                            peerProcess.peerInfoHashMap.get(remotePeerId).state = 5;
                            peerProcess.peerInfoHashMap.get(remotePeerId).handshakedTrue = 1;
                        }
                        else if (messageType.equals(Constants.INTERESTED_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " receieved an INTERESTED message from Peer " + remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).interestedTrue = 1;
                            peerProcess.peerInfoHashMap.get(remotePeerId).handshakedTrue = 1;

                            if(!peerProcess.preferred_Neighbors.containsKey(remotePeerId) && !peerProcess.unchokedPeers.containsKey(remotePeerId))
                            {
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.CHOKE_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).chokedTrue = 1;
                                peerProcess.peerInfoHashMap.get(remotePeerId).state  = 6;
                            }
                            else
                            {
                                peerProcess.peerInfoHashMap.get(remotePeerId).chokedTrue = 0;
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.UNCHOKE_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 4 ;
                            }
                        }
                        break;

                    case 4:
                        if (messageType.equals(Constants.REQUEST_DATA_MESSAGE)) {
                            sendPeice(peerProcess.peerIdToSocket.get(remotePeerId), dataMessage, remotePeerId);
                            // CHOKE/UNCHOKE message
                            if(!peerProcess.preferred_Neighbors.containsKey(remotePeerId) && !peerProcess.unchokedPeers.containsKey(remotePeerId))
                            {
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.CHOKE_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).chokedTrue = 1;
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 6;
                            }
                        }
                        break;

                    case 8:
                        if (messageType.equals(Constants.BITFIELD_DATA_MESSAGE)) {
                            //interested/not.
                            if(interestedTrue(dataMessage,remotePeerId))
                            {
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.INTERESTED_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 9;
                            }
                            else
                            {
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.NOTINTERESTED_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 13;
                            }
                        }
                        break;

                    case 9:
                        if (messageType.equals(Constants.CHOKE_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " is CHOKED by Peer " + remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).state = 14;
                        }
                        else if (messageType.equals(Constants.UNCHOKE_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " is UNCHOKED by Peer " + remotePeerId);
                            int firstdiff = peerProcess.curBitField.returnFirstDiff(peerProcess.peerInfoHashMap.get(remotePeerId).bitField);
                            if(firstdiff != -1)
                            {
                                peerProcess.printLog("Peer "+peerProcess.peerId + " is Requesting PIECE " + firstdiff + " from peer " + remotePeerId);
                                sendRequest(peerProcess.peerIdToSocket.get(remotePeerId), firstdiff, remotePeerId);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 11;
                                peerProcess.peerInfoHashMap.get(remotePeerId).startTime = new Date();
                            }
                            else
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 13;
                        }
                        break;

                    case 11:
                        if (messageType.equals(Constants.PIECE_DATA_MESSAGE)) {
                            byte[] buffer = dataMessage.getPayload();
                            peerProcess.peerInfoHashMap.get(remotePeerId).finishTime = new Date();
                            long timeLapse = peerProcess.peerInfoHashMap.get(remotePeerId).finishTime.getTime() -
                                    peerProcess.peerInfoHashMap.get(remotePeerId).startTime.getTime() ;

                            peerProcess.peerInfoHashMap.get(remotePeerId).dataRate = ((double)(buffer.length + Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG)/(double)timeLapse) * 100;

                            PieceClass p = PieceClass.initializePiece(buffer);
                            peerProcess.curBitField.updateBF(remotePeerId, p);

                            int toGetPeiceIndex = peerProcess.curBitField.returnFirstDiff(peerProcess.peerInfoHashMap.get(remotePeerId).bitField);
                            if(toGetPeiceIndex != -1)
                            {
                                peerProcess.printLog("Peer "+peerProcess.peerId + " Requesting piece " + toGetPeiceIndex + " from peer " + remotePeerId);
                                sendRequest(peerProcess.peerIdToSocket.get(remotePeerId),toGetPeiceIndex, remotePeerId);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state  = 11;
                                peerProcess.peerInfoHashMap.get(remotePeerId).startTime = new Date();
                            }
                            else
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 13;

                            //peerInfo updated
                            peerProcess.getPeerInfo();

                            Enumeration<String> keys = peerProcess.peerInfoHashMap.keys();
                            while(keys.hasMoreElements())
                            {
                                String key = (String)keys.nextElement();
                                PeerInfo pref = peerProcess.peerInfoHashMap.get(key);

                                if(key.equals(peerProcess.peerId))continue;
                                peerProcess.printLog("Peer "+peerProcess.peerId + ":::: isCompleted =" + pref.completedTrue + " interestedTrue =" + pref.interestedTrue + " chokedTrue =" + pref.chokedTrue);
                                if (pref.completedTrue == 0 && pref.chokedTrue == 0 && pref.handshakedTrue == 1)
                                {
                                    peerProcess.printLog("Peer "+peerProcess.peerId + " isCompleted =" + pref.completedTrue + " isInterested =" + pref.interestedTrue + " isChoked =" + pref.chokedTrue);

                                    sendHave(peerProcess.peerIdToSocket.get(key), key);

                                    peerProcess.peerInfoHashMap.get(key).state = 3;

                                }

                            }

                            buffer = null;
                            dataMessage = null;

                        }
                        else if (messageType.equals(Constants.CHOKE_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " is CHOKED by Peer " + remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).state = 14;
                        }
                        break;

                    case 14:
                        if (messageType.equals(Constants.HAS_DATA_MSG)) {
                            //interested/not.
                            if(interestedTrue(dataMessage,remotePeerId))
                            {
                                peerProcess.printLog("Peer "+peerProcess.peerId + " is interested in Peer " + remotePeerId);
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.INTERESTED_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 9;
                            }
                            else
                            {
                                peerProcess.printLog("Peer "+peerProcess.peerId + " is not interested in Peer " + remotePeerId);
                                sendDataMessageWithType(peerProcess.peerIdToSocket.get(remotePeerId), remotePeerId,Constants.NOTINTERESTED_DATA_MESSAGE);
                                peerProcess.peerInfoHashMap.get(remotePeerId).state = 13;
                            }
                        }
                        else if (messageType.equals(Constants.UNCHOKE_DATA_MESSAGE)) {
                            peerProcess.printLog("Peer "+peerProcess.peerId + " is UNCHOKED by Peer " + remotePeerId);
                            peerProcess.peerInfoHashMap.get(remotePeerId).state = 14;
                        }
                        break;

                }
            }

        }

    }

    RandomAccessFile raf;
    private void sendPeice(Socket sckt, DataMsg dataMessage, String remotePeerID)
    {
        byte[] bytePieceIndex = dataMessage.getPayload();
        int pieceIndex = byteArrayToInt(bytePieceIndex);

        peerProcess.printLog("Peer "+peerProcess.peerId + " sending a PIECE message for piece " + pieceIndex + " to Peer " + remotePeerID);

        byte[] byteRead = new byte[CommonCfg.pieceSize];
        int noBytesRead = 0;

        File file = new File(peerProcess.peerId,CommonCfg.fileName);
        try
        {
            raf = new RandomAccessFile(file,"r");
            raf.seek(pieceIndex*CommonCfg.pieceSize);
            noBytesRead = raf.read(byteRead, 0,CommonCfg.pieceSize);
        }
        catch (IOException e)
        {
            peerProcess.printLog("MessageHAndler :: "+peerProcess.peerId + " ERROR in reading the file : " +  e.toString());
        }
        if( noBytesRead == 0)
        {
            peerProcess.printLog("MessageHAndler :: "+peerProcess.peerId + " ERROR :  Zero bytes read from the file !");
        }
        else if (noBytesRead < 0)
        {
            peerProcess.printLog("MessageHAndler :: "+peerProcess.peerId + " ERROR : File could not be read properly.");
        }

        byte[] buffer = new byte[noBytesRead + Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(bytePieceIndex, 0, buffer, 0, Constants.PIECE_INDEX_LENGTH);
        System.arraycopy(byteRead, 0, buffer, Constants.PIECE_INDEX_LENGTH, noBytesRead);

        DataMsg sendMessage = new DataMsg(Constants.PIECE_DATA_MESSAGE, buffer);
        byte[] b =  DataMsg.encodeMessage(sendMessage);
        sendData(sckt, b);

        //release the memory
        buffer = null;
        byteRead = null;
        b = null;
        bytePieceIndex = null;
        sendMessage = null;

        try{
            raf.close();
        }
        catch(Exception e){}
    }


    private byte[] intTobyte(int i) {
        return new byte[] {
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    private int byteToint(byte[] b1) {
        return  b1[3] & 0xFF |
                (b1[2] & 0xFF) << 8 |
                (b1[1] & 0xFF) << 16 |
                (b1[0] & 0xFF) << 24;
    }

    private void sendRequest(Socket sckt, int pieceNo, String remotePeerID) {

        byte[] pieceByte = new byte[Constants.PIECE_INDEX_LENGTH];
        for (int i = 0; i < Constants.PIECE_INDEX_LENGTH; i++) {
            pieceByte[i] = 0;
        }

        byte[] pieceIndexByte = intToByteArray(pieceNo);
        System.arraycopy(pieceIndexByte, 0, pieceByte, 0,
                pieceIndexByte.length);
        DataMsg dataMessage = new DataMsg(Constants.REQUEST_DATA_MESSAGE, pieceByte);
        byte[] b = DataMsg.encodeMessage(dataMessage);
        sendData(sckt, b);

        pieceByte = null;
        pieceIndexByte = null;
        b = null;
        dataMessage = null;
    }

    private boolean interestedTrue(DataMsg d, String rPeerId) {
        if(d.getPayload()==null) System.out.println("why is this null");
        BitFieldCls b = BitFieldCls.decodeInput(d.getPayload());

        System.out.println("bitfield of "+peerProcess.peerInfoHashMap .get(rPeerId).getPeerId());
        peerProcess.peerInfoHashMap.get(rPeerId).bitField = b;

        return peerProcess.curBitField.compare(b);
    }

    private boolean sendDataMessageWithType(Socket sckt,String toPeerId,String messageType) {

        peerProcess.printLog("Sending "+Constants.ConstantMap.get(Integer.parseInt(messageType))+ " message from "+peerId+" to "+toPeerId);
        DataMsg dataMessage=new DataMsg(messageType);

        byte[] msg = DataMsg.encodeMessage(dataMessage);

        sendData(sckt,msg);
        return true;
    }

    private void sendHave(Socket sckt,String toPeerId){
        peerProcess.printLog("Peer "+peerProcess.peerId + " sending BITFIELD message to Peer " + toPeerId);
        byte[] encodedBitField = peerProcess.curBitField.encodeInput();

        System.out.println("Sent : "+ new  String(encodedBitField, StandardCharsets.UTF_8)) ;

        DataMsg d = new DataMsg(Constants.HAS_DATA_MSG, encodedBitField);
        sendData(sckt,DataMsg.encodeMessage(d));


    }

    private void sendBitField(Socket sckt, String remotePeerID) {

        peerProcess.printLog("Peer "+peerProcess.peerId + " sending BITFIELD message to Peer " + remotePeerID);

        byte[] encodedBitField = peerProcess.curBitField.encodeInput();

        System.out.println("Sent : "+ new  String(encodedBitField, StandardCharsets.UTF_8)) ;

        DataMsg d = new DataMsg(Constants.BITFIELD_DATA_MESSAGE, encodedBitField);
        sendData(sckt,DataMsg.encodeMessage(d));
    }

    private void sendData(Socket sckt, byte[] encodedMsg){
        try {
            OutputStream outputStream = sckt.getOutputStream();
            outputStream.write(encodedMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}