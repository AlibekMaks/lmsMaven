package arta.studyroom.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;
import arta.tests.common.TestMessages;
import arta.studyroom.logic.StudyRoomTestingsManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;


public class TestingsHandler extends TemplateHandler {

    int lang;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();
    int roleID;
    int personID;
    int subgroupID;

    StudyRoomTestingsManager manager;

    public TestingsHandler(int lang, ServletContext servletContext, int roleID, int personID, int subgroupID) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.roleID = roleID;
        this.personID = personID;
        this.subgroupID = subgroupID;
        manager = new StudyRoomTestingsManager(subgroupID);
        manager.search(roleID, personID);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.APPOINTED_TESTINGS, null));
        } else if (name.equals("items")){
            for (int i=0; i < manager.testings.size(); i++){
                new Parser(new FileReader("studyroom/content/testing.item.txt").read(servletContext), pw,
                        new TestingHandler(manager.testings.get(i), lang, trsf, -789, roleID)).parse();
            }
        }
    }
}
