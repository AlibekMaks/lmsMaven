package arta.tests.questions.sequence;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.util.StringTransform;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class SequenceQuestionViewHandler extends TemplateHandler {

    SequenceQuestion question;
    int lang;

    StringTransform stringTransform = new StringTransform();
    StringBuffer str;

    public SequenceQuestionViewHandler(SequenceQuestion question, int lang, 
                                       ServletContext servletContext) {
        this.question = question;
        this.lang = lang;
        FileReader fileReader = new FileReader("tests\\questions\\sequence/variant.view.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantViewHandler handler = new SingleVariantViewHandler(question.tags.get(i),
                        lang, stringTransform);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        }
    }
}
