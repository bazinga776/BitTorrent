public class CommonCfg {
    public static  String fileName;
    public static  int numberOfPreferredNeighbors;
    public static  int unchokingInterval;
    public static  int optimisticUnchokingInterval;
    public static  int fileSize;
    public static   int pieceSize;


    public static void setAll(String fileName, int numberOfPreferredNeighbors,int  unchokingInterval,
                         int optimisticUnchokingInterval,int   fileSize,int  pieceSize){
        CommonCfg.fileName=fileName;
        CommonCfg.numberOfPreferredNeighbors=numberOfPreferredNeighbors;
        CommonCfg.unchokingInterval=unchokingInterval;
        CommonCfg.optimisticUnchokingInterval=optimisticUnchokingInterval;
        CommonCfg.fileSize=fileSize;
        CommonCfg.pieceSize=pieceSize;
    }


}
