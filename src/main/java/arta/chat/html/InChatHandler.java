package arta.chat.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.chat.servlet.ChatServlet;
import arta.chat.ChatMessages;

import java.io.PrintWriter;


public class InChatHandler extends TemplateHandler {

    int roomID;
    int lang;


    public InChatHandler(int roomID, int lang) {
        this.roomID = roomID;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("get in chat")){
            pw.print(ChatServlet.GET_IN_CHAT_OPTION);
        } else if (name.equals("roomID")){
            pw.print(roomID);
        } else if (name.equals("in chat")){
            pw.print(MessageManager.getMessage(lang, ChatMessages.IN_CHAT, null));
        } else if (name.equals("change status")){
            pw.print(ChatServlet.CHANGE_STUDENT_STATUS_OPTION);
        }
    }
}
