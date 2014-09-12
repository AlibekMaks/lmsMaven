package arta.common.servlet;


import arta.check.logic.TestSubject;
import arta.check.logic.Testing;
import arta.common.html.handler.PageGenerator;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.dbchecker.html.dbCheckerMainHandler;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.login.logic.Access;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.TestForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

public class DBChecker extends HttpServlet {

    public Student student;
    //public Testing testing;
    int lang;


    protected void service (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }


            Message message = new Message();

            int studentID = extractor.getInteger(request.getParameter("studentID"));
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            //int testingID = extractor.getInteger(request.getParameter("testingID"));
            int language = extractor.getInteger(request.getParameter("language"));
            boolean btnGenerate = (extractor.getRequestString(request.getParameter("generate")) != null && !extractor.getRequestString(request.getParameter("generate")).equals("") );
            boolean btnSave = (extractor.getRequestString(request.getParameter("save")) != null && !extractor.getRequestString(request.getParameter("save")).equals("") );
            Person person = (Person) session.getAttribute("person");
            this.lang = extractor.getInteger(session.getAttribute("lang"));

            if(language<1 | language>2) language = 1;

            Testing testing = new Testing();
                testing.mainTestingID = mainTestingID;
                testing.testingID = mainTestingID + (language-1);
                testing.studentID = studentID;
                testing.xacking = true;
                //testing.load();

            student = new Student();
            student.loadById(studentID);

            boolean testIsPassed = testing.studentIsPassedThisTest(studentID);
            boolean testIsSaved = testing.IsExistsXTestResults(student);

            if(!testIsPassed && !testIsSaved){
                getSubjectsForTesting(testing);
                if(btnSave){
                    getValues(request, extractor, testing);

                    if(testing.saveXTestResults(student)){
                        testIsSaved = true;
                        message.setMessageType(Message.INFORMATION_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.PARAMETER_HAVE_BEEN_SAVED, null));
                    } else {
                        message.setMessageType(Message.ERROR_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.WHILE_SAVING_THE_ERROR_OCCRED, null));
                    }
                } else if(btnGenerate){
                    getValues(request, extractor, testing);

                    testing.load();
                    testing.state = testing.STARTED;
                    if (testing.check(student, lang)) {
                        message.setMessageType(Message.INFORMATION_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.GENERATION_WAS_SUCCESSFUL, null));
                        testIsPassed = true;
                    } else {
                        message.setMessageType(Message.ERROR_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.WHEN_GENERATING_AN_ERROR_OCCURRED_, null));
                    }
                }
            } else {
                if(testIsPassed){
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.THIS_BIDDER_HAS_PASSED_TESTING, null));
                } else if(testIsSaved){
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.PARAMETER_HAVE_BEEN_SAVED, null));
                }
            }

            new PageGenerator().writeHtmlPage(new dbCheckerMainHandler(testing, student, testIsPassed, testIsSaved, lang, person.getRoleID(),
                    getServletContext(), message, language), pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc) {
            Log.writeLog(exc);
        }
    }

    public void getSubjectsForTesting(Testing testing){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t1.subjectID, s1.name"+Languages.getLang(lang)+" AS name, t1.easy, t1.middle, t1.difficult FROM testingstudents t " +
                    " INNER JOIN testsfortesting t1 ON t.testingID = t1.testingID " +
                    " INNER JOIN students s ON t.studentID = s.studentid " +
                    " INNER JOIN subjects s1 ON t1.subjectID = s1.subjectID " +
                    " WHERE (t.testingID = "+testing.testingID+") AND (t.studentID = "+student.getPersonID()+") AND (t1.classID = s.classID)");
            while(res.next()){
                if(testing.xsubjects.containsKey(res.getInt("subjectID"))){
                    TestSubject test = (TestSubject)testing.xsubjects.get(res.getInt("subjectID"));
                        test.easy += res.getInt("easy");
                        test.middle += res.getInt("middle");
                        test.difficult += res.getInt("difficult");

                        test.x_easy = test.easy;
                        test.x_middle = test.middle;
                        test.x_difficult = test.difficult;

                        test.tmp_easy = test.easy;
                        test.tmp_middle = test.middle;
                        test.tmp_difficult = test.difficult;
                    //testing.xsubjects.put(test.subjectID, test);
                } else {
                    TestSubject test = new TestSubject();
                        test.subjectID = res.getInt("subjectID");
                        test.subjectName = res.getString("name");
                        test.easy = res.getInt("easy");
                        test.middle = res.getInt("middle");
                        test.difficult = res.getInt("difficult");

                        test.x_easy = test.easy;
                        test.x_middle = test.middle;
                        test.x_difficult = test.difficult;

                        test.tmp_easy = test.easy;
                        test.tmp_middle = test.middle;
                        test.tmp_difficult = test.difficult;
                    testing.xsubjects.put(test.subjectID, test);
                }
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


    public void getValues(HttpServletRequest request, DataExtractor extractor, Testing testing){

        int pos = 0;
        int subjectID = 0;
        int subjectType = 0;

        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()){
            String original_name = (String) en.nextElement();
            String name = original_name;

            if (name.length()>6 && name.substring(0, 4).equals("var_")){
                name = name.substring(4, name.length());
                if(name.length()>=3 && (name.indexOf("_") > 0)){
                    pos = name.indexOf("_");

                    subjectID = Integer.parseInt(name.substring(0, pos));
                    subjectType = Integer.parseInt(name.substring(pos+1, name.length()));

                    if(testing.xsubjects.containsKey(subjectID)){
                        TestSubject test = (TestSubject)testing.xsubjects.get(subjectID);
                        int value = extractor.getInteger(request.getParameter(original_name));
                        if(value<0) value = 0;

                        if(subjectType == 1){
                            if(value > test.easy) value = test.easy;
                            test.x_easy = value;
                            test.tmp_easy = test.x_easy;
                        } else if(subjectType == 2){
                            if(value > test.middle) value = test.middle;
                            test.x_middle = value;
                            test.tmp_middle = test.x_middle;
                        } else if(subjectType == 3){
                            if(value > test.difficult) value = test.difficult;
                            test.x_difficult = value;
                            test.tmp_difficult = test.x_difficult;
                        }
                    }
                }
            }
        }
    }



}
