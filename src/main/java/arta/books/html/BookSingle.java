package arta.books.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.books.logic.Book;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.SubjectMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class BookSingle  extends TemplateHandler {

    Book book;
    int lang;
    SearchParams params;
    int subjectID;
    StringTransform trsf = new StringTransform();


    public BookSingle(int lang, SearchParams params, int subjectID) {
        this.lang = lang;
        this.params = params;
        this.subjectID = subjectID;
    }

    public void set(Book book){
        this.book = book;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print("<a href=\"book?bookID="+book.getId()+"&subjectID="+subjectID+"&"+params.getFullParams()+"\" " +
                    " title=\""+trsf.getHTMLString(book.getName())+"\"  class=\"href\">");
            pw.print(trsf.getHTMLString(book.getName(), "-"));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("name", book.getName());
            pw.print("<a href=\"subject?option=-1&bookID="+book.getId()+"&subjectID="+subjectID+"&"+params.getFullParams()+"\" " +
                    " onClick='return confirm(\""+MessageManager.getMessage(lang, SubjectMessages.DO_YOU_REALLY_WANT_DELETE_BOOK, prop) +
                    "\");' class=\"href\" title=\""+trsf.getHTMLString(book.getName())+"\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        } else if (name.equals("open")){
            if (book.canBeViewed()){
                pw.print("<a href=\"bookdownload?bookID="+book.getId()+"&inline=true&nocache="+ Rand.getRandString() +
                        "\" class=\"href\" target=\"_blank\" title=\""+trsf.getHTMLString(book.getName())+"\">");
                pw.print("<img src=\"images/open.inline.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print("</a>");
            }
        } else if (name.equals("download")){
            if (book.canBeViewed()){
                pw.print("<a href=\"bookdownload?bookID="+book.getId()+"&nocache="+ Rand.getRandString() +
                        "\" class=\"href\" title=\""+trsf.getHTMLString(book.getName())+"\">");
                pw.print("<img src=\"images/download.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print("</a>");
            }
        } else if (name.equals("type")){
            pw.print(book.getBookTypeValue(lang));
        }
    }
}
