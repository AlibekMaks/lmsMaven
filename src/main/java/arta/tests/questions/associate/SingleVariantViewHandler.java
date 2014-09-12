package arta.tests.questions.associate;

import arta.common.logic.util.StringTransform;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SingleVariantViewHandler extends TemplateHandler {

    int lang;
    SingleAssociateTag tag;
    StringTransform stringTransform;

    public SingleVariantViewHandler(int lang, SingleAssociateTag tag, StringTransform stringTransform) {
        this.lang = lang;
        this.tag = tag;
        this.stringTransform = stringTransform;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("first tag")){
            pw.print(tag.firstTag);
        } else if (name.equals("second tag")){
            pw.print(tag.secondTag);
        }
    }

}
