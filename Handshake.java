import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.*;


public class Handshake
{
	// Attributes
	private byte[] header = new byte[Constants.SIZE_OF_HANDSHAKE_HEADER];
	private byte[] peer_ID = new byte[Constants.SIZE_OF_HANDSHAKE_PEERID];
    private byte[] zero_Bits = new byte[Constants.SIZE_OF_ZEROBITS_HANDSHAKE];
	private String handshake_Msg_Header; 
    private String handshake_Msg_PeerID;
    static Logger logger = new Logger();

    public Handshake()
    {
        logger.init("hello.txt");
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
			logger.printLOG(e.toString());
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
			logger.printLOG(e.toString());
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
    
    public static Handshake decodeMessage(byte[] msg_Received) 
    {

		Handshake handshake = null;
		byte[] header_Message = null;
        byte[] peerID_Message = null;
       
    

        try 
        {
			if (msg_Received.length != Constants.SIZE_OF_MESSAGE_HANDSHAKE)
				throw new Exception("Byte array length not matching.");

			handshake = new Handshake();
			header_Message = new byte[Constants.SIZE_OF_HANDSHAKE_HEADER];
            peerID_Message = new byte[Constants.SIZE_OF_HANDSHAKE_PEERID];
            
          System.out.println("Initialized");
            
            List<Byte> l1 = new ArrayList<>();
            int i=0;
            for(i=0;i<Constants.SIZE_OF_HANDSHAKE_HEADER;i++)
                l1.add(msg_Received[i]);
            for(int j=i;j<header_Message.length;j++)
                l1.add(header_Message[j]);

            System.out.println("Done Copying");    
            
            header_Message = new byte[l1.size()];
            for(i=0;i<l1.size();i++)
                header_Message[i] = l1.get(i);

            System.out.println("Converting");

            int sourcePosition = Constants.SIZE_OF_HANDSHAKE_HEADER + Constants.SIZE_OF_ZEROBITS_HANDSHAKE;
            
            l1.clear();
            for(i=sourcePosition;i<Constants.SIZE_OF_HANDSHAKE_PEERID;i++)
                l1.add(peerID_Message[i]);
            for(int j=i;j<peerID_Message.length;j++)
                l1.add(peerID_Message[j]);
            peerID_Message = new byte[l1.size()];
            for(i=0;i<l1.size();i++)
                peerID_Message[i] = l1.get(i);
    

            System.out.println(header_Message.toString());
            System.out.println(peerID_Message.toString());

			handshake.setHeader(header_Message);
			handshake.setPeerID(peerID_Message);

        } 
        catch (Exception e) 
        {
			logger.printLOG(e.toString());
			handshake = null;
		}
		return handshake;
	}

    public static byte[] encodeMessage(Handshake handshake) 
    {

        byte[] msg_Send = new byte[Constants.SIZE_OF_MESSAGE_HANDSHAKE];

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
                List<Byte> l2 = new ArrayList<>();
                int i=0;
                for(i=0;i<handshake.getHeader().length;i++)
                    l2.add(handshake.getHeader()[i]);
                for(int j=i;j<msg_Send.length;j++)
                    l2.add(msg_Send[j]);
                
                    msg_Send = new byte[l2.size()];
                for(i=0;i<l2.size();i++)
                    msg_Send[i] = l2.get(i);
			}

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
                List<Byte> l3 = new ArrayList<>();
                int i=0;
                for(i=0;i<Constants.SIZE_OF_HANDSHAKE_HEADER;i++)
                    l3.add(msg_Send[i]);

                for(int j=0;j<Constants.SIZE_OF_ZEROBITS_HANDSHAKE - 1;j++)
                    l3.add(handshake.getZeroBits()[j]);

                for(;i<msg_Send.length;i++)
                    l3.add(msg_Send[i]);
                
                msg_Send = new byte[l3.size()];
                for(i=0;i<l3.size();i++)
                    msg_Send[i] = l3.get(i);

			}
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
                List<Byte> l4 = new ArrayList<>();
                int i=0;
                for(i=0;i<Constants.SIZE_OF_HANDSHAKE_HEADER + Constants.SIZE_OF_ZEROBITS_HANDSHAKE;i++)
                    l4.add(msg_Send[i]);

                for(int j=0;j<handshake.getPeerID().length;j++)
                    l4.add(handshake.getPeerID()[j]);

                for(;i<msg_Send.length;i++)
                    l4.add(msg_Send[i]);
                
                msg_Send = new byte[l4.size()];
                for(i=0;i<l4.size();i++)
                    msg_Send[i] = l4.get(i);
			}

		}
		catch (Exception e) 
		{
			logger.printLOG(e.toString());
			msg_Send = null;
		}

		return msg_Send;
    }
    public static void main(String[] args)
    {
        Handshake hs = new Handshake();
        byte[] header="header".getBytes();
        hs.setHeader(header);
        hs.setPeerID("1001".getBytes());
        
        byte[] handshake=Handshake.encodeMessage(hs);
        Handshake handshake2=Handshake.decodeMessage(handshake);

        System.out.println(handshake2.getPeerID().toString());
        System.out.println(handshake2.getHeader().toString());

    }
}