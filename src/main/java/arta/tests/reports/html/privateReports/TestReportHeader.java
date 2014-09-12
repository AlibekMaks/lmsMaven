package arta.tests.reports.html.privateReports;


import arta.tests.common.TestMessages;
import arta.check.logic.Testing;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.students.Student;
import arta.classes.ClassMessages;
import arta.subjects.logic.SubjectMessages;

public class TestReportHeader {

    public StringBuffer getHeader(Testing testing, Student student, String tutorName,
                                  String subject, int lang, Date date){
        StringBuffer header = new StringBuffer();
        header.append("<table border=0 width=\"100%\" class=\"table\">");
        header.append("<tr><td class=\"header\" align=\"center\" colspan=2>");
        header.append(MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT, null));
        header.append("</td></tr>");
        header.append("<tr><td width=\"20%\" ><b>");
        header.append(MessageManager.getMessage(lang, Constants.STUDENT_MESSAGE, null));
        header.append("</td><td>");
        header.append(student.getFullName());
        header.append("</td></tr>");
        header.append("<tr><td><b>");
        header.append(MessageManager.getMessage(lang, ClassMessages.CLASS, null));
        header.append("</td><td >"+student.getClassName()+"</td></tr>");
//        header.append("<tr><td><b>");
//        header.append(MessageManager.getMessage(lang, SubjectMessages.SUBJECT, null));
//        header.append("</td><td >");
//        header.append(subject);
//        header.append("</td></tr>");

        header.append("<tr><td ><b>");
        header.append(MessageManager.getMessage(lang, TestMessages.TUTOR, null));
        header.append("</td><td >");
        header.append(tutorName);
        header.append("</td></tr>");

        header.append("<tr><td ><b>");
        header.append(MessageManager.getMessage(lang, TestMessages.DATE, null));
        header.append("</td><td >");
        header.append(date.getDate());
        header.append("</td></tr>");

        header.append("<tr><td ><b>");
        header.append(MessageManager.getMessage(lang, TestMessages.MARK, null));
        header.append("</td><td >");
        header.append(testing.mark);
        header.append("</td></tr>");

        header.append("<tr><td class=\"header1\" align=\"center\" colspan=2>");
        header.append(MessageManager.getMessage(lang, TestMessages.TESTING_QUESTIONS, null));
        header.append("</td></tr>");

        header.append("</table>");

        return header;
    }

}
