package arta.books.logic;

import arta.common.logic.download.Downloadable;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.common.http.DefaultHttpHandler;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.InputStream;
import java.io.OutputStream;


public class BookResource implements Downloadable {

    int bookID;
    int contentID;
    private String name;
    private String fileName;
    int lang;
    private int contentLength = 0;
    private String mimeType = "";


    public BookResource(int bookID) {
        this.bookID = bookID;
    }


    public void init() {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT resources.name as name, " +
                    " resources.language as lang,  " +
                    " resources.filename as file, " +
                    " resources.mimetype as mimetype, " +
                    " resources.length as size, " +
                    " contents.contentID as contentID " +
                    " FROM resources LEFT JOIN contents on contents.resourceid=resources.resourceid " +
                    " WHERE resources.resourceID="+bookID);
            if (res.next()){
                name = res.getString("name");
                lang = res.getInt("lang");
                fileName = res.getString("file");
                mimeType = res.getString("mimetype");
                contentLength = res.getInt("size");
                contentID = res.getInt("contentID");
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void writeResource(OutputStream out) {
        writeResource(0, out);
    }

    public void writeResource(int start, OutputStream out) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();


            int partsCount = contentLength/ DefaultHttpHandler.bufferCapacity;
            if (contentLength% DefaultHttpHandler.bufferCapacity != 0)
                partsCount ++;
            int currentPart = 1;
            currentPart = start/DefaultHttpHandler.bufferCapacity + 1;
            start = start%DefaultHttpHandler.bufferCapacity;

            for (int i = currentPart; i <= partsCount; i++){
                ResultSet rs = st.executeQuery("SELECT length(content), content FROM contentparts " +
                        " WHERE contentID="+contentID+" AND contentparts.partnumber="+i);
                if (rs.next()){
                    try{
                        out.write(rs.getBytes(2), start, rs.getInt(1)-start);
                        start = 0;
                    } catch (Exception e){
                        System.out.println("EXCEPTION");
                        System.out.println("start="+start+" contentlength="+rs.getInt(1));
                    }
                }
                out.flush();
                rs = null;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public int getLength() {
        return contentLength;
    }

    public String getFileName() {
        return fileName;
    }


    public String getMimeType() {
        return mimeType;
    }
}
