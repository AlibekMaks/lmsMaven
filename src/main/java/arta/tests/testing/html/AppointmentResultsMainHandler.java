package arta.tests.testing.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 27.03.2008
 * Time: 10:42:50
 * To change this template use File | Settings | File Templates.
 */
public class AppointmentResultsMainHandler extends PageContentHandler {

    ServletContext servletContext;
    int lang;
    int roleID;
    Message message;
    Testing testing;


    public AppointmentResultsMainHandler(ServletContext servletContext,
                                         int lang, int roleID,
                                         Message message, Testing testing) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.roleID = roleID;
        this.message = message;
        this.testing = testing;
    }

    public void getHeader(PrintWriter out) {
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/testing.appointment.result.html").read(servletContext),
                pw, new AppointmentResultsHandler(lang, message, servletContext, testing)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
