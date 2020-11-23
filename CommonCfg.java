public class CommonCfg {
    private final String fileName;
    private final int numberOfPreferredNeighbors;
    private final int unchokingInterval;
    private final int optimisticUnchokingInterval;
    public static final int FileSize;
    public static final int PieceSize;

    public CommonCfg(String fileName, int numberOfPreferredNeighbors, int unchokingInterval,
                     int optimisticUnchokingInterval, int fileSize, int pieceSize) {
        this.fileName = fileName;
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.FileSize = fileSize;
        this.PieceSize = pieceSize;
    }

    public String getFileName() {
        return fileName;
    }

    public int getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }

    public int getFileSize() {
        return FileSize;
    }

    public int getPieceSize() {
        return PieceSize;
    }

    public void printAll(){
        System.out.println(fileName+"\n"+numberOfPreferredNeighbors+unchokingInterval+
                optimisticUnchokingInterval+fileName+PieceSize);
    }
}
