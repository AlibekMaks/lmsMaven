package arta.scheduled.tests.logic;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.filecabinet.logic.students.Student;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.servlet.TestingStudentsServlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class SheduledTesting {

    public int mainTestingID;
    public int testingID;
    public int tutorID;
    public Date testingDate;
    public int testingTime;
    public int lang;
    public String namekz;
    public String nameru;
    public int kzTestingID;
    public int ruTestingID;
    public Message message;

    public ArrayList<Integer> students = new ArrayList<Integer>();
    public ArrayList<Integer> notDeletedStudents = new ArrayList<Integer>();

    StringTransform trsf = new StringTransform();


    public SheduledTesting(int mainTestingID, Message message, int lang){
        this.mainTestingID = mainTestingID;
        this.message = message;
        this.lang = lang;
    }


    public void getTestingsIDs(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        this.kzTestingID = 0;
        this.ruTestingID = 0;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer sql = new StringBuffer("SELECT t.testingID as id, t.lang as lang FROM testings t WHERE t.mainTestingID = " + this.mainTestingID);
            res = st.executeQuery(sql.toString());
            while (res.next()){
                if(res.getInt("lang")==Languages.KAZAKH){
                    this.kzTestingID = res.getInt("id");
                } else if(res.getInt("lang")==Languages.RUSSIAN){
                    this.ruTestingID = res.getInt("id");
                }
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public int getMainTestingID(){
        return this.mainTestingID;
    }

    public void setMainTestingID(int mainTestingID){
        this.mainTestingID = mainTestingID;
    }

    public int getTestingID(){
        return this.testingID;
    }

    public void setTestingID(int testingID){
        this.testingID = testingID;
    }

    public int getTutorID(){
        return this.tutorID;
    }

    public void setTutorID(int tutorID){
        this.tutorID = tutorID;
    }

    public Date getTestingDate(){
        return this.testingDate;
    }

    public void setTestingDate(Date testingDate){
        this.testingDate = testingDate;
    }

    public int getTestingTime(){
        return this.testingTime;
    }

    public void setTestingTime(int testingTime){
        this.testingTime = testingTime;
    }

    public int getLang(){
        return this.lang;
    }

    public void setLang(int lang){
        this.lang = lang;
    }

    public String getNamekz(){
        return this.namekz;
    }

    public void setNamekz(String namekz){
        this.namekz = namekz;
    }

    public String getNameru(){
        return this.nameru;
    }

    public void setNameru(String nameru){
        this.nameru = nameru;
    }

    public String getName(int lang){
        if(lang == Languages.KAZAKH){
            return this.namekz;
        } else if(lang == Languages.RUSSIAN){
            return this.nameru;
        } else return "";
    }

    public void load(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer sql = new StringBuffer("SELECT * FROM testings t WHERE t.testingID = " + this.testingID);

            res = st.executeQuery(sql.toString());
            if(res.next()){
                this.mainTestingID = res.getInt("mainTestingID");
                this.tutorID = res.getInt("tutorID");
                this.testingDate = res.getDate("testingDate");
                this.testingTime = res.getInt("testingTime");
                this.lang = res.getInt("lang");
                this.namekz = res.getString("name");
                this.nameru = res.getString("name");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public void getStudentsForCancelTheTest(){
        students.clear();
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String sql = "SELECT r.studentid AS sid FROM registrar r WHERE r.mainTestingID = "+this.mainTestingID;
            res = st.executeQuery(sql);
            while (res.next()){
                students.add(res.getInt("sid"));
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public boolean geleteTestForStudent(int studentID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        int status = 0;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String sql = "SELECT r.status AS st FROM registrar r WHERE (r.mainTestingID = "+this.mainTestingID+") AND (r.studentid = "+studentID+")";
            res = st.executeQuery(sql);
            if(res.next()){
                status = res.getInt("st");
            }

            if(status != 2){
                st.execute("DELETE FROM registrar WHERE (mainTestingID = "+this.mainTestingID+") AND (studentid = "+studentID+")");
                st.execute("DELETE FROM testingstudents WHERE (testingID in ("+this.kzTestingID+","+this.ruTestingID+")) AND (studentID = "+studentID+")");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return (status != 2);
    }

    public void checkOnRemoveTheTest(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        int count = 0;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String sql = "SELECT COUNT(*)as count FROM registrar r WHERE r.mainTestingID = "+this.mainTestingID;
            res = st.executeQuery(sql);
            if(res.next()){
                count = res.getInt("count");
            }

            if(count == 0){
                st.execute("DELETE FROM testings WHERE mainTestingID = "+this.mainTestingID);
                st.execute("DELETE FROM testingstudents WHERE testingID in ("+this.kzTestingID+","+this.ruTestingID+")");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public void cancelTheTest(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        notDeletedStudents.clear();
        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            getTestingsIDs();
            boolean result;
            String zap = "";
            for (int i=0; i < students.size(); i++){
                result = geleteTestForStudent(students.get(i));
                if(!result){
                    notDeletedStudents.add(students.get(i));
                    Student student = new Student();
                    student.loadById(students.get(i));

                    if(i<(students.size()-1)) zap = ", ";
                    message.addReason(trsf.getHTMLString("\""+student.getFullName()+"\""+zap), null);
                    student = null;
                }
            }
            if(notDeletedStudents.size()==0){
                checkOnRemoveTheTest();
            } else {
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.FOR_STUDENTS_LISTED_BELLOW_CAN_NOT_BE_UNDONE_TESTING, null));
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public static boolean CanRemove(int mainTestingID){

        boolean result = false;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT COUNT(*) FROM testings t \n"+
                                  " WHERE (t.mainTestingID = "+mainTestingID + ") AND (DATE(t.testingDate) < DATE(NOW()))");
            if(res.next()){
                result = (res.getInt(1) == 0);
            }

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return result;
    }

    public static void CheckAndUpdateTestings(){

        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();

            res = st.executeQuery("SELECT t.mainTestingID as mid \n"+
                                  "  FROM testings t \n" +
                                  "  WHERE (DATE(t.testingDate) < DATE(NOW())) AND (t.timeIsUp = FALSE) \n" +
                                  "  GROUP BY t.mainTestingID ");
            while(res.next()){
                int mainTestingID = res.getInt("mid");

                st1.execute("UPDATE registrar r SET r.status = 3, r.mark = 0, r.isPassed = 0 WHERE (r.mainTestingID = "+mainTestingID+") AND (r.status IS NULL) AND (r.testingID IS NULL)");

                st1.execute("UPDATE testingstudents t SET t.status = 3 WHERE t.status = 0 AND t.testingID IN ("+mainTestingID+", "+(mainTestingID+1)+")");

                st1.execute("UPDATE testings t SET t.timeIsUp = TRUE WHERE t.mainTestingID = "+mainTestingID);
            }

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (st1 != null) st1.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

}

