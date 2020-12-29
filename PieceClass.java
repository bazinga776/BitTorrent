
public class PieceClass {

    int pieceIdx;
    String myPeerId;
    boolean isPrsnt;
    byte[] pcData;

    public PieceClass()
    {
        pieceIdx = -1;
        myPeerId = null;
        isPrsnt = false;
        pcData = new byte[CommonCfg.pieceSize];
    }

    public static PieceClass initializePiece(byte []payload)
    {   PieceClass pc = new PieceClass();
        byte[] Idx = new byte[Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload, 0, Idx, 0, Constants.PIECE_INDEX_LENGTH);
        pc.pieceIdx = ArrToIntConv(Idx);
        pc.pcData = new byte[payload.length-Constants.PIECE_INDEX_LENGTH];
        System.arraycopy(payload,Constants.PIECE_INDEX_LENGTH, pc.pcData, 0, payload.length-Constants.PIECE_INDEX_LENGTH);
        return pc;
    }

    public static int ArrToIntConv(byte[] b) {
        return ArrToIntConv(b, 0);
    }

    public static int ArrToIntConv(byte[] b, int offsetVal)
    {
        int output = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            output += (b[i + offsetVal] & 0x000000FF) << shift;
        }
        return output;
    }

    public boolean isPrsnt() {
        return isPrsnt;
    }

    public void setPcData(byte[] pcData) {
        this.pcData = pcData;
    }

    public void setPcIdx(int pieceIdx) {
        this.pieceIdx = pieceIdx;
    }

    public int getPcIdx() {  return pieceIdx;  }

    public String getFromPeerId() {
        return myPeerId;
    }

    public byte[] getPcData() {
        return pcData;
    }

    public void setMyPeerId(String myPeerId) {
        this.myPeerId = myPeerId;
    }

    public void setIsPrsnt(boolean isPrsnt) {
        this.isPrsnt = isPrsnt;
    }

}