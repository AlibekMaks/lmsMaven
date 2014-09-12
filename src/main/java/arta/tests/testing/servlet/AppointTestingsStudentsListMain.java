package arta.tests.testing.servlet;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class AppointTestingsStudentsListMain extends PageContentHandler{

    Person person;
    Message message;
    StudentSearchParams params;
    ServletContext servletContext;
    int lang;
    Testing testing;


    public AppointTestingsStudentsListMain(Person person, Message message, Testing testing, StudentSearchParams params,
                                           ServletContext servletContext, int lang) {
        this.person = person;
        this.message = message;
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.testing = testing;
    }

    public void getHeader(PrintWriter out) {
        out.println("<script language=\"javascript\" src=\"jscripts/JavaScriptUtil.js\"></script>\n");
        out.println("<script language=\"javascript\" src=\"jscripts/Parsers.js\"></script>\n");
        out.println("<script language=\"javascript\" src=\"jscripts/InputMask.js\"></script>\n");
        JScript.writeSetupFunction(out, lang);

        out.print(HTMLCalendar.getScriptSRC());
        HTMLCalendar calendar = new HTMLCalendar(lang);
        calendar.initializeCalendar(out);
        out.println("<script language=\"javascript\" src=\"jscripts/jquery-1.10.2.js\"></script>\n");
        out.println("<script language=\"javascript\" src=\"jscripts/jstree/jquery.jstree.js\"></script>\n");


        out.print("<script language=\"JavaScript\">");
        JScript.myPrintSelectAllFunction2(out, "std", "all", "appointtests");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) {
        pw.write(" onload=\"setup()\" ");
    }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("students/list.copy.html").read(servletContext), pw,
                new AppointTestingsStudentsList(person, message, servletContext, testing, params, lang)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }

}
