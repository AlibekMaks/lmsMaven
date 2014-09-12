package arta.folder.directorComment;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.common.TestMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class PrivateReportHandler extends TemplateHandler {

    int lang;
    CommonTestSearchParams params;
    int testingID;
    int studentID;
    StringBuffer report;
    ServletContext servletContext;
    private boolean isAttestatReport;


    public PrivateReportHandler(int lang, CommonTestSearchParams params, int testingID, int studentID,
                                StringBuffer report, ServletContext servletContext) {
        this.lang = lang;
        this.params = params;
        this.testingID = testingID;
        this.studentID = studentID;
        this.report = report;
        this.servletContext = servletContext;
    }
    
    public void setIsAttestatReport(boolean isAtt) {
    	this.isAttestatReport = isAtt;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("return link")){
            pw.print("testcommonreport?testingID="+testingID+"&"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("print link")){
        	String link = (isAttestatReport)?"test_attestat_report":"privatetestreport";
            pw.print(link + "?testingID="+testingID+"&studentID="+studentID+"&print=true");
        } else if (name.equals("print title")){
            pw.print(MessageManager.getMessage(lang, TestMessages.VERSION_FOR_PRINT, null));
        } else if (name.equals("report")){
            if (report != null)
                pw.print(report);
            else
                pw.print("&nbsp;");
        } else if (name.equals("xls link")){
            pw.print("xlsreport?testingID="+testingID+"&studentID="+studentID);
        } else if(name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT));
        } else if(name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
