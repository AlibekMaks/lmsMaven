package arta.tests.common;


import arta.common.html.handler.PageContentHandler;


import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class QuestionCheckMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    int questionNumber;
  //  StudentCheck studentCheck;
    ServletContext servletContext;
/*
    public QuestionCheckMainHandler(int lang, int roleID, int questionNumber,
                                    StudentCheck studentCheck, ServletContext servletContext ) {
        this.lang = lang;
        this.roleID = roleID;
        this.questionNumber = questionNumber;
        this.studentCheck = studentCheck;
        this.servletContext = servletContext;
    }      */

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
     /*   QuestionCheckHandler handler = new QuestionCheckHandler(lang, questionNumber, servletContext, studentCheck);
        FileOpener fileOpener = new FileOpener("tests/questions/question.common.check.template.txt");
        Parser parser = new Parser(fileOpener.read(servletContext), pw, handler);
        parser.parse(); */
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
