package arta.departments.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.departments.logic.DepartmentMessages;
import arta.departments.logic.DepartmentsManager;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class DepartmentsList extends TemplateHandler {

    SearchParams params;
    ServletContext servletContext;
    int lang;
    Message message;

    int partsNumber;
    DepartmentsManager manager = new DepartmentsManager();
    StringTransform trsf = new StringTransform();


    public DepartmentsList(SearchParams params, ServletContext servletContext, int lang, Message message) {
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.message = message;
        manager.search(params, lang);
        partsNumber = params.getPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("id")){
            pw.print(MessageManager.getMessage(lang, Constants.INDEX_ID, null));
        }else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("records")){
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            DepartmentSingle handler = new DepartmentSingle(params, lang);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("departments/single.txt").read(servletContext);
            for (int i=0; i<manager.departments.size(); i++){
                handler.set(manager.departments.get(i).id, manager.departments.get(i), params.countInPart*params.partNumber + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("add link")){
            pw.print("department?departmentID=0&"+params.getFullParams());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("return link")){
            pw.print("main");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));            
        } else if (name.equals("message")){
            if(message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "departments?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("department list")){
            pw.print(MessageManager.getMessage(lang, DepartmentMessages.REGIONS, null));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        }
    }
}
