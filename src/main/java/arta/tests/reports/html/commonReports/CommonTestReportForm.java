package arta.tests.reports.html.commonReports;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.tests.common.TestMessages;
import arta.tests.reports.logic.commonReports.CommonTestReportView;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class CommonTestReportForm extends TemplateHandler {

    int mainTestingID;
    int lang;
    CommonTestReportView report;
    CommonTestSearchParams params;
    boolean print;
    ServletContext servletContext;

    StringBuffer str;

    public CommonTestReportForm(int mainTestingID, int lang, CommonTestReportView report, boolean print,
                                ServletContext servletContext, CommonTestSearchParams params) {
        this.mainTestingID = mainTestingID;
        this.lang = lang;
        this.report = report;
        this.print = print;
        this.params = params;
        this.servletContext = servletContext;
        FileReader fileReader = null;
        if (print){
            fileReader = new FileReader("tests/reports/common/singleStudent.txt");
        } else {
            fileReader = new FileReader("tests/reports/common/singleStudent.print.txt");        
        }
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("paper")){
            pw.print(MessageManager.getMessage(lang, TestMessages.COMMON_REPORT_PAPER, null));
        } else if (name.equals("report name")){
            pw.print(report.testingName);
        } else if (name.equals("testing result")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_RESULT, null));
        } else if (name.equals("testing date")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DATE, null));
        } else if (name.equals("date value")){
            pw.print(report.date.getInputValue());
        }
//        else if (name.equals("questions count")){
//            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTIONS_COUNT, null));
//        } else if (name.equals("questions count value")){
//            //pw.print(report.questionsCount);
//            pw.print(report.kz_questionsCount + "(" + MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null) + ")" + " / " +
//                     report.ru_questionsCount + "(" + MessageManager.getMessage(lang, TestMessages.RUS_MSG, null) + ")");
//        } else if (name.equals("easy questions count")){
//            pw.print(MessageManager.getMessage(lang, TestMessages.EASY_QUESTIONS_COUNT, null));
//        } else if (name.equals("easy questions count value")){
//            //pw.print(report.easyCount);
//            pw.print(report.kz_easyCount + "(" + MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null) + ")" + " / " +
//                     report.ru_easyCount + "(" + MessageManager.getMessage(lang, TestMessages.RUS_MSG, null) + ")");
//        } else if (name.equals("middle question count")){
//            pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTIONS_COUNT, null));
//        } else if (name.equals("middle questions counr value")){
//            //pw.print(report.middleCount);
//            pw.print(report.kz_middleCount + "(" + MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null) + ")" + " / " +
//                     report.ru_middleCount + "(" + MessageManager.getMessage(lang, TestMessages.RUS_MSG, null) + ")");
//        } else if (name.equals("difficult questions count")){
//            pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT_QUESTIONS_COUNT, null));
//        } else if (name.equals("difficult questions count value")){
//            //pw.print(report.difficultCount);
//            pw.print(report.kz_difficultCount + "(" + MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null) + ")" + " / " +
//                     report.ru_difficultCount + "(" + MessageManager.getMessage(lang, TestMessages.RUS_MSG, null) + ")");
//        } else if (name.equals("progress")){
//            if (Constants.SHOW_TEST_REPORT)
//                pw.print(MessageManager.getMessage(lang, TestMessages.PROGRESS, null)+":");
//        } else if (name.equals("progress value")){
//            if (Constants.SHOW_TEST_REPORT)
//                pw.print(report.progress);
//        } else if (name.equals("quality percent")){
//            if (Constants.SHOW_TEST_REPORT)
//                pw.print(MessageManager.getMessage(lang, TestMessages.QUALITY_PERCENT, null)+":");
//        } else if (name.equals("quality percent value")){
//            if (Constants.SHOW_TEST_REPORT)
//                pw.print(report.quality);
//        }
          else if (name.equals("student name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.STUDENT_FIO, null));
        } else if (name.equals("class name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.STUDENT_POST, null));
        } else if (name.equals("test language")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_LANGUAGE, null));
        } else if (name.equals("right answers")){
            pw.print(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWERS, null));
        } else if (name.equals("mark")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MARK, null));
        } else if (name.equals("easy")){
            pw.print(MessageManager.getMessage(lang, TestMessages.EASY, null));
        } else if (name.equals("middle")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE, null));
        } else if (name.equals("difficult")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT, null));
        } else if (name.equals("students")){
            for (int i=0; i<report.students.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleStudentHandler handler = new SingleStudentHandler(lang, report.students.get(i), report.testingID,
                        report.mainTestingID, params);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("tutor")){
            pw.print(MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE, null));
        } else if (name.equals("tutor value")){
            pw.print(report.tutorName);
        } else if (name.equals("return link")){
            pw.print("testreports?mainTestingID="+mainTestingID+"&"+params.getFullParams());
        } else if (name.equals("return title")){            
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
//        } else if (name.equals("return title")){
//            pw.print("testreports?mainTestingID="+mainTestingID+"&"+params.getFullParams());
        } else if (name.equals("print link")){
            pw.print("testcommonreport?mainTestingID="+mainTestingID+"&testingID="+report.testingID + "&print=true");
        } else if (name.equals("print title")){
            pw.print(MessageManager.getMessage(lang, TestMessages.VERSION_FOR_PRINT, null));
        } else if (name.equals("xls link")){
            pw.print("commonxlsrepotr?mainTestingID="+mainTestingID+"&testingID="+report.testingID);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("page header")){
            StringBuffer str = new StringBuffer(MessageManager.getMessage(lang, TestMessages.COMMON_REPORT_PAPER, null));
            str.append("<br>");
            str.append(report.testingName);
            pw.print(str);
        }
    }

}
