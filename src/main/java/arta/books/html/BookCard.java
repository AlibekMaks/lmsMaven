package arta.books.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.books.logic.Book;
import arta.books.logic.BookTypeSelect;
import arta.filecabinet.logic.SearchParams;
import arta.subjects.logic.SubjectMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class BookCard  extends TemplateHandler {

    Book book;
    SimpleObject subject;
    SearchParams params;
    int lang;
    Message message;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();


    public BookCard(Book book, SimpleObject subject, SearchParams params, int lang, Message message,
                    ServletContext servletContext) {
        this.book = book;
        this.subject = subject;
        this.params = params;
        this.lang = lang;
        this.message = message;
        this.servletContext = servletContext;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("rnd")) {
            pw.print(Rand.getRandString());
        } else if (name.equals("bookID")) {
            pw.print(book.getId());
        } else if (name.equals("subjectID")) {
            pw.print(subject.id);
        } else if (name.equals("hidden inputs")) {
            params.writeHiddenInputs(pw);
        } else if (name.equals("path")) {
            pw.print(MessageManager.getMessage(lang, Constants.PATH, null));
        } else if (name.equals("name")) {
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("name value")) {
            pw.print(trsf.getHTMLString(book.getName()));
        } else if (name.equals("lang")) {
            pw.print(MessageManager.getMessage(lang, Constants.LANGUAGE, null));
        } else if (name.equals("lang select")) {
            Languages.writeLanguageSelect(lang,  100, "lang", pw, true, book.getLang(), "lang");
        } else if (name.equals("save")) {
            pw.print(MessageManager.getMessage(lang,  Constants.SAVE, null));
        } else if (name.equals("return href")) {
            pw.print("subject?subjectID="+subject.id+"&"+params.getFullParams()+"");
        } else if (name.equals("return title")) {
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("maxlength")) {
            pw.print(Varchar.NAME);
        } else if (name.equals("message")){
            if (message!=null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("page header")){
            Properties prop = new Properties();
            prop.setProperty("subject", subject.getName());
            pw.print(MessageManager.getMessage(lang, SubjectMessages.BOOK_ON_SUBJECT, prop));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("type")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.BOOK_TYPE));
        } else if (name.equals("type select")){
            BookTypeSelect select = new BookTypeSelect();
            select.writeSimpleSelect("booktype", book.getBookTypeID(), 100, pw, lang, "typeselect",
                    "onchange='changeState()'", book.getId() > 0, true);
        } else if (name.equals("SCORM_TYPE")){
            pw.print(Book.SCORM_TYPE);
        } else if (name.equals("FILE_TYPE")){
            pw.print(Book.FILE_TYPE);
        } else if (name.equals("params")){
            pw.print(params.getFullParams());
        } else if (name.equals("validate")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.VALIDATE));
        } else if(name.equals("scorm file disabled")){
            if (book.getId() > 0 && book.getBookTypeID() == Book.SCORM_TYPE)
                pw.print(" disabled ");
        } else if (name.equals("choose type")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.CHOOSE_BOOK_TYPE));
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        }

    }
}
