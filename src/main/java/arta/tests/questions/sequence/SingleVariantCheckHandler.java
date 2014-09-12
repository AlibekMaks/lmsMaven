package arta.tests.questions.sequence;

import arta.tests.common.NumberSelect;
import arta.common.logic.util.StringTransform;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SingleVariantCheckHandler extends TemplateHandler {

    int lang;
    SingleSequenceTag tag;
    StringTransform stringTransform;
    int maxVariantNumber;
    int number;

    public SingleVariantCheckHandler(int lang, SingleSequenceTag tag,
                                     StringTransform stringTransform, int maxVariantNumber, int number) {
        this.lang = lang;
        this.tag = tag;
        this.stringTransform = stringTransform;
        this.maxVariantNumber = maxVariantNumber;
        this.number = number;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("tag")){
            pw.print(tag.tag);
        } else if (name.equals("tag number select")){
            NumberSelect select = new NumberSelect(lang);
            select.writeSimpleNumberSelect(pw, "var="+number, tag.number, maxVariantNumber, 100,
                    " onmouseover='sel(\""+number+"\");' onmouseout='unsel(\""+number+"\");' ");
        } else if (name.equals("number")){
            pw.print(number);
        }
    }
}
