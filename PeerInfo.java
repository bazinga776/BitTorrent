import java.util.Date;

public class PeerInfo {


    public final String peerId;
    public final String peerAddrs;
    public final String peerPort;
    public boolean hasFile;
    public boolean isFirst;
    public int state = -1;
    int index;

    public double dataRate = 0;
    public int interestedTrue = 1;
    public int isPreferredNeighbor = 0;
    public int isOptUnchokedNeighbor = 0;
    public int chokedTrue = 1;
    public BitFieldCls bitField;
    public int completedTrue = 0;
    public int handshakedTrue = 0;
    public Date startTime;
    public Date finishTime;

    public PeerInfo(String peerId, String peerAddrs, String peerPort, int index) {
        this.peerId = peerId;
        this.peerAddrs = peerAddrs;
        this.peerPort = peerPort;

        this.index=index;

    }


    public PeerInfo(String peerId, String peerAddrs, String peerPort, boolean hasFile, int index,boolean isFirst) {
        this.peerId = peerId;
        this.peerAddrs = peerAddrs;
        this.peerPort = peerPort;
        this.hasFile = hasFile;
        this.index=index;
        this.isFirst=isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getPeerAddress() {
        return peerAddrs;
    }

    public String getPeerPort() {
        return peerPort;
    }

    public boolean hasFile() {
        return hasFile;
    }

    public void printAll(){
        System.out.println(getPeerAddress()+getPeerId()+getPeerPort()+hasFile);
    }

    public int getIndex() {
        return index;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getDataRate() {
        return dataRate;
    }

    public void setDataRate(double dataRate) {
        this.dataRate = dataRate;
    }

    public int getIsInterested() {
        return interestedTrue;
    }

    public void setIsInterested(int interestedTrue) {
        this.interestedTrue = interestedTrue;
    }

    public int getIsPreferredNeighbor() {
        return isPreferredNeighbor;
    }

    public void setIsPreferredNeighbor(int isPreferredNeighbor) {
        this.isPreferredNeighbor = isPreferredNeighbor;
    }

    public int getIsOptUnchokedNeighbor() {
        return isOptUnchokedNeighbor;
    }

    public void setIsOptUnchokedNeighbor(int isOptUnchokedNeighbor) {
        this.isOptUnchokedNeighbor = isOptUnchokedNeighbor;
    }

    public int getIsChoked() {
        return chokedTrue;
    }

    public void setIsChoked(int chokedTrue) {
        this.chokedTrue = chokedTrue;
    }

    public BitFieldCls getBitField() {
        return bitField;
    }

    public void setBitField(BitFieldCls bitField) {
        this.bitField = bitField;
    }

    public int getIsCompleted() {
        return completedTrue;
    }

    public void setIsCompleted(int isCompleted) {
        this.completedTrue = isCompleted;
    }

    public int getIsHandShaked() {
        return handshakedTrue;
    }

    public void setIsHandShaked(int isHandShaked) {
        this.handshakedTrue = isHandShaked;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int compareTo(PeerInfo peerInfo) {

        if (this.dataRate > peerInfo.dataRate)
            return 1;
        else if (this.dataRate == peerInfo.dataRate)
            return 0;
        else
            return -1;
    }

}
