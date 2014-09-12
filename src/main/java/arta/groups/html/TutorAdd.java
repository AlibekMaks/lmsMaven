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


public class TutorAdd extends TemplateHandler {

    int lang;
    ServletContext servletContext;
    SearchParams params;

    int index;
    int subjectID;

    TutorsManager manager;
    int partsNumber;


    public TutorAdd(int lang, ServletContext servletContext, SearchParams params, int index, int subjectID) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
        this.index = index;
        this.subjectID = subjectID;
        manager = new TutorsManager();
        manager.search(params);
        partsNumber = params.getPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("index")){
            pw.print(index);
        } else if (name.equals("subjectID")){
            pw.print(subjectID);
        } else if (name.equals("navigation")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang, "tutorselect?index="+index
                    + "&subjectID="+subjectID+"&" + params.getParams(), params.partNumberStr).writeLinks(pw);
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
            pw.print(MessageManager.getMessage(lang, Constants.FIO, null));
        } else if (name.equals("select")){
           pw.print(MessageManager.getMessage(lang, Constants.SELECT, null));
        } else if (name.equals("records")){
            TutorAddSingle handler = new TutorAddSingle(lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer  str = new FileReader("groups/tutor.add.single.txt").read(servletContext);
            for (int i=0; i<manager.tutors.size(); i++){
                handler.setTutor(manager.tutors.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        }
    }
}