package arta.check.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.check.logic.Testing;
import arta.check.html.TimerHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class TimerServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Testing testing = (Testing) session.getAttribute("studenttesting");

            new Parser(new FileReader("tests/check/timer.html").read(getServletContext()), pw,
                    new TimerHandler(testing.passed, testing.testingTime, lang)).parse();
            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
