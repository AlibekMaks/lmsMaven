package arta.login.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.login.logic.LoginMessages;

import java.io.PrintWriter;


public class AccessDeniedHandler extends TemplateHandler{

    int lang;

    public AccessDeniedHandler(int lang) {
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("Access denied")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.ACCESS_DENIED, null));
        }
    }
}
