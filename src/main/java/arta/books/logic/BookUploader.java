package arta.books.logic;

import arta.common.http.DefaultHttpHandler;
import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.subjects.logic.SubjectMessages;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.FileNotFoundException;


public class BookUploader extends DefaultHttpHandler {

    Book book;

    DataExtractor extractor = new DataExtractor();
    StringTransform trsf = new StringTransform();
    boolean canBeSaved = true;
    Connection con;
    Statement st;
    PreparedStatement prst;
    ResultSet res;
    int currentPartNumber = 0;


    String fileName = null;
    String mimeType = null;

    Message message;
    int lang;

    public BookUploader(Book book, Message message, int lang) {
        this.book = book;
        this.message = message;
        this.lang = lang;
    }

    public void getConnection(){
        try{
            ConnectionPool pool = new ConnectionPool();
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.createStatement();
        } catch (Exception exc){
            Log.writeLog(exc);
            canBeSaved = false;
        }
    }

    public void releaseConnection(){
        if (!canBeSaved){
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            try{
                con.rollback();
            } catch (Exception exc){
                Log.writeLog(exc);
            } finally{
                try{
                    if (con != null) con.close();
                    if (st != null) st.close();
                    if (prst != null) prst.close();
                    if (res != null) res.close();
                } catch (Exception e){
                    Log.writeLog(e);
                }
            }
        } else {
            try{
                con.commit();
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
                message.setMessageType(Message.INFORMATION_MESSAGE);
            } catch (Exception exc){
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                Log.writeLog(exc);
                try{
                    con.rollback();
                } catch (Exception e){
                    Log.writeLog(e);
                }
            } finally {
                try{
                    if (con != null) con.close();
                    if (st != null) st.close();
                    if (prst != null) prst.close();
                    if (res != null) res.close();
                } catch (Exception exc){
                    Log.writeLog(exc);
                }
            }
        }
    }

    public void startFile(String name, String fileName, String mimeType, byte[] b) {
        try{
            book.startSave(b, con, res, st);
            if (b != null)
                currentPartNumber = 1;
            this.fileName = fileName;
            this.mimeType = mimeType;
        }catch (Exception exc){
            Log.writeLog(exc);
            canBeSaved = false;
            releaseConnection();
        }
    }

    public void appendFile(String name, byte[] b) {
        if (!canBeSaved)
            return;
        try {
            currentPartNumber ++ ;
            book.append(currentPartNumber, b, con, prst);
        } catch (Exception exc){
            Log.writeLog(exc);
            canBeSaved = false;
            releaseConnection();
        }
    }


    public void dataParsed() {
        try{
            if (canBeSaved){
                book.dataParsed(st, res, extractor.getInteger(getParameter("bookID")),
                        fileName, getParameter("name"), mimeType,
                        extractor.getInteger(getParameter("lang")),
                        extractor.getInteger(getParameter("subjectID")),
                        trsf);
            }
        }catch(FileNotFoundException exc){
            canBeSaved = false;
            message.setMessage(MessageManager.getMessage(lang, Constants.FILE_NOT_FOUND, null));
            Log.writeLog(exc);
        } catch (Exception exc){
            canBeSaved = false;
            Log.writeLog(exc);
        }
    }


    public String getParameter(String name) {
        return super.getParameter(name);
    }
}
