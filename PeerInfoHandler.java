import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PeerInfoHandler implements Runnable{
    private Socket peerSckt = null;
    private int connType;
    String selfId,peerId;
    private static final int ACTIVE_CONNECTION=1;
    private Handshake handshake;

    private static final int PASSIVE_CONNECTION=0;

    private InputStream inputStream;
    private OutputStream outputStream;


    public PeerInfoHandler(Socket peerSckt, int connType, String selfId) {

        this.peerSckt = peerSckt;
        this.connType = connType;
        this.selfId = selfId;

        initializeTheBuffers();

    }

    private void initializeTheBuffers(){
        try{

            System.out.println("before buffer");
            inputStream=peerSckt.getInputStream();
            outputStream=peerSckt.getOutputStream();
            System.out.println("after buffers");

        }catch (Exception exception){
            peerProcess.printLog(exception.toString());
        }
    }

    public PeerInfoHandler(String address, int port, int connType, String selfId) {
        this.connType = connType;
        this.selfId = selfId;
        System.out.println("Before constructor");
        try{
        this.peerSckt = new Socket(address, port);}
        catch (Exception exception){
            peerProcess.printLog(exception.toString());
        }

        initializeTheBuffers();
        System.out.println("After constructor");
    }

    public void run(){
        //TBI

        byte []inputHandShake = new byte[32];

        byte []dataBufferExcludingPayload = new byte[Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG];
        byte[] messageLength;
        byte[] messageType;
        DataMsgWrapper dataMsgWrapper = new DataMsgWrapper();

        try {
            if (this.connType == ACTIVE_CONNECTION) {

                if (!sendHandShakeMessage()) {
                    peerProcess.printLog("Failed sending Handshake for " + selfId);
                    System.exit(0);
                } else {
                    peerProcess.printLog("Success sending Handshake for " + selfId);
                }
                while (true) {
                    inputStream.read(inputHandShake);
                    handshake = Handshake.decode(inputHandShake);
                    String header = new String(handshake.getHeaderMsg());

                    if (header.equals(Constants.HANDSHAKE_HEADER_NAME)) {

                        peerId = new String(handshake.getPeer_Id());

                        peerProcess.printLog("Peer "+selfId + " makes a connection to Peer " + peerId);

                        peerProcess.printLog("received a handshake message from Peer " + peerId+" to Peer "+selfId);

                        peerProcess.peerIdToSocket.put(peerId,this.peerSckt);

                        break;
                    } else {
                        continue;
                    }
                }
                // Sending BitField...
                DataMsg d = new DataMsg(Constants.BITFIELD_DATA_MESSAGE, peerProcess.curBitField.encodeInput());
                byte  []b = DataMsg.encodeMessage(d);
                outputStream.write(b);
                peerProcess.peerInfoHashMap.get(peerId).state = 8;
            }else{

                System.out.println("Passive Connection");
                while(true)
                {
                    inputStream.read(inputHandShake);
                    handshake = Handshake.decode(inputHandShake);

                    String header = new String(handshake.getHeaderMsg());

                    if(header.equals(Constants.HANDSHAKE_HEADER_NAME))
                    {
                        peerId = new String( handshake.getPeer_Id());

                        peerProcess.printLog("Peer " +selfId + " is making a connection to Peer " + peerId);

                        peerProcess.printLog("received a handshake message from " + peerId+" to "+selfId);

                        peerProcess.peerIdToSocket.put(peerId,this.peerSckt);

                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
                if(!sendHandShakeMessage())
                {

                    peerProcess.printLog("Failed sending Handshake for " + selfId);
                    System.exit(0);
                } else {
                    peerProcess.printLog("Success sending Handshake for " + selfId);
                }
                peerProcess.peerInfoHashMap.get(peerId).state = 2;
            }

            while(true)
            {

                int headerBytes = inputStream.read(dataBufferExcludingPayload);

                if(headerBytes == -1)
                    break;

                messageLength = new byte[Constants.SIZE_OF_DATA_MSG];
                messageType = new byte[Constants.TYPE_OF_DATA_MSG];
                System.arraycopy(dataBufferExcludingPayload, 0, messageLength, 0, Constants.SIZE_OF_DATA_MSG);
                System.arraycopy(dataBufferExcludingPayload, Constants.SIZE_OF_DATA_MSG, messageType, 0, Constants.TYPE_OF_DATA_MSG);
                DataMsg dataMessage = new DataMsg();
                dataMessage.setMsgLen(messageLength);
                dataMessage.setMsgType(messageType);
                if(dataMessage.getMessageTypeString().equals(Constants.CHOKE_DATA_MESSAGE)
                        ||dataMessage.getMessageTypeString().equals(Constants.UNCHOKE_DATA_MESSAGE)
                        ||dataMessage.getMessageTypeString().equals(Constants.INTERESTED_DATA_MESSAGE)
                        ||dataMessage.getMessageTypeString().equals(Constants.NOTINTERESTED_DATA_MESSAGE)){
                    dataMsgWrapper.dataMsg = dataMessage;
                    dataMsgWrapper.myPeerID = this.peerId;
                    peerProcess.pushToMsgQueue(dataMsgWrapper);
                }
                else {
                    int bytesAlreadyRead = 0;
                    int bytesRead;
                    byte []dataBuffPayload = new byte[dataMessage.getMessageLengthInt()-1];
                    while(bytesAlreadyRead < dataMessage.getMessageLengthInt()-1){
                        bytesRead = inputStream.read(dataBuffPayload, bytesAlreadyRead, dataMessage.getMessageLengthInt()-1-bytesAlreadyRead);
                        if(bytesRead == -1)
                            return;
                        bytesAlreadyRead += bytesRead;
                    }

                    byte []dataBuffWithPayload = new byte [dataMessage.getMessageLengthInt()+Constants.SIZE_OF_DATA_MSG];
                    System.arraycopy(dataBufferExcludingPayload, 0, dataBuffWithPayload, 0, Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG);
                    System.arraycopy(dataBuffPayload, 0, dataBuffWithPayload, Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG, dataBuffPayload.length);

                    DataMsg dataMsgWithPayload = DataMsg.decodeMessage(dataBuffWithPayload);
                    dataMsgWrapper.dataMsg = dataMsgWithPayload;
                    dataMsgWrapper.myPeerID = peerId;
                    peerProcess.pushToMsgQueue(dataMsgWrapper);
                    dataBuffPayload = null;
                    dataBuffWithPayload = null;
                    bytesAlreadyRead = 0;
                    bytesRead = 0;
                }
            }

        }
        catch (IOException ioException)
        {
            peerProcess.printLog(ioException.toString());
        }

    }

    private boolean sendHandShakeMessage()
    {
        boolean success=true;
        try
        {
            outputStream.write(Handshake.encode(new Handshake(Constants.HANDSHAKE_HEADER_NAME,selfId)));
        }
        catch (IOException exception)
        {
            peerProcess.printLog(exception.toString());
            success=false;
        }
        return success;
    }
}
