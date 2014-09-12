package arta.lessonmaterials.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.timetable.designer.logic.LessonConstants;
import arta.books.logic.Book;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class TutorBook extends Book {


    public TutorBook(int tutorID) {
        type = Constants.TUTOR_BOOK;
        this.tutorID = tutorID; 
    }

    public void load(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT name, language FROM resources " +
                    " WHERE resourceID="+id);
            if (res.next()){
                name = res.getString(1);
                lang = res.getInt(2);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }


    public boolean delete(Message message, int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            con.setAutoCommit(false);
            st.execute("DELETE FROM groupmaterials WHERE resourceID="+id);
            st.execute("DELETE FROM resources WHERE resourceID="+id);

            con.commit();
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con != null && !con.getAutoCommit())
                    con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!= null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return true;
    }

    public boolean appoint(int bookID, int subgroupID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT COUNT(*) FROM resources WHERE " +
                    " resourceID="+bookID+" AND type="+Constants.TUTOR_BOOK+" AND tutorID="+tutorID+" ");
            if (res.next() && res.getInt(1) == 0){
                return false;
            }

            res = st.executeQuery("SELECT COUNT(*) FROM groupmaterials WHERE subgroupID="+subgroupID+" " +
                    " AND resourceID="+bookID+" ");
            if (res.next() && res.getInt(1) > 0){
                return true;
            }

            st.execute("INSERT INTO groupmaterials (subgroupID, resourceID) " +
                    " VALUES ("+subgroupID+", "+bookID+") ");

            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean cancel(int bookID, int subgroupID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM groupmaterials WHERE " +
                    " subgroupID="+subgroupID+" AND resourceID="+bookID+" ");

            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }
}
