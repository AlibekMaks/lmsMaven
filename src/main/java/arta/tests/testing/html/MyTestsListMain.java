package arta.tests.testing.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class MyTestsListMain extends PageContentHandler {

    Person person;
    int lang;
    SearchParams params;
    ServletContext servletContext;
    Testing testing;

    Message message;

    public MyTestsListMain(Person person, int lang, SearchParams params,
                           ServletContext servletContext, Testing testing) {
        this.person = person;
        this.lang = lang;
        this.params = params;
        this.servletContext = servletContext;
        this.testing = testing;
    }


    public MyTestsListMain(Person person, int lang, SearchParams params,
                           ServletContext servletContext, Testing testing, Message message) {
        this.person = person;
        this.lang = lang;
        this.params = params;
        this.testing = testing;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/mytests.to.add.list.html").read(servletContext), pw,
                new MyTestsListHandler(params, lang, testing, servletContext, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
