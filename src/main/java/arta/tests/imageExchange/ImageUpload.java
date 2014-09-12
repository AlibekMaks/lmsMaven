package arta.tests.imageExchange;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.awt.image.BufferedImage;


public class ImageUpload {


    public int uploadImage(byte [] image, int tutorID, int testID, int type,
                           int width, int height, String signature){
        return uploadImage(image, tutorID, testID, type, width, height, signature, "image/jpeg");
    }

    public int uploadImage(byte [] image, int tutorID, int testID, int type,
                           int width, int height, String signature, String filetype){

        int result = 0;
        Connection con = null;
        PreparedStatement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.prepareStatement("INSERT INTO testimages " +
                    " (image, tutorID, type, width, height, signature, filetype, testID) VALUES " +
                    " (?, "+tutorID+", "+type+", "+width+", "+height+", '"
                    + trsf.getDBString(signature)+"', '"+trsf.getDBString(filetype)+"', "+testID+")",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            st.setBytes(1, image);
            st.execute();

            res = st.getGeneratedKeys();
            if (res.next())
                result = res.getInt(1);

        }catch (Exception exc){
            Log.writeLog(exc);
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
            if (res!=null) res.close();
        }catch (Exception exc){            
            Log.writeLog(exc);
        }

        return result;
    }

    public void updateImages(ArrayList<Integer> imagesID, byte [] image){

        Connection con = null;
        PreparedStatement st = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            for (int i=0; i<imagesID.size(); i++){
                st = con.prepareStatement("UPDATE testimages SET image=? WHERE imageID="+imagesID.get(i));
                st.setBytes(1, image);
                st.execute();
            }

        }catch (Exception exc){
            Log.writeLog(exc);
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }

}
