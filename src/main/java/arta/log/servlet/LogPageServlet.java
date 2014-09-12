package arta.log.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.log.LogMessages;
import arta.log.html.LogPageHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class LogPageServlet extends HttpServlet {

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

            int option = extractor.getInteger(request.getParameter("option"));

            Message message = null;

            if (option == -1){
                message = new Message();
                if (Log.deleteLogs()){
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, LogMessages.LOGS_HAVE_BEEN_DELETED, null));
                } else {
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, LogMessages.LOGS_HAVE_NOT_BEEN_DELETED, null));
                }
            }

            new Parser(new FileReader("log/logs.page.txt").read(getServletContext()), pw,
                    new LogPageHandler(lang, message)).parse();
        } catch (Exception exc){
            exc.printStackTrace();
        }
    }
}
