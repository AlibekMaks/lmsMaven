package arta.tests.reports.html.privateReports;

import arta.common.logic.util.Languages;
import arta.exams.logic.Ticket;
import arta.settings.logic.Settings;
import arta.tests.common.TestMessages;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.testing.logic.TestingMessages;
import arta.check.logic.Testing;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.students.Student;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import kz.arta.plt.common.Person;

public class TestingAnalysisHandler extends PageContentHandler {

    int lang;
    int roleID;
    int testingId;
    int mainTestingID;
    Testing testing;
    Student student;
    boolean print = false;
    StringBuffer report;
    CommonTestSearchParams params;
    String return_link;
    //CommonTestReportView report;
    List<TestResultSubject> resultSubjects;
    ServletContext servletContext;
    Ticket ticket;
    
    Settings settings = new Settings();

    public TestingAnalysisHandler(int lang, int roleID, int testingId, int studentId, int mainTestingID, CommonTestSearchParams params, String return_link,
                                 ServletContext servletContext, boolean print, StringBuffer report) {
        this.lang = lang;
        this.roleID = roleID;
        this.testingId = testingId;
        this.mainTestingID = mainTestingID;
        this.params = params;
        this.return_link = return_link;
        this.servletContext = servletContext;
        this.print = print;
        this.report = report;

        settings.load();
        if(settings.usesubjectball){
            settings.loadSubjectsMark();
        }

        this.student = new Student();
        this.student.loadById(studentId);

        testing = new Testing();
        testing.studentID = studentId;
        testing.mainTestingID = mainTestingID;
        testing.testingID = testingId;
        testing.load();
        testing.getTestingIsPassed(studentId);
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        /*report = new CommonTestReportView(testingId);
        report.load(lang);*/
        
        resultSubjects = TestResultSubjectsManager.myload(testingId, mainTestingID, student.getPersonID(), lang);

        ticket = new Ticket();
          ticket.getTestingTicketForStudent(student.getPersonID(), mainTestingID);
          ticket.loadById(ticket.ticketID, lang, true);

        InnerHandler handler = new InnerHandler();
        FileReader fileReader = null;
        if(print){
            fileReader = new FileReader("tests/reports/private/mytestingAnalysis.print.txt");
        } else {
            fileReader = new FileReader("tests/reports/private/mytestingAnalysis.txt");
        }
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) {
//        pw.print("<script language=\"JavaScript\">\n");
//        pw.print("function disabledCheckboxes() {\n");
//        pw.print("   var objs = document.getElementsByTagName(\"input\");\n");
//        pw.print("   for (var i = 0; i < objs.length; i++) {\n");
//        pw.print("     objs[i].disabled = true;\n");
//        pw.print("   } \n");
//        pw.print("}\n");
//        pw.print("</script>\n");
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
    
    class InnerHandler extends TemplateHandler {

        StringBuffer str;

        public InnerHandler() {
            FileReader fileReader = null;
            if (print){
                fileReader = new FileReader("tests/reports/private/singleSubject.print.txt");
            } else {
                fileReader = new FileReader("tests/reports/private/singleSubject.txt");
            }
            str = fileReader.read(servletContext);
        }

        public void replace(String name, PrintWriter pw) {
            /*if (name.equals("paper")){
                pw.print(MessageManager.getMessage(lang, TestMessages.COMMON_REPORT_PAPER, null));
            } else if (name.equals("report name")){
                pw.print(report.testingName);
            } else if (name.equals("testing date")){
                pw.print(MessageManager.getMessage(lang, TestMessages.DATE, null));
            } else if (name.equals("date value")){
                pw.print(report.date.getInputValue());
            } else if (name.equals("questions count")){
                pw.print(MessageManager.getMessage(lang, TestMessages.QUESTIONS_COUNT, null));
            } else if (name.equals("questions count value")){
                pw.print(report.questionsCount);
            } else if (name.equals("easy questions count")){
                pw.print(MessageManager.getMessage(lang, TestMessages.EASY_QUESTIONS_COUNT, null));
            } else if (name.equals("easy questions count value")){
                pw.print(report.easyCount);
            } else if (name.equals("middle question count")){
                pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTIONS_COUNT, null));
            } else if (name.equals("middle questions counr value")){
                pw.print(report.middleCount);
            } else if (name.equals("difficult questions count")){
                pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT_QUESTIONS_COUNT, null));
            } else if (name.equals("difficult questions count value")){
                pw.print(report.difficultCount);
            } else if (name.equals("progress")){
                if (Constants.SHOW_TEST_REPORT)
                    pw.print(MessageManager.getMessage(lang, TestMessages.PROGRESS, null)+":");
            } else if (name.equals("progress value")){
                if (Constants.SHOW_TEST_REPORT)
                    pw.print(report.progress);
            } else if (name.equals("quality percent")){
                if (Constants.SHOW_TEST_REPORT)
                    pw.print(MessageManager.getMessage(lang, TestMessages.QUALITY_PERCENT, null)+":");
            } else if (name.equals("quality percent value")){
                if (Constants.SHOW_TEST_REPORT)
                    pw.print(report.quality);
            } else*/ if (name.equals("student name")){
                pw.print(MessageManager.getMessage(lang, TestMessages.STUDENT_FIO, null));
            } else if (name.equals("right answers")){
                pw.print(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWERS, null));
            } else if (name.equals("test result")){
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.TEST_RESULTS , null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.TEST_RESULTS , null));
            } else if (name.equals("mark")){
                pw.print(MessageManager.getMessage(lang, TestMessages.MARK, null));
            } else if (name.equals("easy")){
                pw.print(MessageManager.getMessage(lang, TestMessages.EASY, null));
            } else if (name.equals("middle")){
                pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE, null));
            } else if (name.equals("difficult")){
                pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT, null));
            } else if (name.equals("subjects")){
                for (int i=0; i<resultSubjects.size(); i++){
                    StringBuffer tmp = new StringBuffer(str);
                    SingleSubjectHandler handler = new SingleSubjectHandler(lang, resultSubjects.get(i), settings, testingId, params);
                    Parser parser = new Parser(tmp, pw, handler);
                    parser.parse();
                }
            } else if (name.equals("return link")){
                //pw.print("testcommonreport?mainTestingID="+mainTestingID+"&testingID=" + testingId + "&" + params.getFullParams());
                pw.print(return_link);
            } else if (name.equals("return title")){
                pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
            } else if (name.equals("print link1")){
                pw.print("testing_analysis?mainTestingID="+mainTestingID+"&testingID="+testingId + "&studentID="+testing.studentID+"&print=true");
            } else if (name.equals("print link2")){
                pw.print("privatetestreport?mainTestingID="+mainTestingID+"&testingID="+testingId + "&studentID="+testing.studentID+"&print=true");
            } else if (name.equals("print title1")){
                pw.print(MessageManager.getMessage(lang, TestMessages.VERSION_FOR_PRINT, null));
            } else if (name.equals("print title2")){
                pw.print(MessageManager.getMessage(lang, TestMessages.PRINT_ANSWERS_CHALLENGER, null));
            } else if (name.equals("size")){
                pw.print(Constants.MENU_IMAGE_SIZE);
            } else if (name.equals("page header")){
                pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT, null));
            } else if (name.equals("report")){
                if (report != null){
                    pw.print(report);

                    pw.print("<script language=\"JavaScript\">\n" +
                            "function disabledCheckboxes() {\n" +
                            "   var objs = document.getElementsByTagName(\"input\");\n" +
                            "   for (var i = 0; i < objs.length; i++) {\n" +
                            "     objs[i].disabled = true;\n" +
                            "   } \n" +
                            "}\n" +
                            "disabledCheckboxes();" +
                            "</script>");
                }
                else
                    pw.print("&nbsp;");
            } else if (name.equals("ticket")){
                if (ticket != null && ticket.ticketID > 0){
                    if(testing.testingIsPassed){
                        ticket.writeTicket(pw, lang);
                    } else {
                        pw.print("&nbsp;");
                    }
                } else {
                    pw.print("&nbsp;");
                }
            } else if (name.equals("pretendent.msg")) {
            	//pw.print(MessageManager.getMessage(lang, Constants.STUDENT_MESSAGE, null));
            	pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.BIDDER , null) + "/" +
                         MessageManager.getMessage(Languages.KAZAKH, TestMessages.BIDDER , null));
            } else if (name.equals("pretendent")) {
            	String studentName = Person.getFullName(student.getLastname(), student.getFirstname(), student.getPatronymic());
            	pw.print(studentName);
            } else if (name.equals("post.msg")) {
            	//pw.print(MessageManager.getMessage(lang, FileCabinetMessages.CLASS, null));
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.POST, null) + "/" +
                         MessageManager.getMessage(Languages.KAZAKH, TestMessages.POST, null));
            } else if (name.equals("post")) {
            	pw.print(student.getClassName());
            } else if (name.equals("testing.date.msg")) {
            	pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_DATE, null));
            } else if (name.equals("actual address msg")) {
                //pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_DATE, null));
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.ACTUAL_ADDRESS, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.ACTUAL_ADDRESS, null));
            } else if (name.equals("results msg")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.RESULTS, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.RESULTS, null));
            } else if (name.equals("registration address msg")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.REGISTRATION_ADDRESS, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.REGISTRATION_ADDRESS, null));
            } else if (name.equals("subject")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.TEST_SUBJECT, null) + "/" +
                         MessageManager.getMessage(Languages.KAZAKH, TestMessages.TEST_SUBJECT, null));

            } else if (name.equals("exam")) {
                if(testing.testingIsPassed){
                    Ticket ticket = new Ticket();
                    ticket.getTestingTicketForStudent(testing.studentID, mainTestingID);

                    if(ticket.ticketID > 0) {
                        pw.print("<table border=0 width=\"100%\">");
                        pw.print("<tr>");
                        pw.print("<td height=\"50px\">");
                        pw.print("</td>");
                        pw.print("</tr>");

                        pw.print("<tr>");
                        pw.print("<td width=* align=\"center\" >");
                        ticket.loadById(ticket.ticketID, lang, true);
                        ticket.writeTicket(pw, lang);
                        pw.print("</td>");
                        pw.print("</tr>");
                        pw.print("</table>");
                    }
                }
            } else if (name.equals("questions.count")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.QUESTIONS_COUNT, null) + "/" +
                         MessageManager.getMessage(Languages.KAZAKH, TestMessages.QUESTIONS_COUNT, null));
            }
            else if (name.equals("right.answers.count")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.NUMBER_OF_RIGHT_ANSWERS, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.NUMBER_OF_RIGHT_ANSWERS, null));
            } else if (name.equals("summary")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.TOTAL, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.TOTAL, null));
            } else if (name.equals("sum.questions.count")) {
            	pw.print(testing.questions.size());
            } else if (name.equals("sum.right.answers.count")) {
            	pw.print(testing.mark);
            } else if (name.equals("redcolor")) {
                if(!testing.testingIsPassed) pw.print(" style=\"color:red;\" ");
            } else if (name.equals("text test result")) {
                if(testing.testingIsPassed){
                    pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.TEST_PASSED, null) + "/" +
                             MessageManager.getMessage(Languages.KAZAKH, TestMessages.TEST_PASSED, null));
                } else {
                    pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.TEST_NOT_PASSED, null) + "/" +
                             MessageManager.getMessage(Languages.KAZAKH, TestMessages.TEST_NOT_PASSED, null));
                }
            } else if (name.equals("elapsed_time")) {

                long diff = testing.finishTime.getTime() - testing.startTime.getTime();
                long hour = (diff / 1000 / 60 / 60) % 24;
                long minutes = (diff / 1000 / 60) % 60;
                long seconds = (diff / 1000) % 60;

                String dateStr = String.format("%02d:%02d:%02d", hour, minutes, seconds);

                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.ELAPSED_TIME, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.ELAPSED_TIME, null) + " : " + dateStr);
            } else if (name.equals("date")) {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String date = df.format(testing.startTime);

                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.DATE2, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.DATE2, null) + ": " + date);
            } else if (name.equals("signature_of_the_applicant")) {
                pw.print(MessageManager.getMessage(Languages.RUSSIAN, TestMessages.SIGNATURE_OF_THE_APPLICANT, null) + "/" +
                        MessageManager.getMessage(Languages.KAZAKH, TestMessages.SIGNATURE_OF_THE_APPLICANT, null));
            }
        }

    }
    
    class SingleSubjectHandler extends TemplateHandler {

        int lang;
        TestResultSubject subject;
        int testingID;
        CommonTestSearchParams params;
        StringTransform trsf = new StringTransform();
        Settings settings;

        public SingleSubjectHandler(int lang, TestResultSubject subject, Settings settings, int testingID, CommonTestSearchParams params) {
            this.lang = lang;
            this.subject = subject;
            this.testingID = testingID;
            this.params = params;
            this.settings = settings;
        }

        public void replace(String name, PrintWriter pw) {
            if (name.equals("subject.name")) {
            	pw.print(subject.subjectName);
            } else if (name.equals("questions.count")) {
            	pw.print(subject.questionsCount);
            } else if (name.equals("right.answers.count")) {
            	pw.print(subject.rightAnswersCount);
            } else if (name.equals("percentage")) {
            	pw.print(subject.rightAnswersPersentage);
            } else if (name.equals("redcolor")) {
                if(!subject.isPassed) pw.print(" style=\"color:red;\" ");
            }
        }

    }
    
}


