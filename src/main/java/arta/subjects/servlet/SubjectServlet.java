package arta.subjects.servlet;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.Subject;
import arta.subjects.html.SubjectCardMain;
import arta.books.logic.Book;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class SubjectServlet extends HttpServlet {
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

            Subject subject = new Subject();
            //subject.setName(Languages.ENGLISH, extractor.getRequestString(request.getParameter("nameen")));
            subject.setName(Languages.KAZAKH, extractor.getRequestString(request.getParameter("namekz")));
            subject.setName(Languages.RUSSIAN, extractor.getRequestString(request.getParameter("nameru")));
            subject.setPreferredMark(extractor.getInteger(request.getParameter("preferredMark")));
            subject.setSubjectID(extractor.getInteger(request.getParameter("subjectID")));
            subject.setKaz_test_ID(extractor.getInteger(request.getParameter("kaz_test")));
            subject.setRus_test_ID(extractor.getInteger(request.getParameter("rus_test")));

            Message message = new Message();
            subject.save(message, lang, params, person.getPersonID());

            if (subject.getSubjectID() > 0)
                subject.load();

            new PageGenerator().writeHtmlPage(new SubjectCardMain(person, getServletContext(), lang,
                    subject, params, message, return_link), pw, getServletContext());

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

            Subject subject = new Subject();

            if (request.getParameter("option") != null){
                if (extractor.getInteger(request.getParameter("option")) ==  -1){
                    message = new Message();
                    int bookID = extractor.getInteger(request.getParameter("bookID"));
                    Book book = new Book();
                    book.setId(bookID);
                    book.delete(message, lang);
                }
            }            

            if (request.getParameter("subjectID") != null){
                int subjectID = extractor.getInteger(request.getParameter("subjectID"));
                subject.loadById(subjectID);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                subject.loadByRecordNumber(params, recordNumber, lang);
            }

            new PageGenerator().writeHtmlPage(new SubjectCardMain(person, getServletContext(), lang,
                    subject, params, message, return_link), pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
