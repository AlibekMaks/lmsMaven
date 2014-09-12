package arta.registrar.tutor.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.registrar.tutor.logic.Registrar;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class RegistrarCardMainHandler extends PageContentHandler {

    int month;
    int year;
    Registrar registrar;
    int lang;
    Person person;
    ServletContext servletContext;
    int studygroupID;
    int subgroupID;
    Message message;


    public RegistrarCardMainHandler(int month, int year, Registrar registrar, int lang,
                                    Person person, ServletContext servletContext,
                                    int studygroupID, int subgroupID, Message message) {
        this.month = month;
        this.year = year;
        this.registrar = registrar;
        this.lang = lang;
        this.person = person;
        this.servletContext = servletContext;
        this.studygroupID = studygroupID;
        this.subgroupID = subgroupID;
        this.message = message;
    }

    public void getHeader(PrintWriter pw) {


    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("registrar/tutor/card.txt").read(servletContext), pw,
            new RegistrarCardHandler(month, year, registrar, lang, servletContext, studygroupID,
                    subgroupID, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
