
import java.io.UnsupportedEncodingException;

public class DataMsg {

    private String messageLen;
    private String messageType;
    Constants Constants = new Constants();
    private int dataLen = Constants.TYPE_OF_DATA_MSG;
    private byte[] length = null;
    private byte[] payload = null;
    private byte[] type = null;

    public DataMsg() { }

    public DataMsg(String Type, byte[] Payload)
    {
        try
        {
            if (Payload != null)
            {

                this.setMessageLength(Payload.length + 1);
                if (this.length.length > Constants.SIZE_OF_DATA_MSG)
                    throw new Exception("length of data message is too large.");

                this.setPayload(Payload);

            }
            else
            {
                if (Type == Constants.CHOKE_DATA_MESSAGE || Type == Constants.UNCHOKE_DATA_MESSAGE
                        || Type == Constants.INTERESTED_DATA_MESSAGE
                        || Type == Constants.NOTINTERESTED_DATA_MESSAGE)
                {
                    this.setMessageLength(1);
                    this.payload = null;
                }
                else
                    throw new Exception("Pay load should not be null");


            }

            this.setMessageType(Type);
            if (this.getMessageType().length > Constants.TYPE_OF_DATA_MSG)
                throw new Exception("Type of data message length is too large.");

        } catch (Exception e) {
            peerProcess.printLog(e.toString());
        }

    }


    public static int byteArrayToInt(byte[] inputArray) {
        int output = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (3 - i) * 8;
            output = output + (inputArray[i + 0] & 0x000000FF) << shift;
        }
        return output;
    }

    public static byte[] intToByteArray(int input)
    {
        byte[] output = new byte[4];
        for (int i = 0; i < 4; i++)
        {
            int offset = (output.length - 1 - i) * 8;
            output[i] = (byte) ((input >>> offset) & 0xFF);
        }
        return output;
    }

    public String getMessageTypeString() {
        return messageType;
    }

    public byte[] getMessageLength() {
        return length;
    }

    public byte[] getMessageType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public static byte[] encodeMessage(DataMsg msg)
    {
        byte[] msgStream = null;
        int msgType;
        Constants Constants = new Constants();

        try
        {

            msgType =Integer.parseInt(msg.getMessageTypeString());
            if (msg.getMessageLength().length > Constants.SIZE_OF_DATA_MSG)
                throw new Exception("Invalid message length.");
            else if (msgType < 0 || msgType > 7)
                throw new Exception("Invalid message type.");
            else if (msg.getMessageType() == null)
                throw new Exception("Invalid message type.");
            else if (msg.getMessageLength() == null)
                throw new Exception("Invalid message length.");

            if (msg.getPayload()!= null) {
                msgStream = new byte[Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG + msg.getPayload().length];

                System.arraycopy(msg.getMessageLength(), 0, msgStream, 0, msg.getMessageLength().length);
                System.arraycopy(msg.getMessageType(), 0, msgStream, Constants.SIZE_OF_DATA_MSG, Constants.TYPE_OF_DATA_MSG);
                System.arraycopy(msg.getPayload(), 0, msgStream, Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG, msg.getPayload().length);


            } else {
                msgStream = new byte[Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG];

                System.arraycopy(msg.getMessageLength(), 0, msgStream, 0, msg.getMessageLength().length);
                System.arraycopy(msg.getMessageType(), 0, msgStream, Constants.SIZE_OF_DATA_MSG, Constants.TYPE_OF_DATA_MSG);

            }

        }
        catch (Exception e)
        {
            peerProcess.printLog(e.toString());
            msgStream = null;
        }

        return msgStream;
    }

    public void setMessageLength(byte[] len) {

        Integer l = byteArrayToInt(len);
        this.messageLen = l.toString();
        this.length = len;
        this.dataLen = l;
    }

    public void setMessageLength(int messageLength) {
        this.dataLen = messageLength;
        this.messageLen = ((Integer)messageLength).toString();
        this.length = intToByteArray(messageLength);
    }

    public void setMessageType(byte[] type) {
        try {
            this.messageType = new String(type, Constants.NAME_OF_MESSAGE_CHAR_SET);
            this.type = type;
        } catch (UnsupportedEncodingException e) {
            peerProcess.printLog(e.toString());
        }
    }

    public void setMessageType(String messageType) {
        try {
            this.messageType = messageType.trim();
            this.type = this.messageType.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            peerProcess.printLog(e.toString());
        }
    }

}
