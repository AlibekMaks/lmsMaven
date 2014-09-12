package arta.studyroom.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.studyroom.logic.StudyRoom;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.03.2008
 * Time: 17:05:41
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoomsListMainHandler extends PageContentHandler {

    int roleID;
    int lang;
    ArrayList<StudyRoom> rooms;
    ServletContext servletContext;


    public StudyRoomsListMainHandler(int roleID, int lang, ArrayList<StudyRoom> rooms, ServletContext servletContext) {
        this.roleID = roleID;
        this.lang = lang;
        this.rooms = rooms;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) {

    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("studyroom/study.rooms.list.txt").read(servletContext),
                pw, new StudyRoomsListHandler(rooms, servletContext, lang, roleID)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
