package arta.tests.testing.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.tests.testing.logic.TestingStudentsSearchParams;
import arta.tests.testing.logic.Testing;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 17:45:22
 * To change this template use File | Settings | File Templates.
 */
public class StudentsToAddListMainHandler extends PageContentHandler {

    int lang;
    Person person;
    ServletContext servletContext;
    Testing testing;
    TestingStudentsSearchParams params;


    public StudentsToAddListMainHandler(int lang, Person person,
                                        ServletContext servletContext, Testing testing,
                                        TestingStudentsSearchParams params) {
        this.lang = lang;
        this.person = person;
        this.servletContext = servletContext;
        this.testing = testing;
        this.params = params;
    }

    public void getHeader(PrintWriter out) {
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/students.to.add.html").read(servletContext),
                pw, new StudentsToAddListHandler(lang, testing,
                        servletContext, params, person.getPersonID())).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
