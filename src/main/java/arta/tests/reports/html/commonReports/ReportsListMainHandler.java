package arta.tests.reports.html.commonReports;

import arta.filecabinet.logic.Person;
import arta.tests.reports.logic.commonReports.CommonTestSearchParams;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.util.Constants;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ReportsListMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    Person person;
    int tutorID;

    CommonTestSearchParams params;
    ServletContext servletContext;
    HTMLCalendar calendar;

    public ReportsListMainHandler(int lang, int roleID, Person person, CommonTestSearchParams params,
                                  ServletContext servletContext, int tutorID) {
        this.lang = lang;
        this.person = person;
        this.roleID = roleID;
        this.params = params;
        this.servletContext = servletContext;
        this.tutorID = tutorID;
        calendar = new HTMLCalendar(lang);
    }

    public void getHeader(PrintWriter out) {
        out.print(HTMLCalendar.getScriptSRC());
        calendar.initializeCalendar(out);
        calendar.writeDIV(out);
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        if (roleID != Constants.TUTOR) tutorID = 0;

        String templateFileName = "tests/reports/common/reportsList.html";
        if(person.isAdministrator) {
            templateFileName = "tests/reports/common/reportsList-superAdmin.html";
        }

        ReportsListHandler handler = new ReportsListHandler(person, params, lang, servletContext, tutorID, calendar, roleID);
        FileReader fileReader = new FileReader(templateFileName);
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
