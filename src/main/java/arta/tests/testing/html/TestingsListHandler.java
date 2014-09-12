package arta.tests.testing.html;

import arta.classes.logic.ClassesSelect;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.folder.department.Department;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.SimpleExaminer;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.TestingsManager;
import arta.tests.testing.logic.TestingsSearchParams;
import arta.tests.testing.servlet.TestingServlet;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TestingsListHandler extends TemplateHandler {

    int lang;
    Person person;
    TestingsSearchParams params;
    ServletContext servletContext;
    Message message;

    StringTransform trsf = new StringTransform();
    TestingsManager manager;

    HTMLCalendar calendar;


    public TestingsListHandler(int lang, Person person, TestingsSearchParams params, ServletContext servletContext,
                               Message message, int tutorID, HTMLCalendar calendar) {
        this.lang = lang;
        this.person = person;
        this.params = params;
        this.servletContext = servletContext;
        this.message = message;
        this.calendar = calendar;
        manager = new TestingsManager(params, person);
        manager.lang = lang;
        manager.search(tutorID, params);
    }

    public void replace(String name, PrintWriter pw) {

        if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTINGS_LIST));
        } else if(name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("fio label")){
            pw.print(MessageManager.getMessage(lang, TestMessages.LFP, null));
        } else if (name.equals("fio value")){
            pw.print(trsf.getHTMLString(params.fio));

        } else if (name.equals("report label")){
            pw.print(MessageManager.getMessage(lang, TestMessages.NAME, null));

        } else if(name.equals("department label")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.DEPARTMENT, null));
        } else if(name.equals("department select")){

            Department manager = new Department();
            SearchParams tmp = new SearchParams();
            tmp.countInPart = 0;
            manager.search(tmp, lang);
            Select select = new Select(Select.POST_SELECT);
            pw.print(select.startSelect("department"));
            pw.print(select.addOption(0, params.departmentID == 0, ""));
            for (int i=0; i<manager.departments.size(); i++){
                pw.print(select.addOption(manager.departments.get(i).id+"",
                        manager.departments.get(i).id == params.departmentID,
                        manager.departments.get(i).name));
            }
            pw.print(select.endSelect());

        } else if(name.equals("tutorName label")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TUTOR_NAME));
        } else if(name.equals("tutorName select")){

            Select select = new Select(Select.POST_SELECT);
            pw.print(select.startSelect("examiner"));
            pw.print(select.addOption(0, params.examinerID == 0, ""));

            for(int examinerID : manager.examiners.keySet()){
                SimpleExaminer examiner = (SimpleExaminer)manager.examiners.get(examinerID);
                pw.print(select.addOption(examinerID, (examinerID == params.examinerID), examiner.getFullName()));
            }
            pw.print(select.endSelect());

        } else if(name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if(name.equals("return link")){
            pw.print("main?" + Rand.getRandString());
        } else if(name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK));
        } else if(name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
//        } else if(name.equals("create link")){
//            pw.print("testing?testingID=0&nocache=" + Rand.getRandString() + "&option=" + TestingServlet.LOAD_OPTION);
//        } else if(name.equals("create title")){
//            pw.print(MessageManager.getMessage(lang, TestingMessages.NEW_TESTING));
        } else if(name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND));
        } else if(name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if(name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang, 
                    "testings?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if(name.equals("name")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_NAME));
        } else if(name.equals("tutor name")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TUTOR_NAME));
        } else if(name.equals("date")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_DATE));
        }
//        else if(name.equals("start time")){
//            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_START_TIME));
//        }
        else if(name.equals("testing time")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_TIME));
        } else if(name.equals("records")){
            StringBuffer template = new FileReader("tests/testings/testings.list.record.txt").read(servletContext);
            TestingsListRecordHandler recordHandler = new TestingsListRecordHandler(trsf, lang, params);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(recordHandler);
            for (int i = 0; i < manager.testings.size(); i ++){
                parser.setStringBuilder(new StringBuffer(template));
                recordHandler.setTesting(manager.testings.get(i));
                parser.parse();
            }
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("from")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FROM));
        } else if (name.equals("from input")){
            calendar.printInput(pw, params.startDate, "dateFrom", "form") ;
        } else if (name.equals("till")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TILL));
        } else if (name.equals("till input")){
            calendar.printInput(pw, params.finishDate, "dateTill", "form");
        }
    }
}
