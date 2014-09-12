package arta.lessonmaterials.servelt;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.tests.test.list.TestsSearchParams;
import arta.lessonmaterials.html.LessonMaterialsMainHandler;
import arta.lessonmaterials.logic.TutorBook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class TutorBooksServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            if (request.getParameter("option")!=null){
                message = new Message();
                if (extractor.getInteger(request.getParameter("option")) == -1){
                    int id = extractor.getInteger(request.getParameter("bookID"));
                    TutorBook tutorBook = new TutorBook(0);
                    tutorBook.setId(id);
                    tutorBook.delete(message, lang);
                }
            }

            new PageGenerator().writeHtmlPage(new LessonMaterialsMainHandler(getServletContext(), lang,
                    person, params, message), pw, getServletContext());
            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
