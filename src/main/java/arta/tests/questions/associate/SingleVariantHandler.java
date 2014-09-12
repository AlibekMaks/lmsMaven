package arta.tests.questions.associate;

import arta.common.html.util.TinyMce;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;


public class SingleVariantHandler extends TemplateHandler {

    int lang;
    SingleAssociateTag variant;
    TinyMce tinyMce;
    int number;
    int questionNumber;

    public SingleVariantHandler(int alang, SingleAssociateTag variant, TinyMce tinyMce, int number, 
                                int questionNumber){
        this.lang = alang;
        this.variant = variant;
        this.tinyMce = tinyMce;
        this.number = number;
        this.questionNumber = questionNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("first tag")){
            tinyMce.writeTinyMceInput(pw, "firstTag="+number, 100, 10, TinyMce.DIMENSION_TYPE__PERCENT_ROW, variant.firstTag);
        } else if (name.equals("second tag")){
            tinyMce.writeTinyMceInput(pw, "secondTag="+number, 100, 10, TinyMce.DIMENSION_TYPE__PERCENT_ROW, variant.secondTag);
        } else if (name.equals("name")){
            pw.print(number + 1);
        } else if (name.equals("number")){
            pw.print((number));
        }
    }
}
