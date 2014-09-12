package arta.tests.questions.associate;

import arta.tests.common.TestMessages;
import arta.common.html.util.TinyMce;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class AssociateQuestionHandler extends TemplateHandler {

    AssociateQuestion question;
    TinyMce tinyMce;
    int lang;
    StringBuffer str;

    public AssociateQuestionHandler(AssociateQuestion question, ServletContext servletContext, TinyMce tinyMce, int lang) {
        this.question = question;
        this.tinyMce = tinyMce;
        this.lang = lang;
        FileReader fileOpener = new FileReader("tests/questions/associate/variant.edit.txt");
        str = fileOpener.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("add variant")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADD_VARIANT, null));
        } else if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleVariantHandler handler = new SingleVariantHandler(lang, question.tags.get(i), tinyMce, i, question.getNumber());
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("add href")){
            pw.print("question?questionNumber="+question.getNumber()+"&variant=-1"+ Rand.getRandString());
        }
    }

}
