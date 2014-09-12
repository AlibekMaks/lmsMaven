package arta.dbchecker.html;

import arta.check.logic.Testing;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.students.Student;
import arta.settings.logic.Settings;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class dbCheckerMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    ServletContext servletContext;
    Message message;
    int language = 1;
    Testing testing;
    Student student;
    boolean testIsPassed;
    boolean testIsSaved;


    public dbCheckerMainHandler(Testing testing, Student student, boolean testIsPassed, boolean testIsSaved, int lang, int roleID, ServletContext servletContext,
                                Message message, int language) {
        this.testing = testing;
        this.student = student;
        this.testIsPassed = testIsPassed;
        this.testIsSaved = testIsSaved;
        this.lang = lang;
        this.roleID = roleID;
        this.servletContext = servletContext;
        this.message = message;
        this.language = language;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("dbchecker/MainCard.html").read(servletContext), pw,
                new dbCheckerHandler(testing, student, testIsPassed, testIsSaved, lang, servletContext, message, language)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
