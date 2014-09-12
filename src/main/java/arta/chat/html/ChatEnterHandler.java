package arta.chat.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Rand;
import arta.chat.servlet.ChatServlet;

import java.io.PrintWriter;


public class ChatEnterHandler extends TemplateHandler {

    int roomID;


    public ChatEnterHandler(int roomID) {
        this.roomID = roomID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("roomID")){
            pw.print(roomID);
        } else if(name.equals("random")){
            Rand.getRandString();
        } else if (name.equals("print message")){
            pw.print(ChatServlet.PRINT_MESSAGES);
        }
    }
}
