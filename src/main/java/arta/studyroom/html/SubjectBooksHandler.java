package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.books.logic.Book;
import arta.chat.ChatMessages;
import arta.studyroom.logic.StudyRoomMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 20.03.2008
 * Time: 15:24:59
 * To change this template use File | Settings | File Templates.
 */
public class SubjectBooksHandler extends TemplateHandler {

    ServletContext servletContext;
    ArrayList<SimpleObject> books;
    int lang;
    int roleID;
    int type;

    StringTransform trsf = new StringTransform();


    public SubjectBooksHandler(ServletContext servletContext, ArrayList<SimpleObject> books, int lang,
                               int roleID, int type) {
        this.servletContext = servletContext;
        this.books = books;
        this.lang = lang;
        this.roleID = roleID;
        this.type = type;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("header")){
            if (type == Book.FILE_TYPE){
                pw.print(MessageManager.getMessage(lang, ChatMessages.BOOKS, null));   
            } else if (type == Book.SCORM_TYPE){
                pw.print(MessageManager.getMessage(lang, StudyRoomMessages.SCORM_BOOKS, null));
            }
        } else if (name.equals("items")){
            StringBuffer template = null;
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            SubjectBookHandler handler = new SubjectBookHandler(trsf, roleID);
            if (type == Book.FILE_TYPE){
                template = new FileReader("studyroom/content/subject.book.record.txt").read(servletContext);
            } else if (type == Book.SCORM_TYPE){
                template = new FileReader("studyroom/content/subject.scorm.book.record.txt").read(servletContext);
            }
            parser.setTemplateHandler(handler);
            for (int i=0; i < books.size(); i ++){
                parser.setStringBuilder(new StringBuffer(template));
                handler.setBook(books.get(i));
                parser.parse();
            }


        }
    }
}
