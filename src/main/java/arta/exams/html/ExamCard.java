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
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class ExamCard extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    SearchParams params;
    Exam exam;
    Message message;
    StringTransform trsf = new StringTransform();

    public ExamCard(ServletContext servletContext, int lang, SearchParams params, Exam exam, Message message) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.params = params;
        this.exam = exam;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.NAME, null));
        } else if (name.equals("name value")){
            pw.print(trsf.getHTMLString(exam.getExamName()));
        } else if (name.equals("add ticket")){
            if(exam.examIsExists()){
                pw.print("<a href=\"ticket?option=2&examID="+exam.getExamID()+"&ticketID=0&" + params.getParamsWithoutRecord()+"\" "+
                           " title=\""+MessageManager.getMessage(lang, ExamMessages.ADD_A_NEW_TICKET, null)+"\">\n" +
                         "   <img src=\"images/buttons.plus.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0>\n" +
                         "</a>");
            }
        } else if (name.equals("maxlenth")){
            pw.print(Varchar.NAME);
        } else if (name.equals("disabled")){
            if(exam.examIsExists()){
                pw.print("disabled=\"disabled\"");
            }
        } else if (name.equals("questionCount label")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.NUMBER_OF_QUESTIONS_IN_A_SINGLE_TICKET, null));
        } else if (name.equals("questionCount value")){
            pw.print(exam.getQuestionCount());
        } else if (name.equals("edit")){
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("delete")){
            pw.print(MessageManager.getMessage(lang, Constants.DELETE, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("exams/ticket.html").read(servletContext);
            TicketSingle handler = new TicketSingle(exam.getExamID(), lang, params, exam.tickets.size(), trsf);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<exam.tickets.size(); i++){
                handler.set(exam.tickets.get(i), i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
            if(exam.getExamID()>0){
                pw.print("<input type=\"hidden\" name=\"questionCount\" value=\""+exam.getQuestionCount()+"\">");
            }
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("examID")){
            pw.print(exam.getExamID());
        } else if (name.equals("return link")){
            pw.print("exams?"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("page header")){
            Properties prop = new Properties();
            prop.setProperty("name", exam.getExamName());
            pw.print(MessageManager.getMessage(lang, ExamMessages.EXAM, prop));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        }
    } 
}
