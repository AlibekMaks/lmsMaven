package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.studyroom.logic.StudyRoomMessages;

import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 11:04:22
 * To change this template use File | Settings | File Templates.
 */
public class TutorBookHandler extends TemplateHandler {

    StringTransform trsf;
    int lang;
    SimpleObject book;
    int roleID;
    int roomID;


    public TutorBookHandler(StringTransform trsf, int lang, int roleID, int roomID) {
        this.trsf = trsf;
        this.lang = lang;
        this.roleID = roleID;
        this.roomID = roomID;
    }


    public void setBook(SimpleObject book) {
        this.book = book;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(book.name));
        } else if (name.equals("bookID")){
            pw.print(book.id);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("delete td")){
            if ((roleID & Constants.TUTOR) > 0){
                Properties prop = new Properties();
                prop.setProperty("name", trsf.getHTMLString(book.name));
                pw.print("<td width=\"16px\" align=center valign=middle>");
                    pw.print("<a href=\"content?option=-1&roomID="+roomID+"&bookID="+book.id+"&nocache="+Rand.getRandString()+"\" " +
                            " title=\""+trsf.getHTMLString(book.name)+"\" " +
                            " onclick='return confirm(\""+ MessageManager.getMessage(lang, StudyRoomMessages.CONFIRM_DELETE_TUTOR_BOOK_FROM_GROUP, prop) +"\");'>");
                        pw.print("<img src=\"images/icon.delete.gif\" width[=\"16px\" height=\"16px\" border=0>");
                    pw.print("</a>");
                pw.print("</td>");
            }
        }
    }
}
