package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.chat.logic.studyRooms.StudyRoomsManager;
import arta.chat.servlet.ChatServlet;
import arta.studyroom.logic.Content;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudyRoomStartHandler extends TemplateHandler {

    int lang;
    ServletContext servletContext;
    int dayNumber;
    int type;
    Person person;

    Content content;
    StudyRoomsManager manager;



    public StudyRoomStartHandler(int lang, ServletContext servletContext, Person person) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.dayNumber = Date.getDayNumber();
        manager = new StudyRoomsManager(lang, person.getPersonID(), type,  dayNumber);
        if (person.getRoleID() == Constants.STUDENT){
            manager.searchForStudent();
        } else {
            manager.searchForTutor();
        }
        content = new Content(person);
        content.load(manager.roomID);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("content")){
            new Parser(new FileReader("studyroom/content/content.txt").read(servletContext), pw,
                    new ContentHandler(lang, manager.roomID, servletContext, content, person.getRoleID())).parse();
        } else if (name.equals("lessonID")){
            pw.print(manager.roomID);
        } else if (name.equals("enter option")){
            pw.print(ChatServlet.ENTER_CHAT);
        } else if (name.equals("subject name")){
            if (manager.roomName != null)
                pw.print(manager.roomName);
            else
                pw.print("&nbsp;");
        }
        }
   }
