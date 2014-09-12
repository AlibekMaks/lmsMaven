package arta.tests.reports.logic.commonReports;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Date;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.filecabinet.logic.tutors.Tutor;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class CommonTestReportView {

    public int mainTestingID;
    public int testingID;
    public String testingName;
    public String tutorName;
    public Date date;

    public int questionsCount;
    public int easyCount;
    public int middleCount;
    public int difficultCount;
    public int progress;
    public int quality;

    public int kz_questionsCount;
    public int kz_easyCount;
    public int kz_middleCount;
    public int kz_difficultCount;
    public int kz_progress;
    public int kz_quality;

    public int ru_questionsCount;
    public int ru_easyCount;
    public int ru_middleCount;
    public int ru_difficultCount;
    public int ru_progress;
    public int ru_quality;

    public ArrayList <CommonTestReportStudent> students = new ArrayList <CommonTestReportStudent> ();

    public CommonTestReportView(int testingID) {
        this.testingID = testingID;
        date = new Date();
    }

    public CommonTestReportView(int mainTestingID, int testingID) {
        this.mainTestingID = mainTestingID;
        this.testingID = testingID;
        date = new Date();
    }

    public void load(int lang){

        Connection con  = null;
        Statement st = null;
        ResultSet res = null;
        int tutorID = 0;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
//            res = st.executeQuery("SELECT students.lastname as ln, " +
//                    " students.firstname as fn, " +
//                    " students.patronymic as p, " +
//                    " testreports.easy as ea, " +
//                    " testreports.middle as md, " +
//                    " testreports.difficult as df, " +
//                    " testreports.mark as mark, " +
//                    " students.studentID as id " +
//                    " FROM (((testings LEFT JOIN testingstudents ON testingstudents.testingID=testings.testingID) " +
//                    " LEFT JOIN students ON students.studentID=testingstudents.studentID)" +
//                    " LEFT JOIN testreports ON (testreports.testingID=testings.testingID AND " +
//                    " testreports.studentID=testingstudents.studentID AND students.studentID=testreports.studentID)) " +
//                    " WHERE testings.testingID="+testingID+" ");
            res = st.executeQuery("SELECT " +
                                    "  s.studentID as id, " +
                                    "  s.lastname as ln, " +
                                    "  s.firstname as fn, " +
                                    "  s.patronymic as p, " +
                                    "  t.easy as ea, " +
                                    "  t.middle as md, " +
                                    "  t.difficult as df, " +
                                    "  t.mark as mark, " +
                                    "  c.classid AS cid, " +
                                    "  c.classname AS classname, " +
                                    "  t1.lang AS lang, " +
                                    "  r.isPassed as isPassed " +
                                    "FROM testreports t " +
                                    "JOIN testings t1 ON t.testingID = t1.testingID " +
                                    "JOIN students s ON t.studentID = s.studentid " +
                                    "JOIN registrar r ON t.studentID = r.studentid AND t1.mainTestingID = r.mainTestingID " +
                                    "JOIN classes c ON s.classID = c.classid " +
                                    "WHERE t1.mainTestingID = "+mainTestingID);

            int exc = 0;
            int good = 0;
            int total = 0;
            int sat = 0;
            while (res.next()){
                CommonTestReportStudent student = new CommonTestReportStudent(lang);
                student.name = res.getString("ln");
                String tmp = res.getString("fn");
                if (tmp!=null && tmp.length()>0){
                    student.name += " " + tmp.substring(0, 1) + ".";
                    tmp = res.getString("p");
                    if (tmp!=null && tmp.length()>0){
                        student.name += tmp.substring(0, 1) + ".";
                    }
                }
                student.easy = res.getInt("ea");
                student.middle = res.getInt("md");
                student.difficult = res.getInt("df");
                student.mark = res.getInt("mark");
                student.studentID = res.getInt("id");
                student.classID = res.getInt("cid");
                student.className = res.getString("classname");
                student.testingLanguage = res.getInt("lang");
                student.testingIsPassed = res.getBoolean("isPassed");
                students.add(student);
                if (student.mark >= Constants.EXCELLENT_MARK){
                    exc ++;
                } else if (student.mark >= Constants.GOOD_MARK){
                    good ++;
                } else if (student.mark >= Constants.SATISFACTORY_MARK){
                    sat ++;
                }
                total ++;
            }


            res = st.executeQuery("SELECT testings.name as name, " +
                    " testings.testingdate as td , " +
                    " testings.tutorID as tutorID " +
                    " FROM testings  WHERE testings.testingID="+testingID+" ");
            if (res.next()){
                testingName = res.getString("name");
                date.loadDate(res.getString("td"), Date.FROM_DB_CONVERT);
                tutorID = res.getInt("tutorID");
            }


            res = st.executeQuery("SELECT tutors.lastname as ln, " +
                    " tutors.firstname as fn, " +
                    " tutors.patronymic as p " +
                    " FROM tutors WHERE tutorID="+tutorID+" ");
            if (res.next()){
                tutorName = Tutor.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
            }



            res = st.executeQuery("SELECT testsfortesting.easy as ea, " +
                    " testsfortesting.middle as md, " +
                    " testsfortesting.difficult as df " +
                    " FROM testsfortesting where testingid="+testingID+"");
            while(res.next()){
                easyCount += res.getInt("ea");
                middleCount += res.getInt("md");
                difficultCount += res.getInt("df");
            }
            questionsCount = easyCount + middleCount + difficultCount;


            progress = (int)(((double)(exc+good+sat)/(double)total)*100);
            quality = (int)(((double)(exc+good)/(double)total)*100);



        }catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            }catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void myload(int lang, int mainTestingID){

        Connection con  = null;
        Statement st = null;
        Statement st2 = null;
        ResultSet res = null;
        int tutorID = 0;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st2 = con.createStatement();

            String sql = "(SELECT \n" +
                        " s.studentID as id, \n" +
                        " s.lastname as ln, \n" +
                        " s.firstname as fn, \n" +
                        " s.patronymic as p, \n" +
                        " t.easy as ea, \n" +
                        " t.middle as md, \n" +
                        " t.difficult as df, \n" +
                        " t.mark as mark, \n" +
                        " t1.testingID AS tid, \n" +
                        " t1.lang AS lang, \n" +
                        " r.isPassed as isPassed, \n" +
                        " IF((r.status = 0) AND (DATE(t1.testingDate) < DATE(NOW())), TRUE, FALSE) as timeIsUp \n" +
                        "FROM testreports t \n" +
                        " JOIN testings t1 ON t.testingID = t1.testingID \n" +
                        " JOIN students s ON t.studentID = s.studentid \n" +
                        " JOIN registrar r ON t.studentID = r.studentid AND t1.mainTestingID = r.mainTestingID \n" +
                        "WHERE t1.mainTestingID = "+mainTestingID+") \n";

                sql += " UNION ";

                sql += "(SELECT \n" +
                        " s.studentid as sid, \n" +
                        " s.lastname as ln, \n" +
                        " s.firstname as fn, \n" +
                        " s.patronymic as p, \n" +
                        " 0 as ea, \n" +
                        " 0 as md, \n" +
                        " 0 as df, \n" +
                        " 0 as mark, \n" +
                        " 0 AS tid, \n" +
                        " 0 AS lang, \n" +
                        " r.isPassed as isPassed, \n" +
                        " true as timeIsUp \n" +
                        "FROM registrar r \n" +
                        "  JOIN testings t ON r.mainTestingID = t.mainTestingID \n" +
                        "  JOIN students s ON s.studentid = r.studentid \n" +
                        "WHERE r.mainTestingID = "+mainTestingID+" AND \n" +
                        "      DATE(t.testingDate) < DATE(NOW()) AND \n" +
                        "      (r.status = 3 OR r.status IS NULL) AND \n" +
                        "      r.testingID IS NULL \n" +
                        "GROUP BY r.mainTestingID, r.studentid)\n";
                sql += " ORDER BY ln, fn, p";


//            System.out.println(sql);


            res = st.executeQuery(sql);

            int exc = 0;
            int good = 0;
            int total = 0;
            int sat = 0;
            while (res.next()){
                CommonTestReportStudent student = new CommonTestReportStudent(lang);
                student.name = res.getString("ln");
                String tmp = res.getString("fn");
                if (tmp!=null && tmp.length()>0){
                    student.name += " " + tmp.substring(0, 1) + ".";
                    tmp = res.getString("p");
                    if (tmp!=null && tmp.length()>0){
                        student.name += tmp.substring(0, 1) + ".";
                    }
                }
                student.testingID = res.getInt("tid");
                student.easy = res.getInt("ea");
                student.middle = res.getInt("md");
                student.difficult = res.getInt("df");
                student.mark = res.getInt("mark");
                student.studentID = res.getInt("id");
                student.testingLanguage = res.getInt("lang");
                student.testingIsPassed = res.getBoolean("isPassed");
                student.timeIsUp = res.getBoolean("timeIsUp");
                students.add(student);
                if (student.mark >= Constants.EXCELLENT_MARK){
                    exc ++;
                } else if (student.mark >= Constants.GOOD_MARK){
                    good ++;
                } else if (student.mark >= Constants.SATISFACTORY_MARK){
                    sat ++;
                }
                total ++;

                if(student.timeIsUp){ // Если время тестирования для данного студента прошло
                    //st2.execute("UPDATE registrar r SET r.status = 3 WHERE (r.mainTestingID = "+mainTestingID + ") AND (r.studentid = "+student.studentID+")");
                    //st2.execute("UPDATE testingstudents t SET t.status = 3 WHERE (t.studentID = "+student.studentID+") AND (t.testingID IN ("+mainTestingID+", "+(mainTestingID+1)+"))");
                }
            }

            int kaz_testingID = 0;
            int rus_testingID = 0;
            res = st.executeQuery("SELECT t.testingID AS id, t.lang AS lang FROM testings t\n" +
                                  " WHERE t.mainTestingID = "+mainTestingID);
            while(res.next()){
                if(res.getInt("lang")==Constants.KAZ_TEST){
                    kaz_testingID = res.getInt("id");
                } else if(res.getInt("lang")==Constants.RUS_TEST){
                    rus_testingID = res.getInt("id");
                }
            }

            res = st.executeQuery("SELECT testsfortesting.easy as ea, " +
                    " testsfortesting.middle as md, " +
                    " testsfortesting.difficult as df " +
                    " FROM testsfortesting where testingid="+kaz_testingID+"");
            while(res.next()){
                kz_easyCount += res.getInt("ea");
                kz_middleCount += res.getInt("md");
                kz_difficultCount += res.getInt("df");
            }
            kz_questionsCount = kz_easyCount + kz_middleCount + kz_difficultCount;


            res = st.executeQuery("SELECT testsfortesting.easy as ea, " +
                    " testsfortesting.middle as md, " +
                    " testsfortesting.difficult as df " +
                    " FROM testsfortesting where testingid="+rus_testingID+"");
            while(res.next()){
                ru_easyCount += res.getInt("ea");
                ru_middleCount += res.getInt("md");
                ru_difficultCount += res.getInt("df");
            }
            ru_questionsCount = ru_easyCount + ru_middleCount + ru_difficultCount;

            res = st.executeQuery("SELECT t.name as name, " +
                    " t.testingdate as td , " +
                    " t.tutorID as tutorID " +
                    " FROM testings t WHERE t.mainTestingID="+mainTestingID+" ");
            if (res.next()){
                testingName = res.getString("name");
                date.loadDate(res.getString("td"), Date.FROM_DB_CONVERT);
                tutorID = res.getInt("tutorID");
            }
            progress = (int)(((double)(exc+good+sat)/(double)total)*100);
            quality = (int)(((double)(exc+good)/(double)total)*100);

            res = st.executeQuery("SELECT tutors.lastname as ln, " +
                    " tutors.firstname as fn, " +
                    " tutors.patronymic as p " +
                    " FROM tutors WHERE tutorID="+tutorID+" ");
            if (res.next()){
                tutorName = Tutor.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
            }

        }catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (st2!=null) st2.close();
                if (res!=null) res.close();
            }catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public String getTEstinName(){
        if (testingName == null)
            return "";
        return testingName;
    }
}
