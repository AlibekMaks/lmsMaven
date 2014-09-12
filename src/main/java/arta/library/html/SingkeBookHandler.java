package arta.library.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;

import java.io.PrintWriter;


public class SingkeBookHandler extends TemplateHandler {

    StringTransform trsf;
    SimpleObject book;
    int lang;


    public SingkeBookHandler(StringTransform trsf, int lang) {
        this.trsf = trsf;
        this.lang = lang;
    }


    public void setBook(SimpleObject book) {
        this.book = book;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(book.name));
        } else if (name.equals("open")){
            pw.print("<a href=\"bookdownload?bookID="+book.id+"&inline="+true+"\" target=\"_blank\"" +
                    " class=\"href\" title=\""+trsf.getHTMLString(book.name)+"\">");
            pw.print("<img src=\"images/open.inline.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        } else if (name.equals("download")){
            pw.print("<a href=\"bookdownload?bookID="+book.id+"\" " +
                    " class=\"href\" title=\""+trsf.getHTMLString(book.name)+"\">");
            pw.print("<img src=\"images/download.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
