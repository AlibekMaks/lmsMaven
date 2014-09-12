package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.servlet.TestingStudentsServlet;
import arta.classes.ClassMessages;
import arta.subjects.logic.SubjectMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:15:13
 * To change this template use File | Settings | File Templates.
 */
public class AddedStudentsListHandler extends TemplateHandler {

    int lang;
    Testing testing;
    ServletContext servletContext;

    StringTransform trsf = new StringTransform();


    public AddedStudentsListHandler(int lang, Testing testing, ServletContext servletContext) {
        this.lang = lang;
        this.testing = testing;
        this.servletContext = servletContext;
    }

    public void replace(String name, PrintWriter pw) {
        /*if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.ADDED_TO_TESTING_STUDENTS));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/testing.card.options.html").read(servletContext), pw,
                new TestingOptionsHandler(lang, testing, "testingstudents?option="+ TestingStudentsServlet.SEARCH_TO_ADD_OPTION
                        + "&nocache="+ Rand.getRandString(), MessageManager.getMessage(lang, TestingMessages.ADD_STUDENTS_TO_TESTING))
            ).parse();
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.FIO));
        } else if (name.equals("class")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS));
        } else if (name.equals("records")){
            AddedStudentsListRecordHandler handler = new AddedStudentsListRecordHandler(trsf, lang);
            StringBuffer template = new FileReader("testing/added.students.single.html").read(servletContext);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i = 0; i < testing.students.size(); i ++){
                parser.setStringBuilder(new StringBuffer(template));
                handler.setStudent(testing.students.get(i));
                parser.parse();
            }
        } else if (name.equals("subject")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT));
        }*/
    }
}
