package arta.scheduled.tests.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.filecabinet.html.students.StudentsListMain;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.login.logic.Access;
import arta.scheduled.tests.html.schStudentsListMain;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

//testingstudents

public class schStudentsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            boolean dbchecker = (session.getAttribute("dbchecker") != null);
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            int testingID = extractor.getInteger(request.getParameter("testingID"));

            String search = extractor.getRequestString(request.getParameter("_search"));
            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);
            params.search = search;

            new PageGenerator().writeHtmlPage(new schStudentsListMain(mainTestingID, testingID, person, null, params, getServletContext(), lang, dbchecker),
                    pw, getServletContext());

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

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            boolean dbchecker = (session.getAttribute("dbchecker") != null);
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            int testingID = extractor.getInteger(request.getParameter("testingID"));

            Message message = new Message();

            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            if (extractor.getInteger(request.getParameter("option")) == -1){ // Отмена назначения
                message = new Message();
                int studentID = extractor.getInteger(request.getParameter("studentID"));
                SheduledTesting testing = new SheduledTesting(mainTestingID, message, lang);
                testing.students.add(studentID);
                if(testing.CanRemove(mainTestingID)){
                    testing.cancelTheTest();
                } else {
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestingMessages.НЩГөСФТөТЩЕөВУДУЕУөЕРШЫөЕУЫЕШТП, null));
                }
            }

            new PageGenerator().writeHtmlPage(new schStudentsListMain(mainTestingID, testingID, person, message, params, getServletContext(), lang, dbchecker),
                    pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
