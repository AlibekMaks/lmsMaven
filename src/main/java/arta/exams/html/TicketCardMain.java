package arta.exams.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;
import arta.exams.ExamMessages;
import arta.exams.logic.Exam;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class TicketCardMain extends PageContentHandler {

    int lang;
    Person person;
    int examID;
    Ticket ticket;
    SearchParams params;
    ServletContext servletContext;
    Message message;
    int option;

    public TicketCardMain(int lang, Person person, int examID, Ticket ticket,
                          SearchParams params, ServletContext servletContext, Message message, int option) {
        this.lang = lang;
        this.person = person;
        this.examID = examID;
        this.ticket = ticket;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
        this.option = option;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("exams/ticket.card.html").read(servletContext), pw,
                    new TicketCard(lang, person, examID, ticket, params, servletContext, message, option)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }

}
