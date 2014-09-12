package arta.lessonmaterials.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.lessonmaterials.logic.TutorBook;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;


public class LessonMaterialSingleHandler extends TemplateHandler {

    SimpleObject tutorBook;
    int lang;
    StringTransform trsf;
    SearchParams params;



    public LessonMaterialSingleHandler(int lang, StringTransform trsf, SearchParams params) {
        this.lang = lang;
        this.trsf = trsf;
        this.params = params;
    }


    public void setTutorBook(SimpleObject tutorBook) {
        this.tutorBook = tutorBook;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print("<a href=\"tutorbook?nocache="+ Rand.getRandString()
                    + "&bookID="+tutorBook.id+"&" + params.getFullParams() + "\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(tutorBook.name)+"\">");
            pw.print(trsf.getHTMLString(tutorBook.name, "-"));
            pw.print("</a>");
        } else if (name.equals("download")){
            pw.print("<a href=\"bookdownload?nocache="+ Rand.getRandString()+
                    "&bookID="+tutorBook.id+"&option=download\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(tutorBook.name)+"\">");
            pw.print("<img src=\"images/download.gif\" width=\"16px\" heght=\"16px\" border=0>");
            pw.print("</a>");
        } else if (name.equals("view")){
            pw.print("<a href=\"bookdownload?nocache="+ Rand.getRandString()+
                    "&bookID="+tutorBook.id+"&inline=true\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(tutorBook.name)+"\" target=\"_blank\">");
            pw.print("<img src=\"images/open.inline.gif\" width=\"16px\" heght=\"16px\" border=0>");
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("name", tutorBook.getName());
            pw.print("<a href=\"tutorbooks?nocache="+ Rand.getRandString()+
                    "&option=-1&bookID="+tutorBook.id+"&"+params.getFullParams()+"\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(tutorBook.name)+"\" " +
                    " onClick='return confirm(\""+MessageManager.getMessage(lang, Constants.CONFIRM_DELETE, prop)+"\")' >");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" heght=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
