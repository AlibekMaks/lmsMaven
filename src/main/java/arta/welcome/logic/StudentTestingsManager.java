package arta.welcome.logic;

import arta.common.logic.util.*;
import arta.tests.testing.logic.SimpleTesting;
import arta.common.logic.sql.ConnectionPool;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 11.04.2008
 * Time: 13:48:40
 * To change this template use File | Settings | File Templates.
 */
public class StudentTestingsManager {

    public ArrayList<StudentTesting> testings = new ArrayList <StudentTesting> ();

    public ArrayList<StudentTesting> finishTestings = new ArrayList <StudentTesting> ();


    public void mysearch(int studentID, int lang){


        Connection con = null;
        Statement st = null;
        Statement st1= null;
        ResultSet res = null;
        ResultSet res1 = null;


        try{
//            res = st.executeQuery("SELECT testings.name as name, " +
//                    " testings.starttime as start, " +
//                    " testings.finishtime as finish, " +
//                    " subjects.name"+ Languages.getLang(lang)+" as sbj " +
//                    " FROM testings JOIN testingstudents ON testingstudents.testingID=testings.testingID " +
//                    " JOIN registrar ON registrar.testingID=testings.testingID AND registrar.studentID=testingstudents.studentID " +
//                    " JOIN studygroups ON studygroups.groupID=registrar.groupID " +
//                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
//                    " WHERE testingstudents.studentID="+studentID+"  " +
//                    " AND testings.testingdate=current_date AND finishtime>current_time" +
//                    " ORDER BY testings.starttime, testings.name");

            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            st1 = con.createStatement();

            boolean testFound = false;
            int mainTestingID = 0;
            res = st.executeQuery("SELECT t.mainTestingID AS mid from testings t " +
                                    " JOIN testingstudents ts ON t.testingID=ts.testingID " +
                                    "  WHERE ts.studentID="+studentID+" AND (ts.status=0 OR ts.status=1) AND t.testingDate= DATE(NOW()) " +
                                    "  GROUP BY t.mainTestingID ");
            if(res.next()){
//                mainTestingID = res.getInt(1);
                testFound = true;
            }

            if(testFound){
                res = st.executeQuery("SELECT t.mainTestingID AS mid from testings t " +
                                        " JOIN testingstudents ts ON t.testingID=ts.testingID " +
                                        "  WHERE ts.studentID="+studentID+" AND (ts.status=0 OR ts.status=1) AND t.testingDate= DATE(NOW()) " +
                                        "  GROUP BY t.mainTestingID ");
                if(res.next()){
                    mainTestingID = res.getInt(1);
                    int kaz_test_id = 0;
                    int rus_test_id = 0;
                    int tutorID = 0;
                    int testingTime = 0;
                    int tmp_lang = 0;
                    int classID = 0;

                    res1 = st1.executeQuery("SELECT t.testingID AS id, t.tutorID AS tid, "+
                                            " t.testingTime AS tt, t.lang AS la, t.classID AS cl "+
                                            " FROM testings t "+
                                            " WHERE t.mainTestingID = "+mainTestingID);
                    while(res1.next()){
                        tutorID = res1.getInt("tid");
                        testingTime = res1.getInt("tt");
                        tmp_lang = res1.getInt("la");
                        classID = res1.getInt("cl");
                        if(tmp_lang == Constants.KAZ_TEST){
                            kaz_test_id = res1.getInt("id");
                        } else if (tmp_lang == Constants.RUS_TEST){
                            rus_test_id = res1.getInt("id");
                        }
                    }
                    StudentTesting testing = new StudentTesting();
                        testing.lang = lang;
                        testing.setMainTestingID(mainTestingID);
                        testing.setClassID(classID);
                        testing.setKaz_test_ID(kaz_test_id);
                        testing.setRus_test_ID(rus_test_id);
                        testing.setTestingTime(testingTime);
                        testing.setTutorID(tutorID);
                    testings.add(testing);
                }
            } else {
                res = st.executeQuery("SELECT t.mainTestingID AS mid from testings t " +
                        " JOIN testingstudents ts ON t.testingID=ts.testingID " +
                        "  WHERE ts.studentID="+studentID+" AND (ts.status!=0 AND ts.status!=1) AND t.testingDate= DATE(NOW()) " +
                        "  ORDER BY t.testingID DESC LIMIT 1 ");
                if(res.next()){
                    int _mainTestingID = res.getInt(1);
                    int _kaz_test_id = 0;
                    int _rus_test_id = 0;
                    int _tutorID = 0;
                    int _testingTime = 0;
                    int _tmp_lang = 0;
                    int _classID = 0;
                    int _testingID = 0;

                    res1 = st1.executeQuery("SELECT r.testingID FROM registrar r WHERE r.studentid="+studentID+" AND r.mainTestingID="+_mainTestingID);
                    while(res1.next()){
                        _testingID = res1.getInt(1);
                    }

                    res1 = st1.executeQuery("SELECT t.testingID AS id, t.tutorID AS tid, "+
                            " t.testingTime AS tt, t.lang AS la, t.classID AS cl "+
                            " FROM testings t "+
                            " WHERE t.mainTestingID = "+_mainTestingID);
                    while(res1.next()){
                        _tutorID = res1.getInt("tid");
                        _testingTime = res1.getInt("tt");
                        _tmp_lang = res1.getInt("la");
                        _classID = res1.getInt("cl");
                        if(_tmp_lang == Constants.KAZ_TEST){
                            _kaz_test_id = res1.getInt("id");
                        } else if (_tmp_lang == Constants.RUS_TEST){
                            _rus_test_id = res1.getInt("id");
                        }
                    }

                    StudentTesting testing = new StudentTesting();
                    testing.lang = lang;
                    testing.setMainTestingID(_mainTestingID);
                    testing.setTestingID(_testingID);
                    testing.setClassID(_classID);
                    testing.setKaz_test_ID(_kaz_test_id);
                    testing.setRus_test_ID(_rus_test_id);
                    testing.setTestingTime(_testingTime);
                    testing.setTutorID(_tutorID);
                    finishTestings.add(testing);
                }
            }
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res1 != null) res1.close();
                if (res != null) res.close();
                if (st1 != null) st1.close();
                if (st != null) st.close();
                if (con != null) con.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void search(int studentID, int lang){


        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT testings.name as name, " +
                    " testings.starttime as start, " +
                    " testings.finishtime as finish, " +
                    " subjects.name"+ Languages.getLang(lang)+" as sbj " +
                    " FROM testings JOIN testingstudents ON testingstudents.testingID=testings.testingID " +
                    " JOIN registrar ON registrar.testingID=testings.testingID AND registrar.studentID=testingstudents.studentID " +
                    " JOIN studygroups ON studygroups.groupID=registrar.groupID " +
                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                    " WHERE testingstudents.studentID="+studentID+"  " +
                    " AND testings.testingdate=current_date AND finishtime>current_time" +
                    " ORDER BY testings.starttime, testings.name");

            while (res.next()){
                StudentTesting testing = new StudentTesting();
                testing.setTestingStartTime(new Time(res.getString("start")));
                testing.setTestingFinishTime(new Time(res.getString("finish")));
                testing.setTestingName(res.getString("name"));
                testing.setSubjectName(res.getString("sbj"));
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
