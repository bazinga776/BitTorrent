
public class DataMsgWrapper
{
    DataMsg dataMessage;
    String sendingPeerID;

    public DataMsgWrapper()
    {
        dataMessage = new DataMsg();
        sendingPeerID = null;
    }
    public void setSendingPeerID(String fromPeerID) {
        this.sendingPeerID = sendingPeerID;
    }

    public void setDataMsg(DataMsg dataMessage) {
        this.dataMessage = dataMessage;
    }

    public DataMsg getDataMessage() {
        return dataMessage;
    }

    public String getSendingPeerID() {
        return sendingPeerID;
    }



}
