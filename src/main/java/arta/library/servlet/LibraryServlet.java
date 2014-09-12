package arta.library.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.library.logic.LibrarySearchParams;
import arta.library.html.LibraryCardMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class LibraryServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserAuthorized(session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            LibrarySearchParams params = new LibrarySearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new LibraryCardMainHandler(params, person, lang, getServletContext()),
                    pw, getServletContext());
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
