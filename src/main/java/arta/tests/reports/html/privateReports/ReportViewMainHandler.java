package arta.tests.reports.html.privateReports;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ReportViewMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    StringBuffer report;
    ServletContext servletContext;
    CommonTestSearchParams params;
    String return_link;
    int mainTestingID;
    int testingID;
    int studentID;
	private boolean isAttestatReport;

    public ReportViewMainHandler(StringBuffer report, int roleID, int lang, 
                                 ServletContext servletContext, int testingID, int mainTestingID, int studentID,
                                 CommonTestSearchParams params, String return_link) {
        this.report = report;
        this.roleID = roleID;
        this.lang = lang;
        this.servletContext = servletContext;
        this.testingID = testingID;
        this.mainTestingID = mainTestingID;
        this.studentID = studentID;
        this.params = params;
        this.return_link = return_link;
    }
    
    public void setIsAttestatReport(boolean isAtt) {
    	this.isAttestatReport = isAtt;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
    	PrivateReportHandler h = new PrivateReportHandler(lang, params, return_link, testingID, mainTestingID, studentID, report, servletContext);
    	h.setIsAttestatReport(isAttestatReport);
        new Parser(new FileReader("tests/reports/private/private.report.txt").read(servletContext), pw,
            h).parse();
    }

    public void getScript(PrintWriter pw) {

    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
