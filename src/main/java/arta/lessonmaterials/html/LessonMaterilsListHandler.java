package arta.lessonmaterials.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.lessonmaterials.logic.TutorBooksManager;
import arta.timetable.designer.logic.LessonConstants;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class LessonMaterilsListHandler extends TemplateHandler {

    SearchParams params ;
    int lang;
    ServletContext servletContext;

    TutorBooksManager manager;
    Message message;
    int tutorID;
    StringTransform trsf = new StringTransform();

    public LessonMaterilsListHandler(SearchParams params, int lang, ServletContext servletContext,
                                     Message message, int tutorID) {
        this.params = params;
        this.lang = lang;
        this.servletContext = servletContext;
        this.message  = message;
        manager = new TutorBooksManager();
        manager.search(params, tutorID);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("records")){
            LessonMaterialSingleHandler handler = new LessonMaterialSingleHandler(lang, trsf, params);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("lessonmaterials/lessonmaterila.single.txt").read(servletContext);
            for (int i=0; i<manager.books.size(); i++){
                handler.setTutorBook(manager.books.get(i));
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
            pw.print("main?nocache=" + Rand.getRandString());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("add link")){
            pw.print("tutorbook?"+params.getParams()+"&bookID=0");
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "tutorbooks?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, LessonConstants.STUDY_MATERIALS));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        }
    }
}
