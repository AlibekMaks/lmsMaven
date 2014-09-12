package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 11:40:10
 * To change this template use File | Settings | File Templates.
 */
public class TutorialAppointRecordHandler extends TemplateHandler {

    int lang;
    StringTransform trsf;

    SimpleObject book;


    public TutorialAppointRecordHandler(int lang, StringTransform trsf) {
        this.lang = lang;
        this.trsf = trsf;
    }


    public void setBook(SimpleObject book) {
        this.book = book;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(book.name));
        } else if (name.equals("bookID")){
            pw.print(book.id);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("title")){
            pw.print("<a href='#' onclick='return appoint("+book.id+")' class=\"href\">");
            pw.print(trsf.getHTMLString(book.name, "-"));
            pw.print("</a>");
        }
    }
}
