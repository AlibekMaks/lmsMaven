package arta.check.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Rand;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.TemplateHandler;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.check.logic.Testing;
import arta.check.html.TestResultsHandler;
import arta.check.html.TestResultsEmptyHandler;
import arta.settings.logic.Settings;
import arta.tests.testing.html.LeftBeforeTestingStartHandler;
import arta.tests.testing.logic.SaveObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class StudentTestingServlet extends HttpServlet {

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

            int testingID = extractor.getInteger(request.getParameter("testingID"));
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));

            if (request.getParameter("main_frame") != null){
                pw.print("<frameset rows=\"100%\">");
                    pw.print("<frame src=\"studenttesting?mainTestingID="+mainTestingID+"&testingID="+testingID
                            + "&nocache="+ Rand.getRandString()+"\" name=\"main_frame\" id=\"main_frame\"/>");
                pw.print("</frameset>");

            } else {
                Person person = (Person) session.getAttribute("person");
                int lang = extractor.getInteger(session.getAttribute("lang"));

                SaveObject saveSession = new SaveObject();
                Testing testing = (Testing)saveSession.getObject(person.getPersonID(), testingID);

                if(testing != null){
                    session.setAttribute("studenttesting", testing);
                } else {
                    testing = (Testing) session.getAttribute("studenttesting");
                }

                boolean needReloadTesting = false;

                if (testing == null){
                    if (testingID > 0){
                        testing = new Testing();
                        testing.studentID = person.getPersonID();
                        testing.mainTestingID = mainTestingID;
                        testing.testingID = testingID;
                        needReloadTesting = true;
                    } else {
                        pw.print(Constants.RETURN_TO_MAIN_PAGE);
                        pw.flush();
                        pw.close();
                        return;
                    }
                } else {
                    if (testingID > 0){
                        if (testing.testingID != testingID){
                            testing.studentID = person.getPersonID();
                            testing.mainTestingID = mainTestingID;
                            testing.testingID = testingID;
                            needReloadTesting = true;
                        }
                    }
                }

                int access = testing.isStartPossible();

                if (access < 0){
                    if (Constants.SHOW_TEST_REPORT){
                        Settings settings = new Settings();
                        settings.load();
//                        new Parser(new FileReader("tests/check/Result.txt").read(getServletContext()), pw,
//                                new TestCheckHandler2(testing, lang, settings, getServletContext())).parse();
                        new Parser(new FileReader("tests/check/test.results.txt").read(getServletContext()), pw,
                                new TestResultsHandler(person.getPersonID(), lang, testingID)).parse();
                    } else { //2   Тестирование завершено  Ваш балл: 0
                        new Parser(new FileReader("tests/check/check.result.empty.txt").read(getServletContext()), pw,
                                new TestResultsEmptyHandler(lang)).parse();
                    }
                    pw.flush();
                    pw.close();
                    return;
                } else if (access > 0){
                    new Parser(new FileReader("tests/check/beforeTesting.txt").read(getServletContext()),
                            pw, new LeftBeforeTestingStartHandler(lang, access, testingID)).parse();
                    pw.flush();
                    pw.close();
                    return;
                }

                if (needReloadTesting){
                    // Нужно еще раз проверить не сдавал ли студент данный тест
                    if(testing.studentIsPassedThisTest(testing.studentID, testing.testingID)){
                        testing = null;

                        saveSession.saveObject(testing.studentID, testing.testingID, (Object)testing);
                        session.setAttribute("studenttesting", testing);

                        pw.print(Constants.RETURN_TO_MAIN_PAGE);
                        pw.flush();
                        pw.close();
                    } else {
                        testing.lang = lang;
                        testing.load();

                        saveSession.saveObject(testing.studentID, testing.testingID, testing);
                        session.setAttribute("studenttesting", testing);

                        Settings setting = new Settings();
                        setting.load();
                        if(!setting.student_test_access){
                            testing.insertHandingOverTesting(request, person.getPersonID(), testingID); // Записать IP адресс и StudentID
                        }
                    }
                }

//                session.setAttribute("studenttesting", testing);

                new Parser(new FileReader("tests/check/checkMain.html").read(getServletContext()), pw,
                        new TemplateHandler()).parse();

                pw.flush();
                pw.close();
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
