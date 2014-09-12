package arta.filecabinet.html.tutors;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class TutorSingle extends TemplateHandler {

    SimpleObject tutor;
    int lang;
    int recordNumber;
    SearchParams params;
    StringTransform trsf = new StringTransform();


    public TutorSingle(int lang, SearchParams params) {
        this.lang = lang;
        this.params = params;
    }

    public void set(SimpleObject tutor, int recordNumber){
        this.tutor = tutor;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("tutorID")){
            pw.print(tutor.id);
        } else if (name.equals("name")){
            pw.print("<a href=\"tutor?"+params.getParamsWithoutRecord()+"&tutorID="+tutor.id+
                    "&recordNumber="+recordNumber+"\" class=\"href\" >");
            pw.print(trsf.getHTMLString(tutor.name));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("object", tutor.name);
            pw.print("<a href=\"tutors?option=-1&tutorID="+tutor.id+"&"+params.getFullParams()+"\" " +
                    " title=\""+trsf.getHTMLString(tutor.name)+"\" onClick='return confirm(\""+
                    MessageManager.getMessage(lang, FileCabinetMessages.DO_YOU_REALLY_WANT_TO_DELETE_TUTOR, prop)+"\");' " +
                    " class=\"href\">");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
