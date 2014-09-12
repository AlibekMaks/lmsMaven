package arta.filecabinet.html.students;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.students.SearchStudent;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;


public class StudentSingle extends TemplateHandler {

    SearchStudent student;
    SearchParams params;
    int lang;
    int recordNumber;
    StringTransform trsf = new StringTransform();


    public StudentSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set(SearchStudent student, int recordNumber){
        this.student = student;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("studentID")){
            pw.print(student.id);
        } else if (name.equals("name")){
            pw.print("<a href=\"student?studentID="+student.id+"&"+params.getParamsWithoutRecord()+
                    "&recordNumber="+recordNumber+"\" class=\"href\" >");
            pw.print(trsf.getHTMLString(student.name));
            pw.print("</a>");
        } else if (name.equals("class")){
            pw.print(student.className);
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("object", student.name);
            pw.print("<a href=\"students?option=-1&studentID="+student.id+"&"+params.getParams()+"\" " +
                    " title=\""+trsf.getHTMLString(student.name)+"\" onClick='return confirm(\""+
                    MessageManager.getMessage(lang, Constants.DO_YOU_REALLY_WANT_TO_DELETE_STUDENT, prop)+"\");' " +
                    " class=\"href\">");            
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" border=0 height=\"16px\">");
            pw.print("</a>");
        }
    }
}
