package arta.classes.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.classes.html.ClassesListMain;
import arta.classes.logic.StudyClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;



public class ClassesServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new ClassesListMain(getServletContext(), lang, person, null, params),
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

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            Message message = null;

            if (request.getParameter("option") != null){
                if (extractor.getInteger(request.getParameter("option")) == -1){
                    message = new Message();
                    int classID = extractor.getInteger(request.getParameter("classID"));
                    StudyClass studyClass = new StudyClass();
                    studyClass.setClassID(classID);
                    studyClass.delete(message, lang, person.getPersonID());
                }
            }

            new PageGenerator().writeHtmlPage(new ClassesListMain(getServletContext(), lang, person, message, params),
                    pw, getServletContext());
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
