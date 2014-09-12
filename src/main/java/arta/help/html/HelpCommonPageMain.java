package arta.help.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class HelpCommonPageMain extends PageContentHandler {

    Person person;
    int lang;
    ServletContext servletContext;


    public HelpCommonPageMain(Person person, int lang, ServletContext servletContext) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("help/help.common.txt").read(servletContext), pw,
                new HelpCommonPage(person, lang, servletContext)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
