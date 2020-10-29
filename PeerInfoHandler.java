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

    public void run(){

    }
}
