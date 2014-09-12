package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;

import java.io.PrintWriter;


public class TutorAddSingle extends TemplateHandler {

    SimpleObject tutor;
    int lang;
    StringTransform trsf = new StringTransform();

    public TutorAddSingle(int lang) {
        this.lang = lang;
    }


    public void setTutor(SimpleObject tutor) {
        this.tutor = tutor;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(tutor.name));
        } else if (name.equals("select")){
            pw.print("<a href=\"#\" onClick='select("+tutor.id+", \""+new StringTransform().getHTMLString(tutor.name)+"\")' class=\"href\">");
            pw.print(MessageManager.getMessage(lang, Constants.SELECT, null));
            pw.print("</a>");
        }
    }
}
