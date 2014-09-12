package arta.lessonmaterials.logic;

import arta.common.logic.download.Downloadable;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.InputStream;
import java.io.OutputStream;


public class TutorBookResource implements Downloadable {

    int bookID;
    private String name;
    private byte [] resource = null;
    private int contentLength = 0;
    private String mimeType = "";


    public TutorBookResource(int bookID) {
        this.bookID = bookID;
    }

    public void getBytes() {
        getBytes(0);
    }

    public void getBytes(int start) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT resource, filename, mimetype FROM resources " +
                    " WHERE resourceID="+bookID);
            if (res.next()){
                name = res.getString(2);
                mimeType = res.getString(3);
                InputStream in = res.getBinaryStream(1);
                contentLength = in.available();
                if (start > contentLength) {
                    resource = new byte[0];
                    return;
                }
                resource = new byte[contentLength-start];
                int a = in.read(new byte[start]);
                int b = in.read(resource);
            }
            if (mimeType == null) mimeType = "text/plain";
            if (name == null) name = "not defined";
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public byte[] get() {
        return resource;
    }

    public int getLength() {
        return contentLength;
    }

    public String getFileName() {
        return name;
    }

    public String getMimeType() {
        return mimeType;
    }


    public void writeResource(OutputStream out) {
        
    }

    public void writeResource(int start, OutputStream out) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void init() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
