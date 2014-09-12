package arta.exams.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.exams.logic.Exam;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ExamCardMain extends PageContentHandler {

    int lang;
    Person person;
    Exam exam;
    SearchParams params;
    ServletContext servletContext;
    Message message;

    public ExamCardMain(int lang, Person person, Exam exam,
                        SearchParams params, ServletContext servletContext, Message message) {
        this.lang = lang;
        this.person = person;
        this.exam = exam;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("exams/card.txt").read(servletContext), pw,
                    new ExamCard(servletContext, lang, params, exam, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
