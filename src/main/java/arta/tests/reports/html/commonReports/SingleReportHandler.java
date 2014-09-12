package arta.tests.reports.html.commonReports;

import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.tutors.Tutor;
import arta.tests.reports.logic.commonReports.CommonTestReport;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SingleReportHandler extends TemplateHandler {

    CommonTestReport report;
    int lang;
    CommonTestSearchParams params;
    StringTransform trsf = new StringTransform();

    public SingleReportHandler(CommonTestReport report, int lang, CommonTestSearchParams params) {
        this.report = report;
        this.lang = lang;
        this.params = params;
    }

//    public SingleReportHandler(CommonTestReport report) {
//        this.report = report;
//    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print("<a href=\"testcommonreport?mainTestingID="+report.mainTestingID+"&testingID="+report.testingID+
                    "&"+params.getFullParamsWithoutRecord()+"&"+ Rand.getRandString()+"\" class=\"href\">");  //params.getFullParams()
            pw.print(trsf.getHTMLString(report.name));
            pw.print("</a>");
        } else if (name.equals("tutor name")){
            pw.print(trsf.getHTMLString(report.tutorGetFullName()));
        } else if (name.equals("date")){
            pw.print(report.date.getInputValue());
        }
    }

}
