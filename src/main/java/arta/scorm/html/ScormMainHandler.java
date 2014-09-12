package arta.scorm.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.messages.MessageManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 10.04.2008
 * Time: 11:58:34
 * To change this template use File | Settings | File Templates.
 */
public class ScormMainHandler extends TemplateHandler {

    ServletContext servletContext;

    HashMap <String, String> messages;
    int lang;


    public ScormMainHandler(ServletContext servletContext, HashMap<String, String> messages, int lang) {
        this.servletContext = servletContext;
        this.messages = messages;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("main part")){
            new Parser(new FileReader("scorm/message.txt").read(servletContext),
                    pw,
                    new ScormMessageHandler(messages, lang)).parse();
        }
    }
}
