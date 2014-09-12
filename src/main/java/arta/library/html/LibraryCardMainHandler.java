package arta.library.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.library.logic.LibrarySearchParams;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class LibraryCardMainHandler extends PageContentHandler {

    LibrarySearchParams params;
    Person person;
    int lang;
    ServletContext servletContext;


    public LibraryCardMainHandler(LibrarySearchParams params, Person person, int lang,
                                  ServletContext servletContext) {
        this.params = params;
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("library/card.txt").read(servletContext), pw,
            new LibraryCardHandler(params, servletContext,  lang)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
