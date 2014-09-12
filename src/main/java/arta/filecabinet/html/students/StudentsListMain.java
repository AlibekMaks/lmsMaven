package arta.filecabinet.html.students;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.scheduled.tests.html.schStudentsList;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudentsListMain extends PageContentHandler {

    Person person;
    Message message;
    StudentSearchParams params;
    ServletContext servletContext;
    int lang;


    public StudentsListMain(Person person, Message message, StudentSearchParams params,
                            ServletContext servletContext, int lang) {
        this.person = person;
        this.message = message;
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
    }

    public void getHeader(PrintWriter out) {
        out.print("<script language=\"javascript\">");
        JScript.myPrintSelectAllFunction(out, "std", "all", "generate");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("students/list.txt").read(servletContext), pw,
                new StudentsList(message, servletContext, params, lang)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
