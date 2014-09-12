package arta.studyroom.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudyRoomMainHandler extends PageContentHandler {

    int lang;
    Person person;
    ServletContext servletContext;


    public StudyRoomMainHandler(int lang, Person person, ServletContext servletContext) {
        this.lang = lang;
        this.person = person;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("studyroom/study.room.main.txt").read(servletContext), pw,
            new StudyRoomStartHandler(lang, servletContext, person)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
