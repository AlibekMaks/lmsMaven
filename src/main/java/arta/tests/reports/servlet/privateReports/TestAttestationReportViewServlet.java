package arta.tests.reports.servlet.privateReports;

import arta.tests.reports.logic.privateReports.TestReport;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.html.privateReports.ReportViewMainHandler;
import arta.check.logic.Testing;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class TestAttestationReportViewServlet extends HttpServlet {

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
            boolean print = request.getParameter("print") != null;


            int studentID = extractor.getInteger(request.getParameter("studentID"));
            int testingID = extractor.getInteger(request.getParameter("testingID"));
            int mainTestingID = extractor.getInteger(request.getParameter("mainTestingID"));
            int tutorID = extractor.getInteger(request.getParameter("tutorID"));
            String chairman = extractor.getRequestString(request.getParameter("chairman"));
            String vicechairman = extractor.getRequestString(request.getParameter("vicechairman"));
            String members = extractor.getRequestString(request.getParameter("members"));
            String secretary = extractor.getRequestString(request.getParameter("secretary"));

            TestReport report = new TestReport(studentID, testingID);
            report.load();
            
            String reportAttestat = report.getReportAttestat(testingID, studentID, tutorID, chairman,vicechairman,members,secretary, lang);
            
            if (!print){

                CommonTestSearchParams params = new CommonTestSearchParams();
                params.extractParameterValues(request, extractor);

                PageGenerator pageGenerator = new PageGenerator();
                ReportViewMainHandler handler = new ReportViewMainHandler(new StringBuffer(reportAttestat), person.getRoleID(), lang,
                        getServletContext(), testingID, mainTestingID, studentID, params, return_link);
                handler.setIsAttestatReport(true);
                pageGenerator.writeHtmlPage(handler, pw, getServletContext());
            } else {
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
                if (report!= null)
                    pw.print(reportAttestat);
                else
                    pw.print("&nbsp;");
                pw.print("</body></html>");
            }

        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
