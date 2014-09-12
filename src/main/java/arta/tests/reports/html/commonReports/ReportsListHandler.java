package arta.tests.reports.html.commonReports;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.navigation.PartsHandler;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.folder.department.Department;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.tests.reports.logic.commonReports.CommonTestReportsManager;
import arta.tests.common.TestMessages;
import arta.subjects.logic.SubjectMessages;
import arta.classes.ClassMessages;
import arta.tests.testing.logic.SimpleExaminer;
import arta.tests.testing.logic.TestingMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ReportsListHandler extends TemplateHandler {

    CommonTestSearchParams params;
    int lang;
    Person person;
    HTMLCalendar calendar;
    int tutorID;
    CommonTestReportsManager manager ;
    int roleID;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();

    StringBuffer str;

    public ReportsListHandler(Person person,
                              CommonTestSearchParams params,
                              int lang, ServletContext servletContext,
                              int tutorID, HTMLCalendar calendar,
                              int roleID) {
        this.person = person;
        this.params = params;
        this.lang = lang;
        this.tutorID = tutorID;
        this.calendar = calendar;
        this.roleID = roleID;
        this.servletContext  = servletContext;
        FileReader fileReader = new FileReader("tests/reports/common/singleReport.txt");
        str = fileReader.read(servletContext);
        manager = new CommonTestReportsManager();
        manager.mysearch(person, params, lang, tutorID);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);

        } else if (name.equals("fio label")){
            pw.print(MessageManager.getMessage(lang, TestMessages.LFP, null));
        } else if (name.equals("fio value")){
            pw.print(trsf.getHTMLString(params.fio));

        } else if (name.equals("report label")){
            pw.print(MessageManager.getMessage(lang, TestMessages.NAME, null));

        } else if (name.equals("testing name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.NAME, null));

        } else if (name.equals("tutor name")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TUTOR_NAME, null));
        } else if (name.equals("tutor name select")){

            Select select = new Select(Select.POST_SELECT);
            pw.print(select.startSelect("examiner"));
            pw.print(select.addOption(0, params.examinerID == 0, ""));

            for(int examinerID : manager.examiners.keySet()){
                SimpleExaminer examiner = (SimpleExaminer)manager.examiners.get(examinerID);
                pw.print(select.addOption(examinerID, (examinerID == params.examinerID), examiner.getFullName()));
            }
            pw.print(select.endSelect());

        } else if (name.equals("department label")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.DEPARTMENT, null));
        } else if (name.equals("department select")){

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

        } else if (name.equals("testing date")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DATE, null));
        } else if (name.equals("subject")){
            pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT, null));
        } else if (name.equals("class name")){
            pw.print(MessageManager.getMessage(lang, ClassMessages.CLASS, null));
        } else if (name.equals("reports")){
            for (int i=0; i<manager.reports.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleReportHandler handler = new SingleReportHandler(manager.reports.get(i), lang, params);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.REPORTS, null));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("from")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FROM));
        } else if (name.equals("from input")){
            calendar.printInput(pw, params.startDate, "dateFrom", "form") ;
        } else if (name.equals("till")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TILL));
        } else if (name.equals("till input")){
            calendar.printInput(pw, params.finishDate, "dateTill", "form");
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("pages")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "testreports?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } 
    }

}
