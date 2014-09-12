package arta.tests.test.view;

import arta.tests.test.Test;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SingleTestViewHandler extends TemplateHandler {

    Test test;
    int lang;
    StringTransform stringTransform;

    public SingleTestViewHandler(Test test, int lang, StringTransform stringTransform) {
        this.test = test;
        this.lang = lang;
        this.stringTransform = stringTransform;
    }

    public void replace(String name, PrintWriter pw) {

        if (name.equals("testID")){
            pw.print(test.testID);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("test name")){
            pw.print(stringTransform.getHTMLString(test.testName));
        } else if (name.equals("created")){
            pw.print(test.created.getInputValue());
        } else if (name.equals("modified")){
            pw.print(test.modified.getInputValue());
        }
    }
}
