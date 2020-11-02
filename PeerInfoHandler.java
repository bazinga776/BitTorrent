import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PeerInfoHandler implements Runnable{
    private Socket peerSocket = null;
    private int connectionType;
    String selfId;
    private static final int ACTIVE_CONNECTION=1;

    private static final int PASSIVE_CONNECTION=1;

    private InputStream inputStream;
    private OutputStream outputStream;

    Logger logger=new Logger();

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
            logger.printLOG(ioException.toString());
        }
    }

    public PeerInfoHandler(String address, int port, int connectionType, String selfId) throws IOException {
        this.connectionType = connectionType;
        this.selfId = selfId;

        try{
        this.peerSocket = new Socket(address, port);}catch (Exception exception){
            logger.printLOG(exception.toString());
        }

        initializeBuffers();

    }

    public void run(){
        //TBI

        if(this.connectionType==ACTIVE_CONNECTION){


        }else {

        }
    }

    private boolean sendHandShakeMessage(){
        boolean success=true;
        try{
            outputStream.write("Dummy HandShake Message".getBytes());
        }catch (Exception e){
            logger.printLOG(e.toString());
            success=false;
        }
        return success;
    }
}
