package arta.filecabinet.html.tutors;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorCardMain extends PageContentHandler {

    Person person;
    Tutor tutor;
    int lang;
    ServletContext servletContext;
    SearchParams params;
    Message message;


    public TutorCardMain(Person person, Tutor tutor, int lang,
                         ServletContext servletContext, SearchParams params,
                         Message message) {
        this.person = person;
        this.tutor = tutor;
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        HTMLCalendar calendar = new HTMLCalendar(lang);
        calendar.initializeCalendar(out);
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        TutorCard handler = new TutorCard(tutor, servletContext, lang, params, message);
        new Parser(new FileReader("tutors/card.txt").read(servletContext), pw, handler).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
