package arta.groups.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;


public class SubGroup {

    private int subGroupID;
    private int tutorID;
    private String tutorName;
    private int groupNumber;

    public ArrayList <SimpleObject> students = new ArrayList <SimpleObject> ();


    public int getSubGroupID() {
        return subGroupID;
    }

    public int getTutorID() {
        return tutorID;
    }

    public String getTutorName() {
        return tutorName;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setSubGroupID(int subGroupID) {
        this.subGroupID = subGroupID;
    }

    public void setTutorID(int tutorID) {
        this.tutorID = tutorID;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public static int getStudyGroupID(int subgroupID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT groupID FROM subgroups " +
                    " WHERE subgroupID=" + subgroupID);
            if (res.next()){
                return res.getInt(1);
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return 0;

    }
    
}

