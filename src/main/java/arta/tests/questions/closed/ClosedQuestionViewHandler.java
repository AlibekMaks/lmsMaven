package arta.tests.questions.closed;


import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;

public class ClosedQuestionViewHandler extends TemplateHandler {

    ClosedQuestion question;
    int lang;

    public ClosedQuestionViewHandler(ClosedQuestion question, int lang) {
        this.question = question;
        this.lang = lang;
    }

    StringTransform stringTransform = new StringTransform();

    public void replace(String name, PrintWriter pw) {
        if (name.equals("right answer is")){
            pw.print(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWER, null));
        } else if (name.equals("answer")){
            pw.print(question.answer);
        }
    }
}
