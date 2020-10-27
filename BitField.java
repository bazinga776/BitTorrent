public class BitField {

    public Piece[] pieces;
    public int size;

    public BitField(String peerId,boolean hasFile)
    {
        size = (int) Math.ceil(((double) CommonProperties.fileSize / (double) CommonProperties.pieceSize));
        this.pieces = new Piece[size];

        if(hasFile){
            for (int i = 0; i < this.size; i++) {
                this.pieces[i].setIsPresent();
                this.pieces[i].setFromPeerID(OwnPeerId);
            }
        }else {
            // If the file exists
            for (int i = 0; i < this.size; i++) {
                this.pieces[i].setIsPresent(1);
                this.pieces[i].setFromPeerID(OwnPeerId);
            }

        }
    }

}
