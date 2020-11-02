import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadForListening implements Runnable {
    private String serverPeerId;
    private ServerSocket listeningSocket;
    Socket senderPeerSocket;
    Logger logger;
    peerProcess peerProcess;
    Thread sender;

    public ThreadForListening(String serverPeerId, ServerSocket listeningSocket){
        this.serverPeerId = serverPeerId;
        this.listeningSocket = listeningSocket;
    }
    public void run(){
        Work();
    }

    public void Work(){
        while(true) {
            logger = new Logger();
            try {
                senderPeerSocket = listeningSocket.accept();
                //Passive type connection
                sender = new Thread(new PeerInfoHandler(senderPeerSocket, 0, serverPeerId));
                logger.printLOG("Established Connection with: " + serverPeerId);
                peerProcess.threadSending.add(sender);
                sender.start();
            } catch (Exception ex) {
                logger.printLOG(this.serverPeerId + " Exception in connection: " + ex.toString());
            }
        }
    }

    public void releaseSocket()
    {
        logger = new Logger();
        try
        {
            if(!senderPeerSocket.isClosed())
                senderPeerSocket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
