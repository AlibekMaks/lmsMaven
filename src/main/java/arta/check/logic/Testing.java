package arta.check.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.common.lock.LockManager;
import arta.settings.logic.Settings;
import arta.tests.questions.Question;
import arta.tests.test.Test;
import arta.tests.reports.html.privateReports.TestResultSubject;
import arta.tests.reports.html.privateReports.TestResultSubjectsManager;
import arta.tests.reports.logic.privateReports.TestReport;
import arta.tests.common.DifficultySelect;
import arta.filecabinet.logic.students.Student;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class Testing implements Serializable {

    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    public int lang;

    public int studentID;
    public int testingID;
    public int classID;
    public int mainTestingID;
    public int state;
    public int mark;
    public ArrayList<Question> questions = new ArrayList<Question>();
    private Map<Integer, List<Question>> questionsByTests = new HashMap<Integer, List<Question>>();
    private Map<Integer, List<Test>> myQuestionsByTests = new HashMap<Integer, List<Test>>();
    public int testingTime;
    int studygroupID;
    int subgroupID;
    public int rightAnswersCount;
    public int passed;
    public int preferredMark;
    public static LockManager studentCheckLockManager = new LockManager();
    public boolean testingIsPassed = false;
    public boolean result_TestingIsPassed = false;
    public java.util.Date startTime = new Date();
    public java.util.Date finishTime = new Date();


    // Xacking
    public boolean xacking = false;
    public Map<Integer, TestSubject> xsubjects = new HashMap<Integer, TestSubject>();



    public int getMainTestingID(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int result = -1;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.mainTestingID as mid FROM testings t WHERE t.testingID = "+testingID);
            if(res.next()){
                result = res.getInt("mid");
            }
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return result;
    }


    public int getPassedStatus(int studentID, int testingID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int result = -1;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.status AS status FROM testingstudents t " +
                                  " WHERE (t.testingID = "+testingID+") AND (t.studentID = "+studentID+")");
            if(res.next()){
                result = res.getInt("status");
            }
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return result;
    }

    public boolean studentIsPassedThisTest(int studentID, int testingID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int mainTestingID = 0;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.mainTestingID as mid FROM testings t WHERE t.testingID = "+testingID);
            if(res.next()){
                mainTestingID = res.getInt(1);
            }


            boolean isPassed = false;
            res = st.executeQuery("SELECT COUNT(*) FROM testings t " +
                                  " JOIN testingstudents t1 ON t.testingID = t1.testingID " +
                                  " WHERE (t1.studentID = "+studentID+") AND (t.mainTestingID = "+mainTestingID+") AND (t1.status = 0)");
            if(res.next()){
                isPassed = (res.getInt(1) != 2);
            }
            return isPassed;
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return false;
    }

    private String getLockString() {
        return studentID + "_" + testingID;
    }

    public void insertHandingOverTesting(HttpServletRequest req, int StudentID, int TestID) throws Exception {
        /**
         System.out.println("getRemoteAddr="+request.getRemoteAddr());
         System.out.println("getRemoteHost="+request.getRemoteHost());
         System.out.println(java.net.InetAddress.getLocalHost().getHostName());
         System.out.println(java.net.InetAddress.getLocalHost());
         System.out.println(java.net.InetAddress.getLocalHost().getAddress());

         for (int i = 0; i < java.net.InetAddress.getLocalHost().getAddress().length; i++)
         System.out.println(java.net.InetAddress.getLocalHost().getAddress()[i]);
         /**/
        /**
         String ip = java.net.InetAddress.getLocalHost().toString();
         String ipAdress = "";
         String hostName = "";
         if(ip.length()>1){
         ipAdress = ip.substring(ip.indexOf("/")+1);
         }
         hostName = ip.substring(0, ip.indexOf("/"));
         /**/

        String ipAddress = req.getHeader("x-forwarded-for");
        if (ipAddress == null) {
            ipAddress = req.getHeader("X_FORWARDED_FOR");
            if (ipAddress == null) {
                ipAddress = req.getRemoteAddr();
            }
        }


        StringTransform trsf = new StringTransform();
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        long resTime = 0;
        int interval = 0;

        java.util.Date dt = new java.util.Date();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        java.text.SimpleDateFormat sdff = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdff.format(dt);

        Format dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        Format datetimeformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = dateformatter.format(dt);
        String endDateTime = "";
        String TestEndTime = "";

        ConnectionPool connectionPool = new ConnectionPool();
        con = connectionPool.getConnection();
        st = con.createStatement();

        try {
            st.execute("DELETE FROM handing_over_testing WHERE enddatetime < '" + currentDateTime + "'");

            res = st.executeQuery("SELECT testingTime as tt " +
                    " FROM testings " +
                    " WHERE testings.testingID = " + testingID);
            if (res.next()) {
                interval = res.getInt("tt");
                //String nowTime = sdf.format(res.getTime("ft"));
                //endDateTime = nowDate+" "+nowTime;
            }

            res = st.executeQuery("SELECT DATE_ADD(NOW(), INTERVAL " + interval + " MINUTE) AS ft");
            if (res.next()) {
                TestEndTime = res.getString("ft");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        }


        try {
            st.execute("INSERT INTO handing_over_testing(StudentID, IP, enddatetime) VALUES(" + StudentID + ", '" + ipAddress + "', '" + TestEndTime + "')");
        } catch (Exception exc) {
            Log.writeLog(exc);
        }


        if (con != null) con.close();
        if (st != null) st.close();
        if (res != null) res.close();
    }

    public void getPreferredMark(int TestingID, int StudentID) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT s.preferredMark as pm FROM test_result tr " +
                    "  JOIN subjects s ON tr.subject_id = s.subjectID " +
                    "  WHERE tr.testing_id = " + TestingID + " AND tr.student_id = " + StudentID);
            if (res.next()) {
                preferredMark = res.getInt("pm");
            }


        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

    }

    public void load() {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            ArrayList<TestingTest> tests = new ArrayList<TestingTest>();
            questions.clear();

//            res = st.executeQuery("SELECT subgroups.groupID as id, " +
//                    " subgroups.subgroupID as subID, " +
//                    " testings.testingTime as t " +
//                    " FROM testings LEFT JOIN registrar ON registrar.testingID=testings.testingID " +

//                    " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
//                    " WHERE testings.testingID="+testingID + " AND registrar.studentID=" + studentID);
            res = st.executeQuery("SELECT testings.testingTime as t " +
                                  " FROM testings " +
                                  " WHERE testings.testingID=" + testingID);
            if (res.next()) {
                studygroupID = -777777777;//res.getInt("id");
                subgroupID = -777777777;//res.getInt("subID");
                testingTime = res.getInt("t");
            }

            if(studentID>0){
                Student student = new Student();
                student.loadById(studentID);
                classID = student.getClassID();
            } else {
                System.out.println("---ERROR--- studentID <= 0");
            }

            res = st.executeQuery("SELECT t.classID AS cid, t.subjectID as sid, s.name"+Languages.getLang(lang)+" AS name, t.mainTestID as mid, t.testID, t.easy, t.middle, t.difficult FROM " +
                                  " testsfortesting t "+
                                  " JOIN testings t1 ON t.testingID = t1.testingID "+
                                  " JOIN subjects s ON t.subjectID = s.subjectID "+
                                  " WHERE (t.testingID = " + testingID + ") AND (t.classID = "+classID+")");
            while (res.next()) {
                TestingTest test = new TestingTest();
                test.classID = res.getInt("cid");
                test.subjectID = res.getInt("sid");
                test.subjectName = res.getString("name");
                test.mainTestingID = res.getInt("mid");
                test.testID = res.getInt("testID");
                test.easy = res.getInt("easy");
                test.middle = res.getInt("middle");
                test.difficult = res.getInt("difficult");
                tests.add(test);
            }


            Timestamp _starttime = null;
            Timestamp _finishtime = null;

            res = st.executeQuery("SELECT starttime, finishtime FROM testreports WHERE studentID = " + studentID+ " AND testingID = "+ testingID);
            while (res.next()) {
                _starttime = res.getTimestamp("starttime");
                _finishtime = res.getTimestamp("finishtime");
            }

            startTime = _starttime;
            finishTime = _finishtime;



            for (int i = 0; i < tests.size(); i++) {
                List<Test> tmps = new ArrayList<Test>();

                Test tmp = new Test(tests.get(i).testID, tests.get(i));
                tmp.loadForCheck(tests.get(i).easy, tests.get(i).middle, tests.get(i).difficult, st, res, 0);
                for (int j = 0; j < tmp.questions.size(); j++) {
                    questions.add(tmp.questions.get(j));
                }
                questionsByTests.put(tmp.testID, tmp.questions);

                if(myQuestionsByTests.containsKey(tests.get(i).subjectID)){
                    tmps = myQuestionsByTests.get(tests.get(i).subjectID);
                }

                tmps.add(tmp);
                myQuestionsByTests.put(tests.get(i).subjectID, tmps);
                tmps = null;
                tmp = null;
            }
            tests = null;



            st.execute("UPDATE testingstudents SET starttime=CURRENT_TIMESTAMP, " +
                       " status=" + STARTED + "  WHERE testingID=" + testingID + " AND studentID=" + studentID + " AND " +
                       " status<>" + FINISHED + " AND status<>" + STARTED + " ");

            st.execute("UPDATE registrar SET status = " + STARTED + ", testingID = " + testingID +
                       " WHERE (mainTestingID = " +mainTestingID + ") AND (studentID=" + studentID + ") AND (status <> "+FINISHED+")");

            res = st.executeQuery("SELECT mark FROM registrar WHERE testingID=" + testingID + " " +
                                  " AND studentID=" + studentID);
            if (res.next()) {
                mark = res.getInt("mark");
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
    }

    public void getTestingIsPassed(int studentID) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT r.isPassed FROM registrar r \n" +
                                  " WHERE (r.studentid ="+studentID+") AND (r.testingID = "+testingID+")");
            if (res.next()) {
                this.testingIsPassed = res.getBoolean("isPassed");
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
    }


    public void myload() {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            ArrayList<TestingTest> tests = new ArrayList<TestingTest>();
            questions.clear();

            res = st.executeQuery("SELECT testings.testingTime as t " +
                    " FROM testings " +
                    " WHERE testings.testingID=" + testingID);
            if (res.next()) {
                studygroupID = -777777777;//res.getInt("id");
                subgroupID = -777777777;//res.getInt("subID");
                testingTime = res.getInt("t");
            }

            res = st.executeQuery("SELECT t1.classID AS cid, t.subjectID as sid, s.name"+Languages.getLang(lang)+" AS name, t.mainTestID as mid, t.testID, t.easy, t.middle, t.difficult FROM " +
                    " testsfortesting t "+
                    " JOIN testings t1 ON t.testingID = t1.testingID "+
                    " JOIN subjects s ON t.subjectID = s.subjectID "+
                    " WHERE t.testingID=" + testingID);
            while (res.next()) {
                TestingTest test = new TestingTest();
                test.classID = res.getInt("cid");
                test.subjectID = res.getInt("sid");
                test.subjectName = res.getString("name");
                test.mainTestingID = res.getInt("mid");
                test.testID = res.getInt("testID");
                test.easy = res.getInt("easy");
                test.middle = res.getInt("middle");
                test.difficult = res.getInt("difficult");
                tests.add(test);
            }

            for (int i = 0; i < tests.size(); i++) {
                Test tmp = new Test(tests.get(i).testID, tests.get(i));
                tmp.loadForCheck(tests.get(i).easy, tests.get(i).middle, tests.get(i).difficult, st, res, 0);
                for (int j = 0; j < tmp.questions.size(); j++) {
                    questions.add(tmp.questions.get(j));
                }
                questionsByTests.put(tmp.testID, tmp.questions);
                tmp = null;
            }
            tests = null;

            st.execute("UPDATE testingstudents SET starttime=CURRENT_TIMESTAMP, " +
                       " status=" + STARTED + "  WHERE testingID=" + testingID + " AND studentID=" + studentID + " AND " +
                       " status<>" + FINISHED + " AND status<>" + STARTED + " ");

            st.execute("UPDATE registrar SET status = " + STARTED + " " +
                       " WHERE (studentID = " + studentID + ") AND (mainTestingID = " + mainTestingID + ") AND (status <> "+FINISHED+")");
            //con.commit();

            res = st.executeQuery("SELECT mark FROM registrar WHERE testingID=" + testingID + " " +
                                  " AND studentID=" + studentID);
            if (res.next()) {
                mark = res.getInt("mark");
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
    }

    public int getStartTime(int studentID, int testingID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        if(studentID <= 0) return -1;
        if(testingID <= 0) return -1;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.start \n" +
                    " FROM testingtime t \n" +
                    " WHERE (t.studentID = "+studentID+") AND (t.testingID = "+testingID+") \n"+
                    " LIMIT 1");
            if (res.next()){
                return res.getInt("start");
            } else {
                return 0;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

        return -1;
    }

    /**
     * @return -2 если тестрование уже пройдено
     *         -1 если время проходления тестирования истекло
     *         0 если можно начинать тестирование
     *         >0 - кол-во минут оставшееся до начала тестирования
     */
    public int isStartPossible() {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int passed = -1;

        String lockString = getLockString();
        studentCheckLockManager.execute(lockString);

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT " +
                    " testings.starttime as starttime, " +
                    " testings.finishtime as finishtime, " +
                    " testingstudents.starttime as studentstart," +
                    " testings.testingTime as  testingTime, " +
                    " status  FROM testingstudents " +
                    " JOIN testings ON testings.testingID=testingstudents.testingID " +
                    " WHERE testings.testingID=" + testingID + " AND studentID=" + studentID + " ");

            if (res.next()) {

                Time testingStartTime = new Time(res.getString("starttime"));
                Time testingFinishTime = new Time(res.getString("finishtime"));

                Time studentStart = new Time(res.getString("studentstart"));

                Time now = new Time();

                this.testingTime = res.getInt("testingTime");

                int status = res.getInt("status");

                if (status == FINISHED) {
                    state = FINISHED;
                    return -2;
                } else if (status == STARTED) {
                    int db_start = getStartTime(studentID, testingID);

//                    passed = new Time().subjsract(studentStart);
                    passed = db_start;

                    this.passed = passed;
//                    if (testingFinishTime.subjsract(studentStart) < testingTime) {
//                        testingTime = testingFinishTime.subjsract(studentStart);
//                    }
                    if (passed > testingTime) {
                        state = FINISHED;
                        return -1;
                    }
                    state = STARTED;
                    return 0;
                } else {
                    int remained = testingStartTime.subjsract(now);
                    if (remained > 0) {
                        return remained;
                    } else {
                        if (now.subjsract(testingFinishTime) > 0) {
                            return -1;
                        } else {
                            if (testingFinishTime.subjsract(now) < testingTime) {
                                testingTime = testingFinishTime.subjsract(now);
                                state = STARTED;
                                return 0;
                            } else {
                                state = STARTED;
                                return 0;
                            }
                        }
                    }
                }
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            studentCheckLockManager.finished(lockString);
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return passed;
    }

    public boolean check(Student student, int lang) {

        String lockString = getLockString();

        System.out.println("ENETERED  " + student.getFullName());
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            studentCheckLockManager.execute(lockString);

            student.loadById(student.getPersonID());

            if (state == STARTED) {

                ConnectionPool connectionPool = new ConnectionPool();
                con = connectionPool.getConnection();
                st = con.createStatement();

                res = st.executeQuery("SELECT status FROM testingstudents WHERE " +
                        " studentID=" + studentID + " AND testingID=" + testingID + " ");
                if (res.next()) {
                    if (res.getInt(1) != STARTED) {
                        System.out.println("RETURNED FALSE " + student.getFullName());
                        return false;
                    }
                }

                con.setAutoCommit(false);

                double rightSum = 0;
                double totalSum = 0;

                int easy = 0;
                int middle = 0;
                int difficult = 0;


                Map<Integer, TestResultSubject> subjectsByTests = new HashMap<Integer, TestResultSubject>();
                int inc = 0;

                // === Xacking =============================================================================
                boolean testIsSaved = this.IsExistsXTestResults(student);
                if(testIsSaved){
                    xacking = true;
                    this.getSubjectsForTesting(student);
                }

                for (int subjectID : myQuestionsByTests.keySet()) {
                    TestResultSubject subject = new TestResultSubject();
                    List<Test> tmps = new ArrayList<Test>();
                    tmps = myQuestionsByTests.get(subjectID);

                    for(int i=0; i < tmps.size(); i++) {
                        for (Question eachQuestion : tmps.get(i).questions) {
                            subject.testID = tmps.get(i).testID;
                            subject.classID = eachQuestion.getClassID();
                            subject.mainTestingID = eachQuestion.getMainTestingID();
                            subject.subjectId = eachQuestion.getSubjectID();
                            res = st.executeQuery("SELECT s.name"+Languages.getLang(lang)+" AS name FROM subjects s WHERE s.subjectID = "+subject.subjectId);
                            if(res.next()){
                                subject.subjectName = res.getString("name");
                            }
                            subject.questionsCount++;
                            totalSum += eachQuestion.getDifficulty();
                            // =========================================================================================
                            // === Xacking =============================================================================
                            // =========================================================================================
                            TestSubject xtestSubject = null;
                            if(xacking){
                                xtestSubject = xsubjects.get(subject.subjectId);
                                if(eachQuestion.getDifficulty() == DifficultySelect.EASY){
                                    eachQuestion.setRightAnswerForQuestion(st, (--xtestSubject.tmp_easy >= 0));
                                } else if(eachQuestion.getDifficulty() == DifficultySelect.MIDDLE){
                                    eachQuestion.setRightAnswerForQuestion(st, (--xtestSubject.tmp_middle >= 0));
                                } else if(eachQuestion.getDifficulty() == DifficultySelect.DIFFICULT){
                                    eachQuestion.setRightAnswerForQuestion(st, (--xtestSubject.tmp_difficult >= 0));
                                }
                            }
                            // =========================================================================================
                            if (eachQuestion.checkAnswer(st)) {
                                subject.rightAnswersCount++;
                                rightSum += eachQuestion.getDifficulty();
                                rightAnswersCount++;
                                if (eachQuestion.getDifficulty() == DifficultySelect.EASY) {
                                    easy++;
                                } else if (eachQuestion.getDifficulty() == DifficultySelect.MIDDLE) {
                                    middle++;
                                } else if (eachQuestion.getDifficulty() == DifficultySelect.DIFFICULT) {
                                    difficult++;
                                }
                            }
                        }
                        inc ++;
                        subjectsByTests.put(inc, subject);
                    }
                }


                /**
                System.out.println("questionsByTests.size() = " + questionsByTests.size());

                for (int testId : questionsByTests.keySet()) {
                    System.out.println("testId = " + testId);
                    TestResultSubject subject = new TestResultSubject();
                    for (Question eachQuestion : questionsByTests.get(testId)) {
                        subject.classID = eachQuestion.getClassID();
                        subject.mainTestingID = eachQuestion.getMainTestingID();
                        subject.subjectId = eachQuestion.getSubjectID();
                        res = st.executeQuery("SELECT s.name"+Languages.getLang(lang)+" AS name FROM subjects s WHERE s.subjectID = "+subject.subjectId);
                        if(res.next()){
                            subject.subjectName = res.getString("name");
                        }

                        System.out.println("eachQuestion.getClassID() = " + eachQuestion.getClassID());
                        System.out.println("eachQuestion.getSubjectID() = " + eachQuestion.getSubjectID());
                        System.out.println("eachQuestion.getMainTestingID() = " + eachQuestion.getMainTestingID());
                        //System.out.println("eachQuestion = " + eachQuestion);
                        System.out.println("eachQuestion.getQuestionID() = " + eachQuestion.getQuestionID());
                        subject.questionsCount++;
                        totalSum += eachQuestion.getDifficulty();
                        if (eachQuestion.checkAnswer(st)) {
                            subject.rightAnswersCount++;
                            rightSum += eachQuestion.getDifficulty();
                            rightAnswersCount++;
                            if (eachQuestion.getDifficulty() == DifficultySelect.EASY) {
                                easy++;
                            } else if (eachQuestion.getDifficulty() == DifficultySelect.MIDDLE) {
                                middle++;
                            } else if (eachQuestion.getDifficulty() == DifficultySelect.DIFFICULT) {
                                difficult++;
                            }
                        }
                    }
                    subject.ball = 159987;
                    subjectsByTests.put(testId, subject);
                }
                **/
                Settings settings = new Settings();
                settings.load();
                if(settings.usesubjectball){
                    settings.loadSubjectsMark();
                }

                //result_TestingIsPassed = true;
                boolean tmp_result = TestResultSubjectsManager.save(subjectsByTests, testingID, studentID, student.isDirector(), settings);

                mark = (int) Math.round((rightSum * (double) Constants.MAX_MARK_VALUE) / totalSum);
                if(!settings.usesubjectball){
                    if(student.isDirector() & mark < settings.attestatThresholdForDirectors){
                        result_TestingIsPassed = false;
                    } else if(!student.isDirector() &  mark < settings.attestatThresholdForEmployee){
                        result_TestingIsPassed = false;
                    } else {
                        result_TestingIsPassed = true;
                    }
                } else {
                    result_TestingIsPassed = tmp_result;
                }

                int mainTestingID = 0;
                res = st.executeQuery("SELECT t.mainTestingID AS mid FROM testings t " +
                        " WHERE t.testingID = " + testingID);
                if(res.next()){
                    mainTestingID = res.getInt("mid");
                }
//                int testingIsPassed_int = 0;
//
//
//                if(settings.usesubjectball){
//                    List<TestResultSubject> resultSubjects = TestResultSubjectsManager.myload(testingID, mainTestingID, student.getPersonID(), lang);
//
//                    for(int i = 0; i <resultSubjects.size(); i++){
//                        TestResultSubject subject = resultSubjects.get(i);
//                        if(subject.rightAnswersPersentage < settings.subjects.get(subject.subjectId)){
//                            testingIsPassed_int ++ ;
//                        }
//                    }
//                } else {
//                    if(student.isDirector() & mark < settings.attestatThresholdForDirectors){
//                        testingIsPassed_int++;
//                    } else if(!student.isDirector() &  mark < settings.attestatThresholdForEmployee){
//                        testingIsPassed_int++;
//                    }
//                }
//
//                testingIsPassed = (testingIsPassed_int == 0);



//                st.execute("UPDATE registrar SET mark=" + mark + " WHERE testingID=" + testingID + " " +
//                        " AND studentID=" + studentID + " ");
                st.execute("UPDATE registrar "+
                           " SET mark = "+mark+", status = " +FINISHED+ ", testingID = "+testingID+", isPassed = "+result_TestingIsPassed+
                           " WHERE (studentID = "+studentID+") AND (mainTestingID = "+mainTestingID+")");

                TestReport testReport = new TestReport(studentID, testingID);
                testReport.makeReport(this, student, st, lang, con, easy, middle, difficult);



                //-------------------------------------------------------------------------------------
                int deletedTestingID = 0;
                res = st.executeQuery("SELECT t.testingID as id FROM testings t " +
                                      " JOIN testingstudents t1 ON t.testingID = t1.testingID " +
                                      " WHERE (t.mainTestingID = "+mainTestingID+") AND (t1.testingID <> "+testingID+")");
                if(res.next()){
                    deletedTestingID = res.getInt("id");
                }

                st.execute("DELETE FROM testingstudents WHERE (testingID = "+deletedTestingID+") AND (studentID = "+student.getPersonID()+")");

                st.execute("UPDATE testingstudents SET status = " + FINISHED + " " +
                           " WHERE studentID=" + student.getPersonID() + " AND testingID=" + testingID + "");

                st.execute("UPDATE registrar SET status = " + FINISHED + " " +
                            " WHERE (studentID = " + student.getPersonID() + ") AND (mainTestingID=" + mainTestingID + ")");
                con.commit();

                if(testIsSaved){
                    this.deleteXTestResults(student);
                }
            }

        } catch (Exception exc) {
            Log.writeLog(exc);
            try {
                con.rollback();
            } catch (Exception e) {
                Log.writeLog(e);
            }
        } finally {
            studentCheckLockManager.finished(lockString);
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        System.out.println("FINISHED " + student.getFullName());
        return true;
    }

    public int getRightAnswersNumber() {
        int count = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isRightAnswer())
                count++;
        }
        return count;
    }

    public boolean studentIsPassedThisTest(int studentID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        boolean result = false;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT r.status AS st FROM registrar r " +
                    " WHERE (r.mainTestingID = "+this.mainTestingID+") AND (r.studentid = "+studentID+")");
            if (res.next()){
                if(res.getInt("st") != 0) result = true;
            }
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

        return result;
    }



    public boolean IsExistsXTestResults(Student student) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        boolean result = false;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.mainTestingID FROM testingsforstudents t "+
                                  " WHERE (t.mainTestingID = "+this.mainTestingID+") AND (t.studentID = " + student.getPersonID() + ") "+
                                  " LIMIT 1");
            if(res.next()){
                result = true;
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
        return result;
    }

    public boolean deleteXTestResults(Student student) {

        Connection con = null;
        Statement st = null;
        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st.execute("DELETE FROM testingsforstudents WHERE (mainTestingID = "+this.mainTestingID+") AND (studentID = " + student.getPersonID() + ")");
        } catch (Exception exc) {
            Log.writeLog(exc);
            return false;
        } finally {
            try {
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return true;
    }


    public boolean saveXTestResults(Student student) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            for (int subjectID : this.xsubjects.keySet()) {
                TestSubject test = (TestSubject)this.xsubjects.get(subjectID);
                    st.execute("INSERT INTO testingsforstudents (mainTestingID,testingID,studentID,subjectID,easy,middle,difficult) "+
                               " VALUES ( "+ this.mainTestingID+", "+ this.testingID+", "+student.getPersonID()+", "+subjectID+", "+test.x_easy+", "+test.x_middle+", "+test.x_difficult+" )");
            }
        } catch (Exception exc) {
            Log.writeLog(exc);
            return false;
        } finally {
            try {
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return true;
    }

    public void getSubjectsForTesting(Student student){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            this.xsubjects.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT * FROM testingsforstudents t  " +
                                  " WHERE (t.mainTestingID = "+this.mainTestingID+") AND (t.testingID = "+this.testingID+") AND (t.studentID = "+student.getPersonID()+")");
            while(res.next()){
                    TestSubject test = new TestSubject();
                    test.subjectID = res.getInt("subjectID");
                    test.tmp_easy = res.getInt("easy");
                    test.tmp_middle = res.getInt("middle");
                    test.tmp_difficult = res.getInt("difficult");

                    this.xsubjects.put(test.subjectID, test);
            }
        } catch (Exception exc) {
            try {
                Log.writeLog(exc);
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception ex) {
                Log.writeLog(ex);
            }
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }
}
