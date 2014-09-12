package arta.tests.questions.open;

import arta.common.logic.util.StringTransform;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class OpenQuestionViewHandler extends TemplateHandler {

    OpenQuestion question;
    int lang;

    StringBuffer str;
    StringTransform stringTransform = new StringTransform();

    public OpenQuestionViewHandler(OpenQuestion question, int lang, ServletContext servletContext) {
        this.question = question;
        this.lang = lang;
        FileReader fileReader = new FileReader("tests/questions/open/variant.view.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        StringTransform stringTransform = new StringTransform();
        if (name.equals("variants")){
            for (int i=0; i<question.answers.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantViewHandler handler = new SingleVariantViewHandler(question.answers.get(i),question.getQuestionType(),
                        stringTransform);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        }
    }
}
