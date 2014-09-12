package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.servlet.TestingStudentsServlet;
import arta.tests.testing.servlet.TestingServlet;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 13:19:28
 * To change this template use File | Settings | File Templates.
 */
public class TestingOptionsHandler extends TemplateHandler {

    int lang;
    Testing testing;
    String addLink, addTitle;

    public TestingOptionsHandler(int lang, Testing testing, String addLink, String addTitle) {
        this.lang = lang;
        this.testing = testing;
        this.addLink = addLink;
        this.addTitle = addTitle;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("return link")){
            pw.print("testings?parmas=old&nocache=" + Rand.getRandString());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("params link")){
            pw.print("testing?testingID=" + testing.getTestingID() + "&nocache=" + Rand.getRandString());
        } else if (name.equals("params title")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_PARAMETERS));
        } else if (name.equals("test link")){
            pw.print("testsfortesting?nocache=" + Rand.getRandString()+"&option=3");
        } else if (name.equals("tests title")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_TESTS));
        } else if (name.equals("students title")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_STUDENTS));
        } else if (name.equals("appoint link")){
            pw.print("testing?option=" + TestingServlet.SAVE_OPTION+"&nocache=" + Rand.getRandString());
        } else if (name.equals("appoint title")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.APPOINT_TESTING));
        } else if (name.equals("add link")){
            if (addLink != null){
                pw.print("<td align=\"center\" valign=\"middle\">");
                    pw.print("<a href=\""+addLink+"\" title=\""+addTitle+"\">");
                        pw.print("<img src=\"images/buttons.plus.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"\" " +
                                " height=\""+Constants.MENU_IMAGE_SIZE+"\" border=0 >");
                    pw.print("</a>");
                pw.print("</td>");
            }
        }
    }
}
