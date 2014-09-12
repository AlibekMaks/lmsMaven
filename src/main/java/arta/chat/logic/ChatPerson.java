package arta.chat.logic;

import arta.common.logic.util.Constants;

import java.io.PrintWriter;


public class ChatPerson {

    public int personID;
    public int roleID;
    public String login;
    public long lastRequestTime;

    public long lastGetMessages;

    public long lastGetElements;

    public long lastGetPrivateMessages;

    public boolean canDraw = true;

    public ChatPerson(int personID, int roleID, String login, long lastRequestTime) {
        this.personID = personID;
        this.roleID = roleID;
        this.login = login;
        this.lastRequestTime = lastRequestTime;
        if (roleID == Constants.STUDENT){
            canDraw = false;
        }
    }

    public ChatPerson(ChatPerson person) {
        this.personID = person.personID;
        this.roleID = person.roleID;
        this.login = person.login;
        this.lastRequestTime = person.lastRequestTime;
        this.canDraw = person.canDraw;
    }

    public void setRequestTime(long time){
        lastRequestTime = time;
    }

    public boolean isUserPresent(long currentTime){
        if (Math.abs(currentTime-lastRequestTime) < ChatRoom.USER_TIME)
            return true;
        return false;
    }

    public void setLastGetMessagesTime(long time){
        lastGetMessages = time;
    }

    public void setLastGetPrivateMessagesTime(long time){
        lastGetPrivateMessages = time;
    }

    public void writeUser(PrintWriter pw, int roleID){
        pw.print("<tr><td width=16px>");
        pw.print("<input type=\"checkbox\" ");
        if (roleID == Constants.STUDENT || this.roleID != Constants.STUDENT)
            pw.print(" disabled ");
        if (canDraw)
            pw.print(" checked ");
        if (roleID != Constants.STUDENT && this.roleID == Constants.STUDENT){
            pw.print(" onclick=\"changeStudentStatus("+personID+", this.checked)\" ");
        }
        pw.print(">");
        pw.print("</td>");
        pw.print("<td width=\"100%\" >");
        pw.print("<a href=\"\" class=\"");
        if (roleID == Constants.TUTOR){
            pw.print("tutorInChat");
        } else {
            pw.print("studentInChat");
        }
        pw.print("\" onClick=\"clickPerson(this.innerHTML, "+personID+", "+roleID+"); return false;\">");
        pw.print(login);
        pw.print("</a>");
        pw.print("</td></tr>");
    }
}
