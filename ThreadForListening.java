import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadForListening implements Runnable {
    private String serverPeerId;
    private ServerSocket listeningSocket;
    Socket senderPeerSocket;
    Thread sender;

    public ThreadForListening(String serverPeerId, ServerSocket listeningSocket){
        this.serverPeerId = serverPeerId;
        this.listeningSocket = listeningSocket;
    }
    public void run(){
        while(true) {
            try {
                senderPeerSocket = listeningSocket.accept();
                //connection type - Passive
                sender = new Thread(new PeerInfoHandler(senderPeerSocket, 0, serverPeerId));
                peerProcess.printLog("Established Connection with: " + serverPeerId);
                peerProcess.threadSending.add(sender);
                sender.start();
            } catch (Exception ex) {
                peerProcess.printLog("ThreadForListening :: "+this.serverPeerId + " Exception in connection: " + ex.toString());
            }
        }
    }

    public void releaseSocket()
    {
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
