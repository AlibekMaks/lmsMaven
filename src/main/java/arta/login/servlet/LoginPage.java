package arta.login.servlet;

import arta.common.logic.util.Log;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Constants;
import arta.common.logic.server.Server;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.html.LoginPageHandler;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class LoginPage extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();


            if (!Server.isURLCorrect){
                response.sendRedirect("invalid");
            }

            int lang = extractor.getInteger(request.getParameter("lang"));
            if (lang != Languages.RUSSIAN && lang != Languages.KAZAKH && lang != Languages.ENGLISH){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }            

            session.setAttribute("lang", lang);

            Access.setLanguage(request, response, getServletContext(), lang);

            new Parser(new FileReader("login/loginpage.html").read(getServletContext()), pw,
                    new LoginPageHandler(lang)).parse();
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
