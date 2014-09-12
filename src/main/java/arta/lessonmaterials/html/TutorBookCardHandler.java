package arta.lessonmaterials.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.lessonmaterials.logic.TutorBook;
import arta.filecabinet.logic.SearchParams;
import arta.timetable.designer.logic.LessonConstants;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorBookCardHandler extends TemplateHandler {

    int lang;
    TutorBook book;
    SearchParams params;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();
    Message message;


    public TutorBookCardHandler(int lang, TutorBook book, SearchParams params, ServletContext servletContext,
                                Message message) {
        this.lang = lang;
        this.book = book;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("return link")) {
            pw.print("tutorbooks?"+params.getFullParams()+"&nocache="+ Rand.getRandString());
        } else if (name.equals("return title")) {
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("file")){
            pw.print(MessageManager.getMessage(lang, Constants.PATH, null));
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("name value")){
            pw.print(trsf.getHTMLString(book.getName()));
        } else if (name.equals("language")){
            pw.print(MessageManager.getMessage(lang, Constants.LANGUAGE, null));
        } else if (name.equals("lang select")){
            Languages.writeLanguageSelect(lang,  100, "lang", pw, true, book.getLang());
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("bookID")){
            pw.print(book.getId());
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, LessonConstants.EDIT_STUDY_MATERIAL));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS   ));
        }
    }
}
