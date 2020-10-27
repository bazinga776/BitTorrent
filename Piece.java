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

    public void initPiece(byte []payload)
    {
        byte[] byteIndex = new byte[Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload, 0, byteIndex, 0, Constants.PIECE_INDEX_LENGTH);
        pieceIndex = byteArrayToInt(byteIndex);
        pieceData = new byte[payload.length-Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload,Constants.PIECE_INDEX_LENGTH, pieceData, 0, payload.length-Constants.PIECE_INDEX_LENGTH);
    }

    public int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }

    public static int byteArrayToInt(byte[] b, int offset)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }



}
