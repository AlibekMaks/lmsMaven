package arta.tests.questions.open;

import arta.common.logic.util.StringTransform;
import arta.common.html.handler.TemplateHandler;
import arta.tests.questions.Question;

import java.io.PrintWriter;


public class SingleVariantViewHandler extends TemplateHandler {

    SingleAnswer answer;
    int type;
    StringTransform stringTransform;

    public SingleVariantViewHandler(SingleAnswer answer, int type, StringTransform stringTransform) {
        this.answer = answer;
        this.type = type;
        this.stringTransform = stringTransform;
    }

    public void replace(String name, PrintWriter pw) {
        if(name.equals("is right")){
            if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                if (answer.isRight){
                    pw.print("<input type=\"checkbox\" checked disabled>");
                } else {
                    pw.print("<input type=\"checkbox\" disabled>");
                }
            } else if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                if (answer.isRight){
                    pw.print("<input type=\"radio\" checked disabled>");
                } else {
                    pw.print("<input type=\"radio\" disabled>");
                }
            }
        } else if (name.equals("variant")){
            pw.print(answer.answer);
        }
    }
}
