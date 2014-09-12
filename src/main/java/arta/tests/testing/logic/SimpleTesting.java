package arta.tests.testing.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Date;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.common.logic.util.Time;
import arta.filecabinet.logic.students.Student;
import arta.welcome.logic.StudentTesting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 11:15:04
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTesting {

    public int lang;

    protected int testingID;
    protected String testingName;
    protected Date testingDate;
    protected Time testingStartTime;
    protected Time testingFinishTime;


    protected int testingTime;
    protected int studentID;
    protected int classID;
    protected int subjectID;
    protected int kaz_test_ID;
    protected int rus_test_ID;
    protected java.util.Date startDate;
    protected int tutorID;
    protected String subjectName;
    protected int mainTestingID;
//    protected String kaz_test_name;
//    protected String rus_test_name;

    

    public void setTestingID(int testingID) {
        this.testingID = testingID;
    }

    public int getTestingID() {
        return testingID;
    }

    public void setTestingName(String testingName) {
        this.testingName = testingName;
    }

    public String getTestingName() {
        return testingName;
    }

    public void setTestingTime(int testingTime) {
        this.testingTime = testingTime;
    }

    public int getTestingTime() {
        if (testingTime <=0 ) return 100;
        return testingTime;
    }

    public void setTestingDate(Date testingDate) {
        this.testingDate = testingDate;
    }

    public Date getTestingDate() {
        return testingDate;
    }


    public Time getTestingStartTime() {
        return testingStartTime;
    }


    public void setTestingStartTime(Time testingStartTime) {
        this.testingStartTime = testingStartTime;
    }

    public boolean hasStarted(){
        int datediff = new Date().compareTo(testingDate);
        if ( datediff == 0 )
            return new Time().subjsract(testingStartTime) >= 0;

        return datediff > 0;
    }


    public Time getTestingFinishTime() {
        return testingFinishTime;
    }


    public void setTestingFinishTime(Time testingFinishTime) {
        this.testingFinishTime = testingFinishTime;
    }

    public int getStudentID(){
        return this.studentID;
    }

    public void setStudentID(int studentID){
        this.studentID = studentID;
    }

    public int getClassID(){
        return this.classID;
    }

    public void setClassID(int classID){
        this.classID = classID;
    }

    public int getSubjectID(){
        return this.subjectID;
    }

    public void setSubjectID(int subjectID){
        this.subjectID = subjectID;
    }

    public int getKaz_test_ID(){
        return this.kaz_test_ID;
    }

    public void setKaz_test_ID(int kaz_test_ID){
        this.kaz_test_ID = kaz_test_ID;
    }

    public int getRus_test_ID(){
        return this.rus_test_ID;
    }

    public void setRus_test_ID(int rus_test_ID){
        this.rus_test_ID = rus_test_ID;
    }

    public java.util.Date getStartDate(){
        return this.startDate;
    }

    public void setStartDate(java.util.Date startDate){
        this.startDate = startDate;
    }

    public int getTutorID(){
        return this.tutorID;
    }

    public void setTutorID(int tutorID){
        this.tutorID = tutorID;
    }

    public int getMainTestingID(){
        return this.mainTestingID;
    }

    public void setMainTestingID(int mainTestingID){
        this.mainTestingID = mainTestingID;
    }

    public String getKaz_test_name(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT t.testName as testname FROM subjects s " +
                                   " INNER JOIN tests t ON t.testID = s.kaz_test_id " +
                                   " WHERE s.subjectID = "+subjectID);
            if(res.next()){
                return res.getString("testname");
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
        return "";
    }

    public String getRus_test_name(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT t.testName as testname FROM subjects s " +
                    " INNER JOIN tests t ON t.testID = s.rus_test_id " +
                    " WHERE s.subjectID = "+subjectID);
            if(res.next()){
                return res.getString("testname");
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
        return "";
    }

    public String getSubjectName(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT s.name"+ Languages.getLang(lang)+" as name FROM subjects s WHERE s.subjectID = "+subjectID);
            if(res.next()){
                return res.getString("testname");
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
        return "";
    }

    public int IsExistsXTestResults(int studentID) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int tid = 0;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT t.testingID as tid FROM testingsforstudents t "+
                                  " WHERE (t.mainTestingID = "+this.mainTestingID+") AND (t.studentID = " + studentID + ")");
            if(res.next()){
                tid = res.getInt("tid");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return tid;
    }
}
