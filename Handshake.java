import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.*;

public class Handshake
{
	private byte[] header = new byte[Constants.SIZE_OF_HANDSHAKE_HEADER];
	private byte[] peer_ID = new byte[Constants.SIZE_OF_HANDSHAKE_PEERID];
    private byte[] zero_Bits = new byte[Constants.SIZE_OF_ZEROBITS_HANDSHAKE];
	private String handshake_Msg_Header; 
    private String handshake_Msg_PeerID;
    static Logger logger = new Logger();

    public Handshake()
    {
        
    }
    
    public Handshake(String h, String p_ID) {

		try {
            logger.init("hello.txt");
			this.handshake_Msg_Header = h;
			this.header = h.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
			if (this.header.length > Constants.SIZE_OF_HANDSHAKE_HEADER)
				throw new Exception("Header is too large.");

			this.handshake_Msg_PeerID = p_ID;
			this.peer_ID = p_ID.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
			if (this.peer_ID.length > Constants.SIZE_OF_HANDSHAKE_HEADER)
				throw new Exception("Peer ID is too large.");

			this.zero_Bits = "0000000000".getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
		} catch (Exception e) {
			logger.printLOG(e.toString()+"in constructor");
		}

	}

    public void setHeader(byte[] HS_header) 
    {
        try 
        {
			this.handshake_Msg_Header = (new String(HS_header, Constants.NAME_OF_MESSAGE_CHAR_SET)).toString().trim();
			this.header = this.handshake_Msg_Header.getBytes();
        } 
        catch (UnsupportedEncodingException e) 
        {
			logger.printLOG(e.toString()+"in setHeader function");
		}
	}

    public void setPeerID(byte[] HS_Peer_ID) 
    {
        try 
        {
			this.handshake_Msg_PeerID = (new String(HS_Peer_ID, Constants.NAME_OF_MESSAGE_CHAR_SET)).toString().trim();
			this.peer_ID = this.handshake_Msg_PeerID.getBytes();

        } 
        catch (UnsupportedEncodingException e) 
        {
			logger.printLOG(e.toString()+"in setPeerID function");
		}
	}
	
    public byte[] getHeader() 
    {
		return header;
	}
	
    public byte[] getPeerID() 
    {
		return peer_ID;
	}

    public byte[] getZeroBits() 
    {
		return zero_Bits;
    }
    
    public static Handshake decode(byte[] msg_Received) 
    {

		Handshake handshake = null;
		
       
        String decode_header = "";
        String decode_Zeroes = "";
        String decode_Peer_ID = "";

        

        try 
        {
			if (msg_Received.length != Constants.SIZE_OF_MESSAGE_HANDSHAKE)
				throw new Exception("Byte array length not matching.");

            String decode_mString=new String(msg_Received);
            
            decode_header=decode_mString.substring(0, 18);
            System.out.println("Header"+decode_header);

            decode_Zeroes=decode_mString.substring(18, 28);
            System.out.println("zeros:"+decode_Zeroes);

            decode_Peer_ID=decode_mString.substring(28, 32);
            System.out.println(decode_Peer_ID);

            

            handshake = new Handshake(decode_header,decode_Peer_ID);


        } 
        catch (Exception e) 
        {
			logger.printLOG(e.toString()+"in decodeMessage function");
			handshake = null;
		}
		return handshake;
	}

    public static byte[] encode(Handshake handshake) 
    {

        byte[] msg_Send = new byte[Constants.SIZE_OF_MESSAGE_HANDSHAKE];

        String header="";
        String zeros="";
        String peerId="";

        System.out.println(msg_Send.length+" before inserting.");

        try 
        {
            if (handshake.getHeader() == null) 
            {
				throw new Exception("Invalid Header.");
			}
			if (handshake.getHeader().length > Constants.SIZE_OF_HANDSHAKE_HEADER || handshake.getHeader().length == 0)
			{
				throw new Exception("Invalid Header.");
            } 
            else
            {   
                header=new String(handshake.getHeader());
            }

            System.out.println(msg_Send.length+" bkp1");

            if (handshake.getZeroBits() == null) 
            {
				throw new Exception("Invalid zero bits field.");
			} 
            if (handshake.getZeroBits().length > Constants.SIZE_OF_ZEROBITS_HANDSHAKE || handshake.getZeroBits().length == 0)
            {
				throw new Exception("Invalid zero bits field.");
            } 
            else 
            {
                zeros=new String(handshake.getZeroBits());
            }
            

            System.out.println(msg_Send.length+" bkp2");
			if (handshake.getPeerID() == null) 
			{
				throw new Exception("Invalid peer id.");
			} 
			else if (handshake.getPeerID().length > Constants.SIZE_OF_HANDSHAKE_PEERID || handshake.getPeerID().length == 0) 
			{
				throw new Exception("Invalid peer id.");
			} 
			else 
			{   
                peerId=new String(handshake.getPeerID());
            }
            
            System.out.println(msg_Send.length+" bkp3");

		}
		catch (Exception e) 
		{
			logger.printLOG(e.toString()+"in encodeMessage()");
			msg_Send = null;
		}
        msg_Send=(header+zeros+peerId).getBytes();
		return msg_Send;
    }
    public static void main(String[] args)
    {
        Handshake hs = new Handshake("012345678911111111","1001");

        System.out.println("h".length());
        
        byte[] handshake=Handshake.encode(hs);
        System.out.println(new String(handshake));
        System.out.println(handshake.length);
        
        Handshake handshake2=Handshake.decode(handshake);
        
        System.out.println(new String(handshake2.getPeerID()));
        System.out.println(new String(handshake2.getHeader()));

    }
}