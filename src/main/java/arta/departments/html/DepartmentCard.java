package arta.departments.html;

import arta.books.html.BookSingle;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.*;
import arta.departments.logic.Department;
import arta.departments.logic.DepartmentMessages;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.TestSelectSingle;
import arta.subjects.logic.TestsSelect;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;


public class DepartmentCard extends TemplateHandler {

    Department department;
    int lang;
    SearchParams params;
    ServletContext servletContext;
    Message message;
    StringTransform trsf = new StringTransform();
    public ArrayList<TestsSelect> tests = new ArrayList<TestsSelect>();


    public DepartmentCard(Department department, int lang, SearchParams params, ServletContext servletContext,
                          Message message) {
        this.department = department;
        this.lang = lang;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name kz")){
            pw.print(MessageManager.getMessage(lang, DepartmentMessages.REGION_KAZAKH, null));
        } else if (name.equals("name kz value")){
            pw.print(trsf.getHTMLString(department.getName(Languages.KAZAKH)));
        } else if (name.equals("name ru")){
            pw.print(MessageManager.getMessage(lang, DepartmentMessages.REGION_IN_RUSSIIAN, null));
        } else if (name.equals("name ru value")){
            pw.print(trsf.getHTMLString(department.getName(Languages.RUSSIAN)));
        } else if (name.equals("departmentID")){
            pw.print(department.getDepartmentID());
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("maxlength")){
            pw.print(Varchar.NAME/2);
        } else if (name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("message")){
            if (message !=  null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("add link")){
            pw.print("department?departmentID=0&" + params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang,  Constants.ADD, null));            
        } else if (name.equals("return link")){
            pw.print("departments?"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang,  Constants.BACK, null));
        } else if (name.equals("page header")){
            StringBuffer str = new StringBuffer(MessageManager.getMessage(lang, DepartmentMessages.REGION, null));
            str.append("&nbsp;");
            str.append(trsf.getHTMLString(department.getName(lang)));
            pw.print(str);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        }
    }

}
