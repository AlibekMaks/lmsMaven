package arta.filecabinet.html.students;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.students.SearchStudent;
import arta.filecabinet.logic.students.StudentsManager;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.classes.logic.ClassesSelect;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudentsList extends TemplateHandler {

    StudentsManager manager;
    int partsNumber;

    int lang;
    StudentSearchParams params;
    ServletContext servletContext;
    Message message;

    StringTransform trsf = new StringTransform();

    public StudentsList(Message message, ServletContext servletContext, StudentSearchParams params, int lang) {
        this.message = message;
        this.servletContext = servletContext;
        this.params = params;
        this.lang = lang;
        manager = new StudentsManager();
        manager.search(params);
        partsNumber = params.getPanelPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("search value")) {
            pw.print(params.getSearch());
        } else if (name.equals("search")) {
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("found")) {
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")) {
            pw.print(params.recordsCount);
        } else if (name.equals("name")) {
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
        } else if (name.equals("class")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.CLASS, null));
        } else if (name.equals("edit")) {
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("records")) {
            StringBuffer str = new FileReader("students/single.txt").read(servletContext);
            StudentSingle handler = new StudentSingle(params, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            int i = 0;
            for(int studentID : manager.students.keySet()){
                SearchStudent student = (SearchStudent)manager.students.get(studentID);

                handler.set(student, params.countInPart * params.partNumber + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
                i ++;
            }
//            for (int i = 0; i < manager.students.size(); i++) {
//                handler.set(manager.students.get(i), params.countInPart * params.partNumber + i);
//                parser.setStringBuilder(new StringBuffer(str));
//                parser.parse();
//            }
        } else if (name.equals("generate")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.GENERATE_LOGINS_AND_PASSWORDS, null));
        } else if (name.equals("all selected")) {
            pw.print(MessageManager.getMessage(lang, Constants.ALL_SELECTED, null));
        } else if (name.equals("all extracted")) {
            pw.print(MessageManager.getMessage(lang, Constants.ALL_EXTRACTED, null));
        } else if (name.equals("roleID")){
            pw.print(Constants.STUDENT);
        } else if (name.equals("params")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("warn1")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE1, null));
        } else if (name.equals("warn2")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE2, null));
        } else if (name.equals("add link")){
            pw.print("student?studentID=0&"+params.getParamsWithoutRecord());
        } else if (name.equals("import link")){
            pw.print("importstudents?import=1");
        } else if (name.equals("import title")){
            pw.print(MessageManager.getMessage(lang, Constants.XLS_IMPORT, null));
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));            
        } else if (name.equals("return link")){
            pw.print("main");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));            
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("students list")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STUDENTS));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        }  else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "students?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("class select")){
            ClassesSelect select = new ClassesSelect(0);
            select.writePostSelect(params.classIDStr, 100, pw, params.classID, true);
        }
    }
}
