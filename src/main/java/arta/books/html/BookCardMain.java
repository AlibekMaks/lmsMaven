package arta.books.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.books.logic.Book;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class BookCardMain extends PageContentHandler {

    Person person;
    int lang;
    Book book;
    int subjectID;
    SearchParams params;
    ServletContext servletContext;
    Message message;


    public BookCardMain(Person person, int lang, Book book, int subjectID,
                        SearchParams params, ServletContext servletContext, Message message) {
        this.person = person;
        this.lang = lang;
        this.book = book;
        this.subjectID = subjectID;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("books/card.txt").read(servletContext), pw,
            new BookCard(book, book.getSubject(subjectID, lang), params, lang, message, servletContext)).parse();                
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
