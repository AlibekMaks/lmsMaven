package arta.tests.questions.associate;


import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.StringTransform;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class AssociateQuestionViewHandler extends TemplateHandler {

    AssociateQuestion question;
    int lang;

    StringBuffer str;
    StringTransform stringTransform = new StringTransform();

    public AssociateQuestionViewHandler(AssociateQuestion question, int lang,
                                        ServletContext servletContext) {
        this.question = question;
        this.lang = lang;
        FileReader fileReader = new FileReader("tests/questions/associate/variant.view.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantViewHandler handler = new SingleVariantViewHandler(lang, question.tags.get(i),
                        stringTransform);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        }
    }
}
