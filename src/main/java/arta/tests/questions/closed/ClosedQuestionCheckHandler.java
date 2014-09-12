package arta.tests.questions.closed;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class ClosedQuestionCheckHandler extends TemplateHandler {

    int lang;
    ClosedQuestion question;

    public ClosedQuestionCheckHandler(int lang, ClosedQuestion question) {
        this.lang = lang;
        this.question = question;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("questionNumber")){
            pw.print(question.getNumber());
        } else if (name.equals("value")){
            if (question.answer != null)
                pw.print(question.answer);
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT, null));
        }
    }

}
