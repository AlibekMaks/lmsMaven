package arta.subjects.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.SubjectMessages;
import arta.subjects.logic.SubjectsManager;
import arta.subjects.logic.SubjectsSelect;
import arta.subjects.logic.SubjectsStatus;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SubjectsList extends TemplateHandler {

    SearchParams params;
    ServletContext servletContext;
    int lang;
    Message message;

    int partsNumber;
    SubjectsManager manager = new SubjectsManager();
    StringTransform trsf = new StringTransform();
    String return_link;


    public SubjectsList(SearchParams params, ServletContext servletContext, int lang, Message message, String return_link) {
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        this.message = message;
        this.return_link = return_link;
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
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("status")) {
            pw.print(MessageManager.getMessage(lang, Constants.STATUS));
        }else if (name.equals("records")){
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            SubjectSingle handler = new SubjectSingle(params, lang);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("subjects/single.txt").read(servletContext);
            for (int i=0; i<manager.subjects.size(); i++){
                handler.set(manager.subjects.get(i), params.countInPart*params.partNumber + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("add link")){
            pw.print("subject?subjectID=0&"+params.getFullParams());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("return link")){
            pw.print(return_link);
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
                    "subjects?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("subjects list")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECTS, null));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("status")){
            pw.print(MessageManager.getMessage(lang, Constants.STATUS));
        } else if (name.equals("select_status")){
           /* SubjectsSelect select = new SubjectsSelect(0);
            select.writePostSelect(params.classIDStr, 100, pw, params.classID, true);*/
            Select select = new Select(Select.POST_SELECT);
            pw.print(select.startSelect("status_select"));
            pw.print(select.addOption(String.valueOf(SubjectsStatus.ARCHIVE.statusId), params.subjectStatusID == SubjectsStatus.ARCHIVE.statusId, MessageManager.getMessage(lang,Constants.ARCHIVE), null));
            pw.print(select.addOption(String.valueOf(SubjectsStatus.ACTIVE.statusId),  params.subjectStatusID == SubjectsStatus.ACTIVE.statusId, MessageManager.getMessage(lang,Constants.ACTIVE), null));
            pw.print(select.endSelect());
        }
    }
}
