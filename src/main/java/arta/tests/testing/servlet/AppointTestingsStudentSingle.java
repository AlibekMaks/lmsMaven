package arta.tests.testing.servlet;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.SearchStudent;
import arta.tests.testing.logic.Testing;

import java.io.PrintWriter;
import java.util.Properties;

public class AppointTestingsStudentSingle extends TemplateHandler {

    SearchStudent student;
    SearchParams params;
    int lang;
    int recordNumber;
    StringTransform trsf = new StringTransform();
    Testing testing;


    public AppointTestingsStudentSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void setTesting(Testing testing){
        this.testing = testing;
    }

    public void set(SearchStudent student, int recordNumber){
        this.student = student;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("studentID")){
            pw.print(student.id);
        } else if (name.equals("checked")){
            pw.print((testing.studentIDisFound(student.id))?"checked":"");
        } else if (name.equals("status")){
            if(student.testing_status_type == student.NEVER_PASSED){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.NEVER_PASSED, null));
            } else if(student.testing_status_type == student.FAILED_TEST){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.FAILED_TEST, null));
            } else if(student.testing_status_type == student.NEED_TO_BE_TESTED){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.NEED_TO_BE_TESTED, null));
            } else if(student.testing_status_type == student.SUCCUESSFULLY_PASSED_THE_TEST){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.SUCCUESSFULLY_PASSED_THE_TEST, null));
            } else if(student.testing_status_type == student.ERROR_START_DATE){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.ERROR_START_DATE, null));
            } else {
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.UNKNOWN, null));
            }
        } else if (name.equals("tr class")){
            if(student.isRecommend){
                pw.print(" class=\"isRecommend\" ");
            }
        } else if (name.equals("startdate")){
            if(student.startdate != null){
                pw.print( student.startdate.toString() );
            } else {
                pw.print("");
            }
        } else if (name.equals("last testing date")){
            if(student.lastTestingDate!=null && student.startdate!=null){
                if(!student.lastTestingDate.toString().equals(student.startdate.toString())){
                    if(student.lastTestingDate != null){
                        pw.print( student.lastTestingDate.toString() );
                    }
                } else {
                    pw.print("");
                }
            } else {
                pw.print("");
            }

        } else if (name.equals("name")){
            //pw.print("<a href=\"student?studentID="+student.id+"&"+params.getParamsWithoutRecord()+
            //        "&recordNumber="+recordNumber+"\" class=\"href\" >");
            pw.print(trsf.getHTMLString(student.name));
            //pw.print("</a>");
        } else if (name.equals("class")){
            pw.print(student.className);
        } else if (name.equals("passed")){
            if(student.passedTextType == Constants.MORE_THAN_A_YEAR){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.MORE_THAN_A_YEAR, null));
            } else if(student.passedTextType == Constants.X_MONTHS){
                Properties prop = new Properties();
                prop.setProperty("0", Integer.toString(student.month));
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.X_MONTHS, prop));
            } else if(student.passedTextType == Constants.X_DAYS){
                Properties prop = new Properties();
                prop.setProperty("0", Integer.toString(student.day));
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.X_DAYS, prop));
            } else if(student.passedTextType == Constants.X_TODAY){
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.X_TODAY, null));
            } else if(student.passedTextType == Constants.X_MONTHS_AND_X_DAYS){
                Properties prop = new Properties();
                prop.setProperty("0", Integer.toString(student.month));
                prop.setProperty("1", Integer.toString(student.day));
                pw.print(MessageManager.getMessage(lang, FileCabinetMessages.X_MONTHS_AND_X_DAYS, prop));
            }
        }
//        else if (name.equals("delete")){
//            Properties prop = new Properties();
//            prop.setProperty("object", student.name);
//            pw.print("<a href=\"students?option=-1&studentID="+student.id+"&"+params.getParams()+"\" " +
//                    " title=\""+trsf.getHTMLString(student.name)+"\" onClick='return confirm(\""+
//                    MessageManager.getMessage(lang, Constants.DO_YOU_REALLY_WANT_TO_DELETE_STUDENT, prop)+"\");' " +
//                    " class=\"href\">");
//            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" border=0 height=\"16px\">");
//            pw.print("</a>");
//        }
    }


}
