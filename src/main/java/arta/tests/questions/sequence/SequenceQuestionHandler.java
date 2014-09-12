package arta.tests.questions.sequence;

import arta.common.html.util.TinyMce;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.tests.common.TestMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SequenceQuestionHandler extends TemplateHandler {

    int lang;
    TinyMce tinyMce;
    SequenceQuestion question;

    StringBuffer str;

    public SequenceQuestionHandler(int lang, ServletContext servletContext, TinyMce tinyMce, SequenceQuestion question) {
        this.lang = lang;
        this.tinyMce = tinyMce;
        this.question = question;
        FileReader fileReader = new FileReader("tests\\questions\\sequence/variant.edit.txt");
        str = fileReader.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("add variant")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADD_VARIANT, null));
        } else if (name.equals("questionNumber")){
            pw.print(question.getNumber());
        } else if (name.equals("sort")){
            pw.print(MessageManager.getMessage(lang, TestMessages.SORT, null));
        } else if (name.equals("variants")){
            for (int i=0; i<question.tags.size(); i++){
                StringBuffer  tmp = new StringBuffer(str);
                SingleVariantHandler handler = new SingleVariantHandler(lang, i,
                        question.tags.size(), question.tags.get(i), tinyMce, question.getNumber());
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("sort href")){
            pw.print("question?questionNumber="+question.getNumber()+ Rand.getRandString());
        } else if (name.equals("add href")){
            pw.print("question?questionNumber="+question.getNumber()+"&variant=-1"+Rand.getRandString());
        }
    }
}
