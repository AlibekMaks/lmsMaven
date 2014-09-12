package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.tutors.TutorsManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorChange extends TemplateHandler {

    int lang;
    ServletContext servletContext;
    SearchParams params;

    int subgroupID;

    TutorsManager manager;
    int partsNumber;


    public TutorChange(int lang, ServletContext servletContext, SearchParams params, int subgroupID) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.subgroupID = subgroupID;
        manager = new TutorsManager();
        manager.search(params);
        partsNumber = params.getPanelPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("subgroupID")){
            pw.print(subgroupID);
        } else if (name.equals("navigation")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "changetutor?subgroupID=" + subgroupID + "&option=show&" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("tutors")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.TUTORS, null));
        } else if (name.equals("search value")){
            pw.print(params.getSearch());
        } else if (name.equals("serach")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("select")){
           pw.print(MessageManager.getMessage(lang, Constants.SELECT, null));
        } else if (name.equals("records")){
            TutorSelectSingle handler = new TutorSelectSingle(lang, subgroupID);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer  str = new FileReader("groups/tutor.select.single.txt").read(servletContext);
            for (int i=0; i<manager.tutors.size(); i++){
                handler.setTutor(manager.tutors.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        }
    }
}
