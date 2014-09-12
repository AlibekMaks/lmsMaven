package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.studyroom.logic.Content;
import arta.chat.ChatMessages;
import arta.books.logic.Book;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ContentHandler extends TemplateHandler {

    int lang;
    int roomID;
    ServletContext servletContext;
    Content content;
    int roleID;


    public ContentHandler(int lang, int roomID, ServletContext servletContext, Content content, 
                          int roleID) {
        this.lang = lang;
        this.roomID = roomID;
        this.servletContext = servletContext;
        this.content = content;
        this.roleID = roleID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("books")){
            new Parser(new FileReader("studyroom/content/subject.books.txt").read(servletContext), pw,
                    new SubjectBooksHandler(servletContext, content.books, lang,
                            roleID, Book.FILE_TYPE)).parse();
        } else if (name.equals("tutor books")){
            new Parser(new FileReader("studyroom/content/tutor.books.list.txt").read(servletContext), pw,
                    new TutorBooksHandler(content.tutorbooks, MessageManager.getMessage(lang, ChatMessages.TUTOR_BOOKS, null),
                            servletContext, lang, roleID, roomID)).parse();
        } else if (name.equals("scorm books")){
            new Parser(new FileReader("studyroom/content/subject.books.txt").read(servletContext), pw,
                    new SubjectBooksHandler(servletContext, content.SCORMBooks, lang, 
                            roleID, Book.SCORM_TYPE)).parse();
        }
    }
}
