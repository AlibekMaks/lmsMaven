package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.tests.testing.logic.TestingStudent;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:59:44
 * To change this template use File | Settings | File Templates.
 */
public class StudentsToAddRecordHandler extends TemplateHandler {

    StringTransform trsf;

    TestingStudent student;


    public StudentsToAddRecordHandler(StringTransform trsf) {
        this.trsf = trsf;
    }


    public void setStudent(TestingStudent student) {
        this.student = student;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("studentID")){
            pw.print(student.studentID);
        } else if (name.equals("name")){
            pw.print(trsf.getHTMLString(student.studentName));
            pw.print("<input type=\"hidden\" name=\"std_"+student.studentID+"_name\" value=\""+trsf.getHTMLString(student.studentName)+"\">");
            pw.print("<input type=\"hidden\" name=\"std_"+student.studentID+"_class\" value=\""+trsf.getHTMLString(student.className)+"\">");
            pw.print("<input type=\"hidden\" name=\"std_"+student.studentID+"_subject\" value=\""+trsf.getHTMLString(student.subjectName)+"\">");
            pw.print("<input type=\"hidden\" name=\"std_"+student.studentID+"_subgroupID\" value=\""+student.subgroupID+"\">");
        } else if (name.equals("class name")){
            pw.print(trsf.getHTMLString(student.className));
        } else if (name.equals("subject")){
            pw.print(trsf.getHTMLString(student.subjectName));
        }
    }
}
