import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.*;

/*
By
Venkata Sindhu Kandula (1914 5414)
Nikhilesh Reddy Tummala (8350-1593)
Adil Shaik (6998-5592)
 */


public class peerProcess {

    static String peerId;
    static CommonCfg commonCfg;
    static PeerInfo peerInfo;
    public static volatile Hashtable<String, PeerInfo> peerInfoHashMap = new Hashtable<>();
    static boolean isFirst;
    static Logger logger;
    static int peerIndex;
    static int PORT_LISTENING_AT;
    static Thread threadForListening;
    static ServerSocket socketForListening = null;
    static Vector<Thread> threadReceiving = new Vector<Thread>();
    static BitField bitField;
    public static Vector<Thread> threadSending = new Vector<Thread>();
    public static BitField curBitField = null;

    static void populateCommonConfiguration() {
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

    static void populatePeerInfo(){
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

    public static void createEmptyFile() {
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

    public static void printLog(String str){peerProcess.logger.printLOG(str);
    }


    public static void main(String[] args) {
        peerProcess.peerId = "1001";


        peerProcess.logger = new Logger();
        peerProcess.logger.init("log_" + peerProcess.peerId + ".txt");
        try {
            peerProcess.populateCommonConfiguration();
            peerProcess.populatePeerInfo();
            peerProcess.isFirst=peerProcess.peerInfoHashMap.get(peerProcess.peerId+"").isFirst();

            peerProcess.commonCfg.printAll();
            peerProcess.peerInfoHashMap.get(peerProcess.peerId).printAll();


            peerProcess.bitField = new
                    BitField(peerProcess.peerId, peerProcess.isFirst, peerProcess.commonCfg);


            if(peerProcess.isFirst)
            {
                try
                {
                    //Start listening on the port.
                    peerProcess.socketForListening = new ServerSocket(peerProcess.PORT_LISTENING_AT);

                    peerProcess.threadForListening = new Thread(new ThreadForListening(peerProcess.peerId, peerProcess.socketForListening));
                    peerProcess.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcess.printLog("Time Out Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + timeOutEx.toString());
                    peerProcess.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcess.printLog("Input Output Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + peerProcess.PORT_LISTENING_AT + " " + IOEx.toString());
                    peerProcess.logger.close();
                    System.exit(0);
                }
            }
            else{
                peerProcess.createEmptyFile();

                    Iterator hmIterator = peerProcess.peerInfoHashMap.entrySet().iterator();

                while(hmIterator.hasNext())
                {
                    Map.Entry mapElement = (Map.Entry)hmIterator.next();
                    PeerInfo peerInfo=(PeerInfo) mapElement.getValue();
                    if(peerProcess.peerIndex > peerInfo.getIndex())
                    {   //Create a connection with ACTIVE connection type
                        Thread tempThread = new Thread(new PeerInfoHandler(
                                peerInfo.getPeerAddress(), Integer
                                .parseInt(peerInfo.getPeerPort()), 1,
                                peerProcess.peerId));
                        peerProcess.threadReceiving.add(tempThread);
                        tempThread.start();
                    }
                }


                try
                {
                    peerProcess.socketForListening = new ServerSocket(peerProcess.PORT_LISTENING_AT);
                    peerProcess.threadForListening = new Thread(new ThreadForListening(peerProcess.peerId, peerProcess.socketForListening));
                    peerProcess.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcess.printLog("Time Out Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + timeOutEx.toString());

                    peerProcess.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcess.printLog("Input Output Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + peerProcess.threadForListening + " " + IOEx.toString());

                    peerProcess.logger.close();
                    System.exit(0);
                }
            }

            peerProcess.logger.close();

        } catch (Exception e) {
            peerProcess.logger.printLOG(e.toString());
        }
    }
}