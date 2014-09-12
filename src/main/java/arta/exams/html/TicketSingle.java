package arta.exams.html;

import arta.classes.ClassMessages;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.exams.ExamMessages;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.StudyGroup;

import java.io.PrintWriter;
import java.util.Properties;


public class TicketSingle extends TemplateHandler {

    int examID = 0;
    Ticket ticket;
    int lang;
    SearchParams params;
    int recordNumber = 0;
    int groupsNumber = 0;
    StringTransform trsf;

    public TicketSingle(int examID, int lang, SearchParams params, int groupsNumber, StringTransform trsf) {
        this.examID = examID;
        this.lang = lang;
        this.params = params;
        this.groupsNumber = groupsNumber;
        this.trsf = trsf;
    }

    public void set (Ticket ticket, int recordNumber){
        this.ticket = ticket;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("number")){
            pw.print("<a class=\"href\" href=\"ticket?option=1&examID="+examID+"&ticketID="+ticket.getTicketID()+"&"+params.getFullParams()+ "\">");
            pw.print(ticket.getTicketNumber());
            pw.print("</a>");
        } else if (name.equals("ticketNumber label")){
            Properties prop = new Properties();
            prop.setProperty("1", Integer.toString(ticket.getTicketNumber()));
            pw.print(MessageManager.getMessage(lang, ExamMessages.TICKET_NUMBER_X, prop));
        } else if (name.equals("ticket questions")){

            for(int i=0; i<ticket.ticketQuestions.size(); i++){
                pw.print("<tr>");
                    pw.print("<td align=\"center\" width=\"20px\">");
                        pw.print(i+1+")");
                    pw.print("</td>");

                    pw.print("<td align=\"left\" width=* >");
                        pw.print(trsf.getHTMLString(ticket.ticketQuestions.get(i).getQuestion(lang)));
                    pw.print("</td>");
                pw.print("</tr>");
            }
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("1", Integer.toString(ticket.getTicketNumber()));

            pw.print("<a class=\"href\" href=\"exam?option=-1&ticketID="+ticket.getTicketID()+
                    "&examID="+examID+"&"+params.getFullParams()+"\" " +
                    " onClick='return confirm(\"" +
                    MessageManager.getMessage(lang, ExamMessages.DO_YOU_REALLY_WANT_TO_DELETE_TICKET_NUMBER_X, prop) +
                    "\")' title=\""+trsf.getHTMLString(MessageManager.getMessage(lang, ExamMessages.TICKET_NUMBER_X, prop))+"\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }

}
