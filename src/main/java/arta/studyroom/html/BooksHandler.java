package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;


public class BooksHandler extends TemplateHandler {

    ArrayList<SimpleObject> books;
    String header;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();


    public BooksHandler(ArrayList<SimpleObject> books, String header, ServletContext servletContext) {
        this.books = books;
        this.header = header;
        this.servletContext = servletContext;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("header")){
            pw.print(header);
        } else if (name.equals("items")){
            for (int i=0; i<books.size(); i++){
                new Parser(new FileReader("studyroom/content/book.item.txt").read(servletContext), pw,
                        new BookHandler(books.get(i), trsf)).parse();
            }
        }
    }
}
