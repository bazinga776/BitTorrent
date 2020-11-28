import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PeerInfoHandler implements Runnable{
    private Socket peerSocket = null;
    private int connectionType;
    String selfId,peerId;
    private static final int ACTIVE_CONNECTION=1;
    private Handshake handshake;

    private static final int PASSIVE_CONNECTION=0;

    private InputStream inputStream;
    private OutputStream outputStream;


    public PeerInfoHandler(Socket peerSocket, int connectionType, String selfId) {

        this.peerSocket = peerSocket;
        this.connectionType = connectionType;
        this.selfId = selfId;

        initializeBuffers();

    }

    private void initializeBuffers(){
        try{
            inputStream=peerSocket.getInputStream();
            outputStream=peerSocket.getOutputStream();

        }catch (IOException ioException){
            peerProcess.printLog(ioException.toString());
        }
    }

    public PeerInfoHandler(String address, int port, int connectionType, String selfId) throws IOException {
        this.connectionType = connectionType;
        this.selfId = selfId;

        try{
        this.peerSocket = new Socket(address, port);}catch (Exception exception){
            peerProcess.printLog(exception.toString());
        }

        initializeBuffers();

    }

    public void run(){
        //TBI

        byte []inputHandShake = new byte[32];


        try {
            if (this.connectionType == ACTIVE_CONNECTION) {

                if (!sendHandShakeMessage()) {
                    peerProcess.printLog("Failed sending Handshake for " + selfId);
                    System.exit(0);
                } else {
                    peerProcess.printLog("Success sending Handshake for " + selfId);
                }
                while (true) {
                    inputStream.read(inputHandShake);
                    handshake = Handshake.decode(inputHandShake);
                    String header = new String(handshake.getHeader());

                    if (header.equals(Constants.HANDSHAKE_HEADER_NAME)) {

                        peerId = new String(handshake.getPeerID());

                        peerProcess.printLog(selfId + " is making a connection to  " + peerId);

                        peerProcess.printLog("received a handshake message from " + peerId+" to "+selfId);

                        break;
                    } else {
                        continue;
                    }
                }
                // Sending BitField...
                DataMsg d = new DataMsg(Constants.BITFIELD_DATA_MESSAGE, peerProcess.curBitField.encode());
                byte  []b = DataMsg.encodeMessage(d);
                outputStream.write(b);
                peerProcess.peerInfoHashMap.get(peerId).state = 8;
            }else{
                while(true)
                {
                    inputStream.read(inputHandShake);
                    handshake = Handshake.decode(inputHandShake);

                    String header = new String(handshake.getHeader());

                    if(header.equals(Constants.HANDSHAKE_HEADER_NAME))
                    {
                        peerId = new String( handshake.getPeerID());

                        peerProcess.printLog(selfId + " is making a connection to  " + peerId);

                        peerProcess.printLog("received a handshake message from " + peerId+" to "+selfId);

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
        catch (Exception e)
        {
            peerProcess.printLog(e.toString());
            success=false;
        }
        return success;
    }
}
