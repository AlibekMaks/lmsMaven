package arta.tests.test.list;

import arta.common.logic.util.Date;
import arta.common.logic.util.Message;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class TutorTestsListMain extends PageContentHandler {

    int lang;
    ServletContext servletContext;
    Message message;
    Person person;
    TestsSearchParams params;

    public TutorTestsListMain(int lang, ServletContext servletContext, Message message,
                              Person person, TestsSearchParams params) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.message = message;
        this.person = person;
        this.params = params;
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {

        TutorTestsListHandler handler = new TutorTestsListHandler(lang, message,  servletContext, params, person);
        FileReader fileReader = new FileReader("tests/tests.list/list.edit.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) {

    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }

}
