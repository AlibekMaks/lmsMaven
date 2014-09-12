package arta.groups.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.SimpleObject;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.classes.logic.StudyClass;
import arta.groups.logic.StudyGroup;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudyGroupCardMain extends PageContentHandler {

    int lang;
    Person person;
    ServletContext servletContext;
    SimpleObject studyClass;
    StudyGroup studyGroup;

    int recordNumber;
    int recordsCount;
    SearchParams params;


    public StudyGroupCardMain(int lang, Person person,
                              ServletContext servletContext, SimpleObject studyClass,
                              StudyGroup studyGroup, int recordNumber, int recordsCount,
                              SearchParams params) {
        this.lang = lang;
        this.person = person;
        this.servletContext = servletContext;
        this.studyClass = studyClass;
        this.studyGroup = studyGroup;
        this.recordNumber = recordNumber;
        this.recordsCount = recordsCount;
        this.params = params;
    }

    public void getHeader(PrintWriter out) {

    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("groups/group.edit.txt").read(servletContext), pw,
                new StudyGroupCard(studyClass, studyGroup, servletContext, lang, 
                        recordNumber, recordsCount, params)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
