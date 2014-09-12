package arta.tests.test.save;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.tests.test.list.TestsSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class FailureSaveMainHandler extends PageContentHandler {

    int lang;
    int role;
    Message  message;
    ServletContext servletContext;
    TestsSearchParams params;

    public FailureSaveMainHandler(int lang, int role, Message message, ServletContext servletContext,
                                  TestsSearchParams params) {
        this.lang = lang;
        this.role = role;
        this.message = message;
        this.servletContext = servletContext;
        this.params = params;
    }

    public void getHeader(PrintWriter out) {

    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        FailureSaveHandler handler = new FailureSaveHandler(lang, message, params);
        FileReader fileReader = new FileReader("tests/save/failure.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) {

    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return role;
    }
}
