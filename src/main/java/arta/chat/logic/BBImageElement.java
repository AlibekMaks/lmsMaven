package arta.chat.logic;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 21.03.2008
 * Time: 13:46:27
 * To change this template use File | Settings | File Templates.
 */
public class BBImageElement extends BBElement{

    byte [] image;

    public BBImageElement(byte[] image, int senderPeronID, int senderRoleID, long time) {
        this.image = image;
        this.senderPeronID = senderPeronID;
        this.senderRoleID = senderRoleID;
        this.time = time;
    }


    public boolean isBinaryData() {
        return true;
    }
}
