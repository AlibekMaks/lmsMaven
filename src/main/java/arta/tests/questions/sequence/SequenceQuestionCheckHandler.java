package arta.tests.questions.sequence;

import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class SequenceQuestionCheckHandler extends TemplateHandler {

    int lang;
    SequenceQuestion question;

    StringBuffer str;
    StringTransform stringTransform = new StringTransform();

    public SequenceQuestionCheckHandler(int lang, SequenceQuestion question, ServletContext servletContext) {
        this.lang = lang;
        this.question = question;
        FileReader fileReader = new FileReader("tests/questions/sequence/variant.check.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("questionNumber")){
            pw.print(question.getNumber());
        } else if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                SingleVariantCheckHandler handler = new SingleVariantCheckHandler(lang, question.tags.get(i),
                        stringTransform, question.tags.size(), i);
                StringBuffer tmp = new StringBuffer(str);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT, null));
        }
    }

}
