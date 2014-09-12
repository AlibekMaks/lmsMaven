package arta.lessonmaterials.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.common.logic.util.Date;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.lessonmaterials.logic.TutorBook;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorBookCardMainHandler extends PageContentHandler {

    int lang;
    Message message;
    ServletContext servletContext;
    Person person;
    TutorBook tutorBook;
    SearchParams params;


    public TutorBookCardMainHandler(int lang, Message message, ServletContext servletContext,
                                    Person person, TutorBook tutorBook,
                                    SearchParams params) {
        this.lang = lang;
        this.message = message;
        this.servletContext = servletContext;
        this.person = person;
        this.tutorBook = tutorBook;
        this.params = params;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("lessonmaterials/lessonmaterial.card.txt").read(servletContext), pw,
            new TutorBookCardHandler(lang, tutorBook, params, servletContext,
                    message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
