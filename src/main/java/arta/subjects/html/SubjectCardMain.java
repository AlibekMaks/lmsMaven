package arta.subjects.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.settings.logic.Settings;
import arta.subjects.logic.Subject;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SubjectCardMain  extends PageContentHandler {

    Person person;
    ServletContext servletContext;
    int lang;
    Subject subject;
    SearchParams params;
    Message message;
    String return_link;
    Settings settings;


    public SubjectCardMain(Person person, ServletContext servletContext, int lang,
                           Subject subject, SearchParams params, Message message, String return_link, Settings settings) {
        this.person = person;
        this.servletContext = servletContext;
        this.lang = lang;
        this.subject = subject;
        this.params = params;
        this.message = message;
        this.return_link = return_link;
        this.settings=settings;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("subjects/card.txt").read(servletContext), pw,
            new SubjectCard(subject, lang, params, servletContext, message, return_link,settings)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
