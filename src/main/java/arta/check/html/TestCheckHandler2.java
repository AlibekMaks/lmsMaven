package arta.check.html;

import arta.check.logic.Testing;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.students.Student;
import arta.settings.logic.Settings;
import arta.tests.common.TestMessages;
import arta.tests.reports.html.privateReports.TestingAnalysisHandler;
import arta.tests.reports.logic.privateReports.TestReport;
import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TestCheckHandler2 extends TemplateHandler {

    Testing testing;
    int lang;
    Settings settings;
    Student student;
    ServletContext servletContext;
    Ticket ticket;

    public TestCheckHandler2(Testing testing, int lang, Settings settings, ServletContext servletContext) {
        this.testing = testing;
        this.lang = lang;
        this.settings = settings;
        this.servletContext = servletContext;

        Student student = new Student();
        student.loadById(testing.studentID);

        if(testing.result_TestingIsPassed){ // Если прошел тестирование
            ticket = new Ticket();
            ticket.getTestingTicketForStudent(testing.studentID, testing.mainTestingID);
            ticket.loadById(ticket.ticketID, lang, true);
        }

    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("simple report")){
            if(!settings.show_report){
                StringBuffer template = new FileReader("tests/check/ResultSubject.txt").read(servletContext);
                StringBuffer tmp = new StringBuffer(template);

                TestCheckSingleHandler2 hd = new TestCheckSingleHandler2(testing, lang, settings);
                Parser parser = new Parser(tmp, pw, hd);
                parser.parse();
            }
        } else if (name.equals("subject report")){
            if(settings.show_report){
                TestingAnalysisHandler handler = new TestingAnalysisHandler(lang, -1, testing.testingID, testing.studentID,
                        testing.getMainTestingID(), null, "", servletContext, true, null);
                handler.getMainPart(pw);
            }
        } else if (name.equals("ticket")){
            if(ticket != null){
                ticket.writeTicket(pw, lang);
            }
        } else if (name.equals("answers report")){
            if(settings.show_answers){
                TestReport report = new TestReport(testing.studentID, testing.testingID);
                report.load();
                if (report != null) pw.print(report.report);
            }
        }
    }
}
