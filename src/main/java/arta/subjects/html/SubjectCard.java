package arta.subjects.html;

import arta.books.logic.Book;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.common.logic.db.Varchar;
import arta.settings.logic.Settings;
import arta.subjects.logic.*;
import arta.filecabinet.logic.SearchParams;
import arta.books.html.BookSingle;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class SubjectCard extends TemplateHandler {

    Subject subject;
    int lang;
    SearchParams params;
    ServletContext servletContext;
    Message message;
    StringTransform trsf = new StringTransform();
    public ArrayList<TestsSelect> tests = new ArrayList<TestsSelect>();
    String return_link;
    Settings settings;


    public SubjectCard(Subject subject, int lang, SearchParams params, ServletContext servletContext,
                       Message message, String return_link,Settings settings) {
        this.subject = subject;
        this.lang = lang;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
        this.return_link = return_link;
        this.settings=settings;
        settings.load();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name kz")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_KAZAKH, null));
        } else if (name.equals("name kz value")){
            pw.print(trsf.getHTMLString(subject.getName(Languages.KAZAKH)));
        } else if (name.equals("name ru")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_RUSSAIN, null));
        } else if (name.equals("name ru value")){
            pw.print(trsf.getHTMLString(subject.getName(Languages.RUSSIAN)));
        /**} else if (name.equals("name en")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.SUBJECT_NAME_IN_ENGLISH, null));
        } else if (name.equals("name en value")){  
            pw.print(trsf.getHTMLString(subject.getName(Languages.ENGLISH)));
        **/
        } else if(name.equals("preferred_mark_div display")){
//            if(settings.isUsetotalball()){
//                pw.print("block");
//            } else {
//                pw.print("none");
//            }
                pw.print("block");
        } else if (name.equals("preferred mark")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.PREFERRED_MARK, null));
        } else if (name.equals("preferred mark value")){
            pw.print(subject.getPreferredMark());
        } else if (name.equals("isArchive")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.IS_ARCHIVE, null));
        } else if (name.equals("isArchive value")){
            if(subject.isArchive()){pw.print("checked");} else {pw.print("");}
        } else if (name.equals("checkbox title kz")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.CHECKBOX_TITLE_KZ, null));
        } else if (name.equals("checkbox title ru")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.CHECKBOX_TITLE_RU, null));
        } else if (name.equals("option values kz")){
            TestSelectSingle ts = new TestSelectSingle();
            ts.GetAllTest();
            pw.print(AddSelectOption(0, MessageManager.getMessage(lang, SubjectMessages.TEST_NOT_CHANGED, null), false));
            for (int i=0; i<ts.tests.size(); i++){
                if(subject.getKaz_test_ID() == ts.tests.get(i).getTestID()){ //Выбранный тест для данного предмета
                    ts.tests.get(i).setSelected(true);
                }
                pw.print(AddSelectOption(ts.tests.get(i).getTestID(), ts.tests.get(i).getTestName(), ts.tests.get(i).getSelected()));
            }
        } else if (name.equals("option values ru")){
            TestSelectSingle ts = new TestSelectSingle();
            ts.GetAllTest();
            pw.print(AddSelectOption(0, MessageManager.getMessage(lang, SubjectMessages.TEST_NOT_CHANGED, null), false));
            for (int i=0; i<ts.tests.size(); i++){
                if(subject.getRus_test_ID() == ts.tests.get(i).getTestID()){ //Выбранный тест для данного предмета
                    ts.tests.get(i).setSelected(true);
                }
                pw.print(AddSelectOption(ts.tests.get(i).getTestID(), ts.tests.get(i).getTestName(), ts.tests.get(i).getSelected()));
            }
        } else if (name.equals("books on subect")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.STUDY_BOOKS, null));
        } else if (name.equals("subjectID")){
            pw.print(subject.getSubjectID());
        } else if (name.equals("params")){
            pw.print(params.getFullParams());
        } else if (name.equals("add link disabled")){
            if (subject.getSubjectID() <= 0)
                pw.print(" disabled onClick='return false;'  ");
        } else if (name.equals("add book")){
            pw.print(MessageManager.getMessage(lang,  SubjectMessages.ADD_BOOK, null));
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("records")){
            if (subject.getSubjectID() == 0) return;
            Parser parser = new Parser();
            StringBuffer str = new FileReader("books/single.txt").read(servletContext);
            parser.setPrintWriter(pw);
            BookSingle handler = new BookSingle(lang, params, subject.getSubjectID());
            parser.setTemplateHandler(handler);
            for (int i=0; i<subject.books.size(); i++){
                handler.set(subject.books.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("maxlength")){
            pw.print(Varchar.NAME/2);
        } else if (name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("message")){
            if (message !=  null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("add link")){
            pw.print("subject?subjectID=0&" + params.getParamsWithoutRecord());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang,  Constants.ADD, null));            
        } else if (name.equals("return link")){
            //pw.print("subjects?"+params.getFullParams());
            pw.print(return_link);
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang,  Constants.BACK, null));
        } else if (name.equals("page header")){
            StringBuffer str = new StringBuffer(MessageManager.getMessage(lang, SubjectMessages.SUBJECT, null));
            str.append("&nbsp;");
            str.append(trsf.getHTMLString(subject.getName(lang)));
            pw.print(str);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("type")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.BOOK_TYPE));
        } else if (name.equals("fill in all of the fields")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        }
    }

    public  String AddSelectOption(Object val, String text, boolean isSelected){
        return "<option value=\""+val+"\" "
                +(isSelected?"selected":"")+">"+text+"</option>";
    }
}
