package arta.classes.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.classes.ClassMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class ClassSingle extends TemplateHandler {

    SearchParams params ;
    int lang;
    SimpleObject studyClass;
    int recordNumber;
    StringTransform trsf = new StringTransform();


    public ClassSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set(SimpleObject studyClass, int recordNumber){
        this.studyClass = studyClass;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("id")){
            pw.print(studyClass.id);
        } else if (name.equals("name")){
            pw.print("<a href=\"class?classID="+studyClass.id+"&recordNumber=" + recordNumber + "&" +
                    params.getParamsWithoutRecord()+"\" class=\"href\" " +
                    " title=\""+trsf.getHTMLString(studyClass.name)+"\" >");
            pw.print(trsf.getHTMLString(studyClass.name, "-"));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("class", studyClass.name);
            pw.print("<a href=\"classes?option=-1&classID="+studyClass.id+"&"+params.getFullParams()+"\" " +
                    " onClick='return confirm(\""+
                    MessageManager.getMessage(lang, ClassMessages.DO_YOU_REALLY_WANNA_DELETE_CLASS, prop) +
                    "\");' title=\""+trsf.getHTMLString(studyClass.name)+"\" class=\"href\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
