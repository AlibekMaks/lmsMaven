package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.TestingStudentsSearchParams;
import arta.tests.testing.logic.StudentsManager;
import arta.tests.testing.servlet.TestingStudentsServlet;
import arta.classes.logic.ClassesSelect;
import arta.classes.ClassMessages;
import arta.subjects.logic.SubjectsSelect;
import arta.subjects.logic.SubjectMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:57:37
 * To change this template use File | Settings | File Templates.
 */
public class StudentsToAddListHandler extends TemplateHandler {

    int lang;
    Testing testing;
    ServletContext servletContext;
    TestingStudentsSearchParams params;
    int tutorID;

    StringTransform trsf = new StringTransform();
    StudentsManager manager;


    public StudentsToAddListHandler(int lang, Testing testing, ServletContext servletContext,
                                    TestingStudentsSearchParams params, int tutorID) {
        this.lang = lang;
        this.testing = testing;
        this.servletContext = servletContext;
        this.params = params;
        this.tutorID = tutorID;
        manager = new StudentsManager(params, testing, tutorID, lang);
        manager.search();
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.ADD_STUDENTS_TO_TESTING));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/testing.card.options.html").read(servletContext),
                    pw, new TestingOptionsHandler(lang, testing, null, null)).parse();
        } else if (name.equals("search option")){
            pw.print(TestingStudentsServlet.SEARCH_TO_ADD_OPTION);
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("class select")){
            ClassesSelect select = new ClassesSelect(tutorID);
            select.writePostSelect(params.classIDStr, 100, pw, params.classID, true);
        } else if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "testingstudents?option="+TestingStudentsServlet.SEARCH_TO_ADD_OPTION+"&"+params.getParams(),
                    params.partNumberStr).writeLinks(pw);
        } else if (name.equals("add option")){
            pw.print(TestingStudentsServlet.ADD_TO_TESTING_OPTION);
        } else if (name.equals("add")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD));
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.FIO));
        } else if (name.equals("class")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS));
        } else if (name.equals("records")){

            StudentsToAddRecordHandler handler = new StudentsToAddRecordHandler(trsf);
            Parser parser = new Parser();
            StringBuffer template = new FileReader("testing/students.to.add.record.html").read(servletContext);
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);

            for (int i = 0; i < manager.students.size(); i ++){
                handler.setStudent(manager.students.get(i));
                parser.setStringBuilder(new StringBuffer(template));
                parser.parse();
            }

        } else if (name.equals("subject select")){
            SubjectsSelect select = new SubjectsSelect(tutorID, params.classID, lang);
            select.writePostSelect(params.subjectIDStr, 100, params.subjectID, pw, true);
        } else if (name.equals("subject")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT));
        } else if (name.equals("select class")){
            if (params.classID == 0){
                pw.print(MessageManager.getMessage(lang, TestingMessages.CHOOSE_CLASS));
            } else {
                pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS));
            }
        } else if (name.equals("select subject")){
            if (params.subjectID == 0){
                pw.print(MessageManager.getMessage(lang, TestingMessages.CHOOSE_SUBJECT));
            } else {
                pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT));
            }
        } else if (name.equals("classID")){
            pw.print(params.classID);
        } else if (name.equals("subjectID")){
            pw.print(params.subjectID);
        }
    }
}
