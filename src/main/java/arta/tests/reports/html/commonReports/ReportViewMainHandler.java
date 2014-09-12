package arta.tests.reports.html.commonReports;

import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.logic.commonReports.CommonTestReportView;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ReportViewMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    int id;
    int mainTestingID;
    CommonTestSearchParams params;
    CommonTestReportView report;
    ServletContext servletContext;

    public ReportViewMainHandler(int lang, int roleID, int id, int mainTestingID, CommonTestSearchParams params,
                                 ServletContext servletContext) {
        this.lang = lang;
        this.roleID = roleID;
        this.id = id;
        this.params = params;
        this.servletContext = servletContext;
        this.mainTestingID = mainTestingID;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {

        report = new CommonTestReportView(id, mainTestingID);
        report.myload(lang, mainTestingID);

        CommonTestReportForm handler = new CommonTestReportForm(mainTestingID, lang, report, true,
                servletContext, params);
        FileReader fileReader = new FileReader("tests/reports/common/reportForm.txt");        
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
