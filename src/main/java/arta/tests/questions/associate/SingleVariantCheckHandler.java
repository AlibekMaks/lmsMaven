package arta.tests.questions.associate;

import arta.common.html.handler.TemplateHandler;
import arta.tests.common.NumberSelect;

import java.io.PrintWriter;


public class SingleVariantCheckHandler extends TemplateHandler {

    int lang;
    SingleAssociateTag tag;
    int maxVariantsCount;

    int number;

    public SingleVariantCheckHandler(int lang, SingleAssociateTag tag, int maxVariantsCount, int number) {
        this.lang = lang;
        this.tag = tag;
        this.maxVariantsCount = maxVariantsCount;
        this.number = number;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("first tag")){
            pw.print(tag.firstTag);
        } else if (name.equals("first tag number select")){
            //NumberSelect select = new NumberSelect(lang);
            //select.writeSimpleNumberSelect(pw, " "+, tag.firstTagNumber, maxVariantsCount, 100, null);
            pw.print("<input type=\"hidden\" name=\"id1="+tag.firstTagID+"\" value=\""+number+"\" >");
            pw.print(number);
        } else if (name.equals("second tag")){
            pw.print(tag.secondTag);
        } else if (name.equals("second tag number select")){
            NumberSelect select = new NumberSelect(lang);
            select.writeSimpleNumberSelect(pw, "id2="+tag.secondTagID, tag.secondTagNumber, maxVariantsCount, 100, null);
        }
    }

}
