package arta.subjects.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.html.SubjectsListMain;
import arta.subjects.logic.Subject;
import arta.books.logic.Book;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class SubjectsServlet extends HttpServlet {

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
            String return_link = request.getHeader("referer");

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new SubjectsListMain(person, lang, getServletContext(), params, null, return_link), pw,
                    getServletContext());
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
            Message message = null;

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            String return_link = request.getHeader("referer");

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            if (request.getParameter("option")!=null){
                if (extractor.getInteger(request.getParameter("option")) == -1){
                    message = new Message();
                    int subjectID = extractor.getInteger(request.getParameter("subjectID"));
                    Subject subject = new Subject();
                    subject.setSubjectID(subjectID);
                    subject.delete(message, lang, person.getPersonID());
                }
            }

            new PageGenerator().writeHtmlPage(new SubjectsListMain(person, lang, getServletContext(), params, message, return_link), pw,
                    getServletContext());
            pw.flush();
            pw.close();         
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
