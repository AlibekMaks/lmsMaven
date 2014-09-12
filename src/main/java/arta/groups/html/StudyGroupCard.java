package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.groups.logic.StudyGroup;
import arta.groups.logic.StudyGroupsSearchParams;
import arta.filecabinet.logic.SearchParams;
import arta.classes.ClassMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class StudyGroupCard extends TemplateHandler {

    SimpleObject studyClass;
    StudyGroup group;
    ServletContext servletContext;
    int lang;
    int recordNumber; // number of study group
    int recordsCount; // number of study groups on this subject
    SearchParams params; // search params of classes


    public StudyGroupCard(SimpleObject studyClass, StudyGroup group,
                          ServletContext servletContext, int lang,
                          int recordNumber, int recordsCount, SearchParams params) {
        this.studyClass = studyClass;
        this.group = group;
        this.servletContext = servletContext;
        this.lang = lang;
        this.recordNumber = recordNumber;
        this.recordsCount = recordsCount;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("classID")){
            pw.print(studyClass.id);
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        }  else if (name.equals("add subgroup")){
            pw.print("<a href=\"studygroup?option=add&studyGroupID="+group.getStudyGroupID()+"&classID="+studyClass.id+"&"+
                    params.getFullParams()+"\" class=\"href\">");
            pw.print(MessageManager.getMessage(lang, ClassMessages.ADD_SUBGROUP, null));
            pw.print("</a>");
        } else if (name.equals("subgroups")){
            SubGroupSingle handler = new SubGroupSingle(studyClass.id, params, lang, group, servletContext);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("groups/subgroup.txt").read(servletContext);
            for (int i=0; i<group.subgroups.size(); i++){
                handler.setGroup(group.subgroups.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("studyGroupID")){
            pw.print(group.getStudyGroupID());
        } else if (name.equals("groupNumber")){
            pw.print(recordNumber);
        } else if (name.equals("groupsCount")){
            pw.print(recordsCount);
        } else if (name.equals("groupNumberStr")){
            pw.print(StudyGroupsSearchParams.RECORD_NUMBER);
        } else if (name.equals("groupsCountStr")){
            pw.print(StudyGroupsSearchParams.RECORDS_COUNT);
        } else if (name.equals("return link")){
            pw.print("class?classID="+studyClass.id + "&" + params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("page header")){
            StringBuffer result = new StringBuffer();
            Properties prop = new Properties();
            prop.setProperty("name", studyClass.name);
            result.append(MessageManager.getMessage(lang, ClassMessages.CLASS, prop));
            result.append("<br>");
            Properties prop1 = new Properties();
            prop1.setProperty("subject", group.getSubjectName());
            result.append(MessageManager.getMessage(lang, ClassMessages.SUBJECT, prop1));
            pw.print(result.toString());
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
