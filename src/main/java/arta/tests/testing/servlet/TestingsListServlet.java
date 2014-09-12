package arta.tests.testing.servlet;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.tests.testing.html.TestingsListMainHandler;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.TestingsSearchParams;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;



public class TestingsListServlet extends HttpServlet {

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
            int option = extractor.getInteger(request.getParameter("option"));
            String paramsOption = extractor.getRequestString(request.getParameter("params"));



            //SearchParams params = new SearchParams();
            TestingsSearchParams params = new TestingsSearchParams();
            params.extractParameterValues(request, extractor);
            Message message = new Message();

            if (option == -1){
                int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
//                int testingID = extractor.getInteger(request.getParameter("testingID"));
//                int studentID = extractor.getInteger(request.getParameter("studentID"));
                SheduledTesting testing = new SheduledTesting(mainTestingID, message, lang);
                if(testing.CanRemove(mainTestingID)){
                    testing.getStudentsForCancelTheTest();
                    testing.cancelTheTest();
                } else {
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestingMessages.НЩГөСФТөТЩЕөВУДУЕУөЕРШЫөЕУЫЕШТП, null));
                }
//                Testing testing = new Testing();
//                testing.setTestingID(testingID);
//                testing.delete(lang, message, person.getPersonID());
            }

            if (paramsOption.equals("old")){
                if (session.getAttribute("params") != null && session.getAttribute("params") instanceof TestingsSearchParams){
                    params = (TestingsSearchParams) session.getAttribute("params");
                }
            } else if (!paramsOption.equals("new")){
                params.extractParameterValues(request, extractor);
            }

            //session.removeAttribute("dbchecker");

            new PageGenerator().writeHtmlPage(new TestingsListMainHandler(params, getServletContext(),
                    lang, person.getRoleID(), person, message, person.getPersonID(), false), pw, getServletContext());

            //session.setAttribute("params", (TestingsSearchParams)params);
            pw.flush();
            pw.close();
        } catch(Exception exc){
            Log.writeLog(exc);
        }
    }
}
