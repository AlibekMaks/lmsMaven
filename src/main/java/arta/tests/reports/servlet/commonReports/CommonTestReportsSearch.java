package arta.tests.reports.servlet.commonReports;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.*;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.html.commonReports.ReportsListMainHandler;
import arta.login.logic.Access;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;

public class CommonTestReportsSearch extends HttpServlet {

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

            CommonTestSearchParams params = new CommonTestSearchParams();

            if (request.getParameter("newSearch") != null){
                params.extractParameterValues(request, extractor);
            } else {
                if (session.getAttribute("reportSearchParams") != null){
                    params = (CommonTestSearchParams)session.getAttribute("reportSearchParams");
                    params.extractParameterValues(request, extractor);
                }
            }
            session.setAttribute("reportSearchParams", params);

            ReportsListMainHandler handler = new ReportsListMainHandler(lang, person.getRoleID(), person, params,getServletContext(), person.getPersonID());
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
