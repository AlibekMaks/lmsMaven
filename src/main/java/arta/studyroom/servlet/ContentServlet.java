package arta.studyroom.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.studyroom.html.ContentHandler;
import arta.studyroom.logic.Content;
import arta.filecabinet.logic.Person;
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
 * Date: 25.04.2009
 * Time: 14:58:36
 * To change this template use File | Settings | File Templates.
 */

public class ContentServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

        int lang = Access.hasLanguage(session);
        Person person = (Person) session.getAttribute("person");

        int subgroupID =  extractor.getInteger(request.getParameter("roomID"));
        int option = extractor.getInteger(request.getParameter("option"));

        if (option == -1){
            int bookID = extractor.getInteger(request.getParameter("bookID"));
            TutorBook tutorBook = new TutorBook(person.getPersonID());
            tutorBook.cancel(bookID, subgroupID);
        }

        Content content = new Content(person);
        content.load(subgroupID);

        new Parser(new FileReader("studyroom/content/content.txt").read(getServletContext()), pw,
                new ContentHandler(lang, subgroupID, getServletContext(), content, person.getRoleID())).parse();

    }
    
}
