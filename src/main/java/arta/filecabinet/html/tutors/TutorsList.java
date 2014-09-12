package arta.filecabinet.html.tutors;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.tutors.TutorsManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorsList extends TemplateHandler {

    int lang;
    SearchParams params;
    ServletContext servletContext;
    TutorsManager manager;
    int partsNumber;
    Message message;

    StringTransform stringTransform = new StringTransform();

    public TutorsList(int lang, SearchParams params, ServletContext servletContext, Message message) {
        this.lang = lang;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
        manager = new TutorsManager();
        manager.search(params);
        partsNumber = params.getPanelPartsNumber();       
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
        } else if (name.equals("edit")){
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("tutors/single.txt").read(servletContext);
            TutorSingle handler = new TutorSingle(lang, params);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<manager.tutors.size(); i++){
                handler.set(manager.tutors.get(i), params.partNumber*params.countInPart+i);
                parser.setStringBuilder(new StringBuffer(str) );
                parser.parse();
            }
        } else if (name.equals("generate")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.GENERATE_LOGINS_AND_PASSWORDS, null));
        } else if (name.equals("all selected")){
            pw.print(MessageManager.getMessage(lang, Constants.ALL_SELECTED, null));
        } else if (name.equals("all extracted")){
            pw.print(MessageManager.getMessage(lang, Constants.ALL_EXTRACTED, null));
        } else if (name.equals("roleID")){
            pw.print(Constants.TUTOR);
        } else if (name.equals("params")){
            params.writeHiddenInputs(pw);            
        } else if (name.equals("add link")){
            pw.print("tutor?tutorID=0&"+params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("return link")){
            pw.print("main");            
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("warn1")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE1, null));
        } else if (name.equals("warn2")){
            pw.print(MessageManager.getMessage(lang, Constants.CONFIRM_LOGINS_GEBERATE2, null));
        } else if (name.equals("tutors list")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.TUTORS));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("search value")){
            pw.print(stringTransform.getHTMLString(params.search));
        } else if (name.equals("parts")){
            PartsHandler handler = new PartsHandler(params.getPartsNumber(), params.partNumber,
                    lang, "tutors?" + params.getParams(), params.partNumberStr);
            handler.writeLinks(pw);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }

    }
}
