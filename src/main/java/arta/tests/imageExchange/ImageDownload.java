package arta.tests.imageExchange;

import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.common.http.MimeTypes;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

public class ImageDownload {

    public String mimetype;

    public byte[] getImge (int id){

        byte [] image = new byte[0];
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT image, filetype from testimages WHERE imageID="+id);
            if (res.next()){
                image = res.getBytes("image");
                mimetype = res.getString("filetype");
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
            if (res!=null) res.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }

        return image;
    }

    public String getMimeType(){
        if (mimetype == null ){
            return "image/jpeg";
        }
        return mimetype;
    }

}
