package arta.check.servlet;

import arta.check.html.TestCheckHandler2;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.check.logic.Testing;
import arta.check.html.TestCheckHandler;
import arta.check.html.TestResultsEmptyHandler;
import arta.check.html.TestResultsHandler;
import arta.settings.logic.Settings;
import arta.tests.common.QuestionCheckHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

import arta.tests.testing.logic.SaveObject;
import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;


public class QuestionServlet extends HttpServlet {

    public ServletContext servletContext;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.STUDENT, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            MimeParser mimeParser = MimeParser.getInstance();
            ParsedData data = mimeParser.parseOnly(request);


            Testing testing = (Testing) session.getAttribute("studenttesting");

            if (testing == null){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            SaveObject saveSession = new SaveObject();
            saveSession.saveObject(person.getPersonID(), testing.testingID, testing);


            int current = extractor.getInteger(data.getParameter("current"));
            int next = extractor.getInteger(data.getParameter("next"));
            servletContext = getServletContext();
            Settings settings = new Settings();
            settings.load();

            if (current >= 0 && current < testing.questions.size() ){
                testing.questions.get(current).parseCheckParameters(data.getParameterNames().toArray(), data);
            }

            if (data.getParameter("option").equals("send")){

                clearTime( person.getPersonID(), testing.testingID);

                if (testing.check((Student)person, lang)) {
                    if (Constants.SHOW_TEST_REPORT){   //1

                        Settings setting = new Settings();
                        setting.load();
                        if(!setting.student_test_access){
                            try{ // Завершение теста
                                StudentEnteredManager unlock = new StudentEnteredManager();
                                unlock.UnLockStudentEntered( person.getPersonID() );
                            } catch (Exception exc){
                                Log.writeLog(exc);
                            }
                        }

                        new Parser(new FileReader("tests/check/Result.txt").read(servletContext), pw,
                                new TestCheckHandler2(testing, lang, settings, servletContext)).parse();

//                        new Parser(new FileReader("tests/check/checkResult.txt").read(getServletContext()), pw,
//                                new TestCheckHandler(testing, lang)).parse();
                    } else {
                        new Parser(new FileReader("tests/check/check.result.empty.txt").read(getServletContext()), pw,
                                new TestResultsEmptyHandler(lang)).parse();
                    }
                } else {
                    if (Constants.SHOW_TEST_REPORT) {
                        new Parser(new FileReader("tests/check/test.results.txt").read(getServletContext()), pw,
                                new TestResultsHandler(person.getPersonID(), lang, testing.testingID)).parse();
                    } else {
                        new Parser(new FileReader("tests/check/check.result.empty.txt").read(getServletContext()), pw,
                                new TestResultsEmptyHandler(lang)).parse();
                    }
                }

                SaveObject removeSession = new SaveObject();
                removeSession.deleteObject(person.getPersonID(), testing.testingID);

                clearTime(person.getPersonID(), testing.testingID);

                session.removeAttribute("studenttesting");
            } else {
                new Parser(new FileReader("tests/questions/myquestion.common.check.template.txt").read(getServletContext()),
                        pw, new QuestionCheckHandler(lang, next, getServletContext(), testing)).parse();
            }

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.STUDENT, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Testing testing = (Testing) session.getAttribute("studenttesting");

            if (testing == null){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            int next = extractor.getInteger(request.getParameter("next"));

            SaveObject saveSession = new SaveObject();
            saveSession.saveObject(person.getPersonID(), testing.testingID, testing);

            new Parser(new FileReader("tests/questions/myquestion.common.check.template.txt").read(getServletContext()),
                pw, new QuestionCheckHandler(lang, next, getServletContext(), testing)).parse();
            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void clearTime(int studentID, int testingID){

        Connection con = null;
        Statement st = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM testingtime t \n" +
                       " WHERE (t.studentID = "+studentID+") AND (t.testingID = "+testingID+") "+
                       " OR (DATE(t.modified) < DATE(NOW()))");

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
