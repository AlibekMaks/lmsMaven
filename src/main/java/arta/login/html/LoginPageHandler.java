package arta.login.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.selects.RolesSelect;
import arta.login.logic.LoginMessages;

import java.io.PrintWriter;


public class LoginPageHandler extends TemplateHandler{

    int lang;

    public LoginPageHandler(int lang) {
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("Role")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.ROLE, null));
        } else if (name.equals("Enter system")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.ENTER_SYSTEM, null));
        } else if (name.equals("Roles list")){
            RolesSelect select = new RolesSelect();
            select.writeRoleSelect(lang, 100, pw);
        } else if (name.equals("Login")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.LOGIN, null));
        } else if (name.equals("Password")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.PASSWORD, null));
        } else if (name.equals("Enter")){
            pw.print(MessageManager.getMessage(lang, LoginMessages.ENTER, null));
        }
    }
}
