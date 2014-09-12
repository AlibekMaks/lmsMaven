package arta.exams.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.exams.ExamMessages;
import arta.exams.logic.SimpleExam;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;


public class ExamSingle extends TemplateHandler {

    SearchParams params ;
    int lang;
    SimpleExam exam;
    int recordNumber;
    StringTransform trsf = new StringTransform();
    int index;


    public ExamSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set(int index, SimpleExam exam, int recordNumber){
        this.index = index;
        this.exam = exam;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("id")){
            pw.print(recordNumber+1);
        } else if (name.equals("name")){
            pw.print("<a href=\"exam?examID="+exam.examID+"&recordNumber=" + recordNumber + "&" +
                    params.getParamsWithoutRecord()+"\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(exam.examName)+"\" >");
            pw.print(trsf.getHTMLString(exam.examName, "-"));
            pw.print("</a>");
        } else if (name.equals("question count")){
            pw.print(exam.questionCount);
        } else if (name.equals("status")){
            if(exam.isUsed){
                pw.print(MessageManager.getMessage(lang, ExamMessages.STATUS_USED, null));
            } else {
                pw.print(MessageManager.getMessage(lang, ExamMessages.STATUS_NOT_USED, null));
            }
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("name", exam.examName);
            pw.print("<a href=\"exams?option=-1&examID="+exam.examID+"&"+params.getFullParams()+"\" " +
                    " onClick='return confirm(\""+
                    MessageManager.getMessage(lang, ExamMessages.DO_YOU_REALLY_WANNA_DELETE_EXAM, prop) +
                    "\");' title=\""+trsf.getHTMLString(exam.examName)+"\" class=\"href\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
