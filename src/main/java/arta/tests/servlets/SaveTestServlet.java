package arta.tests.servlets;

import arta.tests.test.Test;
import arta.tests.test.list.TestsSearchParams;
import arta.tests.test.save.FailureSaveMainHandler;
import arta.tests.test.save.SuccessSaveMainHandler;
import arta.filecabinet.logic.Person;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveTestServlet extends HttpServlet {

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
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);
            Test test = null;

            if (session.getAttribute("test")!=null)
                test = (Test) session.getAttribute("test");

            if (test == null){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                return;
            }

            if (!test.saveTest(person.getPersonID(), request.getRemoteHost(), params, true)){
                PageGenerator pageGenerator = new PageGenerator();
                FailureSaveMainHandler handler = new FailureSaveMainHandler(lang, person.getRoleID(), test.message,
                        getServletContext(), params);
                pageGenerator.writeHtmlPage(handler,  pw,  getServletContext());
            } else {
                PageGenerator pageGenerator = new PageGenerator();
                SuccessSaveMainHandler handler = new SuccessSaveMainHandler(lang, person.getRoleID(), getServletContext(), params);
                pageGenerator.writeHtmlPage(handler, pw, getServletContext());
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
