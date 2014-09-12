package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.groups.logic.StudyGroup;
import arta.groups.logic.StudyGroupsSearchParams;
import arta.filecabinet.logic.SearchParams;
import arta.classes.ClassMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class GroupSingle extends TemplateHandler {

    int classID = 0;
    StudyGroup group;
    int lang;
    SearchParams params;
    int recordNumber = 0;
    int groupsNumber = 0;
    StringTransform trsf;

    public GroupSingle(int classID, int lang, SearchParams params, int groupsNumber, StringTransform trsf) {
        this.classID = classID;
        this.lang = lang;
        this.params = params;
        this.groupsNumber = groupsNumber;
        this.trsf = trsf;
    }

    public void set (StudyGroup group, int recordNumber){
        this.group = group;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
//        if (name.equals("name")){
//            pw.print("<a href=\"studygroup?studygroupID="+group.getStudyGroupID()+"&"+ StudyGroupsSearchParams.RECORD_NUMBER+"="+recordNumber+
//                    "&"+StudyGroupsSearchParams.RECORDS_COUNT+"="+groupsNumber+"&classID="+classID+"&"+params.getFullParams()+"\" class=\"href\" >");
//            pw.print(trsf.getHTMLString(group.getSubjectName(), "-"));
//            pw.print("</a>");
        if (name.equals("name")){
            pw.print("<a href=\"subject?subjectID="+group.getSubjectID()+"\" class=\"href\" >");
            pw.print(trsf.getHTMLString(group.getSubjectName()));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("subject", group.getSubjectName());
            pw.print("<a class=\"href\" href=\"class?option=-1&groupID="+group.getStudyGroupID()+
                    "&classID="+classID+"&"+params.getFullParams()+"\" " +
                    " onClick='return confirm(\"" +
                        MessageManager.getMessage(lang, ClassMessages.DO_YOU_REALLY_WANNA_DELETE_SUBJECT_FROM_STUDY_LIST, prop) +
                    "\")' title=\""+trsf.getHTMLString(group.getSubjectName())+"\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
