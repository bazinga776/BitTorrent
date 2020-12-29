import java.io.*;

public class BitFieldCls {

    public PieceClass[] pcs;
    public int totalNoOfPieces;


    public BitFieldCls()
    {
        totalNoOfPieces = (int) Math.ceil(((double) CommonCfg.fileSize / (double) CommonCfg.pieceSize));
        this.pcs = new PieceClass[totalNoOfPieces];

        for (int i = 0; i < this.totalNoOfPieces; i++)
            this.pcs[i] = new PieceClass();

    }

    public BitFieldCls(String peerIdValue,boolean hasFile)
    {
        totalNoOfPieces = (int) Math.ceil(((double) CommonCfg.fileSize / (double) CommonCfg.pieceSize));
        this.pcs = new PieceClass[totalNoOfPieces];

        for (int i = 0; i < totalNoOfPieces; i++)
            this.pcs[i] = new PieceClass();

        System.out.println("Total no of pcs: "+totalNoOfPieces);
        System.out.println("Has file:"+hasFile);
        if(hasFile){
            for (int i = 0; i < totalNoOfPieces; i++) {
                this.pcs[i].setIsPrsnt(true);
                this.pcs[i].setMyPeerId(peerIdValue);
                System.out.println(i + " has been set to true");
            }
        }else {
            for (int i = 0; i <totalNoOfPieces; i++) {
                this.pcs[i].setIsPrsnt(false);
                this.pcs[i].setMyPeerId(peerIdValue);
            }

        }


    }

    public byte[] encodeInput()
    {
        return this.getBytes();
    }

    public byte[] getBytes()
    {
        int s = this.totalNoOfPieces / 8;

        System.out.println("Total no of pieces (getby): "+totalNoOfPieces);
        if (totalNoOfPieces % 8 != 0)
            s = s + 1;
        byte[] iP = new byte[s];
        int tInt = 0;
        int sum = 0;
        int tally;
        for (tally = 1; tally <= this.totalNoOfPieces; tally++)
        {
            boolean tPiece = this.pcs[tally-1].isPrsnt();
            System.out.println(tPiece+" for "+(tally-1));
            tInt = tInt << 1;
            if (tPiece)
            {
                tInt = tInt + 1;
            } else
                tInt = tInt + 0;

            if (tally % 8 == 0 && tally!=0) {
                iP[sum] = (byte) tInt;
                sum++;
                tInt = 0;
            }

        }
        if ((tally-1) % 8 != 0)
        {
            int tShift = ((totalNoOfPieces) - (totalNoOfPieces / 8) * 8);
            tInt = tInt << (8 - tShift);
            System.out.println("Temp INt "+tInt);
            iP[sum] = (byte) tInt;
        }
        return iP;
    }


    public static BitFieldCls decodeInput(byte[] b)
    {
        BitFieldCls returnBitField = new BitFieldCls();
        for(int i = 0 ; i < b.length; i ++)
        {
            int sum = 7;
            while(sum >=0)
            {
                int test = 1 << sum;
                if(i * 8 + (8-sum-1) < returnBitField.totalNoOfPieces)
                {
                    if((b[i] & (test)) != 0)
                        returnBitField.pcs[i * 8 + (8-sum-1)].setIsPrsnt(true);
                    else
                        returnBitField.pcs[i * 8 + (8-sum-1)].setIsPrsnt(false);
                }
                sum--;
            }
        }

        return returnBitField;
    }


    public int getSize(){return totalNoOfPieces;}

    public PieceClass[] getPieces(){return pcs;}

    public synchronized boolean compare(BitFieldCls yourBitField) {
        int yourSize = yourBitField.getSize();

        for (int i = 0; i < yourSize; i++) {
            if (yourBitField.getPieces()[i].isPrsnt()
                    && !this.getPieces()[i].isPrsnt()) {
                return true;
            } else
                continue;
        }

        return false;
    }

