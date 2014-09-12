package arta.tests.reports.servlet.commonReports;

import arta.common.logic.util.Log;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.download.ResourceDownloader;
import arta.tests.reports.logic.commonReports.CommonTestReportView;
import arta.tests.reports.logic.commonReports.CommonXLSReport;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

public class CommonXLSReportServlet extends HttpServlet {
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{

            DataExtractor dataExtractor = new DataExtractor();
            HttpSession session = request.getSession();
            int mainTestingID = dataExtractor.getInteger(request.getParameter("mainTestingID"));
            int testingID = dataExtractor.getInteger(request.getParameter("testingID"));
            int lang = dataExtractor.getInteger(session.getAttribute("lang"));
            CommonTestReportView report = new CommonTestReportView(mainTestingID, testingID);
            report.load(lang);

            response.addHeader(ResourceDownloader.CONTENT_DISPOSITON, ResourceDownloader.ATTACHMENT +
                    ResourceDownloader.FILE+"report.xls\"");
            response.addHeader(ResourceDownloader.CONTENT_TYPE, "application/x-msexcel");
            OutputStream out = response.getOutputStream();

            CommonXLSReport xlsReport = new CommonXLSReport(report, lang);
            xlsReport.buildReport();
            xlsReport.writeReport(out);
            out.flush();
            out.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }

}
