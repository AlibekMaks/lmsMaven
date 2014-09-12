package arta.tests.reports.servlet.privateReports;

import arta.common.logic.download.ResourceDownloader;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.tests.reports.logic.privateReports.PrivateXLSReport;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

public class XLSReportServlet extends HttpServlet {    

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            String filename = "report.xls";
            DataExtractor extractor = new DataExtractor();
            response.addHeader(ResourceDownloader.CONTENT_DISPOSITON, ResourceDownloader.ATTACHMENT +
                    ResourceDownloader.FILE+filename+"\"");
            response.addHeader(ResourceDownloader.CONTENT_TYPE, "application/x-msexcel");
            OutputStream out = response.getOutputStream();

            int testingID = extractor.getInteger(request.getParameter("testingID"));
            int studentID  = extractor.getInteger(request.getParameter("studentID"));

            PrivateXLSReport xlsReport = new PrivateXLSReport();
            xlsReport.loadAndWrite(studentID, testingID, out);
            out.flush();
            out.close();
        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
