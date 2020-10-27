public class PeerInfo {
    public final String peerId,peerAddress,peerPort;
    public final boolean firstPeer;

    public PeerInfo(String peerId, String peerAddress, String peerPort, boolean firstPeer) {
        this.peerId = peerId;
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
        this.firstPeer = firstPeer;
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
}
