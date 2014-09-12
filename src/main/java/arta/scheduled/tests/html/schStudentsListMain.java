package arta.scheduled.tests.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.StudentSearchParams;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;


public class schStudentsListMain extends PageContentHandler {

    Person person;
    Message message;
    StudentSearchParams params;
    ServletContext servletContext;
    int lang;
    int mainTestingID;
    int testingID;
    boolean dbchecker = false;


    public schStudentsListMain(int mainTestingID, int testingID, Person person, Message message, StudentSearchParams params,
                               ServletContext servletContext, int lang, boolean dbchecker) {
        this.person = person;
        this.message = message;
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.mainTestingID = mainTestingID;
        this.testingID = testingID;
        this.dbchecker = dbchecker;
    }

    public void getHeader(PrintWriter out) {
        out.print("<script language=\"javascript\">");
        JScript.printSelectAllFunction(out, "std", "selectAll");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("sheduledTests/sch.list.txt").read(servletContext), pw,
                new schStudentsList(mainTestingID, testingID, message, servletContext, params, lang, dbchecker)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
