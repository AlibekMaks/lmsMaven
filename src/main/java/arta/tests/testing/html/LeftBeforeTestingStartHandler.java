package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 28.03.2008
 * Time: 15:05:42
 * To change this template use File | Settings | File Templates.
 */
public class LeftBeforeTestingStartHandler extends TemplateHandler {

    int lang;
    int leftMinutes;
    int testingID;


    public LeftBeforeTestingStartHandler(int lang, int leftMinutes, int testingID) {
        this.lang = lang;
        this.leftMinutes = leftMinutes;
        this.testingID = testingID;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("before start left")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TIME_LEFT_BEFORE_TESTING));
        } else if (name.equals("min left")){
            pw.print(leftMinutes);
        } else if (name.equals("hour")){
            pw.print(MessageManager.getMessage(lang, Constants.HOUR_SHORT));
        } else if (name.equals("min")){
            pw.print(MessageManager.getMessage(lang, Constants.MINUTE_SHORT));
        } else if (name.equals("testingID")){
            pw.print(testingID);
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        }
    }
}
