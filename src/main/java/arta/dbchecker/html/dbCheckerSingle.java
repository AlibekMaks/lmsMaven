package arta.dbchecker.html;

import arta.check.logic.TestSubject;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.SearchStudent;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;
import java.util.Properties;


public class dbCheckerSingle extends TemplateHandler {

    int lang;
    TestSubject testSubject;
    StringTransform trsf = new StringTransform();


    public dbCheckerSingle(SearchParams params, int lang) {
        this.lang = lang;
    }

    public void set(int mainTestingID, int testingID, TestSubject testSubject){
        this.testSubject = testSubject;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(trsf.getHTMLString(testSubject.subjectName));
        } else if (name.equals("questions counts")){

            String easy_disabled = (testSubject.easy == 0)? "disabled" : "";
            String middle_disabled = (testSubject.middle == 0)? "disabled" : "";
            String difficult_disabled = (testSubject.difficult == 0)? "disabled" : "";

            pw.print("<table width=\"100%\" class=\"table\" >");
                pw.print("<tr>");
                    pw.print("<td width=\"150px\">");
                        pw.print(trsf.getHTMLString(MessageManager.getMessage(lang, TestMessages.EASY, null)));
                    pw.print("</td>");
                    pw.print("<td>");
                        pw.print("<input name=\"var_"+testSubject.subjectID+"_1"+"\" style=\"width:50px; text-align:center\" value=\""+testSubject.x_easy+"\" class=\"input\" maxlength=\"3\" "+easy_disabled+" />");
                    pw.print("</td>");
                pw.print("</tr>");

                pw.print("<tr>");
                    pw.print("<td width=\"150px\">");
                        pw.print(trsf.getHTMLString(MessageManager.getMessage(lang, TestMessages.MIDDLE, null)));
                    pw.print("</td>");
                    pw.print("<td>");
                        pw.print("<input name=\"var_"+testSubject.subjectID+"_2"+"\" style=\"width:50px; text-align:center\" value=\""+testSubject.x_middle+"\" class=\"input\" maxlength=\"3\" "+middle_disabled+" />");
                    pw.print("</td>");
                pw.print("</tr>");

                pw.print("<tr>");
                    pw.print("<td width=\"150px\">");
                        pw.print(trsf.getHTMLString(MessageManager.getMessage(lang, TestMessages.DIFFICULT, null)));
                    pw.print("</td>");
                    pw.print("<td>");
                        pw.print("<input name=\"var_"+testSubject.subjectID+"_3"+"\" style=\"width:50px; text-align:center\" value=\""+testSubject.x_difficult+"\" class=\"input\" maxlength=\"3\" "+difficult_disabled+" />");
                    pw.print("</td>");
                pw.print("</tr>");
            pw.print("</table>");
        }
    }
}
