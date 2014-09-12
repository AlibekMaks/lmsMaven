package arta.tests.reports.html.commonReports;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.students.Student;
import arta.tests.common.TestMessages;
import arta.tests.reports.logic.commonReports.CommonTestReportStudent;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SingleStudentHandler extends TemplateHandler {

    int lang;
    CommonTestReportStudent student;
    int testingID;
    int mainTestingID;
    CommonTestSearchParams params;
    StringTransform trsf = new StringTransform();

    public SingleStudentHandler(int lang, CommonTestReportStudent student, int testingID, int mainTestingID,
                                CommonTestSearchParams params) {
        this.lang = lang;
        this.student = student;
        this.testingID = testingID;
        this.mainTestingID = mainTestingID;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(student.name);
        } else if (name.equals("class name")){
            Student st = new Student();
            st.loadById(student.studentID);
            pw.print(trsf.getHTMLString(st.getClassName()));
        } else if (name.equals("test language")){
            if(student.testingLanguage == Languages.KAZAKH){
                pw.print(MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null));
            } else if(student.testingLanguage == Languages.RUSSIAN){
                pw.print(MessageManager.getMessage(lang, TestMessages.RUS_MSG, null));
            }
        } else if (name.equals("testing result")){
            if(student.testingIsPassed){
                pw.print(MessageManager.getMessage(lang, TestMessages.TEST_PASSED, null).toLowerCase());
            } else {
                pw.print(MessageManager.getMessage(lang, TestMessages.TEST_NOT_PASSED, null).toLowerCase());
            }
        } else if (name.equals("easy")){
            pw.print(student.easy);
        } else if (name.equals("middle")){
            pw.print(student.middle);
        } else if (name.equals("difficult")){
            pw.print(student.difficult);
        } else if (name.equals("mark")){
            pw.print(student.mark);
        }
//        else if (name.equals("more")){
//            pw.print("<a href=\"privatetestreport?mainTestingID="+mainTestingID+"&testingID="+student.testingID+"&studentID="+student.studentID+"&"+
//                    params.getFullParams() + "\" class=\"href\" title=\""+trsf.getHTMLString(student.name)+"\">");
//            pw.print(MessageManager.getMessage(lang, Constants.VIEW_REPORT, null));
//            pw.print("</a>");
//        }
        else if (name.equals("more-attestat")) {
        	pw.print("<a href=\"test_attestat_report?mainTestingID="+mainTestingID+"&testingID="+student.testingID+"&studentID="+student.studentID+"\""+
                    " class=\"href\" title=\""+trsf.getHTMLString(student.name)+"\">"); //params.getFullParams() +
            pw.print(MessageManager.getMessage(lang, Constants.VIEW_ATTESTAT, null));
            pw.print("</a>");
        } else if (name.equals("more")) {
        	pw.print("<a href=\"testing_analysis?mainTestingID="+mainTestingID+"&testingID="+student.testingID+"&studentID="+student.studentID+"\""+
                    " class=\"href\" title=\""+trsf.getHTMLString(student.name)+"\">");
            pw.print(MessageManager.getMessage(lang, Constants.VIEW_REPORT, null));
            pw.print("</a>");
        }
    }

}
