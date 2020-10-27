import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

public class peerProcess {

    String peerId;
    CommonCfg commonCfg;
    PeerInfo peerInfo;
    HashMap<String, PeerInfo> peerInfoHashMap = new HashMap<String, PeerInfo>();
    boolean isFirst;

    void populateCommonConfiguration() {
        File myObj = new File("Common.cfg");
        String fileName="";
        int numberOfPreferredNeighbors=0;
        int unchokingInterval=0;
        int optimisticUnchokingInterval=0;
        int fileSize=0;
        int pieceSize=0;

        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] splited = line.split(" ");
                switch (splited[0].toLowerCase()){
                    case "numberffpreferredneighbors":
                        numberOfPreferredNeighbors=Integer.parseInt(splited[1]);
                        break;
                    case "unchokinginterval":
                        unchokingInterval=Integer.parseInt(splited[1]);
                        break;
                    case "optimisticunchokinginterval":
                        optimisticUnchokingInterval=Integer.parseInt(splited[1]);
                        break;
                    case "filename":
                        fileName=splited[1];
                        break;
                    case "filesize":
                        fileSize=Integer.parseInt(splited[1]);
                        break;
                    case "piecesize":
                        pieceSize=Integer.parseInt(splited[1]);
                        break;
                }

            }
            commonCfg=new CommonCfg( fileName,  numberOfPreferredNeighbors,  unchokingInterval,
             optimisticUnchokingInterval,  fileSize,  pieceSize);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void populatePeerInfo(){
        File myObj = new File("PeerInfo.cfg");

        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] splited = line.split(" ");
                boolean isFirst=Integer.parseInt(splited[3])==1;
                peerInfo=new PeerInfo(splited[0], splited[1], splited[2],isFirst);
                peerInfoHashMap.put(splited[0],peerInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        peerProcess peerProcess = new peerProcess();
        peerProcess.peerId = "1001";


        Logger logger = new Logger();
        logger.init("log_" + peerProcess.peerId + ".txt");
        try {
            peerProcess.populateCommonConfiguration();
            peerProcess.populatePeerInfo();
            peerProcess.isFirst=peerProcess.peerInfoHashMap.get(peerProcess.peerId+"").isFirstPeer();

            peerProcess.commonCfg.printAll();
            peerProcess.peerInfoHashMap.get(peerProcess.peerId).printAll();

            logger.printLOG("Testing logs");
            logger.close();
        } catch (Exception e) {
        }


    }

}