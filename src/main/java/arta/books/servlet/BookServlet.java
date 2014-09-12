package arta.books.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.common.http.SimpleHandler;
import arta.common.http.HttpRequestParser;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.books.logic.Book;
import arta.books.logic.BookUploader;
import arta.books.html.BookCardMain;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;
import org.xml.sax.helpers.DefaultHandler;
import org.adl.samplerte.server.CourseService;
import org.adl.samplerte.server.ValidationResults;


public class BookServlet extends HttpServlet {
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

            Book book = new Book("");
            SearchParams params = new SearchParams();
            int contentLength = extractor.getInteger(request.getHeader("Content-Length"));
            int subjectID = 0;
            Message message = new Message();
            params.extractParameterValues(request, extractor);
            subjectID = extractor.getInteger(request.getParameter("subjectID"));

            int type = extractor.getInteger(request.getParameter("type"));
            if (type == Book.FILE_TYPE){

                BookUploader uploader = new BookUploader(book, message, lang);
                uploader.getConnection();
                HttpRequestParser parser = new HttpRequestParser(uploader, request);
                parser.parse();
                uploader.dataParsed();
                uploader.releaseConnection();
                book.load();

            } else {

                book.setId(extractor.getInteger(request.getParameter("bookID")));
                book.importSCORMBook(request,
                        extractor.getRequestString(request.getParameter("name")),
                        extractor.getInteger(request.getParameter("subjectID")),
                        extractor.getInteger(request.getParameter("lang")),
                        message,
                        lang, request.getParameter("validate") != null);
                book.load();

            }
            
            new PageGenerator().writeHtmlPage(new BookCardMain(person, lang, book, subjectID,
                    params, getServletContext(), message),
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

            Book book = new Book();
            book.setId(extractor.getInteger(request.getParameter("bookID")));
            int subjectID = extractor.getInteger(request.getParameter("subjectID"));

            book.load();

            new PageGenerator().writeHtmlPage(new BookCardMain(person, lang, book, subjectID, params, getServletContext(), null),
                    pw, getServletContext());
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
