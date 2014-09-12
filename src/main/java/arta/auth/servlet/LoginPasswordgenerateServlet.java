package arta.auth.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.messages.MessageManager;
import arta.login.logic.Access;
import arta.login.logic.LoginMessages;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.auth.logic.LoginPasswordGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;

public class LoginPasswordgenerateServlet extends HttpServlet {
                        
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            int generateType = extractor.getInteger(request.getParameter("generateType"));
            int roleID = extractor.getInteger(request.getParameter("roleID"));

            pw.print("<html>\n" +
                    "<head>\n" +
                    "<META HTTP-Equiv=\"Cache-Control\" Content=\"no-cache\">\n" +
                    "<META HTTP-Equiv=\"Pragma\" Content=\"no-cache\">\n" +
                    "<META HTTP-Equiv=\"Expires\" Content=\"Tue, 01 Jan 1980 1:00:00 GMT\">\n" +
                    "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                    "<LINK TYPE=\"text/css\" REL=\"stylesheet\" \n" +
                    "      HREF=\"css/common.css\">\n" +
                    "<title>Pythagorus</title>\n" +
                    "</head>\n" +
                    "<body topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0>");
            pw.print("<table border=0 width=100% cellpadding=20px ><tr><td>");
            pw.print("<table border=0 width=100% class=\"table\">");
            pw.print("<tr><td align=\"center\" class=\"header\">");
            pw.print(MessageManager.getMessage(lang, Constants.REPORT_ON_GENERATION_LOGINS_AND_PASSWRODS, null));
            pw.print("</td></tr>");
            pw.print("<tr><td><table border=1 class=\"table\" width=\"100%\">");
            pw.print("<tr><td width=*>");
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
            pw.print("</td>");
            pw.print("<td width=\"20%\">");
            pw.print(MessageManager.getMessage(lang, LoginMessages.LOGIN, null));
            pw.print("</td>");
            pw.print("<td width=\"20%\">");
            pw.print(MessageManager.getMessage(lang, LoginMessages.PASSWORD, null));
            pw.print("</td></tr>");

            if (generateType == 0){
                ArrayList<Integer> ids = new  ArrayList<Integer>();
                String tmp = "tut";
                if (roleID == Constants.STUDENT)
                    tmp = "std";

                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()){
                    String name = (String) en.nextElement();
                    if (name.length() > 3 && name.substring(0, 3).equals(tmp)){
                        int id = extractor.getInteger(name.substring(3, name.length()));
                        if (id > 0)
                            ids.add(id);
                    }
                }
                LoginPasswordGenerator generator = new LoginPasswordGenerator();
                generator.generate(ids, roleID, pw);
            } else if (generateType == 1){
                StudentSearchParams params = new StudentSearchParams();
                params.extractParameterValues(request, extractor);
                LoginPasswordGenerator generator = new LoginPasswordGenerator();
                generator.generate(params, roleID, pw);
            }
            pw.print("</table>");
            pw.print("</td></tr></table>");
            pw.print("</td></tr></table>");

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
