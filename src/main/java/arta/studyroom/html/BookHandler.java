package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;


public class BookHandler extends TemplateHandler {

    SimpleObject book;
    StringTransform trsf;

    public BookHandler(SimpleObject book, StringTransform trsf) {
        this.book = book;
        this.trsf = trsf;
    }


    public void replace(String name, PrintWriter pw) {
        if (name.equals("bookID")){
            pw.print(book.id);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("name")){
            pw.print(trsf.getHTMLString(book.name));
        }
    }
}
