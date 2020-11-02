import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.*;

public class peerProcess {

    String peerId;
    CommonCfg commonCfg;
    PeerInfo peerInfo;
    HashMap<String, PeerInfo> peerInfoHashMap = new HashMap<String, PeerInfo>();
    boolean isFirst;
    Logger logger;
    int peerIndex;
    int PORT_LISTENING_AT;
    Thread threadForListening;
    ServerSocket socketForListening = null;
    Vector<Thread> threadReceiving = new Vector<Thread>();
    BitField bitField;
    Vector<Thread> threadSending = new Vector<Thread>();

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
            int i=0;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] splited = line.split(" ");
                boolean hasFile=Integer.parseInt(splited[3])==1;
                boolean isFirst=false;


                if(i==0)
                    isFirst=true;
                peerInfo=new PeerInfo(splited[0], splited[1], splited[2],hasFile,i,isFirst);
                peerInfoHashMap.put(splited[0],peerInfo);
                i=i+1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createEmptyFile() {
        try {
            File dir = new File(peerId);
            dir.mkdir();

            File directory = new File(peerId);
            File newFile = new File(directory, commonCfg.getFileName());
            FileWriter os = new FileWriter(newFile, true);

            byte b = 0;

            for (int i = 0; i < commonCfg.getFileSize(); i++)
                os.write(b);
            os.close();
        }
        catch (IOException e) {
            printLog("Could not create file for "+peerId+e.toString());
        }

    }

    public void printLog(String str){
        this.logger.printLOG(str);
    }


    public static void main(String[] args) {
        peerProcess peerProcessObj = new peerProcess();
        peerProcessObj.peerId = "1001";


        peerProcessObj.logger = new Logger();
        peerProcessObj.logger.init("log_" + peerProcessObj.peerId + ".txt");
        try {
            peerProcessObj.populateCommonConfiguration();
            peerProcessObj.populatePeerInfo();
            peerProcessObj.isFirst=peerProcessObj.peerInfoHashMap.get(peerProcessObj.peerId+"").isFirst();

            peerProcessObj.commonCfg.printAll();
            peerProcessObj.peerInfoHashMap.get(peerProcessObj.peerId).printAll();


            peerProcessObj.bitField = new
                    BitField(peerProcessObj.peerId, peerProcessObj.isFirst, peerProcessObj.commonCfg);


            if(peerProcessObj.isFirst)
            {
                try
                {
                    //Start listening on the port.
                    peerProcessObj.socketForListening = new ServerSocket(peerProcessObj.PORT_LISTENING_AT);

                    peerProcessObj.threadForListening = new Thread(new ThreadForListening(peerProcessObj.peerId, peerProcessObj.socketForListening));
                    peerProcessObj.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcessObj.printLog("Time Out Exception for : "+peerProcessObj.peerId + "when starting the thread that is listening" + timeOutEx.toString());
                    peerProcessObj.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcessObj.printLog("Input Output Exception for : "+peerProcessObj.peerId + "when starting the thread that is listening" + peerProcessObj.PORT_LISTENING_AT + " " + IOEx.toString());
                    peerProcessObj.logger.close();
                    System.exit(0);
                }
            }
            else{
                peerProcessObj.createEmptyFile();

                Iterator hmIterator = peerProcessObj.peerInfoHashMap.entrySet().iterator();

                while(hmIterator.hasNext())
                {
                    Map.Entry mapElement = (Map.Entry)hmIterator.next();
                    PeerInfo peerInfo=(PeerInfo) mapElement.getValue();
                    if(peerProcessObj.peerIndex > peerInfo.getIndex())
                    {   //Create a connection with ACTIVE connection type
                        Thread tempThread = new Thread(new PeerInfoHandler(
                                peerInfo.getPeerAddress(), Integer
                                .parseInt(peerInfo.getPeerPort()), 1,
                                peerProcessObj.peerId));
                        peerProcessObj.threadReceiving.add(tempThread);
                        tempThread.start();
                    }
                }


                try
                {
                    peerProcessObj.socketForListening = new ServerSocket(peerProcessObj.PORT_LISTENING_AT);
                    peerProcessObj.threadForListening = new Thread(new ThreadForListening(peerProcessObj.peerId, peerProcessObj.socketForListening));
                    peerProcessObj.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcessObj.printLog("Time Out Exception for : "+peerProcessObj.peerId + "when starting the thread that is listening" + timeOutEx.toString());

                    peerProcessObj.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcessObj.printLog("Input Output Exception for : "+peerProcessObj.peerId + "when starting the thread that is listening" + peerProcessObj.threadForListening + " " + IOEx.toString());

                    peerProcessObj.logger.close();
                    System.exit(0);
                }
            }

            peerProcessObj.logger.close();

        } catch (Exception e) {
        }
    }
}