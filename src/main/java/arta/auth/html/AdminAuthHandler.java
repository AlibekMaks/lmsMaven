package arta.auth.html;

import arta.filecabinet.logic.SearchParams;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.auth.logic.AuthorizationMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class AdminAuthHandler extends TemplateHandler {

    int personID;
    int roleID;
    SearchParams params;
    int lang;
    ServletContext servletContext;
    Message message;


    public AdminAuthHandler(int personID, int roleID, SearchParams params, int lang,
                            ServletContext servletContext, Message message) {
        this.personID = personID;
        this.roleID = roleID;
        this.params = params;
        this.lang = lang;
        this.servletContext = servletContext;
        this.message = message;
    }
                                                                    
    public void replace(String name, PrintWriter pw) {
        if (name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("auth params")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.AUTH_PARAMS, null));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("login")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.LOGIN, null));
        } else if (name.equals("password")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.PASSWORD, null));
        } else if (name.equals("confirmation")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.CONFIRM_PASSWORD, null));
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if(name.equals("roleID")){
            pw.print(roleID);
        } else if (name.equals("personID")){
            pw.print(personID);
        } else if (name.equals("return link")){
            String back = "tutor";
            String role = "tutorID";
            if (roleID == Constants.STUDENT) {back = "student"; role = "studentID";}
            pw.print(back + "?" + params.getFullParams() + "&" + role + "=" +personID);
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.AUTH_PARAMS));
        } else if (name.equals("enter all data")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        } else if (name.equals("pass and conf are not equal")){
            pw.print(MessageManager.getMessage(lang, AuthorizationMessages.PASSWORD_AND_ITS_CONFIRM_ARE_NOT_EQUAL));
        }
    }
}
