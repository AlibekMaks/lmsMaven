package arta.groups.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.SimpleObject;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.SubjectsSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class GroupsAddMain extends PageContentHandler {

    int lang;
    ServletContext servletContext;
    Person person;
    SearchParams params;
    SubjectsSearchParams sbParams;
    SimpleObject studyClass;


    public GroupsAddMain(int lang, ServletContext servletContext, Person person,
                         SearchParams params, SubjectsSearchParams sbParams, SimpleObject studyClass) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.person = person;
        this.params = params;
        this.sbParams = sbParams;
        this.studyClass = studyClass;
    }

    public void getHeader(PrintWriter out) {
        new Parser(new FileReader("groups/group.add.js").read(servletContext), out,
                 new GroupsAddJS(lang)).parse();
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("groups/group.add.txt").read(servletContext), pw,
                new GroupsAdd(servletContext, lang, params, sbParams, studyClass)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
