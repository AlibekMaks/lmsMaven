package arta.tests.testing.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 15:15:13
 * To change this template use File | Settings | File Templates.
 */
public class MyAddedTestsMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    ServletContext servletContext;
    Testing testing;
    Message message;


    public MyAddedTestsMainHandler(int lang, int roleID, ServletContext servletContext,
                                   Testing testing, Message message) {
        this.lang = lang;
        this.roleID = roleID;
        this.servletContext = servletContext;
        this.testing = testing;
        this.message = message;
    }

    public void getHeader(PrintWriter out) {

    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/myadded.tests.list.txt").read(servletContext),
                pw,
                new MyAddedTestsHandler(lang, testing, servletContext, message)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
