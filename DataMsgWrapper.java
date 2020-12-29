
public class DataMsgWrapper
{
	DataMsg dataMsg;
	String myPeerID;
	
	public DataMsgWrapper()
	{
		dataMsg = new DataMsg();
		myPeerID = null;
	}
    public void setFromPeerID(String myPeerID) {
        this.myPeerID = myPeerID;
    }
    
    public void setDataMsg(DataMsg dataMsg) {
        this.dataMsg = dataMsg;
    }
	
    public DataMsg getDataMsg() {
		return dataMsg;
	}

	public String getMyPeerID() {
		return myPeerID;
	}
	

}
