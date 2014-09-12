package arta.help.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.help.HelpMessages;

import java.io.PrintWriter;


public class StartPageHandler extends TemplateHandler {

    int lang;

    public StartPageHandler(int lang) {
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("select help item")){
           
        }
    }
}
