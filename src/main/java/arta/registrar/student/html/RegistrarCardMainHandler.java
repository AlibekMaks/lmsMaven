package arta.registrar.student.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.filecabinet.logic.Person;
import arta.registrar.student.logic.StudentRegistrar;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class RegistrarCardMainHandler extends PageContentHandler {

    Person person;
    int lang;
    ServletContext servletContext;
    StudentRegistrar registrar;
    int month, year;


    public RegistrarCardMainHandler(Person person, int lang, ServletContext servletContext,
                                    StudentRegistrar registrar, int month, int year) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.registrar = registrar;
        this.month = month;
        this.year = year;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("registrar/student/student.registrar.txt").read(servletContext), pw,
                new RegistrarCardHandler(month, year, registrar, servletContext, lang)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
