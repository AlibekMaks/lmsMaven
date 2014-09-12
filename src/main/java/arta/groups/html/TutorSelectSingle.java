package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.messages.MessageManager;

import java.io.PrintWriter;


public class TutorSelectSingle extends TemplateHandler {

    SimpleObject tutor;
    int lang;
    int subgroupID;
    StringTransform trsf = new StringTransform();


    public TutorSelectSingle(int lang, int subgroupID) {
        this.lang = lang;
        this.subgroupID = subgroupID;
    }

    public void setTutor(SimpleObject tutor) {
        this.tutor = tutor;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(tutor.name));
        } else if (name.equals("change")){
            pw.print("<a href=\"#\" onClick='setTutor(\""+new StringTransform().getHTMLString(tutor.name)+"\", "+tutor.id+"); return false;' " +
                    " class=\"href\" >");
            pw.print(MessageManager.getMessage(lang, Constants.SELECT, null));
            pw.print("</a>");
        }
    }
}
