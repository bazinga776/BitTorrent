public class BitField {

    public Piece[] pieces;
    public int totalNoOfPieces;

    public BitField(String peerId,boolean hasFile,CommonCfg commonCfg)
    {
        totalNoOfPieces = (int) Math.ceil(((double) commonCfg.getFileSize() / (double) commonCfg.getPieceSize()));
        this.pieces = new Piece[totalNoOfPieces];

        if(hasFile){
            for (int i = 0; i < this.totalNoOfPieces; i++) {
                this.pieces[i]=new Piece(true,peerId);
            }
        }else {
            for (int i = 0; i < this.totalNoOfPieces; i++) {
                this.pieces[i]=new Piece(false,peerId);
            }

        }
    }

}
