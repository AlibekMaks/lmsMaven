package arta.registrar.student.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Date;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.registrar.student.logic.StudentRegistrar;
import arta.registrar.tutor.logic.RegistrarMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class RegistrarCardHandler extends TemplateHandler {

    int month;
    int year;
    StudentRegistrar registrar;
    ServletContext servletContext;
    int lang;
    Date tmp ;


    public RegistrarCardHandler(int month, int year, StudentRegistrar registrar,
                                ServletContext servletContext, int lang) {
        this.month = month;
        this.year = year;
        this.registrar = registrar;
        this.servletContext = servletContext;
        this.lang = lang;
        tmp = new Date(year, month, 1);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("journal table")){
            StudentRegistrarTable table = new StudentRegistrarTable(pw, lang, registrar.journalHeader,
                    registrar.subjects);
            table.writeTable(month, year);
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));            
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.REGISTRAR, null));
        } else if (name.equals("prev month")){
            pw.print("<a href=\"studentregistrar?minus=true&month="+month+"&year="+year+"&nocache="+ Rand.getRandString()+"\">");
            pw.print("<img src=\"images/buttons.left.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                    " height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0 " +
                    " title=\""+MessageManager.getMessage(lang, RegistrarMessages.PREVIOUS_MONTH)+"\">");
            pw.print("</a>");
        } else if (name.equals("month select")){
            tmp.writeMonthServletSelect(pw, lang, "month", 100, "studentregistrar?year=" + year + "&nocache=" + Rand.getRandString());                    
        } else if (name.equals("year select")){
            tmp.writeYearServletSelect(pw, "year", 2, 2, "studentregistrar?month=" + month+"&nocache=" + Rand.getRandString());
        } else if (name.equals("next month")){
            pw.print("<a href=\"studentregistrar?plus=true&month="+month+"&year="+year+"&nocache="+Rand.getRandString()+"\">");
            pw.print("<img src=\"images/buttons.right.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                    " height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0 " +
                    " title=\""+MessageManager.getMessage(lang, RegistrarMessages.NEXT_MONTH)+"\">");
            pw.print("</a>");
        }
    }
}
