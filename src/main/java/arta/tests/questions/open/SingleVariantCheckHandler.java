package arta.tests.questions.open;

import arta.tests.questions.Question;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SingleVariantCheckHandler extends TemplateHandler {

    int lang;
    SingleAnswer answer;
    int number;
    int type;
    int size;

    public SingleVariantCheckHandler(int lang, SingleAnswer answer, int type, int number, int size) {
        this.lang = lang;
        this.answer = answer;
        this.type = type;
        this.number = number;
        this.size = size;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("variant")){
            pw.print(answer.answer);
        } else if (name.equals("check")){
            String selected = "";
            if (answer.isRight)
                selected = " checked ";
            if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                pw.print("<input type=\"checkbox\" onchange='chng(\""+number+"\", \""+size+"\")' name=\"var="+number+"\" "+selected+" id=\""+number+"\"/>");
            } else {
                pw.print("<input type=\"radio\" onchange='chng(\""+number+"\", \""+size+"\")' name=\"var\" value=\""+number+"\" "+selected+" id=\""+number+"\"/>");
            }
        } else if (name.equals("number")){
            pw.print(number);
        } else if (name.equals("chckbg")){
            if(answer.isRight){
                pw.print(" bgcolor=\""+Question.RIGHT_CHCK_COLOR+"\" ");
            } else {
                pw.print(" bgcolor=\""+Question.UNSELCOLOR+"\" ");
            }
        } else if (name.equals("varbg")){
            if(answer.isRight){
                pw.print(" bgcolor=\""+Question.RIGHT_VAR_COLOR+"\" ");
            } else {
                pw.print(" bgcolor=\""+Question.UNSELCOLOR+"\" ");
            }
        } else if(name.equals("max")){
            pw.print(size);
        }
    }
}
