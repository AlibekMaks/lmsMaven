package arta.departments.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.departments.logic.DepartmentMessages;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;


public class DepartmentSingle extends TemplateHandler {

    int number;
    SearchParams params;
    SimpleObject object;
    int lang;
    int recordNumber;
    StringTransform trsf = new StringTransform();


    public DepartmentSingle(SearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void set (int number, SimpleObject object, int recordNumber){
        this.number = number;
        this.object = object;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("id")){
            pw.print(number);//object.id
        } else if (name.equals("name")){
            pw.print("<a href=\"department?departmentID="+object.id+"&recordNumber="+recordNumber+"&"+
                    params.getParamsWithoutRecord()+"\" class=\"href\" title=\""+trsf.getHTMLString(object.name)+"\" >");
            pw.print(trsf.getHTMLString(object.name));
            pw.print("</a>");
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("name", object.getName());
            pw.print("<a href=\"departments?option=-1&departmentID="+object.id+"&"+params.getFullParams()+"\" " +
                    " class=\"href\" title=\""+trsf.getHTMLString(object.name)+"\" " +
                    " onClick='return confirm(\""+MessageManager.getMessage(lang, DepartmentMessages.CONFIRM_DELETE_REGION, prop)+"\");'>");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
            pw.print("</a>");
        }
    }
}
