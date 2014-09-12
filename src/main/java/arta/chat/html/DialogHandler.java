package arta.chat.html;

import arta.common.html.handler.TemplateHandler;
import arta.chat.servlet.ChatServlet;

import java.io.PrintWriter;


public class DialogHandler extends TemplateHandler {

    int roomID;


    public DialogHandler(int roomID) {
        this.roomID = roomID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("roomID")){
            pw.print(roomID);
        } else if (name.equals("get messages")){
            pw.print(ChatServlet.GET_MESSAGES_OPTION);
        }
    }
}
