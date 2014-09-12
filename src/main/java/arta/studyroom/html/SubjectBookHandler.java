package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 15:38:15
 * To change this template use File | Settings | File Templates.
 */
public class SubjectBookHandler extends TemplateHandler {

    StringTransform trsf;
    int roleID;

    SimpleObject book;


    public SubjectBookHandler(StringTransform trsf, int roleID) {
        this.trsf = trsf;
        this.roleID = roleID;
    }


    public void setBook(SimpleObject book) {
        this.book = book;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("bookID")){
            pw.print(book.id);
        } else if (name.equals("name")){
            pw.print(trsf.getHTMLString(book.name));
        } else if (name.equals("link")){
            if (roleID == Constants.STUDENT){
                pw.print("<td width=\"16px\">");
                    pw.print("<a href=\"runtime/StartCourse.jsp?courseID="+book.id+"&nocache="+ Rand.getRandString()
                            + "\" target=\"_blank\">");
                        pw.print("<img src=\"images/open.blank.gif\" width=\"16px\" height=\"16px\" border=0>");
                    pw.print("</a>");
                pw.print("</td>");
            }
        }
    }
}
