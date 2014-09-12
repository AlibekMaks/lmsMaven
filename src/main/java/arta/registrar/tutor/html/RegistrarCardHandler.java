package arta.registrar.tutor.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.registrar.tutor.logic.Registrar;
import arta.registrar.tutor.logic.RegistrarMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class RegistrarCardHandler extends TemplateHandler {

    int month;
    int year;
    Registrar registrar;
    int lang;    
    ServletContext servletContext;
    int studygroupID;
    int subgroupID;
    Message message;
    Date date = new Date();

    public RegistrarCardHandler(int month, int year, Registrar registrar, int lang, 
                                 ServletContext servletContext, int studygroupID, int subgroupID,
                                 Message message) {
        this.month = month;
        this.year = year;
        this.registrar = registrar;
        this.lang = lang;
        this.servletContext = servletContext;
        this.studygroupID = studygroupID;
        this.subgroupID = subgroupID;
        date.year = year;
        date.month = month;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("students table")){
            date.month = month;
            date.year = year;
            date.day = 1;
            RegistrarTable table = new RegistrarTable(pw, date.getNumberOfDaysAMonth(), date.getDayfWeekNumber(), lang,
                    registrar.journalHeader, registrar.students, new Date(), year, month, studygroupID, subgroupID);
            table.writeTable();
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("studygroupID")){
            pw.print(studygroupID);
        } else if (name.equals("subgroupID")){
            pw.print(subgroupID);
        } else if (name.equals("month")){
            pw.print(month);
        } else if (name.equals("year")){
            pw.print(year);
        } else if (name.equals("return link")){
            pw.print("tutorgroups");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.REGISTRAR));
        } else if (name.equals("prev month")){
            pw.print("<input type=\"image\" src=\"images/buttons.left.gif\" border=0 " +
                    " name=\"minus\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" height=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                    " onClick='document.getElementById(\"action\").value=-1;" +
                    "   document.getElementById(\"form\").submit();' " +
                    " title=\""+MessageManager.getMessage(lang, RegistrarMessages.PREVIOUS_MONTH)+"\">");
        } else if (name.equals("month select")){


            date.writeMonthComboBox(pw, lang, "month_select", "month_select", "" +
                    " document.getElementById(\"action\").value=-2; " +
                    " document.getElementById(\"new_month\").value=this.value; " +
                    " document.getElementById(\"new_year\").value=document.getElementById(\"year_select\").value;" +
                    " document.getElementById(\"form\").submit();");
            
        } else if (name.equals("year select")){

            date.writeYearComboBox(pw, "year_select", "year_select", 2, 2, "" +
                    " document.getElementById(\"action\").value=-2;" +
                    " document.getElementById(\"new_year\").value=this.value; " +
                    " document.getElementById(\"new_month\").value=document.getElementById(\"month_select\").value; " +
                    " document.getElementById(\"form\").submit();");

        } else if (name.equals("next month")){
            pw.print("<input type=\"image\" src=\"images/buttons.right.gif\" border=0 " +
                    " name=\"plus\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" height=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                    " onClick='document.getElementById(\"action\").value=1;" +
                    " document.getElementById(\"form\").submit();' " +
                    " title=\""+MessageManager.getMessage(lang, RegistrarMessages.NEXT_MONTH)+"\">");
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("confirmDelete")){
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.CONFIRM_DELETE_MARK));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        }
    }
}
