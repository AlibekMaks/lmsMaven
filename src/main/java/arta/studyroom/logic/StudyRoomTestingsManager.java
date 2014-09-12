package arta.studyroom.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Time;
import arta.common.logic.sql.ConnectionPool;
import arta.tests.testing.logic.SimpleTesting;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 27.03.2008
 * Time: 15:27:44
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoomTestingsManager {

    public ArrayList <SimpleTesting> testings = new ArrayList <SimpleTesting> ();

    int subgroupID;


    public StudyRoomTestingsManager(int subgroupID) {
        this.subgroupID = subgroupID;
    }

    public StudyRoomTestingsManager() {
        this.subgroupID = -1;
    }


    public void mysearch(int roleID, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            if (roleID == Constants.STUDENT){
                res = st.executeQuery("SELECT testings.name as name, " +
                        " testings.testingID as id, " +
                        " testings.starttime as starttime, " +
                        " testings.finishtime as finishtime " +
                        " FROM testings LEFT JOIN registrar ON testings.testingID=registrar.testingID " +
                        " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
                        " WHERE testings.testingdate=current_date AND registrar.studentID=" + personID + " " +
                        " AND ( subgroups.subgroupID="+subgroupID+" or "+subgroupID+" = -1 ) " +
                        " ORDER BY testings.name, testings.testingID");
            } else {
                res = st.executeQuery("SELECT DISTINCT testings.name as name, " +
                        " testings.testingID as id, " +
                        " testings.starttime as starttime, " +
                        " testings.finishtime as finishtime " +
                        " FROM testings JOIN registrar ON registrar.testingID=testings.testingID " +
                        " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
                        " WHERE testings.tutorID="+personID+" AND testingdate=current_date " +
                        " AND ( subgroups.subgroupID="+subgroupID+"  or "+subgroupID+" = -1 ) " +
                        " ORDER BY testings.name, testings.testingID ");
            }

            while (res.next()){
                SimpleTesting testing = new SimpleTesting();
                testing.setTestingID(res.getInt("id"));
                testing.setTestingName(res.getString("name"));
                testing.setTestingStartTime(new Time(res.getString("starttime")));
                testing.setTestingFinishTime(new Time(res.getString("finishtime")));
                testings.add(testing);
            }


        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }


    public void search(int roleID, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            String sql = "";
            if (roleID == Constants.STUDENT){
                sql = "SELECT testings.name as name, " +
                        " testings.testingID as id, " +
                        " testings.starttime as starttime, " +
                        " testings.finishtime as finishtime " +
                        " FROM testings LEFT JOIN registrar ON testings.testingID=registrar.testingID " +
                        " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
                        " WHERE testings.testingdate=current_date AND registrar.studentID=" + personID + " " +
                        " AND ( subgroups.subgroupID="+subgroupID+" or "+subgroupID+" = -1 ) " +
                        " ORDER BY testings.name, testings.testingID";
            } else {
                sql = "SELECT DISTINCT testings.name as name, " +
                        " testings.testingID as id, " +
                        " testings.starttime as starttime, " +
                        " testings.finishtime as finishtime " +
                        " FROM testings JOIN registrar ON registrar.testingID=testings.testingID " +
                        " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
                        " WHERE testings.tutorID="+personID+" AND testingdate=current_date " +
                        " AND ( subgroups.subgroupID="+subgroupID+"  or "+subgroupID+" = -1 ) " +
                        " ORDER BY testings.name, testings.testingID ";
            }

            res = st.executeQuery(sql);
            while (res.next()){
                SimpleTesting testing = new SimpleTesting();
                testing.setTestingID(res.getInt("id"));
                testing.setTestingName(res.getString("name"));
                testing.setTestingStartTime(new Time(res.getString("starttime")));
                testing.setTestingFinishTime(new Time(res.getString("finishtime")));
                testings.add(testing);
            }


        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

}
