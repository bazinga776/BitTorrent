public class Constants 
{
    public static final int PIECE_INDEX_LENGTH=4;
    public static final int SIZE_OF_HANDSHAKE_HEADER = 18;
    public static final int SIZE_OF_HANDSHAKE_PEERID = 4;
    public static final int SIZE_OF_ZEROBITS_HANDSHAKE = 10;
    public static final int SIZE_OF_MESSAGE_HANDSHAKE = 32;
    public static final String NAME_OF_MESSAGE_CHAR_SET= "UTF8";

    // For Handshake message header
    public static final int SIZE_OF_DATA_MSG = 4;
    public static final int TYPE_OF_DATA_MSG = 1;
    public static final String CHOKE_DATA_MESSAGE = "0";
    public static final String UNCHOKE_DATA_MESSAGE = "1";
    public static final String INTERESTED_DATA_MESSAGE = "2";
    public static final String NOTINTERESTED_DATA_MESSAGE = "3";
    public static final String DATA_MSG_HAVE = "4";
    public static final String BITFIELD_DATA_MESSAGE = "5";
    public static final String REQUEST_DATA_MESSAGE = "6";
    public static final String PIECE_DATA_MESSAGE = "7";
    public static final String HANDSHAKE_HEADER_NAME="P2PFILESHARINGPROJ";
}
