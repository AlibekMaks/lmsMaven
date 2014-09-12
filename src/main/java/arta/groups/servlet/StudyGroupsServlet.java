package arta.groups.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.SimpleObject;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.SubjectsSearchParams;
import arta.groups.html.GroupsAddMain;
import arta.classes.logic.StudyClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class StudyGroupsServlet extends HttpServlet {
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

            SubjectsSearchParams sbParams = new SubjectsSearchParams();
            sbParams.extractParameterValues(request, extractor);

            StudyClass studyClass = new StudyClass();

            new PageGenerator().writeHtmlPage(new GroupsAddMain(lang, getServletContext(), person, params,
                    sbParams, studyClass.loadAsSimpleObject(extractor.getInteger(request.getParameter("classID")))),
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

            SubjectsSearchParams sbParams = new SubjectsSearchParams();
            sbParams.extractParameterValues(request, extractor);

            StudyClass studyClass = new StudyClass();

            new PageGenerator().writeHtmlPage(new GroupsAddMain(lang, getServletContext(), person, params,
                    sbParams, studyClass.loadAsSimpleObject(extractor.getInteger(request.getParameter("classID")))),
                    pw, getServletContext());
            pw.flush();
            pw.close();            

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
