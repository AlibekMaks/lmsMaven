package arta.departments.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.departments.logic.Department;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.html.SubjectCard;
import arta.subjects.logic.Subject;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class DepartmentCardMain extends PageContentHandler {

    Person person;
    ServletContext servletContext;
    int lang;
    SearchParams params;
    Message message;
    Department department;


    public DepartmentCardMain(Person person, ServletContext servletContext, int lang,
                              Department department, SearchParams params, Message message) {
        this.person = person;
        this.servletContext = servletContext;
        this.lang = lang;
        this.department = department;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("departments/card.txt").read(servletContext), pw,
            new DepartmentCard(department, lang, params, servletContext, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
