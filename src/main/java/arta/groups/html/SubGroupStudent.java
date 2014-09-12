package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.Select;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.groups.logic.StudyGroup;

import java.io.PrintWriter;


public class SubGroupStudent extends TemplateHandler {

    SimpleObject student;
    StudyGroup group;
    StringTransform trsf = new StringTransform();


    public SubGroupStudent(StudyGroup group) {
        this.group = group;
    }


    public void setStudent(SimpleObject student) {
        this.student = student;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("studentID")){
            pw.print(student.id);
        } else if (name.equals("name")){
            pw.print(trsf.getHTMLString(student.name));
        } else if (name.equals("subgroup select")){
            Select select  = new Select(Select.SIMPLE_SELECT);
            pw.print(select.getStartSelect("stdsel"+student.id, "stdsel"+student.id, true));
            for (int i=0; i<group.subgroups.size(); i++){
                pw.print(select.addOption(group.subgroups.get(i).getSubGroupID() + "", false,
                        group.subgroups.get(i).getGroupNumber() + ""));
            }
            pw.print(select.endSelect());
        }
    }
}
