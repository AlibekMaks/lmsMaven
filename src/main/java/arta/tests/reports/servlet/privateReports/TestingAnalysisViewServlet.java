package arta.tests.reports.servlet.privateReports;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Date;
import arta.common.logic.util.Log;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.exams.logic.Ticket;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.logic.commonReports.CommonTestReportView;
import arta.tests.reports.html.commonReports.CommonTestReportForm;
import arta.tests.reports.html.commonReports.ReportViewMainHandler;
import arta.tests.reports.html.privateReports.TestingAnalysisHandler;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;
import arta.tests.reports.logic.privateReports.TestReport;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class TestingAnalysisViewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();
            String return_link = request.getHeader("referer");

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params1 = new SearchParams();
            params1.extractParameterValues(request, extractor);

            CommonTestSearchParams params = new CommonTestSearchParams();
            params.extractParameterValues(request, extractor);            
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            int testingId = extractor.getInteger(request.getParameter("testingID"));
            int studentId = extractor.getInteger(request.getParameter("studentID"));
            int option = extractor.getInteger(request.getParameter("option"));
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
                        "<body topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0 >");

                        TestingAnalysisHandler handler = new TestingAnalysisHandler(lang, person.getRoleID(),testingId,studentId,
                                mainTestingID, params, return_link, getServletContext(), print, null);
                        handler.getMainPart(pw);
                pw.print("</html>");
            } else {
                TestReport report = new TestReport(studentId, testingId);
                report.load();

            	TestingAnalysisHandler handler = new TestingAnalysisHandler(lang, person.getRoleID(),testingId,studentId,
                        mainTestingID, params, return_link, getServletContext(), print, report.report);

                PageGenerator generator = new PageGenerator();
                generator.writeHtmlPage(handler,  pw, getServletContext());
            }


        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
