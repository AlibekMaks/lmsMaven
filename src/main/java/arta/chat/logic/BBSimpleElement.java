package arta.chat.logic;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 21.03.2008
 * Time: 13:45:56
 * To change this template use File | Settings | File Templates.
 */
public class BBSimpleElement extends BBElement{

    public String element;


    public BBSimpleElement(String element, int senderPeronID, int senderRoleID, long time) {
        this.element = element;
        this.senderPeronID = senderPeronID;
        this.senderRoleID = senderRoleID;
        this.time = time;
    }


    public boolean isBinaryData() {
        return false;
    }
}
