package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.Select;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;


public class GroupAddSingle extends TemplateHandler {

    SimpleObject subject;
    StringTransform trsf = new StringTransform();


    public GroupAddSingle() {
    }


    public void setSubject(SimpleObject subject) {
        this.subject = subject;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("subjectID")){
            pw.print(subject.id);
        } else if (name.equals("subject name")){
            pw.print(trsf.getHTMLString(subject.name));
        }
//        else if (name.equals("subgroups select")){
//            Select select = new Select(Select.SIMPLE_SELECT);
//            select.change_function = " set(\"sub"+subject.id+"td\", this.selectedIndex+1, "+subject.id+") ";
//            pw.print(select.getStartSelect("subsel"+subject.id, "subsel"+subject.id, true));
//            for (int i=0; i< Constants.MAX_SUBGROUPS_NUMBER; i++){
//                pw.print(select.addOption((i+1)+"", false, (i+1)+""));
//            }
//            pw.print(select.endSelect());
//        }
    }
}
