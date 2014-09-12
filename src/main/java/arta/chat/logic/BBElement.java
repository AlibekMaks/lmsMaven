package arta.chat.logic;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 21.03.2008
 * Time: 13:44:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class BBElement {

    public long  time;
    public int senderRoleID, senderPeronID;

    public static final int ENABLE_TYPE = 2;
    public static final int ELEMENT_TYPE = 1;

    public int type = ELEMENT_TYPE;


    public static final String IMAGE_DATA_HEADER = "Image";
    public static final String ARRAY_LIST_HEADER = "Elements List       ";
    public static final String EMPTY_HEADER = "no data             ";

    public static final int HEADER_LENGHT = 20;

    public boolean isDeletePossible(long time){

        if (Math.abs(time - this.time) < ChatRoom.MESSAGE_LIFE_TIME)
            return false;
        return true;

    }

    public abstract boolean isBinaryData();


    public static String getImageDataHeader(int size){

        String header = new String(IMAGE_DATA_HEADER);

        header += " ";

        header += size;

        while (header.length() < HEADER_LENGHT){
            header += " ";
        }

        return header;
    }

}
