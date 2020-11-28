import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MessageHandler implements Runnable{
    private String peerId=null;
    boolean running_flag = true;

    public MessageHandler(String peerId) {
        this.peerId=peerId;
    }

    public void run(){
        DataMsg dataMessage;
        DataMsgWrapper dataMessageWrapper;
        String messageType;
        String remotePeerId;

        while(running_flag){

        }

    }

    private void sendNotInterested(Socket socket, String remotePeerId){

    }

    private boolean SendDataMessage(Socket socket, byte[] encodedBitField) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(encodedBitField);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
