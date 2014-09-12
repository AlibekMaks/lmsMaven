package arta.studyroom.html;

import arta.check.logic.Testing;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.SaveObject;
import arta.tests.testing.logic.SimpleTesting;

import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TestingHandler extends TemplateHandler {

    SimpleTesting testing;
    int lang;
    StringTransform trsf;
    int studentID;
    int roleID;

    boolean kaz_test = true;
    boolean rus_test = true;


    public TestingHandler(SimpleTesting testing, int lang, StringTransform trsf, int studentID, int roleID) {
        this.testing = testing;
        this.lang = lang;
        this.trsf = trsf;
        this.studentID = studentID;
        this.roleID = roleID;

        try{
            SaveObject saveSession = new SaveObject();
            arta.check.logic.Testing kaz_testing = (arta.check.logic.Testing)saveSession.getObject(studentID, testing.getKaz_test_ID() );
            if(kaz_testing != null){
                rus_test = false;
            }

            arta.check.logic.Testing rus_testing = (arta.check.logic.Testing)saveSession.getObject(studentID, testing.getRus_test_ID() );
            if(rus_testing != null){
                kaz_test = false;
            }

        } catch (Exception exc){

        }

    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING));
//            if (roleID == Constants.STUDENT){
//                pw.print("<a href=\"studenttesting?main_frame=true&testingID="+testing.getTestingID()+"&nocache="+ Rand.getRandString()+"\" class=\"chatlink\" " +
//                        " target=\"inner_content\" title=\""+MessageManager.getMessage(lang, TestMessages.PASS_TESTING)+"\" ");
//                pw.print(">");
//                //pw.print(trsf.getHTMLString(testing.getSubjectName()));
//                pw.print(trsf.getHTMLString("Тестирование"));
//                pw.print("</a>");
//            } else {
//                pw.print("<a href=\"testcommonreport?testingID="+testing.getTestingID()+"&print=true&nocache="
//                        + Rand.getRandString()+"\" class=\"chatlink\" " +
//                        " target=\"inner_content\" title=\""+MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT, null)+"\" ");
//                pw.print(" >");
//                pw.print(trsf.getHTMLString(testing.getTestingName()));
//                pw.print("</a>");
//            }

        } else if (name.equals("kaz lang")){
            int xTestingID = testing.IsExistsXTestResults(studentID);
            if((xTestingID>0 & xTestingID != testing.getKaz_test_ID()) || !kaz_test){
            } else {
                pw.print("<a class=\"lang\" style=\"color:#\" href=\"studenttesting?main_frame=true&mainTestingID="+testing.getMainTestingID()+"&testingID="+ testing.getKaz_test_ID()+"&nocache="+Rand.getRandString()+"\" "+
                         " target=\"inner_content\" title=\""+MessageManager.getMessage(lang, TestMessages.PASS_TESTING)+"\" >");
                pw.print("<img src=\"images/kaz1.jpg\" width=\"90px\" height=\"40px\" border=0 onmouseout=\"this.src='images/kaz1.jpg'\"  onmouseover=\"this.src='images/kaz2.jpg'\">");
                pw.print("</a>");
            }

        } else if (name.equals("rus lang")){
            int xTestingID = testing.IsExistsXTestResults(studentID);
            if((xTestingID>0 && xTestingID != testing.getRus_test_ID()) || !rus_test){
            } else {
                pw.print("<a class=\"lang\" style=\"color:#\" href=\"studenttesting?main_frame=true&mainTestingID="+testing.getMainTestingID()+"&testingID="+ testing.getRus_test_ID()+"&nocache="+Rand.getRandString()+"\""+
                        " target=\"inner_content\" title=\""+MessageManager.getMessage(lang, TestMessages.PASS_TESTING)+"\" >");
                pw.print("<img src=\"images/rus1.jpg\" width=\"90px\" height=\"40px\" border=0 onmouseout=\"this.src='images/rus1.jpg'\"  onmouseover=\"this.src='images/rus2.jpg'\">");
                pw.print("</a>");
            }

        } else if (name.equals("period")){
            //pw.print(testing.getTestingStartTime().getValue()+"-" +testing.getTestingFinishTime().getValue());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");   // hh:mm:ss
            java.util.Date dt = new java.util.Date();
            String s = formatter.format(dt);
            pw.print(s);
        }

    }
}
