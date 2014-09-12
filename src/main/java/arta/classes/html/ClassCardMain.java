package arta.classes.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.classes.logic.StudyClass;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ClassCardMain extends PageContentHandler {

    int lang;
    Person person;
    StudyClass studyClass;
    SearchParams params;
    ServletContext servletContext;
    Message message;

    public ClassCardMain(int lang, Person person, StudyClass studyClass,
                         SearchParams params, ServletContext servletContext, Message message) {
        this.lang = lang;
        this.person = person;
        this.studyClass = studyClass;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("classes/card.txt").read(servletContext), pw,
                    new ClassCard(servletContext, lang, params, studyClass, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
