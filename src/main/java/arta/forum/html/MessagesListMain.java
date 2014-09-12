package arta.forum.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.ForumParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class MessagesListMain extends PageContentHandler {

    int lang;
    Person person;
    int themeid;
    ServletContext servletContext;
    SearchParams params;
    String option;
    Message message;

    public MessagesListMain(SearchParams params, int lang, Person person, int themeid,
                            ServletContext servletContext, String option, Message message) {
        this.params = params;
        this.lang = lang;
        this.person = person;
        this.themeid = themeid;
        this.servletContext = servletContext;
        this.option = option;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {
        out.print("<script language=\"javascript\">");
        JScript.printSelectAllFunction(out, "std", "selectAll");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("forum/messageslist.txt").read(servletContext), pw,
                new MessagesList(params, person, lang, servletContext, themeid, option, message)).parse();
    }

    public int getLanguage() {
        return  lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
