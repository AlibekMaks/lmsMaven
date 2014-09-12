package arta.studyroom.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Languages;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.studyroom.html.TutorialAppointListHandler;
import arta.lessonmaterials.logic.TutorBooksManager;
import arta.lessonmaterials.logic.TutorBook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 11:25:34
 * To change this template use File | Settings | File Templates.
 */

public class AppointTutorialServlet extends HttpServlet {


    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();

        if (!Access.isUserAuthorized(session)){
            pw.print(Constants.RETURN_TO_MAIN_PAGE);
            pw.flush();
            pw.close();
            return;
        }

        try{


            Person person = (Person) session.getAttribute("person");
            int lang = Access.hasLanguage(session);
            int roomID = extractor.getInteger(request.getParameter("roomID"));

            int option = extractor.getInteger(request.getParameter("option"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            if (option == 2){

                String id = extractor.getRequestString(request.getParameter("JsHttpRequest"));
                id = id.substring(0, id.indexOf("-"));
                int bookID = extractor.getInteger(request.getParameter("bookID"));
                String result = "0";

                TutorBook tutorBook = new TutorBook(person.getPersonID());
                if (tutorBook.appoint(bookID, roomID))
                    result = "1";

                pw.print("JsHttpRequest.dataReady( " +
                        "  "+id+" ," +
                        "  'messages'," +
                        "   {  " +
                        "   result : '" + result + "' " +
                        "   }" +
                        ")");
            } else {
                                               
                new Parser(new FileReader("studyroom/content/appoint.txt").read(getServletContext()), pw,
                        new TutorialAppointListHandler(params, lang, getServletContext(),
                                person.getPersonID(), roomID)).parse();
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        }

    }
}
