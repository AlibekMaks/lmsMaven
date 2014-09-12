package arta.registrar.student.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Date;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.registrar.student.html.RegistrarCardMainHandler;
import arta.registrar.student.logic.StudentRegistrar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class StudentRegistrarServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.STUDENT, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            int month = extractor.getInteger(request.getParameter("month"));
            int year = extractor.getInteger(request.getParameter("year"));

            if(request.getParameter("plus") != null){
                month ++;
                if (month > 12){
                    month = 1;
                    year ++ ;
                }
            }

            if (request.getParameter("minus") != null){
                month --;
                if (month == 0){
                    month = 12;
                    year -- ;
                }
            }

            Date date = new Date();
            if (month == 0) month = date.month;
            if (year == 0) year = date.year;
            
            StudentRegistrar registrar = new StudentRegistrar(person.getPersonID());
            registrar.load(month, year, lang);

            new PageGenerator().writeHtmlPage(new RegistrarCardMainHandler(person, lang, getServletContext(),
                    registrar, month, year), pw, getServletContext());
            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