    public synchronized void updateBF(String peerIdValue, PieceClass pc) {
        try
        {
            if (peerProcess.curBitField.pcs[pc.pieceIdx].isPrsnt()) {
                peerProcess.printLog(peerIdValue + " Piece already received!!");
            }
            else
            {
                String fileName = CommonCfg.fileName;
                File file = new File(peerProcess.peerId, fileName);
                int offvalue = pc.pieceIdx * CommonCfg.pieceSize;
                RandomAccessFile randAF = new RandomAccessFile(file, "rw");
                byte[] byteWrite;
                byteWrite = pc.getPcData();

                randAF.seek(offvalue);
                randAF.write(byteWrite);

                this.pcs[pc.pieceIdx].setIsPrsnt(true);
                this.pcs[pc.pieceIdx].setMyPeerId(peerIdValue);
                randAF.close();

                if(pc.pieceIdx==318){
                    System.out.println("WHY IS THIS NEVER GETTING PRINTED!!!!!!");
                    System.out.println(isCompleted());
                }

                peerProcess.printLog("Peer "+peerProcess.peerId
                        + " has downloaded the PIECE " + pc.pieceIdx
                        + " from Peer " + peerIdValue
                        + ". Now the number of pieces it has is "
                        + peerProcess.curBitField.myPcs());

                if (peerProcess.curBitField.isCompleted()) {
                    peerProcess.peerInfoHashMap.get(peerProcess.peerId).interestedTrue = 0;
                    peerProcess.peerInfoHashMap.get(peerProcess.peerId).completedTrue = 1;
                    peerProcess.peerInfoHashMap.get(peerProcess.peerId).chokedTrue = 0;
                    updatePeerInfo(peerProcess.peerId, 1);

                    peerProcess.printLog("Peer "+peerProcess.peerId + " has DOWNLOADED the complete file.");
                }
            }

        } catch (Exception e) {
            peerProcess.printLog("Peer "+peerProcess.peerId
                    + " ERROR while updating the bitfield " + e.getMessage());
        }

    }

    public int myPcs()
    {
        int sum = 0;
        for (int i = 0; i < this.totalNoOfPieces; i++)
            if (this.pcs[i].isPrsnt())
                sum++;

        return sum;
    }

    public boolean isCompleted() {

        for (int i = 0; i < totalNoOfPieces; i++) {
            if (!this.pcs[i].isPrsnt()) {
                return false;
            }
        }
        return true;
    }

    // PeerInfo.cfg - Updated
    public void updatePeerInfo(String clientID, int hasFile)
    {
        BufferedWriter out = null;
        BufferedReader in = null;

        try
        {
            in= new BufferedReader(new FileReader("PeerInfo.cfg"));

            String line;
            StringBuffer buffer = new StringBuffer();

            while((line = in.readLine()) != null)
            {
                if(line.trim().split("\\s+")[0].equals(clientID))
                {
                    buffer.append(line.trim().split("\\s+")[0] + " " + line.trim().split("\\s+")[1] + " " + line.trim().split("\\s+")[2] + " " + hasFile);
                }
                else
                {
                    buffer.append(line);

                }
                buffer.append("\n");
            }

            in.close();

            out= new BufferedWriter(new FileWriter("PeerInfo.cfg"));
            out.write(buffer.toString());

            out.close();
        }
        catch (Exception e)
        {
            peerProcess.printLog(clientID + " Error in updating the PeerInfo.cfg " +  e.getMessage());
        }
    }


    public synchronized int returnFirstDiff(BitFieldCls yourBitField)
    {
        int mySize = this.getSize();
        int yourSize = yourBitField.getSize();

        if (mySize >= yourSize) {
            for (int i = 0; i < yourSize; i++) {
                if (yourBitField.getPieces()[i].isPrsnt()
                        && !this.getPieces()[i].isPrsnt() ) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < mySize; i++) {
                if (yourBitField.getPieces()[i].isPrsnt()
                        && !this.getPieces()[i].isPrsnt()) {
                    return i;
                }
            }
        }

        return -1;
    }
}
