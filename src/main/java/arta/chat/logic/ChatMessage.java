package arta.chat.logic;

import arta.common.logic.util.Constants;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.ArrayList;


public class ChatMessage {

    public static final int COMMON_MESSAGE = 0;
    public static final int PRIVATE_MESSAGE = 1;

    public String message;
    public String senderName;
    public int senderRoleID;
    public int senderPersonID;
    public String messageTime;
    public long time;

    public int messageType = COMMON_MESSAGE;
    public ArrayList <SimpleChatPerson> recipients = new ArrayList <SimpleChatPerson> ();

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderName, int senderRoleID, long time){
        GregorianCalendar calendar = new GregorianCalendar();
        this.message = message;
        this.senderName = senderName;
        this.senderRoleID = senderRoleID;
        this.time = time;
        messageTime = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);
    }

    /**
     * return true if message can be deleted
     * @return
     */
    public boolean isDeletePossible(long time){
        if (Math.abs(time - this.time) < ChatRoom.MESSAGE_LIFE_TIME)
            return false;
        return true;
    }

    public String toString() {
        StringBuffer result = new StringBuffer("<br>");
        StringBuffer bg = new StringBuffer("");
        if (senderRoleID == Constants.TUTOR){
            bg.append(" class=\"tutorMessage\" ");
        } else {
            bg.append(" class=\"studentMessage\" ");
        }
        result.append("<font "+bg+" >"+messageTime+"</font>&nbsp;");
        if (senderRoleID == Constants.TUTOR){
            result.append("<font "+bg+" ><b>"+senderName+":</b></font>&nbsp;");
            result.append("<font "+bg+" >"+message+"</font>&nbsp;");
        } else {
            result.append("<font "+bg+" ><b>"+senderName+":</b></font>&nbsp;");
            result.append("<font "+bg+" >"+message+"</font>&nbsp;");
        }

        return result.toString();
    }
}
