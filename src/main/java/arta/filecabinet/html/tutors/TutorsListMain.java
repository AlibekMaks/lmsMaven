package arta.filecabinet.html.tutors;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.JScript;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.tutors.TutorsManager;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorsListMain extends PageContentHandler {

    Tutor tutor;
    int lang;
    ServletContext servletContext;
    SearchParams params;
    Message message;

    public TutorsListMain(Tutor tutor, int lang, ServletContext servletContext, SearchParams params, Message message) {
        this.tutor = tutor;
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {
        out.print("<script language=\"javascript\">");
        JScript.myPrintSelectAllFunction(out, "tut", "all",  "generate");
        out.print("</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("tutors/list.txt").read(servletContext), pw,
                new TutorsList(lang, params, servletContext, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return tutor.getRoleID();
    }
}
