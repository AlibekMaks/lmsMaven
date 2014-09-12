package arta.registrar.tutor.groupslist.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.registrar.tutor.logic.RegistrarMessages;
import arta.registrar.tutor.groupslist.logic.StudyGroupsManager;
import arta.subjects.logic.SubjectMessages;
import arta.classes.ClassMessages;
import arta.timetable.designer.logic.LessonConstants;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class GroupsListHandler extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    StudyGroupsManager manager = new StudyGroupsManager();


    public GroupsListHandler(ServletContext servletContext, int lang, int tutorID) {
        this.servletContext = servletContext;
        this.lang = lang;
        manager.search(tutorID, lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("subject")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT, null));
        } else if (name.equals("class")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS, null));
        } else if (name.equals("subgroup number")){
            pw.print(MessageManager.getMessage(lang, LessonConstants.SUBGROUP_NUMBER, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("registrar/tutor/single.group.txt").read(servletContext);
            SingleGroupHandler handler = new SingleGroupHandler(lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<manager.groups.size(); i++){
                handler.setGroup(manager.groups.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("open")){
            pw.print(MessageManager.getMessage(lang, Constants.OPEN, null));
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null   ));            
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.GROUPS_LIST));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
