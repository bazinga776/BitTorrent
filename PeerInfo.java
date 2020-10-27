public class PeerInfo {
    public String peerId,peerAddress,peerPort;
    public boolean firstPeer;
    int index;

    public PeerInfo(String peerId, String peerAddress, String peerPort, boolean firstPeer, int index) {
        this.peerId = peerId;
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.firstPeer = firstPeer;
        this.index=index;
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

    public boolean isFirstPeer() {
        return firstPeer;
    }

    public void printAll(){
        System.out.println(getPeerAddress()+getPeerId()+getPeerPort()+isFirstPeer());
    }

    public int getIndex() {
        return index;
    }
}
