package arta.forum.logic;

import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.forum.ForumMessages;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Part {

    public String title;
    public int partID;
    public int lang;
    public int authorID;
    public Date date;
    public int classID;
    public int roleID;
    public boolean isNew;
    public String author;
    public String className;
    public String dateString;

    public Part(int partID) {
        this.partID = partID;
    }

    public Part (String title, int authorID, int roleID, int classID) {
        this.title = title;
        this.authorID = authorID;
        this.roleID = roleID;
        this.classID = classID;
    }

    public Part (int partID, String title, String author, String dateString, Boolean isNew, int roleID) {
        this.partID = partID;
        this.title = title;
        this.author = author;
        this.dateString = dateString;
        this.isNew = isNew;
        this.roleID = roleID;
    }

    public boolean create (int lang, Message message){
        this.lang = lang;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();

            st = con.createStatement();
            StringBuffer query = new StringBuffer("SELECT title FROM forumparts WHERE title='");
            query.append(trsf.getDBString(title)).append("'");

            res = st.executeQuery(query.toString());

            if (res.next()) {
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.PART_CANNOT_BE_CREATED));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            } else {
                                          
                st.execute("INSERT INTO forumparts (Title, CreatedDate, AuthorID, RoleID, ClassID, "
                        + "lastmessagedate) VALUES ('" + trsf.getDBString(title) + "', current_timestamp,"
                        + authorID + ", " + roleID + ", " + classID + ", current_timestamp)",
                          Statement.RETURN_GENERATED_KEYS);

                res = st.getGeneratedKeys();

                if (res.next()) {
                    partID = res.getInt(1);
                }
                message.setMessage(MessageManager.getMessage(lang, ForumMessages.PART_SUCCESFULLY_CREATED));
                message.setMessageType(Message.ERROR_MESSAGE);
                return true;
            }
        } catch(Exception e) {
            Log.writeLog(e);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            return false;
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete(Message message, int lang) {
        
        this.lang = lang;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Statement state = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            state = con.createStatement();

            res = st.executeQuery("SELECT themeID FROM forumthemes WHERE partid=" + partID);

            while(res.next()) {
                state.execute("DELETE FROM forummessages WHERE themeID=" + res.getInt(1));
            }

            st.execute("DELETE FROM forumparts WHERE PartID=" + partID);
            st.execute("DELETE FROM forumthemes WHERE partid=" + partID);
            st.execute("DELETE FROM forumvisitedparts WHERE partid=" + partID);

            message.setMessage(MessageManager.getMessage(lang, ForumMessages.PART_SUCCESFULLY_DELETED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
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
                if (state != null) state.close();

            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

    }

    public int getClassID (int partid) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res  = st.executeQuery("SELECT classid FROM forumparts where partid=" + partid);

            if (res.next()) {
                classID = res.getInt(1);
            }

        } catch(Exception exc) {
           Log.writeLog(exc);
        } finally{
            try {
                if (st != null) st.close();
                if (res != null) res.close();

            } catch( Exception e) {
                Log.writeLog(e);
            }
        }

        return classID;
    }

    public String getTitle() {
        return title;
    }

    public int getPartID() {
        return partID;
    }

    public String getDateString () {
        return dateString;
    }

    public String getAuthor () {
        return author;
    }

    public int getRoleId() {
        return roleID;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public int getClassID () {
        return classID;
    }

}
