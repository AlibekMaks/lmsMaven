package arta.scheduled.tests.html;

import arta.classes.logic.ClassesSelect;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.html.students.StudentSingle;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.filecabinet.logic.students.StudentsManager;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.scheduled.tests.logic.students.schStudentsManager;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.TestingMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class schStudentsList extends TemplateHandler {

    schStudentsManager manager;
    int partsNumber;

    int lang;
    int mainTestingID;
    int testingID;
    StudentSearchParams params;
    ServletContext servletContext;
    Message message;
    SheduledTesting testing;
    boolean dbchecker = false;

    StringTransform trsf = new StringTransform();

    public schStudentsList(int mainTestingID, int testingID, Message message, ServletContext servletContext, StudentSearchParams params, int lang, boolean dbchecker) {
        this.message = message;
        this.servletContext = servletContext;
        this.params = params;
        this.lang = lang;
        this.mainTestingID = mainTestingID;
        this.testingID = testingID;
        this.dbchecker = dbchecker;

        SheduledTesting testing = new SheduledTesting(mainTestingID, message, lang);
        testing.setTestingID(testingID);
        testing.load();

        this.testing = testing;

        manager = new schStudentsManager();
        manager.appooint_students_search(mainTestingID, testingID, testing, params);
        manager.getStudentsTestingStatus(mainTestingID);
        partsNumber = params.getPanelPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("search value")) {
            pw.print(params.getSearch());
        } else if (name.equals("mainTestingID")) {
            pw.print(this.mainTestingID);
        } else if (name.equals("testingID")) {
            pw.print(this.testingID);
        } else if (name.equals("testing result")) {
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_STATUS, null));
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
            StringBuffer str = new FileReader("sheduledTests/sch.single.txt").read(servletContext);
            schStudentSingle handler = new schStudentSingle(params, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i = 0; i < manager.students.size(); i++) {
                handler.set(mainTestingID, testingID, manager.students.get(i), params.countInPart * params.partNumber + i, dbchecker);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("roleID")){
            pw.print(Constants.STUDENT);
        } else if (name.equals("params")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("warn1")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE1, null));
        } else if (name.equals("warn2")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE2, null));
        } else if (name.equals("return link")){
            pw.print("testings?" + params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));            
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("students list")){
            //pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STUDENTS));
            pw.print(MessageManager.getMessage(lang, TestingMessages.WAITING_LIST));
            pw.print("<br>");
            pw.print(trsf.getHTMLString( "\"" + testing.getName(lang) + "\"" ));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        }  else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "testingstudents?testingID="+testingID+"&mainTestingID="+mainTestingID+"&" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("class select")){
            ClassesSelect select = new ClassesSelect(0);
            select.writePostSelect(params.classIDStr, 100, pw, params.classID, true);
        }
    }
}
