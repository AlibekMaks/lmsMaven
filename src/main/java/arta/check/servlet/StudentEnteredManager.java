package arta.check.servlet;


import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentEnteredManager {

    protected Connection con = null;
    protected Statement st = null;
    protected ResultSet res = null;

    public void UnLockStudentEntered(int studentID) throws Exception {

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM handing_over_testing WHERE StudentID = "+studentID);
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            if (con != null) con.close();
            if (st!= null) st.close();
            if (res != null) res.close();
        }

    }

}
