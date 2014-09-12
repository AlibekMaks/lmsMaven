package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.TestForTesting;

import java.io.PrintWriter;
import java.util.Properties;


public class MyTestSingleHandler extends TemplateHandler {

    TestForTesting test;
    StringTransform trsf;
    int lang;


    public MyTestSingleHandler(StringTransform trsf, int lang) {
        this.trsf = trsf;
        this.lang = lang;
    }


    public void setTest(TestForTesting test) {
        this.test = test;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("testID")){
            pw.print(test.testID);
        } else if (name.equals("testname")){
            pw.print(trsf.getHTMLString(test.name));
        } else if (name.equals("all easy")){
            pw.print(test.alleasy);
        } else if (name.equals("all middle")){
            pw.print(test.allmiddle);
        } else if (name.equals("all difficult")){
            pw.print(test.alldifficult);
        } else if (name.equals("easy value")){
            if (test.easy != 0)
                pw.print(test.easy);
        } else if (name.equals("middle value")){
            if (test.middle != 0)
                pw.print(test.middle);
        } else if (name.equals("difficult value")){
            if (test.difficult != 0)
                pw.print(test.difficult);
        } else if (name.equals("delete")){
            Properties prop = new Properties();
            prop.setProperty("test", test.getTestName());
            pw.print("<a href=\"addtests?testID="+test.testID+"&option=-1&"+
                    "&nocache="+ Rand.getRandString()+"\" class=\"href\" " +
                    " onClick='return confirm(\""+MessageManager.getMessage(lang, TestMessages.CONFIRM_DELETE_TEST, prop)+"\")' " +
                    " title='"+trsf.getHTMLString(test.getTestName())+"' >");                        
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0 alt=\"delete\">");
            pw.print("</a>");
        }
    }
}
