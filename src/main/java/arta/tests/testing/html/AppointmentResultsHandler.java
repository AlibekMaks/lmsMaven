package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 27.03.2008
 * Time: 10:37:34
 * To change this template use File | Settings | File Templates.
 */
public class AppointmentResultsHandler extends TemplateHandler {

    int lang;
    Message message;
    ServletContext servletContext;
    Testing testing;


    public AppointmentResultsHandler(int lang, Message message,
                                     ServletContext servletContext, Testing testing) {
        this.lang = lang;
        this.message = message;
        this.servletContext = servletContext;
        this.testing = testing;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("pageheader")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_APPOINTMENT));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/testing.card.options.html").read(servletContext),
                    pw,
                    new TestingOptionsHandler(lang, testing, null, null)).parse();
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                message.writeMessage(pw);
            }
        }
    }
}
