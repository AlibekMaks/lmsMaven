package arta.tests.testing.logic;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.common.lock.LockManager;
import arta.filecabinet.logic.Person;
import arta.tests.common.TestMessages;
import arta.tests.testing.servlet.TestsForTestingServlet;
import arta.tests.testing.servlet.TestingStudentsServlet;
import arta.filecabinet.logic.students.Student;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


public class Testing extends SimpleTesting{


    protected class TestsQuestions{
        public int mainTestID;
        public int testID;
        public int easy;
        public int middle;
        public int difficult;
    }

    protected class SubjectManager{
        public int subjectID;
        public String name;
        public int kaz_test_id;
        public ArrayList <TestsQuestions> kaz_quesions = new ArrayList <TestsQuestions> ();

        public int rus_test_id;
        public ArrayList <TestsQuestions> rus_quesions = new ArrayList <TestsQuestions> ();
    }

    protected class ClassesManager{
        public int testingID_kz;
        public int testingID_ru;
        public int classID;
        public int examID;
        public String examName = "";
        public ArrayList<Integer> ticketIDs = new ArrayList<Integer>();
        public int mainTestingID;
        public ArrayList <SubjectManager> subjects = new ArrayList <SubjectManager> ();

    }

    public static LockManager testingLocker = new LockManager();

    private int tutorID;
    public Person person;
    private int roleID;
    //private boolean isAdministrator = false;
    public int mainTestID = 0;
    public Date testingStartDate;
    public Date testingFinishDate;
    public Message message;
    public int lang;

    public ArrayList <TestForTesting> tests = new ArrayList<TestForTesting>();
    public ArrayList <Integer> students = new ArrayList <Integer> ();
    public ArrayList <Integer> selectedStudents = new ArrayList <Integer> ();
    public ArrayList <ClassesManager> classes = new ArrayList <ClassesManager> ();

    private Connection con = null;
    private Statement st = null;
    private ResultSet res = null;
    private Statement st1 = null;




    public Testing() {
        testingStartTime = new Time();
        testingFinishTime = new Time();
        testingStartDate = new Date();
        testingFinishDate = new Date();
        testingDate = new Date();
    }

    private String getLockString(int tutorID){
        return testingID + "_" + tutorID;
    }

