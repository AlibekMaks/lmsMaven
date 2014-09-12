package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.menu.MenuGenerator;
import arta.common.html.menu.HTMLMenu;
import arta.common.html.menu.MenuMessages;
import arta.common.html.menu.HTMLMenuItem;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.chat.ChatMessages;
import arta.chat.servlet.ChatServlet;

import javax.swing.*;
import java.io.PrintWriter;
import java.awt.*;


public class StudyRoomMenuHandler extends TemplateHandler{

    int lang;
    int roomID;
    int roleID;


    public StudyRoomMenuHandler(int lang, int roomID, int roleID) {
        this.lang = lang;
        this.roomID = roomID;
        this.roleID = roleID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("menu")){
            new MenuGenerator().writeStudyRoomMenu(lang, pw, roomID, roleID);
        }
    }
}
