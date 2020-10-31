import java.io.IOException;
import java.net.Socket;

public class PeerInfoHandler implements Runnable{
    private Socket peerSocket = null;
    private int connType;
    String selfId;

    public PeerInfoHandler(Socket peerSocket, int connType, String selfId) {

        this.peerSocket = peerSocket;
        this.connType = connType;
        this.selfId = selfId;
    //TBI
    }

    public PeerInfoHandler(String address, int port, int connType, String selfId) throws IOException {
        this.connType = connType;
        this.selfId = selfId;
        this.peerSocket = new Socket(address, port);
        //TBI
    }

    public void run(){
        //TBI
    }
}
