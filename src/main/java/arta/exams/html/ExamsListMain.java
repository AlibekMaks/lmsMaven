package arta.exams.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ExamsListMain extends PageContentHandler {

    ServletContext servletContext;
    int lang;
    Person person;
    Message message;

    SearchParams params;


    public ExamsListMain(ServletContext servletContext, int lang, Person person, Message message,
                         SearchParams params) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.person = person;
        this.message = message;
        this.params = params;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("exams/list.html").read(servletContext), pw,
            new ExamsList(servletContext, lang, params, message)).parse();
    }

    public int getLanguage() { return lang; }

    public int getRole() { return person.getRoleID(); }
}
