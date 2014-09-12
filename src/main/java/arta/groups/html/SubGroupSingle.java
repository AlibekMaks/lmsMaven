package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.menu.MenuMessages;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.groups.logic.SubGroup;
import arta.groups.logic.StudyGroup;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.classes.ClassMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class SubGroupSingle extends TemplateHandler {

    SubGroup group;
    int classID;
    SearchParams params;
    int lang;
    StudyGroup studyGroup;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();


    public SubGroupSingle(int classID, SearchParams params, int lang,
                          StudyGroup studyGroup, ServletContext servletContext) {
        this.classID = classID;
        this.params = params;
        this.lang = lang;
        this.studyGroup = studyGroup;
        this.servletContext = servletContext;
    }


    public void setGroup(SubGroup group) {
        this.group = group;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("delete")){
            pw.print("<a href=\"studygroup?option=delete&subgroupid="+group.getSubGroupID()+"&classID="+classID+
                    "&"+params.getFullParams()+"&studyGroupID="+studyGroup.getStudyGroupID()+"\" ");
            if (group.students.size()>0){
                pw.print(" onClick='return false' ");
            }
            pw.print(" class=\"href\">");
            pw.print(MessageManager.getMessage(lang, Constants.DELETE, null));
            pw.print("</a>");
        } else if (name.equals("subgroup name")){
            Properties prop = new Properties();
            prop.setProperty("number", group.getGroupNumber() + "");
            pw.print(MessageManager.getMessage(lang, ClassMessages.SUBGROUP, prop));
        } else if (name.equals("tutor")){
            pw.print(trsf.getHTMLString(group.getTutorName()));
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
        } else if (name.equals("move to")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.MOVE_TO, null));
        } else if (name.equals("records")){
            SubGroupStudent handler = new SubGroupStudent(studyGroup);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("groups/subgroup.student.txt").read(servletContext);
            for (int i=0; i<group.students.size(); i++){
                handler.setStudent(group.students.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("tut")){
            pw.print(MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE, null));
        } else if (name.equals("change tutor")){
            pw.print("<a class=\"href\" href=\"#\" onClick='selectTutor("+group.getSubGroupID()+");return false;'>");
            pw.print("...");
            pw.print("</a>");
        } else if (name.equals("subgroupID")){
            pw.print(group.getSubGroupID());
        }
    }
}
