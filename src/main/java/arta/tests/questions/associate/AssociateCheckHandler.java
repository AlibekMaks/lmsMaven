package arta.tests.questions.associate;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class AssociateCheckHandler extends TemplateHandler {

    AssociateQuestion question;
    int lang;

    StringBuffer str;

    public AssociateCheckHandler(AssociateQuestion question, int lang, ServletContext servletContext) {
        this.question = question;
        this.lang = lang;
        FileReader fileReader = new FileReader("tests/questions/associate/variant.check.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("questionNumber")){
            pw.print(question.getNumber());
        } else if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantCheckHandler handler = new SingleVariantCheckHandler(lang, question.tags.get(i),
                        question.tags.size(), i+1);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT, null));
        }
    }
}
