package arta.tests.common;

import arta.tests.test.Test;
import arta.tests.test.list.TestsSearchParams;
import arta.common.html.handler.Parser;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class QuestionViewMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    Test test;
    int questionNumber;
    ServletContext servletContext;
    TestsSearchParams params;

    public QuestionViewMainHandler(int lang, int roleID, Test test, int questionNumber,
                                   ServletContext servletContext, TestsSearchParams params) {
        this.lang = lang;
        this.roleID = roleID;
        this.test = test;
        this.questionNumber = questionNumber;
        this.servletContext = servletContext;
        this.params = params;
    }

    public void getHeader(PrintWriter out) {
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        QuestionViewHandler handler = new QuestionViewHandler(lang, test, questionNumber, servletContext, params);
        FileReader fileReader = new FileReader("tests/questions/question.common.view.template.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) {}

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
