package arta.studyroom.logic;

import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 19.03.2008
 * Time: 17:47:17
 * To change this template use File | Settings | File Templates.
 */
public class BoardHandler extends TemplateHandler {

    int lang;
    int roomID;
    int roleID;
    int personID;


    public BoardHandler(int lang, int roomID, int roleID, int personID) {
        this.lang = lang;
        this.roomID = roomID;
        this.roleID = roleID;
        this.personID = personID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("roomID")){
            pw.print(roomID);
        } else if (name.equals("lang")){
            pw.print(lang);
        } else if (name.equals("roleID")){
            pw.print(roleID);
        } else if (name.equals("personID")){
            pw.print(personID);
        }
    }
}
