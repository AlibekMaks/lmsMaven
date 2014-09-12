package arta.dbchecker.html;

import arta.check.logic.Testing;
import arta.classes.logic.ClassesManager;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.dbchecker.logic.dbCheckerMessages;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.scheduled.tests.html.schStudentSingle;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.settings.logic.Settings;
import arta.settings.logic.SettingsMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class dbCheckerHandler extends TemplateHandler {

    Testing testing;
    int lang;
    ServletContext servletContext;
    Message message;
    int language = 1;
    SheduledTesting sheduledTesting;
    Student student;
    boolean testIsPassed;
    boolean testIsSaved;

    StringTransform trsf = new StringTransform();


    public dbCheckerHandler(Testing testing, Student student, boolean testIsPassed, boolean testIsSaved, int lang, ServletContext servletContext, Message message, int language) {
        this.testing = testing;
        this.student = student;
        this.testIsPassed = testIsPassed;
        this.testIsSaved = testIsSaved;
        this.lang = lang;
        this.servletContext = servletContext;
        this.message = message;
        this.language = language;   //??????????

        this.sheduledTesting = new SheduledTesting(testing.mainTestingID, message, lang);
        this.sheduledTesting.setTestingID(testing.mainTestingID);
        this.sheduledTesting.load();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("page header")){
            pw.print(sheduledTesting.getName(lang));
        } else if (name.equals("generate")){
            if(!testIsPassed && !testIsSaved){
                pw.print("<input type=\"submit\" name=\"generate\" value=\""+MessageManager.getMessage(lang, dbCheckerMessages.GENERATE, null)+"\" class=\"button\">");
            }
        } else if (name.equals("save")){
            if(!testIsPassed && !testIsSaved){
                pw.print("<input type=\"submit\" name=\"save\" value=\""+MessageManager.getMessage(lang, Constants.SAVE, null)+"\" class=\"button\">");
            }
        } else if (name.equals("student name")){
            pw.print(trsf.getHTMLString(student.getFullName()));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("<td></tr>");
            }

        } else if (name.equals("return link")){
            pw.print("testingstudents?mainTestingID="+testing.mainTestingID+"&testingID="+testing.testingID+"");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));

        } else if (name.equals("language label")){
            if(!testIsPassed && !testIsSaved){
                pw.print(MessageManager.getMessage(lang, dbCheckerMessages.SELECT_A_LANGUAGE_TEST, null));
            }
        } else if (name.equals("language")){
            if(!testIsPassed && !testIsSaved){
                Select select = new Select(Select.SIMPLE_SELECT);
                select.width = 20;
                select.change_function = "form.submit()";
                pw.print(select.startSelect("language"));
                    pw.print(select.addOption(1, (language == 1), MessageManager.getMessage(lang, dbCheckerMessages.KAZAKH, null)));
                    pw.print(select.addOption(2, (language == 2), MessageManager.getMessage(lang, dbCheckerMessages.RUSSIAN, null)));
                pw.print(select.endSelect());
            }
        } else if (name.equals("records")){
            if(!testIsPassed && !testIsSaved){
                StringBuffer str = new FileReader("dbchecker/dbc.single.txt").read(servletContext);
                dbCheckerSingle handler = new dbCheckerSingle(null, lang);
                Parser parser = new Parser();
                parser.setPrintWriter(pw);
                parser.setTemplateHandler(handler);

                for (int subjectID : testing.xsubjects.keySet()){
                    handler.set(testing.mainTestingID, testing.testingID, testing.xsubjects.get(subjectID));
                    parser.setStringBuilder(new StringBuffer(str));
                    parser.parse();
                }
            }
        } else if (name.equals("hidden params")){
            pw.print("<input type=\"hidden\" name=\"mainTestingID\" value=\""+testing.mainTestingID+"\">");
            pw.print("<input type=\"hidden\" name=\"testingID\" value=\""+testing.testingID+"\">");
            pw.print("<input type=\"hidden\" name=\"studentID\" value=\""+student.getPersonID()+"\">");
        }
    }




























}
