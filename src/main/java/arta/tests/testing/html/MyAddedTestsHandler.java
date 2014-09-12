package arta.tests.testing.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.servlet.TestsForTestingServlet;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 15:00:27
 * To change this template use File | Settings | File Templates.
 */
public class MyAddedTestsHandler extends TemplateHandler {

    int lang;
    Testing testing;
    ServletContext servletContext;
    Message message;

    StringTransform trsf = new StringTransform();

    public MyAddedTestsHandler(int lang, Testing testing, ServletContext servletContext,
                               Message message) {
        this.lang = lang;
        this.testing = testing;
        this.servletContext = servletContext;
        this.message = message;
    }



    public void replace(String name, PrintWriter pw) {
        if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADDED_TEST));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/mytesting.card.options.html").read(servletContext),
                    pw, new MyTestingOptionsHandler(lang, testing,
                    "addtests?option="+ TestsForTestingServlet.SEARCH_TO_ADD_OPTION +"&nocache=" + Rand.getRandString(),
                    MessageManager.getMessage(lang, TestingMessages.ADD_TESTS))).parse();
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr>");
                    pw.print("<td>");
                        message.writeMessage(pw);
                    pw.print("</td>");
                pw.print("</tr>");
            }
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_NAME));
        } else if (name.equals("test name")){
            pw.print( trsf.getHTMLString("\""+testing.getTestName(testing.mainTestID)+"\""));
        } else if (name.equals("total")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TOTAL_QUESTIONS_COUNT));
        } else if (name.equals("add")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADDED_TEST));
        } else if (name.equals("easy")){
            pw.print(MessageManager.getMessage(lang, TestMessages.EASY));
        } else if (name.equals("middle")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE));
        } else if (name.equals("difficult")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT));
        } else if (name.equals("added records")){
            StringBuffer template = new FileReader("testing/myadded.test.single.txt").read(servletContext);
            MyTestSingleHandler handler = new MyTestSingleHandler(trsf, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);

            for (int i = 0; i < testing.tests.size(); i ++){
                parser.setStringBuilder(new StringBuffer(template));
                handler.setTest(testing.tests.get(i));
                parser.parse();
            }

//        } else if (name.equals("change")){
//            pw.print(MessageManager.getMessage(lang, Constants.CHANGE));
        }  else if (name.equals("change")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE));
        } else if (name.equals("change option")){
            pw.print(TestsForTestingServlet.CHANGE_OPTION);
        }
    }
}
