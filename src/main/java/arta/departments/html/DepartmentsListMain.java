package arta.departments.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class DepartmentsListMain extends PageContentHandler {

    Person person;
    int lang;
    ServletContext servletContext;
    SearchParams params;
    Message message;


    public DepartmentsListMain(Person person, int lang, ServletContext servletContext, SearchParams params,
                               Message message) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("departments/list.txt").read(servletContext), pw,
                new DepartmentsList(params, servletContext, lang, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
