package arta.tests.questions.open;

import arta.tests.common.TestMessages;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.util.TinyMce;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class OpenQuestionHandler extends TemplateHandler {

    TinyMce tinyMce;
    int lang;
    OpenQuestion question;
    ServletContext servletContext;
    StringBuffer str;

    public OpenQuestionHandler(TinyMce tinyMce, int lang, OpenQuestion question, ServletContext servletContext) {
        this.tinyMce = tinyMce;
        this.lang = lang;
        this.question = question;
        FileReader fileReader = new FileReader("tests/questions/open/variant.edit.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("add variant")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADD_VARIANT, null));
        } else if (name.equals("variants")){
            for (int i=0; i<question.answers.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantHandler handler = new SingleVariantHandler(question.answers.get(i), tinyMce, i, lang,
                        question.getQuestionType(), question.getNumber());
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("add href")){
            pw.print("question?questionNumber="+question.getNumber()+"&variant=-1"+ Rand.getRandString());
        }
    }

}
