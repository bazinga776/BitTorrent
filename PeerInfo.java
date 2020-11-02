public class PeerInfo {
    private final String peerId;
    private final String peerAddress;
    private final String peerPort;
    private final boolean hasFile;
    private final boolean isFirst;
    int index;

    public PeerInfo(String peerId, String peerAddress, String peerPort, boolean hasFile, int index,boolean isFirst) {
        this.peerId = peerId;
        this.peerAddress = peerAddress;
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
        return peerAddress;
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
}
