package arta.scheduled.tests.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.SearchStudent;
import arta.tests.testing.logic.TestingMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class schStudentSingle extends TemplateHandler {

    SearchStudent student;
    SearchParams params;
    int lang;
    int recordNumber;
    int mainTestingID;
    int testingID;
    boolean dbchecker = false;
    StringTransform trsf = new StringTransform();


    public schStudentSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set(int mainTestingID, int testingID, SearchStudent student, int recordNumber, boolean dbchecker){
        this.student = student;
        this.recordNumber = recordNumber;
        this.mainTestingID = mainTestingID;
        this.testingID = testingID;
        this.dbchecker = dbchecker;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("studentID")){
            pw.print(student.id);
        } else if (name.equals("red color")){
            if(student.testingIsEnded & student.testingStatus<2){ // время тестирования прошло
                pw.print(" class=\"isRecommend\" ");
            }
        } else if (name.equals("testing result")){
            if(student.testingIsEnded & student.testingStatus<2){ // время тестирования прошло
                pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_TIME_HAS_PASSED, null));
            } else if(student.testingStatus == 0){ // еще не проходил тестирование
                pw.print(MessageManager.getMessage(lang, TestingMessages.HAS_NOT_YET_BEEN_TESTED, null));
            } else if(student.testingStatus == 1){ // сдает тестирование
                pw.print(MessageManager.getMessage(lang, TestingMessages.LEASSES_TESTING, null));
            } else if(student.testingStatus == 2){ // Тестирование завершено
                pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_COMPLETE, null));
            }
        } else if (name.equals("name")){
            if(dbchecker){
                pw.print("<a href=\"dbchecker?mainTestingID="+mainTestingID+"&testingID="+testingID+"&studentID="+student.id+"&"+params.getParams()+"\" class=\"href\" >");
                pw.print(trsf.getHTMLString(student.name));
                pw.print("</a>");
            } else {
                pw.print(trsf.getHTMLString(student.name));
            }
        } else if (name.equals("class")){
            pw.print(student.className);
        } else if (name.equals("delete")){
            //if(student.testingStatus == 0){
                Properties prop = new Properties();
                prop.setProperty("object", student.name);
                pw.print("<a href=\"testingstudents?option=-1&mainTestingID="+mainTestingID+"&testingID="+testingID+"&studentID="+student.id+"&"+params.getParams()+"\" " +
                        " title=\""+trsf.getHTMLString(student.name)+"\" onClick='return confirm(\""+
                        MessageManager.getMessage(lang, Constants.DO_YOU_REALLY_WANT_ABORT_TESTING_TO_STUDENT, prop)+"\");' " +
                        " class=\"href\">");
                pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" border=0 height=\"16px\">");
                pw.print("</a>");
            //}
        }
    }
}
