package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.SearchParams;
import arta.studyroom.logic.StudyRoomMessages;
import arta.lessonmaterials.logic.TutorBooksManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 11:36:20
 * To change this template use File | Settings | File Templates.
 */
public class TutorialAppointListHandler extends TemplateHandler {

    SearchParams params;
    int lang;
    ServletContext servletContext;
    int tutorID;
    int subgroupID;

    StringTransform trsf = new StringTransform();
    TutorBooksManager manager;


    public TutorialAppointListHandler(SearchParams params, int lang, ServletContext servletContext, 
                                      int tutorID, int subgroupID) {
        this.params = params;
        this.lang = lang;
        this.servletContext = servletContext;
        this.tutorID = tutorID;
        this.subgroupID = subgroupID;
        manager = new TutorBooksManager();
        manager.search(params, tutorID);         
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, StudyRoomMessages.ADD_TUTORIAL_PAGE_HEADER));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("found")){
           pw.print(MessageManager.getMessage(lang, Constants.FOUND));
        } else if (name.equals("number of found records")){
           pw.print(params.recordsCount);
        } else if (name.equals("pages")){
           new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                   "appointtutorial?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME));
        } else if (name.equals("records")){
            StringBuffer template = new FileReader("studyroom/content/appoint.record.txt").read(servletContext);
            TutorialAppointRecordHandler handler = new TutorialAppointRecordHandler(lang, trsf);
            Parser parser = new Parser();
            parser.setTemplateHandler(handler);
            parser.setPrintWriter(pw);
            for (int i = 0; i < manager.books.size(); i ++){
                handler.setBook(manager.books.get(i));
                parser.setStringBuilder(new StringBuffer(template));
                parser.parse();
            }
        } else if (name.equals("roomID")){
            pw.print(subgroupID);
        } else if (name.equals("book has been appointed")){
            pw.print(MessageManager.getMessage(lang, StudyRoomMessages.TUTORIAL_HAS_BEEN_SUCCESSFULLY_ADDED));
        } else if (name.equals("book has not been appointed")){
            pw.print(MessageManager.getMessage(lang, StudyRoomMessages.TUTORIAL_HAS_NOT_BEEN_ADDED));
        }
    }
}
