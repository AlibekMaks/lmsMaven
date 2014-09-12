package arta.filecabinet.html.students;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudentCardMain extends PageContentHandler {

    int lang;
    ServletContext servletContext;
    SearchParams params;
    Student student;
    Message message;
    Person person;


    public StudentCardMain(int lang, ServletContext servletContext, SearchParams params,
                           Student student, Message message, Person person) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.student = student;
        this.message = message;
        this.person = person;
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        HTMLCalendar calendar = new HTMLCalendar(lang);
        calendar.initializeCalendar(out);
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("students/card.txt").read(servletContext), pw,
                new StudentCard(servletContext, lang, message, student, params)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
