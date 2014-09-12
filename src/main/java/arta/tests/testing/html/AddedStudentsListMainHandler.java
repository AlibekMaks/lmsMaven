package arta.tests.testing.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:37:22
 * To change this template use File | Settings | File Templates.
 */
public class AddedStudentsListMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    ServletContext servletContext;
    Testing testing;


    public AddedStudentsListMainHandler(int lang, int roleID, ServletContext servletContext, Testing testing) {
        this.lang = lang;
        this.roleID = roleID;
        this.servletContext = servletContext;
        this.testing = testing;
    }

    public void getHeader(PrintWriter out) {

    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("testing/added.students.list.html").read(servletContext),
                pw,
                new AddedStudentsListHandler(lang, testing, servletContext)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
