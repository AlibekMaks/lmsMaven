package arta.tests.questions.closed;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.TinyMce;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;


public class ClosedQuestionHandler extends TemplateHandler {

    int lang;
    ClosedQuestion question;
    TinyMce tinyMce;

    public ClosedQuestionHandler(int lang, ClosedQuestion question, TinyMce tinyMce) {
        this.lang = lang;
        this.question = question;
        this.tinyMce = tinyMce;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("right answer")){
            pw.print(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWER, null));
        } else if (name.equals("right answer input")){
            String val = question.answer !=null ? question.answer : "";
            pw.print("<textarea style=\"width:100%\" rows=10 class=\"comonInput\" name=\"rightAnswer\">"+
                     val + "</textarea>");
        }
    }

}
