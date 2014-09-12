package arta.auth.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class AdminAuthMainHandler extends PageContentHandler{

    int lang;
    Person person;
    ServletContext servletContext;
    int personID, roleID;
    SearchParams params;
    Message message;


    public AdminAuthMainHandler(int lang, Person person,
                                ServletContext servletContext, int personID,
                                int roleID, SearchParams params, Message message) {
        this.lang = lang;
        this.person = person;
        this.servletContext = servletContext;
        this.personID = personID;
        this.roleID = roleID;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {}

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("authorization/ath.params.change.txt").read(servletContext), pw,
                new AdminAuthHandler(personID, roleID, params, lang, servletContext, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
