package arta.dbchecker;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.dbchecker.html.dbCheckerLoginPageHandler;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.login.html.LoginPageHandler;
import arta.login.logic.Access;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.tests.testing.html.TestingsListMainHandler;
import arta.tests.testing.logic.TestingsSearchParams;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "xacktestings", urlPatterns = {"/xack"})

public class dbcTestingsListServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            int lang = extractor.getInteger(session.getAttribute("lang"));
            boolean dbchecker_auth = (session.getAttribute("dbchecker_auth") != null);
            //boolean submit = (extractor.getRequestString(request.getParameter("Submit1")) != null);

            if(!dbchecker_auth){
                String login = extractor.getRequestString(request.getParameter("login"));
                String password = extractor.getRequestString(request.getParameter("password"));
                if(login.equalsIgnoreCase("Admin") && password.equals("LMSManager")){
                    session.setAttribute("dbchecker_auth", true);
                    dbchecker_auth = true;
                }
            }


            if(!dbchecker_auth){
                new Parser(new FileReader("dbchecker/loginpage.html").read(getServletContext()), pw,
                        new dbCheckerLoginPageHandler(lang)).parse();
            } else {
                Person person = (Person) session.getAttribute("person");
                int option = extractor.getInteger(request.getParameter("option"));

                String paramsOption = extractor.getRequestString(request.getParameter("params"));

                TestingsSearchParams params = new TestingsSearchParams();
                Message message = new Message();

                boolean dbchecker = true;

                if (option == -1){
                    int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
                    SheduledTesting testing = new SheduledTesting(mainTestingID, message, lang);
                    testing.getStudentsForCancelTheTest();
                    testing.cancelTheTest();
                }

                if (paramsOption.equals("old")){
                    if (session.getAttribute("params") != null && session.getAttribute("params") instanceof SearchParams){
                        params = (TestingsSearchParams) session.getAttribute("params");
                    }
                } else if (!paramsOption.equals("new")){
                    params.extractParameterValues(request, extractor);
                }

                if(dbchecker) {
                    session.setAttribute("dbchecker", true);
                } else {
                    session.removeAttribute("dbchecker");
                }

                new PageGenerator().writeHtmlPage(new TestingsListMainHandler(params, getServletContext(),
                        lang, person.getRoleID(), person, message, person.getPersonID(), dbchecker), pw, getServletContext());
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        }
    }
}
