package arta.lessonmaterials.servelt;

import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.common.http.HttpRequestParser;
import arta.common.http.SimpleHandler;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.tests.test.list.TestsSearchParams;
import arta.lessonmaterials.html.TutorBookCardMainHandler;
import arta.lessonmaterials.logic.TutorBook;
import arta.books.logic.BookUploader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;


public class TutorBookServlet extends HttpServlet {
    
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

            Message message = new Message();
            TutorBook book;

            book = new TutorBook(person.getPersonID());
            BookUploader uploader = new BookUploader(book, message, lang);
            uploader.getConnection();
            HttpRequestParser parser = new HttpRequestParser(uploader, request);
            parser.parse();
            uploader.dataParsed();
            uploader.releaseConnection();
            params.extractParameterValues(uploader, extractor);

            new PageGenerator().writeHtmlPage(new TutorBookCardMainHandler(lang, message, getServletContext(),
                    person, book, params), pw, getServletContext());
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

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);

            Message message = null;
            TutorBook book = new TutorBook(0);
            book.setId(extractor.getInteger(request.getParameter("bookID")));
            book.load();

            new PageGenerator().writeHtmlPage(new TutorBookCardMainHandler(lang, message, getServletContext(),
                    person, book, params), pw, getServletContext());
            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
