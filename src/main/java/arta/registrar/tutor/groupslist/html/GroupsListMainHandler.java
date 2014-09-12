package arta.registrar.tutor.groupslist.html;

import arta.filecabinet.logic.Person;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class GroupsListMainHandler extends PageContentHandler {

    ServletContext servletContext;
    int lang;
    Person person;


    public GroupsListMainHandler(ServletContext servletContext, int lang, Person person) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.person = person;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("registrar/tutor/groups.list.txt").read(servletContext), pw,
                new GroupsListHandler(servletContext, lang, person.getPersonID())).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
