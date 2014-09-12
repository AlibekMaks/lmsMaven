package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.server.Server;
import arta.studyroom.logic.StudyRoomMessages;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.io.PrintWriter;


public class TutorBooksHandler extends TemplateHandler {

    ArrayList<SimpleObject> books;
    String header;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();
    int roleID;
    int lang;
    int roomID;


    public TutorBooksHandler(ArrayList<SimpleObject> books, String header, ServletContext servletContext,
                             int lang, int roleID, int roomID) {
        this.books = books;
        this.header = header;
        this.servletContext = servletContext;
        this.lang = lang;
        this.roleID = roleID;
        this.roomID = roomID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("header")){
            pw.print(header);
        } else if (name.equals("items")){
            StringBuffer template = new FileReader("studyroom/content/tutor.book.item.txt").read(servletContext);
            Parser parser = new Parser();
            TutorBookHandler handler = new TutorBookHandler(trsf, lang, roleID, roomID);
            parser.setTemplateHandler(handler);
            parser.setPrintWriter(pw);
            for (int i=0; i<books.size(); i++){
                parser.setStringBuilder(new StringBuffer(template));
                handler.setBook(books.get(i));
                parser.parse();
            }
        } else if (name.equals("add tutorial")){
             if ((roleID & Constants.TUTOR) > 0){
                 pw.print("<tr><td style=\"padding-left:5px; padding-right:5px;\">");
                 pw.print("<a href=\"appointtutorial?nocache="+Rand.getRandString()+"\" " +
                         " onclick='return openAppointWindow()' class=\"href\">");
                 pw.print(MessageManager.getMessage(lang, StudyRoomMessages.ADD_TUTORIAL));
                 pw.print("</a>");
                 pw.print("</td></tr>");
            } else {
                 pw.print("<tr><td style=\"padding-left:5px; padding-right:5px;\">");
                 pw.print("<a href=\"#\" " +
                         " onclick='document.location.href=\""+ Server.MAIN_URL +"content?roomID="+roomID
                         + "&nocache=" + Rand.getRandString() + "\"; return false;' class=\"href\">");
                 pw.print(MessageManager.getMessage(lang, StudyRoomMessages.REFRESH));
                 pw.print("</a>");
                 pw.print("</td></tr>");
             }
        } else if (name.equals("delete td")){
            if ((roleID & Constants.TUTOR) > 0){
                pw.print("<td width=\"16px\">");
                pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\">");
                pw.print("</td>");
            }
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("roomID")){
            pw.print(roomID);
        }
    }
}
