public class BitField {

    public Piece[] pieces;
    public int totalNoOfPieces;

    public int size;

    public BitField(CommonCfg commonCfg)
    {
        size = (int) Math.ceil(((double) commonCfg.getFileSize() / (double) commonCfg.getPieceSize()));
        this.pieces = new Piece[size];

        for (int i = 0; i < this.size; i++)
            this.pieces[i] = new Piece(commonCfg);

    }

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

    public byte[] encode()
    {
        return this.getBytes();
    }

    public byte[] getBytes()
    {
        int s = this.size / 8;
        if (size % 8 != 0)
            s = s + 1;
        byte[] iP = new byte[s];
        int tempInt = 0;
        int count = 0;
        int Cnt;
        for (Cnt = 1; Cnt <= this.size; Cnt++)
        {
            boolean tempP = this.pieces[Cnt-1].present;
            tempInt = tempInt << 1;
            if (tempP == true)
            {
                tempInt = tempInt + 1;
            } else
                tempInt = tempInt + 0;

            if (Cnt % 8 == 0 && Cnt!=0) {
                iP[count] = (byte) tempInt;
                count++;
                tempInt = 0;
            }

        }
        if ((Cnt-1) % 8 != 0)
        {
            int tempShift = ((size) - (size / 8) * 8);
            tempInt = tempInt << (8 - tempShift);
            iP[count] = (byte) tempInt;
        }
        return iP;
    }


}
