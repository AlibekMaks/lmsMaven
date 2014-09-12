package arta.forum.logic;

import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.forum.ForumMessages;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Theme {

    int partID;
    String title;
    int authorID;
    int roleID;
    int themeID;
    int lang;
    boolean isNew;
    String author;
    int answersCount;
    String dateOfLastMsg;
    String authorOfLastMsg;

    public Theme(int themeID) {
        this.themeID = themeID;
    }

    public Theme (int partID, String title, int authorID, int roleID, int lang) {
        this.partID = partID;
        this.title = title;
        this.authorID = authorID;
        this.roleID = roleID;
        this.lang = lang;
    }

    public Theme (int themeID, String title, String author, int answersCount,
                  String dateOfLastMsg, String authorOfLastMsg, Boolean isNew) {
        this.themeID = themeID;
        this.title = title;
        this.author = author;
        this.answersCount = answersCount;
        this.dateOfLastMsg = dateOfLastMsg;
        this.authorOfLastMsg = authorOfLastMsg;
        this.isNew = isNew;

    }

    public boolean create (Message message){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        boolean result = false;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            title = title.replaceAll("'", "''");

            res = st.executeQuery("SELECT title FROM forumthemes WHERE title = '" + title + "' and partid=" + partID);

            if (res.next()) {
                result = false;
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.THEME_CANNOT_BE_CREATED));
            } else {
                st.execute("INSERT INTO forumthemes (PartID, Title, AuthorID, RoleID, CreatedDate, " +
                    "LastMessageID) VALUES ("+ partID + ", '"+ title+ "', " + authorID + ", " + roleID + ", current_timestamp," +
                    0 + ")", Statement.RETURN_GENERATED_KEYS);


                res = st.getGeneratedKeys();

                if (res.next()) {
                    themeID = res.getInt(1);
                }
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.THEME_SUCCESFULLY_CREATED));
                message.setMessageType(Message.ERROR_MESSAGE);
                result = true;
            }

        } catch(Exception e) {
            Log.writeLog(e);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            result =  false;
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();

            } catch (Exception exc){
                Log.writeLog(exc);
            }

        }


     return result;
    }

    public boolean delete(Message message, int lang) {

        this.lang = lang;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Statement state;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            state = con.createStatement();

            res = st.executeQuery("SELECT themeID FROM forumthemes WHERE partid=" + themeID);

            while(res.next()) {
                state.execute("DELETE FROM forummessages WHERE themeID=" + res.getInt(1));
            }

            st.execute("DELETE FROM forumthemes WHERE themeid=" + themeID);

            message.setMessage(MessageManager.getMessage(lang, ForumMessages.THEME_SUCCESFULLY_DELETED));
            return true;


        } catch(Exception exc) {
            Log.writeLog(exc);
            message.setMessage(MessageManager.getMessage(lang, Constants.NOT_DELETED));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally{
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

    }

    public void changeLastMsgData (int msgID, int themeid) {

        ThemeManager tm = new ThemeManager();


        Connection con = null;
        Statement st = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            st.execute("update forumthemes set lastmessagedate=current_timestamp, lastmessageid=" 
                    + msgID + " where themeid=" + themeid);
            st.execute("update forumparts set lastmessagedate=current_timestamp where partid=" + tm.getPartID(themeid));

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

     public boolean isNew() {
        return isNew;
    }

    public String getTitle () {
        return title;
    }

    public String getAuthor () {
        return author;
    }

    public int getAnswersCount () {
        return answersCount;
    }

    public String getLastDate () {
        return dateOfLastMsg;
    }

    public String getLastAuthor () {
        return  authorOfLastMsg;
    }

    public int getThemeID () {
        return themeID;
    }

}
