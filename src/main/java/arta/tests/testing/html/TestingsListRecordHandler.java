package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.tutors.Tutor;
import arta.tests.testing.logic.SimpleTesting;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.servlet.TestingServlet;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;

public class TestingsListRecordHandler extends TemplateHandler {

    SearchParams params;
    StringTransform trsf;
    int lang;

    SimpleTesting testing;


    public TestingsListRecordHandler(StringTransform trsf, int lang, SearchParams params) {
        this.trsf = trsf;
        this.lang = lang;
        this.params = params;
    }


    public void setTesting(SimpleTesting testing) {
        this.testing = testing;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
                pw.print("<a href=\"testingstudents?testingID="+testing.getTestingID()+"&mainTestingID="+testing.getMainTestingID() +
                        "&"+params.getFullParams()+ "\" class=\"href\">"); //target="_blank"
                pw.print(trsf.getHTMLString(testing.getTestingName()));
                pw.print("</a>");
        } else if (name.equals("tutor name")){
            Tutor tutor = new Tutor();
            tutor.setPersonID(testing.getTutorID());
            tutor.loadName();
            pw.print(trsf.getHTMLString(tutor.getFullName()));
        } else if (name.equals("date")){
            pw.print(testing.getTestingDate().getInputValue());
        } else if (name.equals("testing time")){
            pw.print(testing.getTestingTime());
        } else if (name.equals("delete link")){
            pw.print("<a href=\"testings?option=-1&testingID="+ testing.getTestingID() + "&mainTestingID="+testing.getMainTestingID()+
                    "&"+ params.getFullParams() + "\" " +
                    " onclick='return confirm(\"" +
                    MessageManager.getMessage(lang, TestingMessages.CONFIRM_CANCEL_TESTING, null) + "\")' >");
            pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" " +
                    " height=\"16px\" border=0 alt=\"delete\">");
            pw.print("</a>");
        }
//        else if (name.equals("delete link")){
//            if (!testing.hasStarted()){
//                Properties prop = new Properties();
//                prop.setProperty("name", testing.getTestingName());
//                pw.print("<a href=\"testings?option=-1&testingID="
//                        + testing.getTestingID() + "&" + params.getFullParams() + "\" " +
//                        " onclick='return confirm(\"" +
//                        MessageManager.getMessage(lang, TestingMessages.CONFIRM_CANCEL_TESTING, prop) + "\")' >");
//                pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" " +
//                        " height=\"16px\" border=0 alt=\"delete\">");
//                pw.print("</a>");
//            }
//        }
    }
}
