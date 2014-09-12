package arta.tests.questions.sequence;

import arta.common.html.util.TinyMce;
import arta.common.html.handler.TemplateHandler;
import arta.tests.common.NumberSelect;

import java.io.PrintWriter;


public class SingleVariantHandler extends TemplateHandler {

    int lang;
    int number;
    int variantsCount;
    SingleSequenceTag variant;
    TinyMce tinyMce;
    int questionNumber;
    
    /**
     *
     * @param lang
     * @param number VARIANT NUMBER
     * @param variantsCount
     * @param variant
     * @param tinyMce
     */
    public SingleVariantHandler(int lang, int number, int variantsCount, SingleSequenceTag variant, TinyMce tinyMce, 
                                int questionNumber) {
        this.lang = lang;
        this.number = number;
        this.variantsCount = variantsCount;
        this.variant = variant;
        this.tinyMce = tinyMce;
        this.questionNumber = questionNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("tag")){
            tinyMce.writeTinyMceInput(pw, "variant="+number, 100, 10, TinyMce.DIMENSION_TYPE__PERCENT_ROW, variant.tag);
        } else if (name.equals("tag number")){
            NumberSelect select = new NumberSelect(lang);
            if (variant.number<0)
                variant.number = 0;
            if (variant.number>variantsCount)
                variant.number = variantsCount-1;
            select.writeSimpleNumberSelect(pw, "number="+number, variant.number, variantsCount, 100, null);
        } else if (name.equals("name")){
            pw.print(number + 1);
        }else if (name.equals("number")){
            pw.print((number));
        }
    }

}
