package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.tests.testing.logic.TestingStudent;
import arta.tests.testing.logic.TestingMessages;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:25:05
 * To change this template use File | Settings | File Templates.
 */
public class AddedStudentsListRecordHandler extends TemplateHandler {

    StringTransform trsf;
    int lang;

    TestingStudent student;


    public AddedStudentsListRecordHandler(StringTransform trsf, int lang) {
        this.trsf = trsf;
        this.lang = lang;
    }


    public void setStudent(TestingStudent student) {
        this.student = student;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(student.studentName));
        } else if (name.equals("class name")){
            pw.print(trsf.getHTMLString(student.className));
        } else if (name.equals("delete link")){
            Properties prop = new Properties();
            prop.setProperty("name", student.studentName);
            pw.print("<a href=\"testingstudents?option=-1&studentID="+student.studentID+"&nocache="+ Rand.getRandString()+"\" " +
                    " onclick='return confirm(\""+ MessageManager.getMessage(lang, TestingMessages.CONFIRM_DELETE_STUDENT_FROM_TESTING, prop) +"\")' " +
                    " class=\"href\" >");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        } else if (name.equals("subject")){
            pw.print(trsf.getHTMLString(student.subjectName));
        }
    }
}
