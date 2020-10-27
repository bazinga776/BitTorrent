import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    BufferedWriter writer;

    public void init(String fileName) {
        try{
        writer = new BufferedWriter(new FileWriter(fileName));}catch (IOException e){
            System.out.println("There was an exception creating the log file--"+e.toString());
        }
    }
//
//    public static void main(String args[]) {
//
//        Logger logger=new Logger();
//        try {
//            logger.init("hello.txt");
//            logger.LOG("hello world");
//            logger.LOG("there there");
//            logger.close();
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//    }

    public void LOG(String str) {
        try {
            writer.write(str+"\n");
        } catch (IOException ioException) {
            System.out.println(ioException.toString());
        }
    }

    public void printLOG(String str){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        LOG(timeStamp+": "+str);
        System.out.println(timeStamp+": "+str);

    }

    public void close() throws IOException {
        writer.close();
    }

}
