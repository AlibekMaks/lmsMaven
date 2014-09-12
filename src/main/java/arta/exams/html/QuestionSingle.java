package arta.exams.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.exams.ExamMessages;
import arta.exams.logic.Ticket;
import arta.exams.logic.TicketQuestion;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;


public class QuestionSingle extends TemplateHandler {

    int examID = 0;
    Ticket ticket;
    int lang;
    SearchParams params;
    int recordNumber = 0;
    StringTransform trsf;

    int number;
    TicketQuestion question;

    public QuestionSingle(int examID, int lang, SearchParams params, Ticket ticket, StringTransform trsf) {
        this.examID = examID;
        this.lang = lang;
        this.params = params;
        this.ticket = ticket;
        this.trsf = trsf;
    }

    public void set(int number, TicketQuestion question){
       this.number = number;
        this.question = question;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("number")){
            pw.print(number);
        } else if (name.equals("ticketQuestionID")){
            if(question != null){
                pw.print(question.getTicketQuestionID());
            } else {
                pw.print(number);
            }
        } else if (name.equals("questionkz value")){
            if(question != null){
                pw.print(question.getQuestionkz());
            }
        } else if (name.equals("questionru value")){
            if(question != null){
                pw.print(question.getQuestionru());
            }
        }
    }

}
