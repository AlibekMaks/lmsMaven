package arta.studyroom.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.studyroom.html.StudyRoomMenuHandler;
import arta.filecabinet.logic.Person;

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
 * Date: 19.03.2008
 * Time: 17:17:04
 * To change this template use File | Settings | File Templates.
 */

public class StudyRoomMenuServlet extends HttpServlet {

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

            int lang = Access.hasLanguage(session);
            int roomID = extractor.getInteger(request.getParameter("roomID"));
            Person person = (Person) session.getAttribute("person");

            new Parser(new FileReader("studyroom/content/menu.txt").read(getServletContext()), pw,
                    new StudyRoomMenuHandler(lang, roomID, person.getRoleID())).parse();

        } catch(Exception exc){
            Log.writeLog(exc);
        }

    }
}
