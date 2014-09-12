package arta.login.servlet;

import arta.common.logic.util.*;
import arta.common.logic.server.Server;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.common.http.MimeTypes;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;
import arta.login.html.AccessDeniedHandler;
import arta.welcome.html.WelcomePageMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;



public class Authorize extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!Server.isURLCorrect){
            response.sendRedirect("invalid");
        }

        try{

            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            HttpSession session = null;

            try{
                session = request.getSession();
            } catch(Exception exc){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                return;
            }


            MimeTypes.init(getServletContext());
            int langID = Access.getLanguage(request, getServletContext());

            String login = extractor.getRequestString(request.getParameter("login"));
            int roleID = extractor.getInteger(request.getParameter("role"));
            String password = extractor.getRequestString(request.getParameter("password"));

            if ((roleID != Constants.STUDENT && roleID!=Constants.TUTOR) || login == null || password == null || langID == 0){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            session.setMaxInactiveInterval(36000);

            Person person = null;

            if (roleID == Constants.STUDENT){
                person = Access.authorizeStudent(request, login, password);
            } else if (roleID == Constants.TUTOR){
                person = Access.authorizeTutor(login, password);
            }

            if (person == null){
                if (langID != Languages.ENGLISH && langID != Languages.KAZAKH && langID!= Languages.RUSSIAN){
                    langID = Languages.ENGLISH;
                }
                new Parser(new FileReader("login/access.denied.txt").read(getServletContext()), pw,
                        new AccessDeniedHandler(langID)).parse();
                pw.flush();
                pw.close();
                return;
            }

            Access.loginUser(request, response, getServletContext(), person.getPersonID(), person.getRoleID(), langID);
            response.sendRedirect(Server.MAIN_URL + "main?init=true&" + Rand.getRandString());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(Constants.RETURN_TO_MAIN_PAGE);
    }
}
