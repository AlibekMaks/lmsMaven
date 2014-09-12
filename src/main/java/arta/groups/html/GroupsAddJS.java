package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.classes.ClassMessages;

import java.io.PrintWriter;


public class GroupsAddJS extends TemplateHandler {

    int lang;


    public GroupsAddJS(int lang) {
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("select tutor")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.SELECT_TUTOR, null));
        }
    }
}
