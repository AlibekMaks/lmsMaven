package arta.tests.servlets;

import arta.tests.test.list.TutorTestsListMain;
import arta.tests.test.list.TestsSearchParams;
import arta.tests.test.Test;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;

public class TutorTestsList extends HttpServlet {

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

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);
            params.tutorID = person.getPersonID();

            TutorTestsListMain handler = new TutorTestsListMain(lang, getServletContext(), null, person, params);
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

            pw.flush();
            pw.close();

        }catch(Exception exc){
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

            Message message = null;

            int option = extractor.getInteger(request.getParameter("option"));
            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);
            params.tutorID = person.getPersonID();

            if (option == -1){
                message = new Message();
                int testID = extractor.getInteger(request.getParameter("testID"));
                Test test = new Test(testID, lang);
                if (!test.deleteTest(person.getPersonID(), request.getRemoteHost())){
                    message.setMessageType(Message.ERROR_MESSAGE);
                } else {
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                }
                message = test.message;
            }

            TutorTestsListMain handler = new TutorTestsListMain(lang, getServletContext(), message, person, params);
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());
            
            pw.flush();
            pw.close();
        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }

}
