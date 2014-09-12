package arta.tests.test.list;

import arta.tests.common.TestMessages;
import arta.tests.servlets.QuestionServlet;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class TutorTestsListHandler extends TemplateHandler {


    private int lang;
    private TutorTestsManager manager;
    Message message;
    ServletContext servletContext;
    TestsSearchParams params;
    int partsNumber;
    StringTransform trsf = new StringTransform();
    Person person;

    //"tests/tests.list/single.test.edit.txt"
    public TutorTestsListHandler(int lang, Message message, ServletContext servletContext, TestsSearchParams params, 
                                 Person person) {
        this.lang = lang;
        this.message = message;
        this.servletContext = servletContext;
        this.params = params;
        this.person = person;
        manager = new TutorTestsManager();
        manager.search(params);
        partsNumber = params.getPartsNumber();
    }

    public void replace(String name, PrintWriter pw) {
        if(name.equals("searchValue")){
            pw.print(params.getSearch());
        } else if(name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if(name.equals("name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_NAME, null));
        } else if(name.equals("created")){
            pw.print(MessageManager.getMessage(lang, TestMessages.CREATED, null));
        } else if(name.equals("modified")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MODIFIED, null));
        } else if(name.equals("delete")){
            pw.print(MessageManager.getMessage(lang, Constants.DELETE, null));
        } else if(name.equals("edit")){
            pw.print(MessageManager.getMessage(lang, Constants.EDIT, null));
        } else if(name.equals("tests")){
            StringBuffer str = new FileReader("tests/tests.list/single.test.edit.txt").read(servletContext);
            for (int i=0; i<manager.tests.size(); i++){
                new Parser(new StringBuffer(str), pw,
                        new TutorTestsListSingleTestHandler(lang, manager.tests.get(i), params, trsf,
                                person.getPersonID(), person.getRoleID())).parse();
            }
        } else if(name.equals("found tests")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if(name.equals("tests count")){
            pw.print(params.recordsCount);
        } else if (name.equals("d u want to delete")){
            pw.print(MessageManager.getMessage(lang, TestMessages.CONFIRM_DELETE_TEST, null));
        } else if (name.equals("random")){
            pw.println("<input name=\"nocache\" type=\"hidden\" value=\""+Rand.getRandString()+"\"/>");
        } else if (name.equals("message")){
            if (message!=null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("add link")){
            pw.print("testedit?"+params.getParamsWithoutRecord()+"&testID=0&option="+ QuestionServlet.LOAD_OPTION);
        } else if (name.equals("add title")){
            pw.append(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("view")){
            pw.print(MessageManager.getMessage(lang, Constants.VIEW, null));
        } else if (name.equals("import link")){
            pw.print("import?"+params.getFullParams());
        } else if (name.equals("import title")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTS_IMPORT, null));
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTS));
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang, "testsedit?" + params.getParams(),
                    params.partNumberStr).writeLinks(pw);
        }
    }
}
