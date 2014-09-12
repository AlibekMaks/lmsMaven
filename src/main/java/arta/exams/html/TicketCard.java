package arta.exams.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.exams.ExamMessages;
import arta.exams.logic.Exam;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class TicketCard extends TemplateHandler {


    private int lang;
    private Person person;
    private int examID;
    private Ticket ticket;
    private SearchParams params;
    private ServletContext servletContext;
    private Message message;
    private StringTransform trsf = new StringTransform();
    private Exam exam;
    private int option;

    public TicketCard(int lang, Person person, int examID, Ticket ticket,
                      SearchParams params, ServletContext servletContext, Message message, int option) {
        this.lang = lang;
        this.person = person;
        this.examID = examID;
        this.ticket = ticket;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
        this.option = option;

        this.exam = new Exam();
        this.exam.loadById(examID, lang, false);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS, null));
        } else if (name.equals("ticketID")){
            pw.print(ticket.getTicketID());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
            pw.print("<input name=\"examID\" value=\""+examID+"\" type=\"hidden\">");
            pw.print("<input name=\"option\" value=\""+option+"\" type=\"hidden\">");
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.TICKETS, null));
        } else if (name.equals("return link")){
            pw.print("exam?examID="+examID+"&"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("add link")){
            pw.print("ticket?option=2&examID="+examID+"&ticketID=0&" + params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("ticketNumber value")){
            pw.print(ticket.getTicketNumber());
        } else if (name.equals("questionkz label")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.WORDING_OF_THE_QUESTION_IN_THE_KAZAKH_LANGUAGE, null));
        } else if (name.equals("questionru label")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.WORDING_OF_THE_QUESTION_IN_THE_RUSSIAN, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("exams/questions.html").read(servletContext);
            QuestionSingle handler = new QuestionSingle(exam.getExamID(), lang, params, ticket, trsf);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            if(ticket.ticketQuestions.size() > 0){
                for (int i=0; i<ticket.ticketQuestions.size(); i++){
                    handler.set(i+1, ticket.ticketQuestions.get(i));
                    parser.setStringBuilder(new StringBuffer(str));
                    parser.parse();
                }
            } else {
                for (int i=0; i<exam.getQuestionCount(); i++){
                    handler.set(i+1, null);
                    parser.setStringBuilder(new StringBuffer(str));
                    parser.parse();
                }
            }
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        }
    } 
}
