package arta.tests.questions.sequence;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;

public class SingleVariantViewHandler extends TemplateHandler {

    SingleSequenceTag tag ;
    int lang;
    StringTransform stringTransform;

    public SingleVariantViewHandler(SingleSequenceTag tag, int lang, StringTransform stringTransform) {
        this.tag = tag;
        this.lang = lang;
        this.stringTransform = stringTransform;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("variant")){
            pw.print(tag.tag);
        }
    }
}
