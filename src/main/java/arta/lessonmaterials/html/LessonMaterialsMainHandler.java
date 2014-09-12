package arta.lessonmaterials.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Date;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class LessonMaterialsMainHandler extends PageContentHandler {

    ServletContext servletContext;
    int lang;
    Person person;
    SearchParams params;
    Message message;


    public LessonMaterialsMainHandler(ServletContext servletContext, int lang, 
                                      Person person, SearchParams params,
                                      Message message) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.person = person;
        this.params = params;
        this.message = message;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("lessonmaterials/lessonmaterials.list.txt").read(servletContext), pw,
                new LessonMaterilsListHandler(params, lang, servletContext,
                        message, person.getPersonID())).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return person.getRoleID();
    }
}
