package arta.chat.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.chat.logic.ChatPerson;
import arta.chat.ChatMessages;

import java.io.PrintWriter;


public class MessagesHandler extends TemplateHandler {

    int roomID;
    ChatPerson person;
    int lang;


    public MessagesHandler(int roomID, ChatPerson person, int lang) {
        this.roomID = roomID;
        this.person = person;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("roomID")){
            pw.print(roomID);
        } else if (name.equals("clear")){
            pw.print(MessageManager.getMessage(lang, ChatMessages.CLEAR_RECEIPENTS_LIST, null));
        } else if (name.equals("receipents")){
            pw.print(MessageManager.getMessage(lang, ChatMessages.TO, null));
        } else if (name.equals("user name")){
            pw.print(person.login);
        } else if (name.equals("send")){
            pw.print(MessageManager.getMessage(lang, Constants.SEND, null));
        }
    }
}
