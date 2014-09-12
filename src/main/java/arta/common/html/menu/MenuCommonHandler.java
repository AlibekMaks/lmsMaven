package arta.common.html.menu;

import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;


public class MenuCommonHandler extends TemplateHandler {

    MenuGenerator generator;

    int lang;
    int roleID;
    int id;


    public MenuCommonHandler(int lang, int roleID, int id) {
        this.lang = lang;
        this.roleID = roleID;
        this.id = id;
        generator = new MenuGenerator();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("menu")){
            generator.writeUserMenu(pw, lang, roleID);
        } else if (name.equals("common")){
            generator.writeCommonMenu(lang, pw);
        }
    }
}
