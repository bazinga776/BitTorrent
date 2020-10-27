public class Piece {
    boolean present;
    String fromPeerId;
    byte[] pieceData;
    int pieceIndex;

    public Piece(boolean present, String fromPeerId) {
        this.present = present;
        this.fromPeerId = fromPeerId;
    }

    public boolean isPresent() {
        return present;
    }

    public String getFromPeerId() {
        return fromPeerId;
    }

    public byte[] getPieceData() {
        return pieceData;
    }

    public int getPieceIndex() {
        return pieceIndex;
    }

    public Piece getPiece(byte []payload)
    {
        byte[] byteIndex = new byte[Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload, 0, byteIndex, 0, Constants.PIECE_INDEX_LENGTH);
        pieceIndex = ConversionUtil.byteArrayToInt(byteIndex);
        pieceData = new byte[payload.length-Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload,Constants.PIECE_INDEX_LENGTH, pieceData.pi, 0, payload.length-MessageConstants.PIECE_INDEX_LEN);
        return piece;
    }


}
