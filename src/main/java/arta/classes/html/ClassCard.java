package arta.classes.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.common.logic.db.Varchar;
import arta.exams.ExamMessages;
import arta.exams.logic.Exam;
import arta.exams.logic.ExamsManager;
import arta.filecabinet.logic.SearchParams;
import arta.classes.logic.StudyClass;
import arta.classes.ClassMessages;
import arta.groups.html.GroupSingle;
import arta.subjects.logic.SubjectMessages;
import arta.subjects.logic.TestSelectSingle;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class ClassCard extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    SearchParams params;
    StudyClass studyClass;
    Message message;
    StringTransform trsf = new StringTransform();
    ExamsManager manager;

    public ClassCard(ServletContext servletContext, int lang, SearchParams params, StudyClass studyClass, Message message) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.params = params;
        this.studyClass = studyClass;
        this.message = message;

        manager = new ExamsManager();
        manager.search(Constants.SELECT_ALL);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("name value")){
            pw.print(trsf.getHTMLString(studyClass.getClassNameru()));
        } else if (name.equals("maxlenth")){
            pw.print(Varchar.NAME);
        } else if (name.equals("subjects")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.SUBJECTS_COVERED, null));
        } else if (name.equals("edit")){
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("delete")){
            pw.print(MessageManager.getMessage(lang, Constants.DELETE, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("groups/single.txt").read(servletContext);
            GroupSingle handler = new GroupSingle(studyClass.getClassID(), lang, params, studyClass.groups.size(), trsf);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<studyClass.groups.size(); i++){
                handler.set(studyClass.groups.get(i), i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("add subject")){
            pw.print("<a href=\"studygroups?classID="+studyClass.getClassID()+"&"+params.getParamsWithoutRecord()+
                    "\" class=\"href\"");
            if (studyClass.getClassID() <=0 )
                pw.print(" disabled onClick='return false;' ");
            pw.print(">");
                pw.print(MessageManager.getMessage(lang, ClassMessages.ADD_SUBJECT, null));
            pw.print("</a>");
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("classID")){
            pw.print(studyClass.getClassID());
        } else if (name.equals("return link")){
            pw.print("classes?"+params.getFullParams());            
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("add link")){
            pw.print("class?classID=0&" + params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));            
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("page header")){
            Properties prop = new Properties();
            prop.setProperty("name", studyClass.getClassNameru());
            pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS, prop));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));

        } else if (name.equals("exam")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.SELECT_EXAM));
        } else if (name.equals("select exam")){

            Select select = new Select(Select.SIMPLE_SELECT);
            select.width = 80;
            pw.print(select.startSelect("examID"));

            pw.print(select.addOption(0, false, MessageManager.getMessage(lang, Constants.NOT_SELECTED, null)));
            for (int i=0; i<manager.exams.size(); i++){
                if(studyClass.getExamID() != null) {
                    pw.print(select.addOption(manager.exams.get(i).examID + "",
                            manager.exams.get(i).examID == studyClass.getExamID(),
                            manager.exams.get(i).getExamName()));
                }
            }
            pw.print(select.endSelect());
        }
    }

}
