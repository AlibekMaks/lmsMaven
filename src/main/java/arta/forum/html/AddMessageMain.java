package arta.forum.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.TinyMce;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.ForumParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: ${Indira}
 * Date: ${21.02.2008}
 * Time: 11:30:49
 * To change this template use File | Settings | File Templates.
 */
public class AddMessageMain extends PageContentHandler {

    Person person;
    ServletContext servletContext;
    int lang;
    TinyMce tinyMce;
    int partid;
    int themeid;
    ForumParams params;
    Message message;
    SearchParams searchParams;

    public AddMessageMain(Person person, ServletContext servletContext, int lang, int partid, int themeid,
                           ForumParams params, Message message, SearchParams searchParams) {
        this.person = person;
        this.servletContext  = servletContext;
        this.lang = lang;
        this.partid = partid;
        this.themeid = themeid;
        this.params = params;
        this.message = message;
        this.searchParams = searchParams;
    }

    public void getHeader(PrintWriter out) {
        tinyMce = new TinyMce(lang);
        tinyMce.initTinyMce(out);
        out.print("<script language=\"javascript\">");
        JScript.printSelectAllFunction(out, "std", "selectAll");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("forum/addmessage.txt").read(servletContext), pw,
                new AddMessage(lang, tinyMce, person, partid, themeid, params, message, searchParams)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
