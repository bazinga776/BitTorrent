import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadForListening implements Runnable {
    private String peerId;
    private ServerSocket listeningSocket;
    Socket peerSocket;
    Logger logger;
    peerProcess peerProcess;
    Thread sender;

    public ThreadForListening(String peerId, ServerSocket listeningSocket){
        this.peerId = peerId;
        this.listeningSocket = listeningSocket;
    }
    public void run(){
        Work();
    }
    public void Work(){
        while(true) {
            logger = new Logger();
            try {
                peerSocket = listeningSocket.accept();
                sender = new Thread(new PeerInfoHandler(peerSocket, 0, peerId));
                logger.printLOG("Established Connection with: " + peerId);
                peerProcess.threadSending.add(sender);
                sender.start();
            } catch (Exception ex) {
                logger.printLOG(this.peerId + " Exception in connection: " + ex.toString());
            }
        }
    }

    public void releaseSocket()
    {
        logger = new Logger();
        try
        {
            if(!peerSocket.isClosed())
                peerSocket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
