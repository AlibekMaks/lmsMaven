package arta.tests.common;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.TinyMce;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.tests.questions.Question;
import arta.tests.test.list.TestsSearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class QuestionMainHandler extends PageContentHandler {

    int lang;
    int role;
    Question question;
    int questionsCount;
    int questionNumber;
    ServletContext servletContext;
    TinyMce tinyMce;
    String testName;
    int testSubjectID;
    int testID;
    TestsSearchParams params;
    String testSignature;

    int tutorID;

    public QuestionMainHandler(int lang, int role, Question question,
                               int questionsCount, int questionNumber,
                               ServletContext servletContext,
                               String testName, int testSubjectID, int testID, TestsSearchParams params, String testSignature, int tutorID) {
        this.lang = lang;
        this.role = role;
        this.question = question;
        this.questionsCount = questionsCount;
        this.questionNumber = questionNumber;
        this.servletContext = servletContext;
        this.testName = testName;
        this.testSubjectID = testSubjectID;
        tinyMce = new TinyMce(lang);
        this.testID = testID;
        this.params = params;
        this.testSignature = testSignature;
        this.tutorID = tutorID;
    }

    public void getHeader(PrintWriter out) {

        tinyMce.initTinyMce(out, tutorID, Constants.TEST_CLIPBOARD_IMAGE_OBJECT, testID, testSignature);
        out.println("<script language=\"javascript\">" +
                "function areYouSure(){" +
                " if (confirm(\""+ MessageManager.getMessage(lang, TestMessages.CONFIRM_DELETE_QUESTION, null)+"\")){" +
                " form.option.value=3;" +
                " form.submit(); " +
                "} " +
                " return false;" +
                "} " +
                "</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        QuestionHandler handler = new QuestionHandler(question,  questionNumber,
                tinyMce, lang, questionsCount, testName, testSubjectID, testID, servletContext, params, testSignature);
        FileReader fileReader = new FileReader("tests/questions/question.common.template.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return role;
    }
}
