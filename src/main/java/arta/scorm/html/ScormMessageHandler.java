package arta.scorm.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.DataExtractor;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 10.04.2008
 * Time: 8:53:06
 * To change this template use File | Settings | File Templates.
 */
public class ScormMessageHandler extends TemplateHandler {

    DataExtractor extractor = new DataExtractor();

    HashMap<String, String> messages;
    int lang;


    public ScormMessageHandler (HashMap<String, String> messages, int lang) {
        this.messages = messages;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("message")){
            if (messages.get(name) != null){
                pw.print(MessageManager.getMessage(lang, extractor.getInteger(messages.get(name))));
            }
        } else {
            if (messages.get(name) != null){
                pw.print(messages.get(name));
            }
        }
    }
}
