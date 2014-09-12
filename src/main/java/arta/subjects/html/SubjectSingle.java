package arta.subjects.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.SubjectMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class SubjectSingle extends TemplateHandler {

    SearchParams params;
    SimpleObject object;
    int lang;
    int recordNumber;
    StringTransform trsf = new StringTransform();


    public SubjectSingle(SearchParams params,int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set (SimpleObject object, int recordNumber){
        this.object = object;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("id")){
            pw.print(object.id);
        } else if (name.equals("name")){
            pw.print("<a href=\"subject?subjectID="+object.id+"&recordNumber="+recordNumber+"&"+
                    params.getParamsWithoutRecord()+"\" class=\"href\" title=\""+trsf.getHTMLString(object.name)+"\" >");
            pw.print(trsf.getHTMLString(object.name));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("name", object.getName());
            pw.print("<a href=\"subjects?option=-1&subjectID="+object.id+"&"+params.getFullParams()+"\" " +
                    " class=\"href\" title=\""+trsf.getHTMLString(object.name)+"\" " +
                    " onClick='return confirm(\""+MessageManager.getMessage(lang, SubjectMessages.CONFIRM_DELETE_SUBJECT, prop)+"\");'>");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
