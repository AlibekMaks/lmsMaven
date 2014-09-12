package arta.tests.testing.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.TestsManager;
import arta.tests.testing.servlet.AddTestsForTestingServlet;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class MyTestsListHandler extends TemplateHandler {


    SearchParams params;
    int lang;
    Testing testing;
    ServletContext servletContext;

    TestsManager manager = new TestsManager();
    MyTestSingleHandler handler ;
    int partsNumber;

    Message message;

    public MyTestsListHandler(SearchParams params, int lang, Testing testing,
                              ServletContext servletContext, Message message) {
        this.params = params;
        this.lang = lang;
        this.testing = testing;
        this.servletContext = servletContext;
        this.message = message;
        manager.search(params, testing.tests);
        partsNumber = params.getPartsNumber(); 
        handler = new MyTestSingleHandler(new StringTransform(), lang);

    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("search value")){
            pw.print(params.getSearch());
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("total")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TOTAL_QUESTIONS_COUNT, null));
        } else if (name.equals("easy")){
            pw.print(MessageManager.getMessage(lang, TestMessages.EASY, null));
        } else if (name.equals("middle")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MIDDLE, null));
        } else if (name.equals("difficult")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DIFFICULT, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("testing/mytest.single.txt").read(servletContext);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<manager.tests.size(); i++){
                handler.setTest(manager.tests.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("add")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("page header")){
             pw.print(MessageManager.getMessage(lang, TestingMessages.ADD_TESTS));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/mytesting.card.options.html").read(servletContext),
                    pw,
                    new MyTestingOptionsHandler(lang, testing, null, null)).parse();
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "addtests?option="+ AddTestsForTestingServlet.SEARCH_TO_ADD_OPTION
                            +"&" + params.getParams(), params.partNumberStr).writeLinks(pw);
        }
    }
}