    public void connection_initialize(){
        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            con.setAutoCommit(false);
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void connection_uninitialize(boolean commit){
        try{
            if(commit)con.commit();
            if (con != null) con.close();
            if (st!= null) st.close();
            if (res != null) res.close();
            if (st1!= null) st1.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected boolean checkStudentData(int studentID){
        try{
            // Проверка есть ли у данного студента должность
            res = st.executeQuery("SELECT s.classID FROM students s " +
                    " INNER JOIN classes c ON s.classID = c.classid " +
                    " WHERE s.studentid = "+studentID);
            boolean classesFound = res.next();
            if(!classesFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_CLASS, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }


            // Проверка есть ли у данного студента предметы в должностях
            res = st.executeQuery("SELECT s1.subjectID FROM students s " +
                    " INNER JOIN classes c ON s.classID = c.classid " +
                    " INNER JOIN studygroups s1 ON s.classID = s1.classID " +
                    " WHERE s.studentid = "+studentID);
            boolean subjectsFound = res.next();
            if(!subjectsFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_SUBJECTS_IN_CLASSES, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }


            // Проверка есть ли у данного студента тесты в предметах
            res = st.executeQuery("SELECT s.classID as cl, s1.subjectID as sub, s2.kaz_test_id as kaz, s2.rus_test_id as rus " +
                    " FROM students s " +
                    " JOIN studygroups s1 ON s.classID = s1.classID " +
                    " JOIN subjects s2 ON s1.subjectID = s2.subjectID " +
                    " WHERE (s2.kaz_test_id = 0 OR s2.rus_test_id = 0) AND s.studentid = "+studentID);
            boolean emptyTestsFound = res.next();
            if(emptyTestsFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_TESTS_IN_SUBJECTS, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean checkStudentsData(){
        for (int i = 0 ; i < students.size(); i ++){
            if(!checkStudentData(students.get(i))) return false;
        }
        return true;
    }

    public boolean saveTesting(){
        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        Statement st2 = null;
        ResultSet res = null;
        ResultSet res1 = null;
        ResultSet res2 = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(tutorID);
        testingLocker.execute(lockString);

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();
            con.setAutoCommit(false);


//            if (tests.size() == 0){
//                message.addReason(MessageManager.getMessage(lang, TestMessages.NO_TESTS_SELECTED, null),
//                        "testsfortesting?option=" + TestsForTestingServlet.SEARCH_TO_ADD_OPTION+"&nocache=" + Rand.getRandString());
//            }

            res = st.executeQuery("SELECT  IF(DATE('"+testingDate.getDate()+"') < DATE(NOW()), true, false) AS dt ");
            if(res.next()){
                if(res.getBoolean(1)){
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.addReason(MessageManager.getMessage(lang, TestingMessages.YOU_CAN_NOT_ASSIGN_TESTING_RETROACTIVALY),null);
                    return false;
                }
            }

            if (testingName.equals("")){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.addReason(MessageManager.getMessage(lang, TestingMessages.NAME_OF_TESTING_CAN_NOT_BE_EMPTY),null);
                return false;
            }

            if (students.size() == 0){
                message.addReason(MessageManager.getMessage(lang, TestingMessages.NO_STUDENT_HAS_BEEN_CHOSEN),null);
                        //"testingstudents?option=" + TestingStudentsServlet.SEARCH_TO_ADD_OPTION +"&nocache=" + Rand.getRandString()
            }

            //-------------------------------------------------------------
            //-----Поиск назначений для данных студентов в данное время----
            //-------------------------------------------------------------
            ArrayList <Integer> busyStudents = new ArrayList <Integer> ();
            for (int i = 0; i < students.size(); i ++){
                res = st.executeQuery("SELECT t.mainTestingID AS mid from testings t " +
                                        " JOIN testingstudents ts ON t.testingID=ts.testingID " +
                                        "  WHERE ts.studentID="+students.get(i)+" AND ts.status=0 AND t.testingDate= DATE('"+testingDate.getDate()+"') " +
                                        "  GROUP BY t.mainTestingID " +
                                        "  HAVING COUNT(*)>1");
                if(res.next()){
                    busyStudents.add(students.get(i));
                }
            }

            if(busyStudents.size()>0){ //Найдены студенты для которых уже было назначено тестирование в данное время
                String allBusyStudents = "<br>";
                for (int i = 0; i < busyStudents.size(); i ++){
                    Student student = new Student();
                    student.loadById(busyStudents.get(i));
                    allBusyStudents = allBusyStudents + trsf.getHTMLString(student.getFullName());
                    if(i < (busyStudents.size() - 1)){allBusyStudents+=", ";}
                }
                message.addReason(MessageManager.getMessage(lang, TestingMessages.STUDENTS_FOUND_TO_HAVE_ALREADY_BEEN_ASSIGNED_TESTING_AT_THIS_TIME)+allBusyStudents,null);
            }

//            if (testingName == null || testingName.length() == 0){
//                message.addReason(MessageManager.getMessage(lang, TestingMessages.NO_TESTING_NAME),
//                        "testing?nocache=" + Rand.getRandString());
//            }

//            if (testingFinishTime.subjsract(testingStartTime) <= 0
//                    || testingFinishTime.subjsract(testingStartTime) < testingTime
//                    || testingTime <= 0
//                    || (testingDate.compareTo(new Date()) == 0 && testingStartTime.subjsract(new Time()) < 0)){
//                message.addReason(MessageManager.getMessage(lang, TestingMessages.INVALID_TESTING_TIME_OR_DURATION),
//                        "testing?nocache=" + Rand.getRandString());
//            }


            //----------------------------------------------------------------------------------------------------------
            //------ Поиск пустых тестов для данных студентов ----------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------

            if (message.reasons.size() > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                return false;
            }

            String studentIDs = "";
            for (int i = 0; i < students.size(); i ++){
                studentIDs = studentIDs + students.get(i);
                if(i < students.size() - 1){studentIDs = studentIDs + ", ";}
            }

            boolean tests_found_no_issues_attached = true;
            String test_name = "";
            String test_link = "";
            classes.clear();
            res = st.executeQuery("SELECT DISTINCT s.classID FROM students s " +
                                  " WHERE s.studentid IN ("+studentIDs+")");
            while(res.next()){
                ClassesManager cl = new ClassesManager();
                cl.classID = res.getInt(1);

                boolean examFound = false;


                int subjectsCountInClass = 0;
                res1 = st1.executeQuery("SELECT COUNT(*) FROM studygroups s WHERE s.classID = " + cl.classID);
                if(res1.next()){
                    subjectsCountInClass = res1.getInt(1);
                }
                if(subjectsCountInClass == 0){ // Найдена должность без предметов
                    String link = null;
                    if(person.isAdministrator) link = "class?classID="+cl.classID+"&"+Rand.getRandString();

                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                    message.addReason(MessageManager.getMessage(lang, TestingMessages.FOUND_A_POST_WITHOUT_ITEMS), link);
                    return false;
                }



                res1 = st1.executeQuery("SELECT c.examID FROM classes c WHERE c.classID = " + cl.classID);
                if(res1.next()){
                    cl.examID = res1.getInt("examID");
                }

                res1 = st1.executeQuery("SELECT e.examname FROM exams e WHERE e.examID = " + cl.examID + " LIMIT 1");
                if(res1.next()){
                    examFound = true;
                    cl.examName = res1.getString("examname");
                }

                if(!examFound){
                    String link = null;
                    if(person.isAdministrator) link = "class?classID="+cl.classID+"&"+Rand.getRandString();

                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                    message.addReason(MessageManager.getMessage(lang, TestingMessages.FOUND_A_POSITION_IN_WHICH_THE_EXAM_IS_NOT_SELECTED), link);
                    return false;
                } else {
                    res1 = st1.executeQuery("SELECT t.ticketID FROM tickets t WHERE t.examID = " + cl.examID);
                    while(res1.next()){
                        cl.ticketIDs.add(res1.getInt("ticketID"));
                    }


                    if(cl.ticketIDs.size() == 0){ // Найден экзамен без билетов
                        String link = null;
                        if(person.isAdministrator) link = "exam?examID="+cl.examID+"&"+Rand.getRandString();

                        Properties prop = new Properties();
                        prop.setProperty("name", cl.examName);

                        message.setMessageType(Message.ERROR_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                        message.addReason(MessageManager.getMessage(lang, TestingMessages.FOUND_EMPTY_EXAM, prop), link);
                        return false;
                    }

                }

                res1 = st1.executeQuery("SELECT s1.subjectID as id, s1.name"+Languages.getLang(lang)+" AS name, s1.kaz_test_id as kz, s1.rus_test_id AS ru FROM studygroups s " +
                                        " INNER JOIN subjects s1 ON s.subjectID = s1.subjectID " +
                                        " WHERE s.classID = "+res.getInt(1));
                while(res1.next()){
                    SubjectManager sm = new SubjectManager();
                    sm.name = res1.getString("name");
                    sm.subjectID = res1.getInt("id");
                    sm.kaz_test_id = res1.getInt("kz");
                    sm.rus_test_id = res1.getInt("ru");
                    test_name = "";
                    test_link = "";
                    // Поиск пустых тестов kaz
                    if(sm.kaz_test_id==0 | sm.rus_test_id==0){
                        if(person.isAdministrator){
                            test_link = "subject?subjectID="+sm.subjectID+"&nocache="+Rand.getRandString();
                        } else {
                            test_link = null;
                        }

                        message.setMessageType(Message.ERROR_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                        message.addReason(MessageManager.getMessage(lang, TestingMessages.FOUND_OBJECT_WITH_UNATTACHED_TEST), null);
                        message.addReason(trsf.getHTMLString(sm.name), test_link);
                        return false;
                    }
                    int count = 0;
                    String countSql = "SELECT COUNT(*) as cnt FROM (SELECT t.mainTestID as mt, t.testID AS id, t.easy as ea, t.middle as mi, t.difficult AS di " +
                                      " FROM testing_all_quetions t " +
                                      " WHERE t.mainTestID = " + sm.kaz_test_id + ") as t ";
                    res2 = st2.executeQuery(countSql);
                    if(res2.next()){
                        count = res2.getInt("cnt");
                    }
                    if(count == 0){ // Найден пустой тест
                        if(tests_found_no_issues_attached){
                            message.addReason(MessageManager.getMessage(lang, TestingMessages.TEST_FOUND_NO_ISSUES_ATTACHED), null);
                            tests_found_no_issues_attached = false;
                        }
                        System.out.println("SELECT t.testName as name FROM tests t WHERE t.testID = "+sm.kaz_test_id);

                        res2 = st2.executeQuery("SELECT t.testName as name FROM tests t WHERE t.testID = "+sm.kaz_test_id);
                        if(res2.next()){
                            test_name = res2.getString("name");
                        }

                        if(person.isAdministrator){
                            test_link = "addtests?testID="+sm.kaz_test_id+"&param=new&mainTestID="+sm.kaz_test_id+"&option=3&nocache=" + Rand.getRandString();
                        } else test_link = null;

                        message.addReason(trsf.getHTMLString(test_name), test_link);
                    }


                    res2 = st2.executeQuery("SELECT t.mainTestID as mt, t.testID AS id, t.easy as ea, t.middle as mi, t.difficult AS di " +
                                            " FROM testing_all_quetions t " +
                                            " WHERE t.mainTestID = " + sm.kaz_test_id);
                    while(res2.next()){
                        TestsQuestions kz = new TestsQuestions();
                        kz.mainTestID = res2.getInt("mt");
                        kz.testID = res2.getInt("id");
                        kz.easy = res2.getInt("ea");
                        kz.middle = res2.getInt("mi");
                        kz.difficult = res2.getInt("di");
                        sm.kaz_quesions.add(kz);
                    }

                    // Поиск пустых тестов rus
                    count = 0;
                    countSql = "SELECT COUNT(*) as cnt FROM (SELECT t.mainTestID as mt, t.testID AS id, t.easy as ea, t.middle as mi, t.difficult AS di " +
                               " FROM testing_all_quetions t " +
                               " WHERE t.mainTestID = " + sm.rus_test_id + ") as t ";
                    res2 = st2.executeQuery(countSql);
                    if(res2.next()){
                        count = res2.getInt("cnt");
                    }
                    if(count == 0){ // Найден пустой тест
                        if(tests_found_no_issues_attached){
                            message.addReason(MessageManager.getMessage(lang, TestingMessages.TEST_FOUND_NO_ISSUES_ATTACHED), null);
                            tests_found_no_issues_attached = false;
                        }
                        res2 = st2.executeQuery("SELECT t.testName as name FROM tests t WHERE t.testID = "+sm.rus_test_id);
                        if(res2.next()){
                            test_name = res2.getString("name");
                        }
                        if(person.isAdministrator){
                            test_link = "addtests?testID="+sm.rus_test_id+"&param=new&mainTestID="+sm.rus_test_id+"&option=3&nocache=" + Rand.getRandString();
                        } else test_link = null;
                        message.addReason(trsf.getHTMLString(test_name), test_link);
                    }

                    res2 = st2.executeQuery("SELECT t.mainTestID as mt, t.testID AS id, t.easy as ea, t.middle as mi, t.difficult AS di " +
                                            " FROM testing_all_quetions t " +
                                            " WHERE t.mainTestID = " + sm.rus_test_id);
                    while(res2.next()){
                        TestsQuestions ru = new TestsQuestions();
                            ru.mainTestID = res2.getInt("mt");
                            ru.testID = res2.getInt("id");
                            ru.easy = res2.getInt("ea");
                            ru.middle = res2.getInt("mi");
                            ru.difficult = res2.getInt("di");
                        sm.rus_quesions.add(ru);
                    }
                    cl.subjects.add(sm);
                }
                classes.add(cl);
            }
            //==========================================================================================================
            if (message.reasons.size() > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                return false;
            }
            //==========================================================================================================

            int testingNumber = 0;

            String kazTestingName = testingName; //MessageManager.getMessage(Languages.KAZAKH, TestingMessages.TESTING_LABEL, null);
            String rusTestingName = testingName; //MessageManager.getMessage(Languages.RUSSIAN, TestingMessages.TESTING_LABEL, null);

            int tmp_mainTestingID = 0;
            int tmp_testingID_kz = 0;
            int tmp_testingID_ru = 0;

            st.execute("INSERT INTO testings(tutorID, testingDate, testingTime, lang, classID, name) " +
                       " VALUES ("+tutorID+", '"+testingDate.getDate()+"', "+testingTime+", "+Constants.KAZ_TEST+", null, '"+trsf.getDBString(testingName)+"')",
                       Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next()){
                tmp_testingID_kz = res.getInt(1);
                tmp_mainTestingID = tmp_testingID_kz;

                testingNumber = (tmp_mainTestingID + 1)/2;
//                kazTestingName += "  № "+testingNumber;
//                rusTestingName += "  № "+testingNumber;

//                st.execute("UPDATE testings "+
//                            " Set mainTestingID = "+tmp_mainTestingID+", "+
//                            " namekz = '"+kazTestingName+"', "+
//                            " nameru = '"+rusTestingName+"' "+
//                            " WHERE testingID = "+tmp_testingID_kz);
                st.execute("UPDATE testings "+
                        " Set mainTestingID = "+tmp_mainTestingID+", "+
                        " name = '"+testingName+"' "+
                        " WHERE testingID = "+tmp_testingID_kz);
            }

            st.execute("INSERT INTO testings(tutorID, testingDate, testingTime, lang, classID, name) " +
                       " VALUES ("+tutorID+", '"+testingDate.getDate()+"', "+testingTime+", "+Constants.RUS_TEST+", null, '"+
                        trsf.getDBString(testingName)+"')", Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next()){
                tmp_testingID_ru = res.getInt(1);
                st.execute("UPDATE testings Set mainTestingID = "+tmp_mainTestingID+" WHERE testingID = "+tmp_testingID_ru);
            }



            for (int i = 0; i < classes.size(); i ++){
                ClassesManager cl = null;
                cl = classes.get(i);
                cl.mainTestingID = tmp_mainTestingID;
                cl.testingID_kz = tmp_testingID_kz;
                cl.testingID_ru = tmp_testingID_ru;
                //---------------------------------------------------------------------------------
                //-----Запись казахского варианта--------------------------------------------------
                //---------------------------------------------------------------------------------
                for (int j = 0; j < cl.subjects.size(); j ++){
                    SubjectManager sm = null;
                    sm = cl.subjects.get(j);
                        for (int k = 0; k < sm.kaz_quesions.size(); k ++){
                            TestsQuestions tq = null;
                            tq = sm.kaz_quesions.get(k);
                            st.execute("INSERT INTO testsfortesting(testingID, classID, subjectID, mainTestID, testID, easy, middle, difficult) VALUES ("+
                                       +cl.testingID_kz+", "+cl.classID+", "+ sm.subjectID+", "+tq.mainTestID+", "+ tq.testID+", "+tq.easy+", "+tq.middle+", "+tq.difficult+")");
                        }
                }

                //---------------------------------------------------------------------------------
                //-----Запись русского варианта----------------------------------------------------
                //---------------------------------------------------------------------------------
                for (int j = 0; j < cl.subjects.size(); j ++){
                    SubjectManager sm = null;
                    sm = cl.subjects.get(j);
                    for (int k = 0; k < sm.rus_quesions.size(); k ++){
                        TestsQuestions tq = null;
                        tq = sm.rus_quesions.get(k);
                        st.execute("INSERT INTO testsfortesting(testingID, classID, subjectID, mainTestID, testID, easy, middle, difficult) VALUES ("+
                                   +cl.testingID_ru+", "+cl.classID+", "+ sm.subjectID+", "+tq.mainTestID+", "+tq.testID+", "+tq.easy+", "+tq.middle+", "+tq.difficult+")");
                    }
                }
            }

            //----------------------------------------------------------------------------------------------------------
            //------Запись студентов в таблицу testingstudents ---------------------------------------------------------
            //----------------------------------------------------------------------------------------------------------
            for (int i = 0; i < classes.size(); i ++){
                ClassesManager cl = null;
                cl = classes.get(i);
                res = st.executeQuery("SELECT s.studentid as id FROM students s " +
                                      " WHERE (s.classID = "+cl.classID+") AND s.studentid IN ("+studentIDs+")");
                while(res.next()){
                    st1.execute("INSERT INTO testingstudents (testingID, studentID, status) "+
                                " VALUES ("+cl.testingID_kz+", "+res.getInt("id")+", 0)");

                    st1.execute("INSERT INTO testingstudents (testingID, studentID, status) "+
                                " VALUES ("+cl.testingID_ru+", "+res.getInt("id")+", 0)");


                    //---- запись данных в таблицу "registrar" -----
                    Random randomGenerator = new Random();
                    int randomInt = randomGenerator.nextInt(cl.ticketIDs.size());
                    int randomTicketID = cl.ticketIDs.get(randomInt);
                    st1.execute("INSERT INTO registrar (studentid, groupID, markdate, mark, marktype, mainTestingID, testingID, examID, ticketID) "+
                                " VALUES ("+res.getInt("id")+", null, '"+testingDate.getDate()+"', null, 2, "+cl.mainTestingID+", null, "+cl.examID+", "+randomTicketID+")");

//                    st1.execute("INSERT INTO registrar (studentid, groupID, markdate, mark, marktype, mainTestingID, testingID) "+
//                                " VALUES ("+res.getInt("id")+", 0, '"+testingDate.getDate()+"', null, 2, "+cl.mainTestingID+", "+cl.testingID_ru+")");
                }
            }


//
//
//            int kazTestingID = 0;
//            int rusTestingID = 0;
//            //testingID, mainTestingID, tutorID, testingDate, testingTime, lang
//            // Записать в testings казахский вариант
//
//
//            kazTestingID = mainTestingID; // testingID для казахского теста
//
//
//            st.execute("UPDATE testings SET mainTestingID = " +mainTestingID+ " WHERE testingID = " + mainTestingID);
//
//
//            for (int i = 0; i < students.size(); i ++){
//                st.execute("INSERT INTO testingstudents(testingID, studentID, status) " +
//                           " VALUES ("+kazTestingID+", "+students.get(i)+", 0)");
//
//                st.execute("INSERT INTO registrar(studentID, groupID, markdate, marktype, testingID) " +
//                            " VALUES ( " +students.get(i)+", null, '"+testingDate.getDate()+"', "
//                            + Constants.TEST_MARK+", "+kazTestingID);
//            }
//
//
//
//
//
//
//            testingID = 0;
//            st.execute("INSERT INTO testings(tutorID, testingdate, finishtime) " +
//                       " VALUES ("+tutorID+", '"+testingDate.getDate()+"', "+testingTime+")", Statement.RETURN_GENERATED_KEYS);
//            res = st.getGeneratedKeys();
//            if (res.next()) testingID = res.getInt(1);
//
//            int questionsCount = 0;
//            for (int i=0; i<tests.size(); i++){
//                st.execute("INSERT INTO testsfortesting(testingID, testID, easy, middle, difficult) VALUES " +
//                        " ("+testingID+", "+tests.get(i).testID+", "+tests.get(i).easy+", "+tests.get(i).middle+", " +
//                        " "+tests.get(i).difficult+")");
//                questionsCount += tests.get(i).easy + tests.get(i).middle + tests.get(i).difficult;
//            }
//
//            if (questionsCount == 0){
//                message.setMessageType(Message.ERROR_MESSAGE);
//                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.NO_TESTS_SELECTED, null));
//                return false;
//            }
//
//            for (int i = 0; i < students.size(); i ++){
//                st.execute("INSERT INTO testingstudents(testingID, studentID, status) " +
//                        " VALUES ("+testingID+", "+students.get(i)+", 0)");
//
//                st.execute("INSERT INTO registrar(studentID, groupID, markdate, marktype, testingID) " +
//                        " SELECT " +
//                        " "+students.get(i)+", subgroups.groupID, '"+testingDate.getDate()+"', "
//                        + Constants.TEST_MARK+", "+testingID+" " +
//                        //" FROM subgroups WHERE subgroupID="+students.get(i).subgroupID+" " +
//                        " FROM students LEFT JOIN classes ON classes.classID=students.classID " +
//                        " LEFT JOIN studygroups ON studygroups.classID=classes.classID " +
//                        " JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
//                        " JOIN studentgroup ON studentgroup.studentID=students.studentID AND studentgroup.groupID=subgroups.subgroupID " +
//                        " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
//                        " WHERE students.studentid=" + students.get(i)
//                );
//            }

            con.commit();
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_SUCCESSFULLY_APPOINTED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con!=null && !con.getAutoCommit()) con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            testingLocker.finished(lockString);
            try{
                if (res != null) res.close();
                if (res1 != null) res1.close();
                if (res2 != null) res2.close();
                if (st!= null) st.close();
                if (st1!= null) st1.close();
                if (st2!= null) st2.close();
                if (con != null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean saveTestingForStudent(int studentID){
        StringTransform trsf = new StringTransform();

        try{
            message = new Message();

            // Проверка есть ли у данного студента должность
            res = st.executeQuery("SELECT s.classID FROM students s " +
                                  " INNER JOIN classes c ON s.classID = c.classid " +
                                  " WHERE s.studentid = "+studentID);
            boolean classesFound = res.next();
            if(!classesFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_CLASS, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }


            // Проверка есть ли у данного студента предметы в должностях
            res = st.executeQuery("SELECT s1.subjectID FROM students s " +
                                  " INNER JOIN classes c ON s.classID = c.classid " +
                                  " INNER JOIN studygroups s1 ON s.classID = s1.classID " +
                                  " WHERE s.studentid = "+studentID);
            boolean subjectsFound = res.next();
            if(!subjectsFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_SUBJECTS_IN_CLASSES, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }


            // Проверка есть ли у данного студента тесты в предметах
            res = st.executeQuery("SELECT s.classID as cl, s1.subjectID as sub, s2.kaz_test_id as kaz, s2.rus_test_id as rus " +
                                  " FROM students s " +
                                  " JOIN studygroups s1 ON s.classID = s1.classID " +
                                  " JOIN subjects s2 ON s1.subjectID = s2.subjectID " +
                                  " WHERE (s2.kaz_test_id = 0 OR s2.rus_test_id = 0) AND s.studentid = "+studentID);
            boolean emptyTestsFound = res.next();
            if(emptyTestsFound){
                message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
                message.setMessage(MessageManager.getMessage(lang,  TestMessages.FOUND_STUDENTS_WITHOUT_TESTS_IN_SUBJECTS, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }



            // Запись данных в базу
            res = st.executeQuery("SELECT s.classID as cl, s1.subjectID as sub, s2.kaz_test_id as kaz, s2.rus_test_id as rus " +
                                    " FROM students s " +
                                    " JOIN studygroups s1 ON s.classID = s1.classID " +
                                    " JOIN subjects s2 ON s1.subjectID = s2.subjectID " +
                                    " WHERE s.studentid = "+studentID);
            while(res.next()){
                st1.execute("INSERT INTO testingsforstudents(studentID, classID, subjectID, kaz_test_id, rus_test_id, startDate, finishDate, testingTime, tutorID) "+
                            " VALUES ("+studentID+", "+res.getInt("cl") +", "+res.getInt("sub")+", "+res.getInt("kaz")+", "+res.getInt("rus")+", "+
                            "'2014-09-29', '2014-09-30', 100, "+tutorID+")");
            }

            //con.commit();
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_SUCCESSFULLY_APPOINTED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }
    }


    public boolean mysave(Message message, int lang, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(tutorID);
        testingLocker.execute(lockString);

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            st.execute("DELETE FROM testing_all_quetions WHERE mainTestID="+mainTestID);

            for (int i=0; i<tests.size(); i++){
                st.execute("INSERT INTO testing_all_quetions(mainTestID, testID, easy, middle, difficult) VALUES " +
                        " ("+mainTestID+", "+tests.get(i).testID+", "+tests.get(i).easy+", "+tests.get(i).middle+", " +tests.get(i).difficult+")");
            }
//            for (int i=0; i<tests.size(); i++){
//                st.execute("INSERT INTO testsfortesting(testingID, testID, easy, middle, difficult, mainTestID) VALUES " +
//                        " ("+mainTestID+", "+tests.get(i).testID+", "+tests.get(i).easy+", "+tests.get(i).middle+", " +
//                        " "+tests.get(i).difficult+", "+mainTestID+")");
//            }
            con.commit();
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_SUCCESSFULLY_APPOINTED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con!=null && !con.getAutoCommit())
                    con.commit();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            testingLocker.finished(lockString);
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }


    public boolean save(Message message, int lang, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(tutorID);
        testingLocker.execute(lockString);

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);


            if (tests.size() == 0){
                message.addReason(MessageManager.getMessage(lang, TestMessages.NO_TESTS_SELECTED, null),
                        "testsfortesting?option=" + TestsForTestingServlet.SEARCH_TO_ADD_OPTION+"&nocache=" + Rand.getRandString());
            }

            if (students.size() == 0){
                message.addReason(MessageManager.getMessage(lang, TestingMessages.NO_STUDENT_HAS_BEEN_CHOSEN),
                        null); //"testingstudents?option=" + TestingStudentsServlet.SEARCH_TO_ADD_OPTION +"&nocache=" + Rand.getRandString()
            }

            if (testingName == null || testingName.length() == 0){
                message.addReason(MessageManager.getMessage(lang, TestingMessages.NO_TESTING_NAME),
                        "testing?nocache=" + Rand.getRandString());
            }

            if (testingFinishTime.subjsract(testingStartTime) <= 0
                    || testingFinishTime.subjsract(testingStartTime) < testingTime
                    || testingTime <= 0
                    || (testingDate.compareTo(new Date()) == 0 && testingStartTime.subjsract(new Time()) < 0)){
                message.addReason(MessageManager.getMessage(lang, TestingMessages.INVALID_TESTING_TIME_OR_DURATION),
                        "testing?nocache=" + Rand.getRandString());
            }

            StringBuffer studentsID = new StringBuffer();
            for (int i = 0; i < students.size(); i ++){
                if (i > 0)
                    studentsID.append(", ");
                studentsID.append(students.get(i));
            }

            if (studentsID.length() > 0){
                res = st.executeQuery("SELECT " +
                        " students.lastname as ln, " +
                        " students.firstname as fn, " +
                        " students.patronymic as p  " +
                        " FROM testings JOIN testingstudents " +
                        " ON testingstudents.testingID=testings.testingID " +
                        " JOIN students ON students.studentID=testingstudents.studentID " +
                        " WHERE testings.testingdate='"+testingDate.getDate()+"' " +
                        " AND testings.starttime<='"+testingFinishTime.getValue()+"' " +
                        " AND testings.finishtime>='"+testingStartTime.getValue()+"' " +
                        " AND (testings.testingID<>"+testingID+" OR "+testingID+"=0) " +
                        " AND students.studentID IN ("+studentsID+")");
                StringBuffer busyStudents = new StringBuffer();
                while (res.next()){
                    if (busyStudents.length() > 0){
                        busyStudents.append(", ");
                    }
                    busyStudents.append(Student.extractName(res.getString("ln"), res.getString("fn"),
                            res.getString("p")));
                }
                if (busyStudents.length() > 0){
                    Properties prop = new Properties();
                    prop.setProperty("students", busyStudents.toString());
                    message.addReason(MessageManager.getMessage(lang,
                            TestingMessages.FOLLOWING_STUDENTS_HAS_TESTING_ON_THIS_TIME, prop),
                            "testingstudents?option=" + TestingStudentsServlet.CHANGE_OPTION
                                    + "&nocache=" + Rand.getRandString());
                }
            }

            if (message.reasons.size() > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                return false;
            }

            if (testingID > 0){
                st.execute("UPDATE testings SET testingdate='"+testingDate.getDate()+"', " +
                        " testingtime="+testingTime+", starttime='"+testingStartTime.getValue()+"', " +
                        " name='"+trsf.getDBString(testingName)+"' , " +
                        " finishtime='"+testingFinishTime.getValue()+"' WHERE testingID="+testingID+"");
                st.execute("DELETE FROM testingstudents WHERE testingID="+testingID);
                st.execute("DELETE FROM testsfortesting WHERE testingID="+testingID);
                st.execute("DELETE FROM registrar WHERE testingID="+testingID);
            } else {
                st.execute("INSERT INTO testings(tutorID, testingdate, " +
                        " testingtime, starttime, name, finishtime) " +
                        " VALUES ("+tutorID+", '"+testingDate.getDate()+"', "+testingTime+"," +
                        " '"+ testingStartTime.getValue()+"', " +
                        " '"+trsf.getDBString(testingName)+"', '"+testingFinishTime.getValue()+"')",
                    Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next())
                    testingID = res.getInt(1);
            }

            int questionsCount = 0;
            for (int i=0; i<tests.size(); i++){
                st.execute("INSERT INTO testsfortesting(testingID, testID, easy, middle, difficult) VALUES " +
                        " ("+testingID+", "+tests.get(i).testID+", "+tests.get(i).easy+", "+tests.get(i).middle+", " +
                        " "+tests.get(i).difficult+")");
                questionsCount += tests.get(i).easy + tests.get(i).middle + tests.get(i).difficult;
            }

            if (questionsCount == 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.NO_TESTS_SELECTED, null));
                return false;
            }

            for (int i = 0; i < students.size(); i ++){
                st.execute("INSERT INTO testingstudents(testingID, studentID, status) " +
                        " VALUES ("+testingID+", "+students.get(i)+", 0)");

                st.execute("INSERT INTO registrar(studentID, groupID, markdate, marktype, testingID) " +
                        " SELECT " +
                        " "+students.get(i)+", subgroups.groupID, '"+testingDate.getDate()+"', "
                        + Constants.TEST_MARK+", "+testingID+" " +
                        //" FROM subgroups WHERE subgroupID="+students.get(i).subgroupID+" " +
                        " FROM students LEFT JOIN classes ON classes.classID=students.classID " +
                        " LEFT JOIN studygroups ON studygroups.classID=classes.classID " +
                        " JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                        " JOIN studentgroup ON studentgroup.studentID=students.studentID AND studentgroup.groupID=subgroups.subgroupID " +
                        " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                        " WHERE students.studentid=" + students.get(i)
                        );
            }

            con.commit();
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_SUCCESSFULLY_APPOINTED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con!=null && !con.getAutoCommit())
                    con.commit();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang,  TestMessages.TESTING_WAS_NOT_APPOINTED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            testingLocker.finished(lockString);
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }
              
    public void myload(int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
//
//            res = st.executeQuery("SELECT testingdate as td, " +
//                    " testingtime as tt," +
//                    " hour(starttime) as start_hour,  " +
//                    " minute(starttime) as start_minute," +
//                    " hour(finishtime) as finish_hour, " +
//                    " minute(finishtime) as finish_minute,  " +
//                    " name as n " +
//                    " FROM testings " +
//                    " WHERE testings.testingID="+testingID+"");
//            if (res.next()){
//                testingDate = new Date(res.getString("td"), Date.FROM_DB_CONVERT);
//                testingTime = res.getInt("tt");
//                testingName = res.getString("n");
//                testingStartTime.hour = res.getInt("start_hour");
//                testingStartTime.minute = res.getInt("start_minute");
//                testingFinishTime.hour = res.getInt("finish_hour");
//                testingFinishTime.minute = res.getInt("finish_minute");
//            }
//
//
//            res = st.executeQuery("SELECT t.testID as tid, t.easy as ea, t.middle as mi, t.difficult as di FROM testing_all_quetions t "+
//                                  " WHERE mainTestID = "+mainTestID);
//            while (res.next()){
//                TestForTesting test = null;
//                test = getTest(res.getInt("tid"));
//                if (test == null){
//                    test = new TestForTesting();
//                    test.alldifficult = res.getInt("di");
//                    test.alleasy = res.getInt("ea");
//                    test.allmiddle = res.getInt("mi");
//                    test.testID = res.getInt("tid");
//                    test.name = "";
//                    tests.add(test);
//                }
//            }


            System.out.println("myload.....\nSELECT tests.easy as alleasy, " +
                    " tests.middle as allmid, " +
                    " tests.difficult as alldif, " +
                    " testing_all_quetions.easy as easy, " +
                    " testing_all_quetions.middle as mid, " +
                    " testing_all_quetions.difficult as dif, " +
                    " testing_all_quetions.testID as id, " +
                    " tests.testname as name " +
                    " FROM testing_all_quetions LEFT JOIN tests ON tests.testID=testing_all_quetions.testID " +
                    " WHERE testing_all_quetions.mainTestID="+mainTestID);

            res = st.executeQuery("SELECT tests.easy as alleasy, " +
                    " tests.middle as allmid, " +
                    " tests.difficult as alldif, " +
                    " testing_all_quetions.easy as easy, " +
                    " testing_all_quetions.middle as mid, " +
                    " testing_all_quetions.difficult as dif, " +
                    " testing_all_quetions.testID as id, " +
                    " tests.testname as name " +
                    " FROM testing_all_quetions LEFT JOIN tests ON tests.testID=testing_all_quetions.testID " +
                    " WHERE testing_all_quetions.mainTestID="+mainTestID);

            while (res.next()){
                TestForTesting test = null;
                test = getTest(res.getInt("id"));
                if (test == null){
                    test = new TestForTesting();
                        test.alldifficult = res.getInt("alldif");
                        test.alleasy = res.getInt("alleasy");
                        test.allmiddle = res.getInt("allmid");
                        test.easy = res.getInt("easy");
                        test.middle = res.getInt("mid");
                        test.difficult = res.getInt("dif");
                        test.testID = res.getInt("id");
                        test.name = res.getString("name");
                    tests.add(test);
                }

            }

//            res = st.executeQuery("SELECT students.lastname as ln, " +
//                    " students.patronymic as p, " +
//                    " students.firstname as fn, " +
//                    " students.studentID as id, " +
//                    " classes.classname as classname, " +
//                    " subjects.name"+Languages.getLang(lang)+" as subject, " +
//                    " subgroups.subgroupID as subID  " +
//                    " FROM testingstudents JOIN registrar ON registrar.studentID=testingstudents.studentID " +
//                                            " AND registrar.testingID=testingstudents.testingID " +
//                    " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
//                    " JOIN studygroups ON studygroups.groupID=registrar.groupID " +
//                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
//                    " JOIN students ON students.studentID=testingstudents.studentID " +
//                    " JOIN studentgroup ON studentgroup.studentID=testingstudents.studentID " +
//                                            " AND studentgroup.groupID=subgroups.subgroupID " +
//                    " JOIN classes ON classes.classID=students.classID " +
//                    " WHERE testingstudents.testingID=" + testingID);
//            while (res.next()){
////                TestingStudent student = new TestingStudent();
////                student.studentID = res.getInt("id");
////                student.studentName = Student.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
////                student.className = res.getString("classname");
////                student.subjectName = res.getString("subject");
////                student.subgroupID = res.getInt("subID");
//                students.add(res.getInt("id"));
//            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void load(int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT testingdate as td, " +
                    " testingtime as tt," +
                    " hour(starttime) as start_hour,  " +
                    " minute(starttime) as start_minute," +
                    " hour(finishtime) as finish_hour, " +
                    " minute(finishtime) as finish_minute,  " +
                    " name as n " +
                    " FROM testings " +
                    " WHERE testings.testingID="+testingID+"");
            if (res.next()){
                testingDate = new Date(res.getString("td"), Date.FROM_DB_CONVERT);
                testingTime = res.getInt("tt");
                testingName = res.getString("n");
                testingStartTime.hour = res.getInt("start_hour");
                testingStartTime.minute = res.getInt("start_minute");
                testingFinishTime.hour = res.getInt("finish_hour");
                testingFinishTime.minute = res.getInt("finish_minute");
            }

            res = st.executeQuery("SELECT tests.easy as alleasy, " +
                    " tests.middle as allmid, " +
                    " tests.difficult as alldif, " +
                    " testsfortesting.easy as easy, " +
                    " testsfortesting.middle as mid, " +
                    " testsfortesting.difficult as dif, " +
                    " tests.testID as id, " +
                    " tests.testname as name " +
                    " FROM testsfortesting LEFT JOIN tests ON tests.testID=testsfortesting.testID " +
                    " WHERE testsfortesting.testingID="+testingID);
            while (res.next()){
                TestForTesting test = new TestForTesting();
                test.alldifficult = res.getInt("alldif");
                test.alleasy = res.getInt("alleasy");
                test.allmiddle = res.getInt("allmid");
                test.easy = res.getInt("easy");
                test.middle = res.getInt("mid");
                test.difficult = res.getInt("dif");
                test.testID = res.getInt("id");
                test.name = res.getString("name");
                tests.add(test);
            }

            res = st.executeQuery("SELECT students.lastname as ln, " +
                    " students.patronymic as p, " +
                    " students.firstname as fn, " +
                    " students.studentID as id, " +
                    " classes.classname as classname, " +
                    " subjects.name"+Languages.getLang(lang)+" as subject, " +
                    " subgroups.subgroupID as subID  " +
                    " FROM testingstudents JOIN registrar ON registrar.studentID=testingstudents.studentID " +
                    " AND registrar.testingID=testingstudents.testingID " +
                    " JOIN subgroups ON subgroups.groupID=registrar.groupID " +
                    " JOIN studygroups ON studygroups.groupID=registrar.groupID " +
                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                    " JOIN students ON students.studentID=testingstudents.studentID " +
                    " JOIN studentgroup ON studentgroup.studentID=testingstudents.studentID " +
                    " AND studentgroup.groupID=subgroups.subgroupID " +
                    " JOIN classes ON classes.classID=students.classID " +
                    " WHERE testingstudents.testingID=" + testingID);
            while (res.next()){
//                TestingStudent student = new TestingStudent();
//                student.studentID = res.getInt("id");
//                student.studentName = Student.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
//                student.className = res.getString("classname");
//                student.subjectName = res.getString("subject");
//                student.subgroupID = res.getInt("subID");
                students.add(res.getInt("id"));
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }


    public void setTutorID(int tutorID) {
        this.tutorID = tutorID;
    }

    public String getTestingName(int lang) {
        if (testingName == null) return ""; //MessageManager.getMessage(lang, TestMessages.TESTING, null);
        return testingName;
    }

    public int getTutorID() {
        return tutorID;
    }

    public void setRoleID(int roleID){
        this.roleID = roleID;
        //person.isAdministrator = ((roleID & Constants.ADMIN) > 0);
    }

    public int getRoleID(){
        return this.roleID;
    }

    public String getTests(){
        StringBuffer result = new StringBuffer();
        for (int i=0; i<tests.size(); i++){
            if (i > 0)
                result.append(", ");
            result.append(tests.get(i).name);
        }
        return result.toString();
    }

    public void deleteTest(int testID, int mainTestID){
        for (int i=0; i<tests.size(); i++){
            if (tests.get(i).testID == testID){
                tests.remove(i);
                deleteTestsForTestingsId(mainTestID, testID);
            }
        }
    }

    public TestForTesting getTest(int testID){
        for (int i=0; i<tests.size(); i++){
            if (tests.get(i).testID == testID)
                return tests.get(i);
        }
        return null;
    }

    public void addStudent(Integer studentId){

        for (int i = 0 ; i < students.size(); i ++){
            if (students.get(i) == studentId){
                return;
            }
        }

        students.add(studentId);

    }

    public void deleteStudent(int studentID){
        for (int i = 0 ; i < students.size(); i ++){
            if (students.get(i) == studentID){
                students.remove(i);
                return;
            }
        }
    }

    public boolean deleteTestsForTestingsId(int mainTestID, int testID){
        Connection con = null;
        Statement st = null;
        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            st.execute("DELETE FROM testing_all_quetions WHERE (testID=" + testID+") AND (mainTestID = "+mainTestID+")");
            System.out.println("mainTestID = " + mainTestID+"  testID="+testID);
            System.out.println("sql = "+"DELETE FROM testing_all_quetions WHERE (testID=" + testID+") AND (mainTestID = "+mainTestID+")");
            return true;
        } catch(Exception exc){
            Log.writeLog(exc);
            return false;
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete(int lang, Message message, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(tutorID);
        testingLocker.execute(lockString);
        
        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT starttime >= current_time " +
                    " FROM testings WHERE testingID=" + testingID);
            if (res.next() && res.getBoolean(1)){
                message.setMessage(MessageManager.getMessage(lang, TestingMessages.TESTING_HAS_BEEN_STARTED_CANNOT_BE_CANCELLED));
                message.setMessageHeader(MessageManager.getMessage(lang, TestingMessages.TESTING_HAS_NOT_BEEN_CANCELLED));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }

            st.execute("DELETE FROM testingstudents WHERE testingID=" + testingID);
            st.execute("DELETE FROM testsfortesting WHERE testingID=" + testingID);
            st.execute("DELETE FROM registrar WHERE testingID=" + testingID);
            st.execute("DELETE FROM testings WHERE testingID=" + testingID);

            message.setMessageHeader(MessageManager.getMessage(lang, TestingMessages.TESTING_HAS_BEEN_CANCELLED));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch(Exception exc){
            message.setMessageHeader(MessageManager.getMessage(lang, TestingMessages.TESTING_HAS_BEEN_CANCELLED));
            message.setMessageType(Message.ERROR_MESSAGE);
            Log.writeLog(exc);
            return false;
        } finally{

            testingLocker.finished(lockString);

            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public boolean studentIDisFound(int studentid){
        for (int i = 0; i < students.size(); i ++){
            if(students.get(i)==studentid){
                return true;
            }
        }
        return false;
    }

    public String getTestName(int testID){
        String name = "";
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.testName AS name FROM tests t WHERE t.testID = " + testID);
            if(res.next()){
                name = res.getString("name");
            }
        } catch (Exception exc){
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
        return name;
    }


    public Date getTestingDate(){
        return testingDate;
    }
}
