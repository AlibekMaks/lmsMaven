package arta.subjects.html;

import arta.common.html.handler.PageGenerator;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SubjectsListMain extends PageContentHandler {

    Person person;
    int lang;
    ServletContext servletContext;
    SearchParams params;
    Message message;
    String return_link;


    public SubjectsListMain(Person person, int lang, ServletContext servletContext, SearchParams params,
                            Message message, String return_link) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.message = message;
        this.return_link = return_link;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("subjects/list.txt").read(servletContext), pw,
                new SubjectsList(params, servletContext, lang, message, return_link)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
