import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.*;

public class Handshake
{
    Constants Constants = new Constants();
	private byte[] headerMsg = new byte[Constants.SIZE_OF_HANDSHAKE_HEADER];
	private byte[] peer_Id = new byte[Constants.SIZE_OF_HANDSHAKE_PEERID];
    private byte[] zero_Bits = new byte[Constants.SIZE_OF_ZEROBITS_HANDSHAKE];
	private String handshake_Msg_Header; 
    private String handshake_Msg_PeerID;

    public Handshake()
    {
        
    }
    
    public Handshake(String h, String p_ID) {

		try {
			this.handshake_Msg_Header = h;
			this.headerMsg = h.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
			if (this.headerMsg.length > Constants.SIZE_OF_HANDSHAKE_HEADER)
				throw new Exception("Handshake :: Header Very large");

			this.handshake_Msg_PeerID = p_ID;
			this.peer_Id = p_ID.getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
			if (this.peer_Id.length > Constants.SIZE_OF_HANDSHAKE_HEADER)
				throw new Exception("Handshake :: PeerId Very large");

			this.zero_Bits = "0000000000".getBytes(Constants.NAME_OF_MESSAGE_CHAR_SET);
		} catch (Exception e) {
			peerProcess.printLog("Handshake :: in constructor" +e.toString()+"constructor");
		}

	}

    public void setHeader(byte[] HS_header) 
    {
        try 
        {
			this.handshake_Msg_Header = (new String(HS_header, Constants.NAME_OF_MESSAGE_CHAR_SET)).toString().trim();
			this.headerMsg = this.handshake_Msg_Header.getBytes();
        } 
        catch (UnsupportedEncodingException e) 
        {
			peerProcess.printLog("Handshake :: " + e.toString()+"in setHeader function");
		}
	}

    public void setPeerId(byte[] HS_Peer_ID) 
    {
        try 
        {
			this.handshake_Msg_PeerID = (new String(HS_Peer_ID, Constants.NAME_OF_MESSAGE_CHAR_SET)).toString().trim();
			this.peer_Id = this.handshake_Msg_PeerID.getBytes();

        } 
        catch (UnsupportedEncodingException e) 
        {
			peerProcess.printLog("Handshake :: " +e.toString()+"in setPeerId function");
		}
	}
	
    public byte[] getHeaderMsg()
    {
		return headerMsg;
	}
	
    public byte[] getPeer_Id()
    {
		return peer_Id;
	}

    public byte[] getZero_Bits()
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
            Constants Constants = new Constants();
			if (msg_Received.length != Constants.SIZE_OF_MESSAGE_HANDSHAKE)
				throw new Exception("Handshake :: LEngth of Byte array is not matching.");

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
			peerProcess.printLog("Handshake :: " +e.toString()+"in decodeMessage function");
			handshake = null;
		}
		return handshake;
	}

    public static byte[] encode(Handshake handshake) 
    {
        Constants Constants = new Constants();
        byte[] msg_Send = new byte[Constants.SIZE_OF_MESSAGE_HANDSHAKE];

        String headerMsg="";
        String zeros="";
        String peer_Id="";

        System.out.println(msg_Send.length+" before inserting.");

        try 
        {
            if (handshake.getHeaderMsg() == null)
            {
				throw new Exception("Handshake :: Invalid Header.");
			}
			if (handshake.getHeaderMsg().length > Constants.SIZE_OF_HANDSHAKE_HEADER || handshake.getHeaderMsg().length == 0)
			{
				throw new Exception("Handshake :: Invalid Header.");
            } 
            else
            {   
                headerMsg=new String(handshake.getHeaderMsg());
            }

            System.out.println(msg_Send.length+" bkp1");

            if (handshake.getZero_Bits() == null)
            {
				throw new Exception("Handshake :: Invalid zero bits field.");
			} 
            if (handshake.getZero_Bits().length > Constants.SIZE_OF_ZEROBITS_HANDSHAKE || handshake.getZero_Bits().length == 0)
            {
				throw new Exception("Handshake :: Invalid zero bits field.");
            } 
            else 
            {
                zeros=new String(handshake.getZero_Bits());
            }
            

            System.out.println(msg_Send.length+" bkp2");
			if (handshake.getPeer_Id() == null)
			{
				throw new Exception("Handshake :: Invalid peer id.");
			} 
			else if (handshake.getPeer_Id().length > Constants.SIZE_OF_HANDSHAKE_PEERID || handshake.getPeer_Id().length == 0)
			{
				throw new Exception("Handshake :: Invalid peer id.");
			} 
			else 
			{   
                peer_Id=new String(handshake.getPeer_Id());
            }
            
            System.out.println(msg_Send.length+" bkp3");

		}
		catch (Exception e) 
		{
			peerProcess.printLog("Handshake :: "+e.toString()+"in encodeMessage()");
			msg_Send = null;
		}
        msg_Send=(headerMsg+zeros+peer_Id).getBytes();
		return msg_Send;
    }
}