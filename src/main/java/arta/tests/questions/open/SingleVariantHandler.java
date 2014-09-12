package arta.tests.questions.open;

import arta.tests.questions.Question;
import arta.common.html.util.TinyMce;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;


public class SingleVariantHandler extends TemplateHandler {

    SingleAnswer variant;
    TinyMce tinyMce;
    int number;
    int questionNumber;
    int lang;
    int type;

    public SingleVariantHandler(SingleAnswer variant, TinyMce tinyMce, int number, int lang, int type, 
                                int questionNumber) {
        this.variant = variant;
        this.tinyMce = tinyMce;
        this.number = number;
        this.lang = lang;
        this.type = type;
        this.questionNumber = questionNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("type")){
            if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                pw.print("checkbox");
            } else if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                pw.print("radio");
            }
        } else if (name.equals("name")){
            if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                pw.print(" name=\"right="+number+"\"");
            } else if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                pw.print(" name=\"right\" value="+number+" ");
            }
        } else if (name.equals("checked")){
            if (variant.isRight)
                pw.print(" checked ");
        } else if (name.equals("variant")){
            tinyMce.writeTinyMceInput(pw, "variant="+number, 100, 10, TinyMce.DIMENSION_TYPE__PERCENT_ROW, variant.answer);
        } else if (name.equals("nnn")){
            pw.print(number+1);
        } else if (name.equals("number")){
            pw.print((number));
        }
    }
}
