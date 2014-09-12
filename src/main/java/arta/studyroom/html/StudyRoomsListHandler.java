package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.studyroom.logic.StudyRoom;
import arta.chat.ChatMessages;
import arta.subjects.logic.SubjectMessages;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.classes.ClassMessages;
import arta.timetable.designer.logic.LessonConstants;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.03.2008
 * Time: 16:48:45
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoomsListHandler extends TemplateHandler {

    ArrayList<StudyRoom> rooms;
    ServletContext servletContext;
    int lang;
    int roleID;

    StringTransform trsf = new StringTransform();


    public StudyRoomsListHandler(ArrayList<StudyRoom> rooms,
                                 ServletContext servletContext, int lang, int roleID) {
        this.rooms = rooms;
        this.servletContext = servletContext;
        this.lang = lang;
        this.roleID = roleID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("study rooms list")){
            pw.print(MessageManager.getMessage(lang, ChatMessages.LIST_OF_AVAILABLE_STUDY_ROOMS));
        } else if (name.equals("subject")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT));
        } else if (name.equals("additionsl field")){
            if (roleID == Constants.STUDENT){
                pw.print(MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE));
            } else {
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.CLASS));
            }
        } else if (name.equals("subgroup number")){
            pw.print(MessageManager.getMessage(lang, LessonConstants.SUBGROUP_NUMBER));
        } else if (name.equals("in room")){
            pw.print(MessageManager.getMessage(lang, ChatMessages.IN_CHAT));
        } else if (name.equals("records")){

            StudyRoomsRecordHandler recordHandler = new StudyRoomsRecordHandler(trsf);
            StringBuffer tempalte = new FileReader("studyroom/study.rooms.record.txt").read(servletContext);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(recordHandler);

            for (int i=0; i < rooms.size(); i ++){
                parser.setStringBuilder(new StringBuffer(tempalte));
                recordHandler.setStudyRoom(rooms.get(i));
                parser.parse();
            }

        }
    }
}
