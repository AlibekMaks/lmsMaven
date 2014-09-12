package arta.common.logic.util;

import arta.common.logic.sql.ConnectionPool;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;


public class Log {
    public static void writeLog(Exception exc){
        exc.printStackTrace();

        Connection con = null;
        PreparedStatement prst = null;

        try{

            StringBuffer log = new StringBuffer();
            log.append(exc.getMessage());
            StackTraceElement [] trace = exc.getStackTrace();
            for (int i = 0; i < trace.length; i++){
                log.append("\r\n");
                log.append(trace[i].toString());
            }

            con = new ConnectionPool().getConnection();
            prst = con.prepareStatement("INSERT INTO logs(log, logdate) VALUES (?, current_timestamp)");
            prst.setBinaryStream(1, new ByteArrayInputStream(log.toString().getBytes()), log.length());
            prst.execute();

        } catch (Exception e){
        } finally {
            try{
                if (con != null) con.close();
                if (prst != null) prst.close();
            } catch (Exception e){
            }
        }

    }

    public static void writeLogs(OutputStream out){

        Connection con = null;
        Statement st = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            ResultSet res = st.executeQuery("SELECT logs.log, logs.logdate FROM logs ORDER BY logs.logID");
            while (res.next()){
                out.write(res.getString(2).getBytes());
                out.write("\r\n".getBytes());
                out.write(res.getBytes(1));
            }

        } catch (Exception exc){
            exc.printStackTrace();
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public static void writeLog(String str){

        Connection con = null;
        PreparedStatement prst = null;

        try{

            con = new ConnectionPool().getConnection();
            prst = con.prepareStatement("INSERT INTO logs(log, logdate) VALUES (?, current_timestamp)");
            prst.setBinaryStream(1, new ByteArrayInputStream(str.toString().getBytes()), str.length());
            prst.execute();

        } catch (Exception e){
        } finally {
            try{
                if (con != null) con.close();
                if (prst != null) prst.close();
            } catch (Exception e){
            }
        }

    }

    public static boolean deleteLogs(){

        Connection con = null;
        Statement st = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM logs");
            return true;
        } catch (Exception exc){
            exc.printStackTrace();
            return false;
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
