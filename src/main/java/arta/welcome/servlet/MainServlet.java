package arta.welcome.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.students.Student;
import arta.common.html.handler.PageGenerator;
import arta.welcome.html.WelcomePageMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class MainServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            int[] params = Access.getLoggedInUserParams(request, getServletContext());
            if (params == null){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = null;

            if (params[Access.USER_ROLE_ID_INDEX] == Constants.STUDENT){
                person = new Student();
            } else if ((params[Access.USER_ROLE_ID_INDEX] & Constants.TUTOR) > 0){
                person = new Tutor();
            }

            if (person == null){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            if (request.getParameter("init") != null){

                person.setPersonID(params[Access.USER_ID_INDEX]);
                person.setRoleID(params[Access.USER_ROLE_ID_INDEX]);
                person.checkIsAdministrator();
                person.loadName();

                session.setAttribute("person", person);
                session.setAttribute("lang", params[Access.USER_LANG_INDEX]);

                session.setAttribute("userid", person.getPersonID() + "");
                session.setAttribute("roleid", person.getRoleID() + "");

            } else {
                person = (Person) session.getAttribute("person");
            }


            new PageGenerator().writeHtmlPage(new WelcomePageMainHandler(person, getServletContext(), params[Access.USER_LANG_INDEX], person.getRoleID(),person.getPersonID()),
                    pw, getServletContext());
            pw.flush();
            pw.close();
            
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
