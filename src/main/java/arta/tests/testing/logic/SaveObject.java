package arta.tests.testing.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import arta.check.logic.Testing;
import sunw.io.Serializable;


public class SaveObject {

    public Object javaObject = null;

    public Object getJavaObject() {
        return javaObject;
    }


    public void setJavaObject(Object javaObject) {
        this.javaObject = javaObject;
    }

    public void deleteObject(int studentID, int testingID) throws Exception
    {
        Connection con = null;
        Statement st = null;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM sessions WHERE (studentID = "+studentID+") AND (testingID = "+testingID+")");

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try{
                if (st != null) st.close();
                if (con != null) con.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void saveObject(int studentID, int testingID, Object obj) throws Exception
    {
        Connection con = null;
        PreparedStatement ps = null;

        try{

            deleteObject(studentID, testingID);

            con = new ConnectionPool().getConnection();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject( obj );
            oos.flush();
            oos.close();
            bos.close();

            byte[] data = bos.toByteArray();

            ps = con.prepareStatement("INSERT INTO sessions(studentID, testingID, testing) values(?, ?, ?)");
                ps.setInt(1, studentID);
                ps.setInt(2, testingID);
                ps.setObject(3, data);
            ps.executeUpdate();

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try{
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }


    public Testing getObject(int studentID, int testingID) throws Exception {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        Testing rmObj = null;

        try{
            Connection conn = new ConnectionPool().getConnection();
            ps = conn.prepareStatement("SELECT * FROM sessions s WHERE (s.studentID = " + studentID + ") AND (s.testingID = "+testingID+")");
            res = ps.executeQuery();

            if(res.next()){
                ByteArrayInputStream bais;
                ObjectInputStream ins;

                try {
                    bais = new ByteArrayInputStream(res.getBytes("testing"));
                    ins = new ObjectInputStream(bais);
                        rmObj = (Testing)ins.readObject();
                    ins.close();
                } catch (Exception exc) {
                    Log.writeLog(exc);
                }
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally{
            try{
                if (ps != null) ps.close();
                if (con != null) con.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

        return rmObj;
    }




}
