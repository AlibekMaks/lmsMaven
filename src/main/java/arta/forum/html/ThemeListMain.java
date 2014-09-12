package arta.forum.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ThemeListMain extends PageContentHandler {

    Person person;
    SearchParams params;
    ServletContext servletContext;
    int lang;
    String option;
    int partid;
    Message message;


    public ThemeListMain(Person person, SearchParams params,
                            ServletContext servletContext, int lang, int partid, String option, Message message) {
        this.person = person;
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.partid = partid;
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
        new Parser(new FileReader("forum/themes.txt").read(servletContext), pw,
                new ThemesList(servletContext, params, lang, person.getRoleID(),
                        person.getPersonID(), partid, option, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
