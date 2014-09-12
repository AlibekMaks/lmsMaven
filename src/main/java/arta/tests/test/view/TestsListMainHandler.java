package arta.tests.test.view;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Date;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class TestsListMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    int sort;
    int partNumber, countInPart;
    Date createStart, createFinish, modifyStart, modifyFinish;
    String search;
    ServletContext servletContext;

    HTMLCalendar calendar ;

    public TestsListMainHandler(int lang, int roleID, int sort, int partNumber, int countInPart,
                                Date createStart, Date createFinish,
                                Date modifyStart, Date modifyFinish,
                                String search, ServletContext servletContext) {
        this.lang = lang;
        this.roleID = roleID;
        this.sort = sort;
        this.partNumber = partNumber;
        this.countInPart = countInPart;
        this.createStart = createStart;
        this.createFinish = createFinish;
        this.modifyStart = modifyStart;
        this.modifyFinish = modifyFinish;
        this.search = search;
        this.servletContext = servletContext;

        calendar = new HTMLCalendar(lang);
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        calendar.initializeCalendar(out);
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        calendar.writeDIV(pw);
        TestsListHandler handler = new TestsListHandler(lang, sort, createStart,
                createFinish, modifyStart, modifyFinish, search, calendar,
                partNumber, countInPart, servletContext);
        FileReader fileReader = new FileReader("tests/tests.list/list.view.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) {
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
