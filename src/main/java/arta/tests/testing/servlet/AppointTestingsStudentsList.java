package arta.tests.testing.servlet;


import arta.classes.logic.ClassesSelect;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.navigation.PartsHandler;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.SearchStudent;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.filecabinet.logic.students.StudentsManager;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;

public class AppointTestingsStudentsList extends TemplateHandler {

    StudentsManager manager;
    int partsNumber;

    int lang;
    Person person;
    StudentSearchParams params;
    ServletContext servletContext;
    Message message;
    HTMLCalendar calendar;
    Testing testing;

    StringTransform trsf = new StringTransform();

    public AppointTestingsStudentsList(Person person, Message message, ServletContext servletContext, Testing testing, StudentSearchParams params, int lang) {
        this.person = person;
        this.message = message;
        this.servletContext = servletContext;
        this.params = params;
        this.lang = lang;
        manager = new StudentsManager();
        manager.appooint_students_search(person, params, testing);
        manager.getRecommendStudents();
        partsNumber = params.getPanelPartsNumber();

        this.testing = testing;
        calendar = new HTMLCalendar(lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("search value")) {
            pw.print(params.getSearch());
        } else if (name.equals("search")) {
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("status")) {
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_STATUS, null));
        } else if (name.equals("testingName label")) {
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_NAME, null));
        } else if (name.equals("testingName value")) {
            pw.print(trsf.getHTMLString(testing.getTestingName(lang)));
        } else if (name.equals("found")) {
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")) {
            pw.print(params.recordsCount);
        } else if (name.equals("name")) {
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
        } else if (name.equals("class")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.CLASS, null));
        } else if (name.equals("period")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PERIOD, null));
        } else if (name.equals("startdate")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.ENTER_DATE, null));
        } else if (name.equals("last testing date")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.LAST_ATTESTATION, null));
        } else if (name.equals("passed")) {
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PASSED, null));
        } else if (name.equals("edit")) {
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("records")) {
            StringBuffer str = new FileReader("students/single.copy.txt").read(servletContext);
            AppointTestingsStudentSingle handler = new AppointTestingsStudentSingle(params, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            int i = 0;
            for(int studentID : manager.students.keySet() ) {
                SearchStudent student = (SearchStudent)manager.students.get(studentID);
                handler.setTesting(testing);
                handler.set(student, params.countInPart * params.partNumber + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
                i++;
            }
        } else if (name.equals("hidden_fields")) {
            pw.print(" <input type=\"hidden\" name=\"studentList\" id=\"studentList\" value=\""+params.studentList+"\">" );
        }else if (name.equals("appoint")) {
            pw.print(MessageManager.getMessage(lang, Constants.APPOINT_BUTTON_VALUE, null));
        }else if (name.equals("roleID")){
            pw.print(Constants.STUDENT);
        } else if (name.equals("params")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("warn1")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE1, null));
        } else if (name.equals("warn2")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE2, null));
        } else if (name.equals("enter testing name")){
            pw.print(trsf.getHTMLString(MessageManager.getMessage(lang, Constants.ENTER_THE_NAME_OF_TESTING, null)));
        } else if (name.equals("wrong date format")){
            pw.print(trsf.getHTMLString(MessageManager.getMessage(lang, FileCabinetMessages.ERROR_IN_THE_DATE, null)));
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
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.APPOINT_TEST_FOR_STUDENTS));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        }  else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
//            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
//                    "appointtests?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("class select")){
            ClassesSelect select = new ClassesSelect(0);
            select.writePostSelect(params.classIDStr, 100, pw, params.classID, true);
        } else if (name.equals("period select")){

            Select select = new Select(Select.POST_SELECT);
            pw.print(select.startSelect("time"));
            pw.print(select.addOption(0, params.timeHasPassed == 0, ""));

            String option_name = "";
            for (int i = 1; i <= 12; i ++){
                if(i==12){
                    option_name = MessageManager.getMessage(lang, FileCabinetMessages.MORE_THAN_ONE_YEAR);
                } else {
                    Properties prop = new Properties();
                    prop.setProperty("0", Integer.toString(i));
                    option_name = MessageManager.getMessage(lang, FileCabinetMessages.MORE_THAN_X_MONTHS, prop);
                }
                pw.print(select.addOption(i, i == params.timeHasPassed, option_name));
            }
            pw.print(select.endSelect());
        }

        else if (name.equals("date")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_DATE, null));
        } else if (name.equals("date input")){
            calendar.printInput(pw, testing.getTestingDate(), "date1", "appointtests"); //form
        } else if (name.equals("time start")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_START_TIME, null));
        } else if (name.equals("time start inputs")){
            testing.getTestingStartTime().writeTimeSelects(lang, pw, "hour", "minute");
        } else if (name.equals("time finish")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_FINISH_TIME, null));
        } else if (name.equals("time finish inputs")){
            testing.getTestingFinishTime().writeTimeSelects(lang, pw, "hourfinish", "minutefinish");
        } else if (name.equals("duration")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_TIME, null));
        } else if (name.equals("duration value")){
            pw.print(testing.getTestingTime());
        }
    }

}
