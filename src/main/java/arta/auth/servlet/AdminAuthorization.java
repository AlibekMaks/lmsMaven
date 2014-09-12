package arta.auth.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.auth.html.AdminAuthMainHandler;
import arta.auth.logic.PersonAuthorization;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class AdminAuthorization extends HttpServlet {
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
            Message message = new Message();

            int personID = extractor.getInteger(request.getParameter("personID"));
            int roleID = extractor.getInteger(request.getParameter("roleID"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            String login = extractor.getRequestString(request.getParameter("login"));
            String password = extractor.getRequestString(request.getParameter("password"));
            String confirm = extractor.getRequestString(request.getParameter("confirm"));

            PersonAuthorization authorization = new PersonAuthorization();
            authorization.changeParams(login, password, confirm, personID, roleID, message, lang);

            new PageGenerator().writeHtmlPage(new AdminAuthMainHandler(lang, person, getServletContext(), personID,
                    roleID, params, message), pw, getServletContext());

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

            int personID = extractor.getInteger(request.getParameter("personID"));
            int roleID = extractor.getInteger(request.getParameter("roleID"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new AdminAuthMainHandler(lang, person, getServletContext(), personID,
                    roleID, params, null), pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
