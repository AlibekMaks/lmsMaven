package arta.welcome.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.students.Student;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class WelcomePageMainHandler extends PageContentHandler{

    Person person;
    ServletContext servletContext;
    int lang;
    int roleID;
    int personID;

    public WelcomePageMainHandler(Person person, ServletContext servletContext, int lang, int roleID, int personID) {
        this.person = person;
        this.servletContext = servletContext;
        this.lang = lang;
        this.roleID = roleID;
        this.personID = personID;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("welcome/welcome.txt").read(servletContext), pw,
                new StudentWelcomePageHandler(person, lang, servletContext,roleID, personID)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
