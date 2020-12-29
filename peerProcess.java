import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
    static PeerInfo peerInfo;
    public static volatile Hashtable<String, PeerInfo> peerInfoHashMap = new Hashtable<>();
    public static volatile Hashtable<String, PeerInfo> preferred_Neighbors = new Hashtable<String, PeerInfo>();
    public static volatile Hashtable<String, PeerInfo> unchokedPeers = new Hashtable<String, PeerInfo>();

    static boolean isFirst;
    static Logger logger;
    public int peerIndex;
    public int PORT_LISTENING_AT;
    public Thread threadForListening;
    public ServerSocket socketForListening = null;
    static Vector<Thread> threadReceiving = new Vector<Thread>();
    public static Vector<Thread> threadSending = new Vector<Thread>();
    public static BitFieldCls curBitField = null;
    public static Hashtable<String, Socket> peerIdToSocket = new Hashtable<String, Socket>();
    public static Thread messageHandler;
    public static volatile Queue<DataMsgWrapper> messageQueue = new LinkedList<DataMsgWrapper>();
    public static volatile Timer timer_pref;
    public static volatile Timer timer_unchok;
    public static boolean hasFinished = false;

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
                String data = myReader.nextLine();
                String[] splited = data.split(" ");
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
            CommonCfg.setAll( fileName,  numberOfPreferredNeighbors,  unchokingInterval,
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
                String data = myReader.nextLine();
                String[] splited = data.split(" ");
                boolean hasFile=Integer.parseInt(splited[3])==1;
                boolean isFirst=false;

                if(i==0)
                    isFirst=true;
                peerInfo=new PeerInfo(splited[0], splited[1], splited[2],hasFile,i,isFirst);
                System.out.println(peerInfo.getPeerId()+" has index "+ peerInfo.getIndex());
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
            File newFile = new File(directory, CommonCfg.fileName);
            FileWriter os = new FileWriter(newFile, true);

            byte b = 0;

            for (int i = 0; i < CommonCfg.fileSize; i++)
                os.write(b);
            os.close();
        }
        catch (IOException e) {
            printLog("peerProcess :: Could not create file for "+peerId+e.toString());
        }

    }

    public static void printLog(String str){peerProcess.logger.printLOG(str);
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        peerProcess peerProcessObj = new peerProcess();
        peerId = args[0];


        peerProcess.logger = new Logger();
        peerProcess.logger.init("log_" + peerProcess.peerId + ".log");
        try {
            populateCommonConfiguration();

            populatePeerInfo();

            initializePrefferedNeighbours();

            boolean isFirst;

            isFirst=peerProcess.peerInfoHashMap.get(peerProcess.peerId+"").isFirst();

            peerProcess.peerInfoHashMap.get(peerProcess.peerId).printAll();


            curBitField = new
                    BitFieldCls(peerProcess.peerId, isFirst);

            messageHandler = new Thread(new MessageHandler(peerId));
            messageHandler.start();

            peerProcessObj.PORT_LISTENING_AT=Integer.parseInt(peerInfoHashMap.get(peerProcess.peerId).getPeerPort());
            peerProcessObj.peerIndex=peerInfoHashMap.get(peerProcess.peerId).getIndex();


            if(isFirst)
            {
                try
                {
                    //Listening on port - started
                    peerProcessObj.socketForListening = new ServerSocket(peerProcessObj.PORT_LISTENING_AT);

                    peerProcessObj.threadForListening = new Thread(new ThreadForListening(peerId, peerProcessObj.socketForListening));
                    peerProcessObj.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcess.printLog("Time Out Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + timeOutEx.toString());
                    peerProcess.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcess.printLog("Input Output Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + peerProcessObj.PORT_LISTENING_AT + " " + IOEx.toString());
                    peerProcess.logger.close();
                    System.exit(0);
                }
            }
            else{
                createEmptyFile();

                Iterator hmIterator = peerInfoHashMap.entrySet().iterator();

                System.out.println("peerInfohashmap is not empty: "+hmIterator.hasNext());

                while(hmIterator.hasNext())
                {
                    Map.Entry mapElement = (Map.Entry)hmIterator.next();
                    PeerInfo peerInfo=(PeerInfo) mapElement.getValue();

                    System.out.println("truely "+peerInfo.getPeerId()+" has "+peerInfo.getIndex());
                    System.out.println("I am "+peerProcessObj.peerIndex);

                    if(peerProcessObj.peerIndex > peerInfo.getIndex())
                    {
                        //Conn type ACTIVE - create
                        System.out.println(peerProcessObj.peerIndex+" is Creating ACTIVE CONNECTION "+peerInfo.getPeerId());
                        Thread tempThread = new Thread(new PeerInfoHandler(
                                peerInfo.getPeerAddress(), Integer
                                .parseInt(peerInfo.getPeerPort()), 1,
                                peerProcess.peerId));
                        threadReceiving.add(tempThread);
                        tempThread.start();
                    }
                }


                try
                {
                    peerProcessObj.socketForListening = new ServerSocket(peerProcessObj.PORT_LISTENING_AT);
                    peerProcessObj.threadForListening = new Thread(new ThreadForListening(peerProcess.peerId, peerProcessObj.socketForListening));
                    peerProcessObj.threadForListening.start();
                }
                catch(SocketTimeoutException timeOutEx)
                {
                    peerProcess.printLog("Time Out Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + timeOutEx.toString());

                    peerProcess.logger.close();
                    System.exit(0);
                }
                catch(IOException IOEx)
                {
                    peerProcess.printLog("Input Output Exception for : "+peerProcess.peerId + "when starting the thread that is listening" + peerProcessObj.threadForListening + " " + IOEx.toString());

                    peerProcess.logger.close();
                    System.exit(0);
                }
            }

            startPreferredNeighbors();
            startUnChokedNeighbors();

            while(true)
            {
                // termination - check
                hasFinished = hasFinished();
                if (hasFinished) {
                    printLog("All peers have completed downloading the file.");

                    stopPreferredNeighbors();
                    stopUnChokedNeighbors();

                    try {
                        Thread.currentThread();
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }

                    if (peerProcessObj.threadForListening.isAlive())
                        peerProcessObj.threadForListening.stop();

                    if (messageHandler.isAlive())
                        messageHandler.stop();

                    for (int i = 0; i < threadReceiving.size(); i++)
                        if (threadReceiving.get(i).isAlive())
                            threadReceiving.get(i).stop();

                    for (int i = 0; i < threadSending.size(); i++)
                        if (threadSending.get(i).isAlive())
                            threadSending.get(i).stop();

                    break;
                } else {
                    try {
                        Thread.currentThread();
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                }
            }


        } catch (Exception e) {
            peerProcess.logger.printLOG(e.toString());
        } finally {
            peerProcess.logger.close();
            System.exit(0);
        }
    }

    public static synchronized DataMsgWrapper popMessageFromQueue()
    {
        DataMsgWrapper message = null;
        if(!messageQueue.isEmpty())
        {
            message = messageQueue.remove();
        }
        return message;
    }

    public static synchronized void pushToMsgQueue(DataMsgWrapper message)
    {
        messageQueue.add(message);
    }


    private static void send_UnChoke(Socket socket, String remotePeerID) {
        printLog(peerId + " is sending UNCHOKE message to remote Peer " + remotePeerID);
        DataMsg d = new DataMsg(Constants.UNCHOKE_DATA_MESSAGE);
        byte[] msgByte = DataMsg.encodeMessage(d);
        sendData(socket, msgByte);
    }

    private static void send_Have(Socket socket, String remotePeerID) {
        byte[] encodedBitField = peerProcess.curBitField.encodeInput();
        printLog(peerId + " sending HAVE message to Peer " + remotePeerID);
        DataMsg d = new DataMsg(Constants.HAS_DATA_MSG, encodedBitField);
        sendData(socket,DataMsg.encodeMessage(d));
        encodedBitField = null;
    }

    private static int sendData(Socket socket, byte[] encodedBitField) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(encodedBitField);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static void getPeerInfo()
    {
        try
        {
            String stringValue;
            BufferedReader input = new BufferedReader(new FileReader("PeerInfo.cfg"));
            while ((stringValue = input.readLine()) != null)
            {
                String[]args = stringValue.trim().split("\\s+");
                String peerID = args[0];
                int completedTrue = Integer.parseInt(args[3]);
                if(completedTrue == 1)
                {
                    peerInfoHashMap.get(peerID).completedTrue = 1;
                    peerInfoHashMap.get(peerID).interestedTrue = 0;
                    peerInfoHashMap.get(peerID).chokedTrue = 0;
                }
            }
            input.close();
        }
        catch (Exception e) {
            printLog(peerId + e.toString());
        }
    }

    public static void startUnChokedNeighbors()
    {
        timer_pref = new Timer();
        timer_pref.schedule(new UnChokedPeers(),
                0,
                CommonCfg.optimisticUnchokingInterval * 1000);
    }

    public static void stopUnChokedNeighbors() {
        timer_pref.cancel();
    }

    public static void startPreferredNeighbors() {
        timer_pref = new Timer();
        timer_pref.schedule(new PreferredNeighbors(),
                0,
                CommonCfg.unchokingInterval * 1000);
    }

    public static void stopPreferredNeighbors() {
        timer_pref.cancel();
    }


    public static class PreferredNeighbors extends TimerTask {
        public void run()
        {
            //peerInfoMap - Updated
            getPeerInfo();
            Enumeration<String> keyValues = peerInfoHashMap.keys();
            int interestedCount = 0;
            String strPref = "";
            while(keyValues.hasMoreElements())
            {
                String key = (String)keyValues.nextElement();
                PeerInfo preferred = peerInfoHashMap.get(key);
                if(key.equals(peerId))continue;
                if (preferred.completedTrue == 0 && preferred.handshakedTrue == 1)
                {
                    interestedCount++;
                }
                else if(preferred.completedTrue == 1)
                {
                    try
                    {
                        preferred_Neighbors.remove(key);
                    }
                    catch (Exception e) {
                    }
                }
            }
            if(interestedCount > CommonCfg.numberOfPreferredNeighbors)
            {
                boolean flag = preferred_Neighbors.isEmpty();
                if(!flag)
                    preferred_Neighbors.clear();
                List<PeerInfo> pv = new ArrayList<PeerInfo>(peerInfoHashMap.values());
                Collections.sort(pv, new DataRateComparator(false));
                int count = 0;
                for (int i = 0; i < pv.size(); i++)
                {
                    if (count > CommonCfg.numberOfPreferredNeighbors - 1)
                        break;
                    if(pv.get(i).handshakedTrue == 1 && !pv.get(i).peerId.equals(peerId)
                            && peerInfoHashMap.get(pv.get(i).peerId).completedTrue == 0)
                    {
                        peerInfoHashMap.get(pv.get(i).peerId).isPreferredNeighbor = 1;
                        preferred_Neighbors.put(pv.get(i).peerId, peerInfoHashMap.get(pv.get(i).peerId));

                        count++;

                        strPref = strPref + pv.get(i).peerId + ", ";

                        if (peerInfoHashMap.get(pv.get(i).peerId).chokedTrue == 1)
                        {
                            send_UnChoke(peerProcess.peerIdToSocket.get(pv.get(i).peerId), pv.get(i).peerId);
                            peerProcess.peerInfoHashMap.get(pv.get(i).peerId).chokedTrue = 0;
                            send_Have(peerProcess.peerIdToSocket.get(pv.get(i).peerId), pv.get(i).peerId);
                            peerProcess.peerInfoHashMap.get(pv.get(i).peerId).state = 3;
                        }


                    }
                }
            }
            else
            {
                keyValues = peerInfoHashMap.keys();
                while(keyValues.hasMoreElements())
                {
                    String key = (String)keyValues.nextElement();
                    PeerInfo preferred = peerInfoHashMap.get(key);
                    if(key.equals(peerId)) continue;

                    if (preferred.completedTrue == 0 && preferred.handshakedTrue == 1)
                    {
                        if(!preferred_Neighbors.containsKey(key))
                        {
                            strPref = strPref + key + ", ";
                            preferred_Neighbors.put(key, peerInfoHashMap.get(key));
                            peerInfoHashMap.get(key).isPreferredNeighbor = 1;
                        }
                        if (preferred.chokedTrue == 1)
                        {
                            send_UnChoke(peerProcess.peerIdToSocket.get(key), key);
                            peerProcess.peerInfoHashMap.get(key).chokedTrue = 0;
                            send_Have(peerProcess.peerIdToSocket.get(key), key);
                            peerProcess.peerInfoHashMap.get(key).state = 3;
                        }

                    }

                }
            }

            if (strPref != "")
                peerProcess.printLog("Peer "+peerProcess.peerId + " has the preferred neighbors " + strPref);
        }
    }

    public static class UnChokedPeers extends TimerTask {

        public void run()
        {
            //peerinfomap - updated
            getPeerInfo();
            if(!unchokedPeers.isEmpty())
                unchokedPeers.clear();
            Enumeration<String> keyValues = peerInfoHashMap.keys();
            Vector<PeerInfo> peers = new Vector<PeerInfo>();
            while(keyValues.hasMoreElements())
            {
                String key = (String)keyValues.nextElement();
                PeerInfo preferred = peerInfoHashMap.get(key);
                if (preferred.chokedTrue == 1
                        && !key.equals(peerId)
                        && preferred.completedTrue == 0
                        && preferred.handshakedTrue == 1)
                    peers.add(preferred);
            }

            // Elements of vector - randomized
            if (peers.size() > 0)
            {
                Collections.shuffle(peers);
                PeerInfo p = peers.firstElement();

                peerInfoHashMap.get(p.peerId).isOptUnchokedNeighbor = 1;
                unchokedPeers.put(p.peerId, peerInfoHashMap.get(p.peerId));

                peerProcess.printLog("Peer "+peerProcess.peerId + " has the optimistically unchoked neighbor Peer " + p.peerId);

                if (peerInfoHashMap.get(p.peerId).chokedTrue == 1)
                {
                    peerProcess.peerInfoHashMap.get(p.peerId).chokedTrue = 0;
                    send_UnChoke(peerProcess.peerIdToSocket.get(p.peerId), p.peerId);
                    send_Have(peerProcess.peerIdToSocket.get(p.peerId), p.peerId);
                    peerProcess.peerInfoHashMap.get(p.peerId).state = 3;
                }
            }

        }

    }

    public static synchronized boolean hasFinished() {

        String data;
        int countForFiles = 1;

        try {
            BufferedReader input = new BufferedReader(new FileReader(
                    "PeerInfo.cfg"));

            while ((data = input.readLine()) != null) {
                countForFiles = countForFiles
                        * Integer.parseInt(data.trim().split("\\s+")[3]);
            }
            if (countForFiles == 0) {
                input.close();
                return false;
            } else {
                input.close();
                return true;
            }

        } catch (Exception e) {
            printLog(e.toString());
            return false;
        }

    }

    private static void initializePrefferedNeighbours()
    {
        Enumeration<String> keyValues = peerInfoHashMap.keys();
        while(keyValues.hasMoreElements())
        {
            String key = (String)keyValues.nextElement();
            if(!key.equals(peerId))
            {
                preferred_Neighbors.put(key, peerInfoHashMap.get(key));
            }
        }
    }

}