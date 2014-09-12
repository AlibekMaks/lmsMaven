package arta.tests.test.list;

import arta.tests.test.Test;
import arta.tests.servlets.QuestionServlet;
import arta.tests.common.TestMessages;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;
import arta.tests.testing.servlet.AddTestsForTestingServlet;

import java.io.PrintWriter;
import java.util.Properties;

public class TutorTestsListSingleTestHandler extends TemplateHandler {

    int lang;
    Test test;
    TestsSearchParams params;
    StringTransform stringTransform;
    int tutorID;
    int roleID;


    public TutorTestsListSingleTestHandler(int lang, Test test, TestsSearchParams params,
                                           StringTransform stringTransform, int tutorID, int roleID) {
        this.lang = lang;
        this.test = test;
        this.params = params;
        this.stringTransform = stringTransform;
        this.tutorID = tutorID;
        this.roleID = roleID;
    }

   public void replace(String name, PrintWriter pw) {
        if (name.equals("testID")){
            pw.print(test.testID);
        } else if (name.equals("test name")){

            pw.print("<a href=\"testView?load=true&testID="+test.testID+"\" class=\"href\"" +
                    " title=\""+stringTransform.getHTMLString(test.getTestName())+"\"  >");
            pw.print(stringTransform.getHTMLString(test.testName));
            pw.print("</a>");

        } else if (name.equals("created")){
            pw.print(test.created.getInputValue());
        } else if (name.equals("modified")){
            pw.print(test.modified.getInputValue());
        } else if (name.equals("add tests")){

            if (tutorID == test.tutorID || (roleID & Constants.ADMIN) > 0){
                pw.print("<a class=\"href\" title=\""+stringTransform.getHTMLString(test.getTestName())+"\"  ");
                pw.print(" href=\"addtests?testID="+test.testID+"&param=new&mainTestID="+test.testID+"&option="+ AddTestsForTestingServlet.CHANGE_OPTION+
                        "&nocache="+params.getFullParams()+"\" ");
                pw.print(">");
                pw.print("<img src=\"images/plus.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print("</a>");
            }

        } else if (name.equals("edit")){

            if (tutorID == test.tutorID || (roleID & Constants.ADMIN) > 0){
                pw.print("<a class=\"href\" title=\""+stringTransform.getHTMLString(test.getTestName())+"\"  ");
                pw.print(" href=\"testedit?testID="+test.testID+"&option="+QuestionServlet.LOAD_OPTION+
                        "&nocache="+params.getFullParams()+"\" ");
                pw.print(">");
                pw.print("<img src=\"images/icon.edit.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print("</a>");
            }

        }else if (name.equals("delete")){
            if (tutorID == test.tutorID || (roleID & Constants.ADMIN) > 0){
                pw.print("<a class=\"href\" ");
                pw.print(" title=\""+stringTransform.getHTMLString(test.getTestName())+"\" ");
                Properties prop = new Properties();
                prop.setProperty("test", stringTransform.getHTMLString(test.getTestName()));
                pw.print(" href=\"");
                pw.print("testsedit?testID="+test.testID+"&option=-1&"+params.getFullParams()+"&");
                pw.print("\" ");
                pw.print(" onClick='return confirm(\""+MessageManager.getMessage(lang, TestMessages.CONFIRM_DELETE_TEST, prop)+"\")' ");
                pw.print(">");
                pw.print("<img src=\"images/icon.delete.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print("</a>");
            }
        } 
    }

}
