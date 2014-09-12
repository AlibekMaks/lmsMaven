package arta.filecabinet.servlet.students.upload.file;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.tests.test.list.TestsSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class MainPageHandler extends PageContentHandler {

    Person person;
    int lang;
    Message message;
    ServletContext servletContext;
    String testName;
    int testSubjectID;
    TestsSearchParams params;

    public MainPageHandler(Person person, int lang, Message message, ServletContext servletContext,
                                 String testName, int testSubjectID, TestsSearchParams params) {
        this.person = person;
        this.lang = lang;
        this.message = message;
        this.servletContext = servletContext;
        this.testName = testName;
        this.testSubjectID = testSubjectID;
        this.params = params;
    }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("tests/import/import2.txt").read(servletContext), pw,
                new PageHandler(lang, message, testName, testSubjectID, servletContext, params)).parse();
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() { return lang; }

    public int getRole() { return person.getRoleID(); }
}
