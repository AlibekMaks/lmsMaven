package arta.tests.questions.open;


import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class OpenQuestionCheckHandler extends TemplateHandler {

    int lang;
    OpenQuestion question;

    StringBuffer str;

    public OpenQuestionCheckHandler(int lang, OpenQuestion question, ServletContext servletContext) {
        this.lang = lang;
        this.question = question;
        FileReader fileReader = new FileReader("tests/questions/open/variant.check.txt");
        str = fileReader.read(servletContext);
    }

        public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("questionNumber")){
            pw.print(question.getNumber());
        } else if (name.equals("variants")){
            for (int i=0; i<question.answers.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantCheckHandler handler = new SingleVariantCheckHandler(lang, 
                        question.answers.get(i), question.getQuestionType(), i, question.answers.size());
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT, null));
        }
    }

}
