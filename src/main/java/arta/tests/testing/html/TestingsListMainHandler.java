package arta.tests.testing.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.tests.testing.logic.TestingsSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TestingsListMainHandler extends PageContentHandler {

    TestingsSearchParams params;
    ServletContext servletContext;
    int lang;
    int roleID;
    Person person;
    Message message;
    int tutorID;
    boolean dbchecker = false;
    HTMLCalendar calendar;


    public TestingsListMainHandler(TestingsSearchParams params, ServletContext servletContext,
                                   int lang, int roleID, Person person, Message message, int tutorID, boolean dbchecker) {
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.roleID = roleID;
        this.person = person;
        this.message = message;
        this.tutorID = tutorID;
        this.dbchecker = dbchecker;
        this.calendar = new HTMLCalendar(lang);
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        calendar.initializeCalendar(out);
        calendar.writeDIV(out);
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        String templateFileName = "tests/testings/mytestings.list.html";
        if(person.isAdministrator) {
            templateFileName = "tests/testings/testings.list-superAdmin.html";
        }

        new Parser(new FileReader(templateFileName).read(servletContext),
                pw,
                new TestingsListHandler(lang, person, params, servletContext, message, tutorID, calendar)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
