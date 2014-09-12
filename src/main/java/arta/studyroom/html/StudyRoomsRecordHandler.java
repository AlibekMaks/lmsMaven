package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.studyroom.logic.StudyRoom;
import arta.chat.servlet.ChatServlet;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.03.2008
 * Time: 16:57:19
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoomsRecordHandler extends TemplateHandler {

    StudyRoom studyRoom;
    StringTransform trsf;

    public StudyRoomsRecordHandler(StringTransform trsf) {
        this.trsf = trsf;
    }


    public void setStudyRoom(StudyRoom studyRoom) {
        this.studyRoom = studyRoom;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print("<a href=\"chat?option="+ ChatServlet.ENTER_CHAT +"&roomID="+studyRoom.getSubGroupID()+"\" " +
                    " class=\"href\">");
            pw.print(trsf.getHTMLString(studyRoom.getSubjectName(), "-"));
            pw.print("</a>");
        } else if (name.equals("additional field")){
            pw.print(trsf.getHTMLString(studyRoom.getAdditionalField()));
        } else if (name.equals("subgroup number")){
            pw.print(studyRoom.getSubGroupNumber());
        } else if (name.equals("in room")){
            pw.print(studyRoom.getPersonsCount());
        }
    }
}
