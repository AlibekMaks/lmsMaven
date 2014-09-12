package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.servlet.AddTestsForTestingServlet;
import arta.tests.testing.servlet.TestingServlet;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 13:19:28
 * To change this template use File | Settings | File Templates.
 */
public class MyTestingOptionsHandler extends TemplateHandler {

    int lang;
    Testing testing;
    String addLink, addTitle;

    public MyTestingOptionsHandler(int lang, Testing testing, String addLink, String addTitle) {
        this.lang = lang;
        this.testing = testing;
        this.addLink = addLink;
        this.addTitle = addTitle;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("return link")){
            if(addLink==null){
                pw.print("addtests?option="+AddTestsForTestingServlet.SEARCH_TO_ADD_OPTION+"&nocache=" + Rand.getRandString()+"&add_tests=1");
            } else {
                pw.print("testsedit?&nocache=" + Rand.getRandString());
            }
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
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
