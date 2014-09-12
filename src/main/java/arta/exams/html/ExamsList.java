package arta.exams.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.exams.ExamMessages;
import arta.exams.logic.ExamsManager;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ExamsList extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    SearchParams params;
    Message message;

    int partsNumber;
    ExamsManager manager;
    StringTransform trsf = new StringTransform();


    public ExamsList(ServletContext servletContext, int lang, SearchParams params, Message message) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.params = params;
        this.message  = message;
        manager = new ExamsManager();
        manager.search(params);
        partsNumber = params.getPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("found records number")){
            pw.print(params.recordsCount);
        } else if (name.equals("index")){
            pw.print(MessageManager.getMessage(lang, Constants.INDEX_NUMBER, null));
        }else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("edit")){
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if (name.equals("question count")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.NUMBER_OF_QUESTIONS, null));
        } else if (name.equals("delete")){
            pw.print(MessageManager.getMessage(lang, Constants.DELETE, null));
        } else if (name.equals("status")){
            pw.print(MessageManager.getMessage(lang, ExamMessages.STATUS, null));
        } else if (name.equals("records")){
            ExamSingle handler = new ExamSingle(params, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("exams/single.html").read(servletContext);
            for (int i = 0; i< manager.exams.size(); i ++){
                handler.set(i+1, manager.exams.get(i), params.countInPart* params.partNumber + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("return link")){
            pw.print("main");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("add link")){
            pw.print("exam?examID=0&" + params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("page header")){
           pw.print(MessageManager.getMessage(lang, ExamMessages.EXAMS, null));
        } else if (name.equals("search value")){
           pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang,Constants.SEARCH));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "exams?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        }
    }
}
