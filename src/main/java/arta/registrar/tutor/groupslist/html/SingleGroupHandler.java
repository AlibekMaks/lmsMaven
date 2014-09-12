package arta.registrar.tutor.groupslist.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.registrar.tutor.groupslist.logic.StudyGroup;

import java.io.PrintWriter;


public class SingleGroupHandler extends TemplateHandler {

    StudyGroup group;
    int lang;
    StringTransform trsf = new StringTransform();

    public SingleGroupHandler(int lang) {
        this.lang = lang;
    }


    public void setGroup(StudyGroup group) {
        this.group = group;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("subject")){
            pw.print(trsf.getHTMLString(group.subject));
        } else if (name.equals("class")){
            pw.print(trsf.getHTMLString(group.className));
        } else if (name.equals("group number")){
            pw.print(group.groupNumber);
        } else if (name.equals("open")){
            pw.print("<a href=\"tutorregistrar?studygroupID="+group.studyGroupID+"&subgroupID="+group.subgroupID +
                    "&nocache="+ Rand.getRandString() + "\" class=\"href\" title=\""+trsf.getHTMLString(group.subject)+" "+
                    trsf.getHTMLString(group.className)+" ("+group.groupNumber+")\">");
            pw.print(MessageManager.getMessage(lang, Constants.OPEN, null));
            pw.print("</a>");
        }
    }
}
