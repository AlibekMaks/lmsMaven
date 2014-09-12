package arta.tests.reports.servlet.commonReports;

import arta.common.logic.util.*;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.logic.commonReports.CommonTestReportView;
import arta.tests.reports.html.commonReports.CommonTestReportForm;
import arta.tests.reports.html.commonReports.ReportViewMainHandler;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class CommonTestReportViewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
            String return_link = request.getHeader("referer");

            SearchParams params1 = new SearchParams();
            params1.extractParameterValues(request, extractor);

            CommonTestSearchParams params = new CommonTestSearchParams();
            params.extractParameterValues(request, extractor);            
            int id = extractor.getInteger(request.getParameter("testingID"));
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            boolean print = request.getParameter("print")!=null;

            if (print){
                pw.print("<html>\n" +
                        "<head>\n" +
                        "<META HTTP-Equiv=\"Cache-Control\" Content=\"no-cache\">\n" +
                        "<META HTTP-Equiv=\"Pragma\" Content=\"no-cache\">\n" +
                        "<META HTTP-Equiv=\"Expires\" Content=\"Tue, 01 Jan 1980 1:00:00 GMT\">\n" +
                        "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                        "<LINK TYPE=\"text/css\" REL=\"stylesheet\" \n" +
                        "      HREF=\"css/common.css\">\n" +
                        "</head>\n" +
                        "<body topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0>");


                CommonTestReportView report = new CommonTestReportView(id, mainTestingID);
                report.myload(lang, mainTestingID);
                CommonTestReportForm handler = new CommonTestReportForm(mainTestingID, lang, report, false, getServletContext(), params);
                FileReader fileReader = new FileReader("tests/reports/common/reportForm.print.txt");
                Parser parser = new Parser(fileReader.read(getServletContext()), pw, handler);
                parser.parse();
                pw.print("</html>");
            } else {
                ReportViewMainHandler handler = new ReportViewMainHandler(lang, person.getRoleID(),id, mainTestingID,
                        params, getServletContext() );
                //handler.mainTestingID = mainTestingID;
                PageGenerator generator = new PageGenerator();
                generator.writeHtmlPage(handler,  pw, getServletContext());
            }


        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
