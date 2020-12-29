import java.util.HashMap;
import java.util.Map;

public class Constants
{
    public static final int PIECE_INDEX_LENGTH=4;

    public static final int SIZE_OF_HANDSHAKE_HEADER = 18;

    public static final int SIZE_OF_HANDSHAKE_PEERID = 4;

    public static final int SIZE_OF_ZEROBITS_HANDSHAKE = 10;

    public static final int SIZE_OF_MESSAGE_HANDSHAKE = 32;

    public static final String NAME_OF_MESSAGE_CHAR_SET= "UTF8";

    // Handshake - header
    public static final int SIZE_OF_DATA_MSG = 4;

    public static final int TYPE_OF_DATA_MSG = 1;

    public static final String CHOKE_DATA_MESSAGE = "0";

    public static final String UNCHOKE_DATA_MESSAGE = "1";

    public static final String INTERESTED_DATA_MESSAGE = "2";

    public static final String NOTINTERESTED_DATA_MESSAGE = "3";

    public static final String HAS_DATA_MSG = "4";

    public static final String BITFIELD_DATA_MESSAGE = "5";

    public static final String REQUEST_DATA_MESSAGE = "6";

    public static final String PIECE_DATA_MESSAGE = "7";

    public static final String HANDSHAKE_HEADER_NAME="P2PFILESHARINGPROJ";

    static final Map<Integer, String> ConstantMap;

    static {
        ConstantMap = new HashMap<Integer, String>();

        ConstantMap.put(0,"DATA_MSG_CHOKE");
        ConstantMap.put(1,"DATA_MSG_UNCHOKE");
        ConstantMap.put(2,"DATA_MSG_INTERESTED");
        ConstantMap.put(3,"DATA_MSG_NOTINTERESTED");
        ConstantMap.put(4,"DATA_MSG_HAVE");
        ConstantMap.put(5,"DATA_MSG_BITFIELD");
        ConstantMap.put(6,"DATA_MSG_REQUEST");
        ConstantMap.put(7,"DATA_MSG_PIECE");

    }
}
