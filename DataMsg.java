import java.io.UnsupportedEncodingException;

public class DataMsg {

    private String msgLen;
    private String msgType;
    private int dataLen = Constants.TYPE_OF_DATA_MSG;
    private byte[] lgth = null;
    private byte[] payload = null;
    private byte[] type = null;

    public DataMsg() { }

    public DataMsg(String msgType){
        try {

            if (msgType == Constants.CHOKE_DATA_MESSAGE || msgType == Constants.UNCHOKE_DATA_MESSAGE
                    || msgType == Constants.NOTINTERESTED_DATA_MESSAGE
                    || msgType == Constants.INTERESTED_DATA_MESSAGE)
            {
                this.setMsgLen(1);
                this.setMsgType(msgType);
                this.payload = null;
            }
            else
                throw new Exception("DataMsg :: Wrong constructor selected");


        } catch (Exception e) {
            peerProcess.printLog(e.toString());
        }
    }

    public DataMsg(String Type, byte[] Payload)
    {
        try
        {
            if (Payload != null)
            {

                this.setMsgLen(Payload.length + 1);
                if (this.lgth.length > Constants.SIZE_OF_DATA_MSG)
                    throw new Exception("length of data message is too large.");

                this.setPayload(Payload);

            }
            else
            {
                if (Type == Constants.CHOKE_DATA_MESSAGE || Type == Constants.UNCHOKE_DATA_MESSAGE
                        || Type == Constants.INTERESTED_DATA_MESSAGE
                        || Type == Constants.NOTINTERESTED_DATA_MESSAGE)
                {
                    this.setMsgLen(1);
                    this.payload = null;
                }
                else
                    throw new Exception("DataMsg :: Pay load should not be null");
            }

            this.setMsgType(Type);
            if (this.getMessageType().length > Constants.TYPE_OF_DATA_MSG)
                throw new Exception("DataMsg :: Type of data message length is very large.");

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
        return msgType;
    }

    public byte[] getMessageLength() {
        return lgth;
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

        try
        {

            msgType =Integer.parseInt(msg.getMessageTypeString());
            if (msg.getMessageLength().length > Constants.SIZE_OF_DATA_MSG)
                throw new Exception("DataMsg :: Invalid message length.");
            else if (msgType < 0 || msgType > 7)
                throw new Exception("DataMsg :: Invalid message type.");
            else if (msg.getMessageType() == null)
                throw new Exception("DataMsg :: Invalid message type.");
            else if (msg.getMessageLength() == null)
                throw new Exception("DataMsg :: Invalid message length.");

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

    public void setMsgLen(byte[] len) {

        Integer l = byteArrayToInt(len);
        this.msgLen = l.toString();
        this.lgth = len;
        this.dataLen = l;
    }

    public void setMsgLen(int messageLength) {
        this.dataLen = messageLength;
        this.msgLen = ((Integer)messageLength).toString();
        this.lgth = intToByteArray(messageLength);
    }

    public void setMsgType(byte[] type) {
        try {
            this.msgType = new String(type, Constants.NAME_OF_MESSAGE_CHAR_SET);
            this.type = type;
        } catch (UnsupportedEncodingException e) {
            peerProcess.printLog(e.toString());
        }
    }

    public void setMsgType(String msgType) {
        try {
            this.msgType = msgType.trim();
            this.type = this.msgType.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
        } catch (UnsupportedEncodingException e) {
            peerProcess.printLog(e.toString());
        }
    }

    public static DataMsg decodeMessage(byte[] Message) {

        DataMsg msg = new DataMsg();
        byte[] msgLength = new byte[Constants.SIZE_OF_DATA_MSG];
        byte[] msgType = new byte[Constants.TYPE_OF_DATA_MSG];
        byte[] payLoad = null;
        int len;

        try
        {

            if (Message == null)
                throw new Exception("DataMsg :: Invalid data.");
            else if (Message.length < Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG)
                throw new Exception("DataMsg :: Byte array length is too small...");


            System.arraycopy(Message, 0, msgLength, 0, Constants.SIZE_OF_DATA_MSG);
            System.arraycopy(Message, Constants.SIZE_OF_DATA_MSG, msgType, 0, Constants.TYPE_OF_DATA_MSG);

            msg.setMsgLen(msgLength);
            msg.setMsgType(msgType);

            len = byteArrayToInt(msgLength);

            if (len > 1)
            {
                payLoad = new byte[len-1];
                System.arraycopy(Message, Constants.SIZE_OF_DATA_MSG + Constants.TYPE_OF_DATA_MSG,	payLoad, 0, Message.length - Constants.SIZE_OF_DATA_MSG - Constants.TYPE_OF_DATA_MSG);
                msg.setPayload(payLoad);
            }

            payLoad = null;
        }
        catch (Exception e)
        {
            peerProcess.printLog("DataMsg :: "+e.toString());
            msg = null;
        }
        return msg;
    }


    public int getMessageLengthInt() {
        return this.dataLen;
    }

}
