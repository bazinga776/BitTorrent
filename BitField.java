public class BitField {

    public Piece[] pieces;
    public int size;

    public BitField(String peerId,boolean hasFile,CommonCfg commonCfg)
    {
        size = (int) Math.ceil(((double) commonCfg.getFileSize() / (double) commonCfg.getPieceSize()));
        this.pieces = new Piece[size];

        if(hasFile){
            for (int i = 0; i < this.size; i++) {
                this.pieces[i].present=true;
                this.pieces[i].fromPeerId=peerId;
            }
        }else {
            // If the file exists
            for (int i = 0; i < this.size; i++) {
                this.pieces[i].present=true;
                this.pieces[i].fromPeerId=peerId;
            }

        }
    }

}
