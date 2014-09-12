package arta.tests.testing.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TestingCardMainHandler extends PageContentHandler {

    Person person;
    int lang;
    ServletContext servletContext;
    Testing testing;
    Message message;


    public TestingCardMainHandler(Person person, int lang, ServletContext servletContext,
                                  Testing testing, Message message) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.testing = testing;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        HTMLCalendar calendar = new HTMLCalendar(lang);
        calendar.initializeCalendar(out);
        out.println("<script language=\"javascript\" src=\"jscripts\\jquery-1.10.2.js\"></script>\n");
        out.println("<script language=\"javascript\" src=\"jscripts\\jstree\\jquery.jstree.js\"></script>\n");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/card.txt").read(servletContext), pw,
            new TestingCardHandler(servletContext, lang, testing, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
