package arta.forum.logic;

import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.forum.ForumMessages;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class ForumMessage {

    int msgID;
    int authorID;
    int roleID;
    int themeID;
    String msgBody;
    String title;
    String authorname;
    String job;
    String date;


    public ForumMessage(int msgID) {
        this.msgID = msgID;
    }

    public ForumMessage(int msgID, String authorname, int roleID, String job, String msgBody, String title, String date) {
        this.msgID = msgID;
        this.authorname = authorname;
        this.roleID = roleID;
        this.job = job;
        this.msgBody = msgBody;
        this.title = title;
        this.date = date;
    }

    public ForumMessage (int authorID, int roleID, int themeID, String msgBody, String title) {
        this.authorID = authorID;
        this.roleID = roleID;
        this.themeID = themeID;
        this.msgBody = msgBody;
        this.title = title;
    }

    public boolean create (int lang, Message message, String option) {

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        StringTransform trsf = new StringTransform();

        try {

            title = title.replaceAll("'", "''");
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.prepareStatement("INSERT INTO forummessages (authorid, roleid, createddate, themeid, messagebody," +
                    " title) VALUES (" + authorID + ", " + roleID + ", current_timestamp, " + themeID +
                    ", ?, '" + trsf.getDBString(title) + "')");

            st.setString(1, msgBody);
            st.execute();
            rs = st.getGeneratedKeys();

            if (rs.next()) {
                msgID = rs.getInt(1);
            }

            if (option.equals("0")) {
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.MESSAGE_SUCCESFULLY_SAVED));
            } else if (option.equals("1")) {
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.THEME_SUCCESFULLY_CREATED));
            }
            return true;

        } catch(Exception exc) {
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally{
            try {
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (rs!=null) rs.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete (int lang, Message message) {

        Connection con = null;
        Statement state = null;

        Statement st = null;
        ResultSet rs = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            state = con.createStatement();
            st = con.createStatement();

            rs = st.executeQuery("select themeid from forummessages where messageID=" + msgID);

            if (rs.next()) {
               themeID = rs.getInt(1);
            }

            rs = st.executeQuery("select messageid from forummessages where themeid=" + themeID);

            int count = 0;
            while(rs.next())
                count ++;

            if (count == 1) {
                st.execute("delete from forumthemes where themeid=" + themeID);
            }

            state.execute("DELETE FROM forummessages WHERE messageID=" + msgID);
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessage(MessageManager.getMessage(lang, ForumMessages.MESSAGE_SUCCESFULLY_DELETED));
            return true;
            
        } catch(Exception exc) {
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang, Message.ERROR_MESSAGE));
            message.setMessage(MessageManager.getMessage(lang, Constants.NOT_DELETED));
            return false;
        } finally{
            try {
                if (con != null) con.close();
                if (state != null) state.close();
                if (rs != null) rs.close();
                if (st != null) st.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public int getMessageID () {
        return msgID;
    }

    public String getAuthor () {
        return authorname;
    }

    public String getBody () {
        return msgBody;
    }

    public String getTitle () {
        return title;
    }

    public String getJob() {
        return job;
    }

    public int getAuthorID() {
        return authorID;
    }

    public int getRoleID() {
        return roleID;
    }

    public String getDate () {
        return date;
    }
}
