package arta.filecabinet.servlet.students;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.filecabinet.html.students.StudentsListMain;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class StudentsServlet extends HttpServlet {
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
            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new StudentsListMain(person, null, params, getServletContext(), lang),
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
            Message message = null;

            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            if (extractor.getInteger(request.getParameter("option")) == -1){
                message = new Message();
                int studentID = extractor.getInteger(request.getParameter("studentID"));
                Student student = new Student();
                student.setPersonID(studentID);
                student.delete(message, lang, person.getPersonID());
            }

            new PageGenerator().writeHtmlPage(new StudentsListMain(person, message, params, getServletContext(), lang),
                    pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
