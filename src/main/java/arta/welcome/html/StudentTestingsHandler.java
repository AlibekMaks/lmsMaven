package arta.welcome.html;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.studyroom.html.TestingHandler;
import arta.studyroom.logic.StudyRoomTestingsManager;
import arta.welcome.logic.StudentTestingsManager;
import arta.welcome.logic.StudentTesting;
import arta.tests.testing.logic.SimpleTesting;
import arta.tests.testing.logic.TestingMessages;
import arta.subjects.logic.SubjectMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 11.04.2008
 * Time: 14:00:50
 * To change this template use File | Settings | File Templates.
 */
public class StudentTestingsHandler extends TemplateHandler {

    int lang;
    int studentID;
    ArrayList <StudentTesting> testings;

    StringTransform trsf = new StringTransform();
    StudyRoomTestingsManager srtmanager;
    int roleID;
    ServletContext servletContext ;

    public StudentTestingsHandler(int lang, int studentID, ArrayList<StudentTesting> testings, StudyRoomTestingsManager srtmanager, int roleID, ServletContext servletContext) {
        this.lang = lang;
        this.studentID = studentID;
        this.testings = testings;
        this.srtmanager = srtmanager;
        this.roleID = roleID;
        this.servletContext = servletContext;

    }

    public void replace(String name, PrintWriter pw) {
//        if (name.equals("min left")){
//            int minute = 0;
//            for (int i = 0; i < testings.size(); i ++){
//                minute = testings.get(i).getTestingTime();
//            }
//            pw.print(minute);
//        } if (name.equals("hour")){
//            int minute = 0;
//            int hour = 0;
//            for (int i = 0; i < testings.size(); i ++){
//                minute = testings.get(i).getTestingTime();
//            }
//            hour = (int)Math.floor(minute/60);
//            pw.print(hour);
//        } if (name.equals("min")){
//            int minute = 0;
//            int hour = 0;
//            for (int i = 0; i < testings.size(); i ++){
//                minute = testings.get(i).getTestingTime();
//            }
//            hour = (int)Math.floor(minute/60);
//            minute = minute%60;
//            pw.print(minute);
//        } else
        if (name.equals("message")){
            pw.print("<table border=0 cellpadding=0 cellspacing=0 " +
                    " style=\"border-collapse:collapse;\" bordercolor=\"#dae0e6\" " +
                    " bgcolor=\"#f1f2f2\" >");
            pw.print("<tr>");
            pw.print("<td class=\"coloredheader\" " +
                    " style=\"padding-left:6px; padding-right:6px\" >");
                pw.print(MessageManager.getMessage(lang, Constants.NAME));
            pw.print("</td>");
            pw.print("<td class=\"coloredheader\" " +
                    "  style=\"padding-left:6px; padding-right:6px\">");
                pw.print(MessageManager.getMessage(lang, SubjectMessages.SUBJECT));
            pw.print("</td>");
            pw.print("<td class=\"coloredheader\" " +
                    "  style=\"padding-left:6px; padding-right:6px\">");
                pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_TIME));
            pw.print("</td>");

            pw.print("</tr>");
            for (int i = 0; i < testings.size(); i ++){
                pw.print("<tr>");
                pw.print("<td class=\"td\" align=center>");
                pw.print(trsf.getHTMLString(testings.get(i).getTestingName()));
                pw.print("&nbsp;&nbsp;</td>");
                pw.print("<td class=\"td\" align=center>");
                pw.print(trsf.getHTMLString(testings.get(i).getSubjectName()));
                pw.print("</td>");
                pw.print("<td class=\"td\" align=center>");
                pw.print(testings.get(i).getTestingStartTime().getValue() + "-"
                            + testings.get(i).getTestingFinishTime().getValue());
                pw.print("</td>");
                pw.print("</tr>");
//                pw.print("<tr>");
//                pw.print("<td class=\"td\" align=center>");
//                pw.print(trsf.getHTMLString(testings.get(i).getTestingName()));
//                pw.print("&nbsp;&nbsp;</td>");
//                pw.print("<td class=\"td\" align=center>");
//                pw.print(trsf.getHTMLString(testings.get(i).getSubjectName()));
//                pw.print("</td>");
//                pw.print("<td class=\"td\" align=center>");
//                pw.print(testings.get(i).getTestingStartTime().getValue() + "-"
//                        + testings.get(i).getTestingFinishTime().getValue());
//                pw.print("</td>");
//                pw.print("</tr>");
            }
            pw.print("</table>");
        }
        else if (name.equals("items")){
            for (int i=0; i < testings.size(); i++){
                new Parser(new FileReader("studyroom/content/testing.item.txt").read(servletContext), pw,
                        new TestingHandler(testings.get(i), lang, trsf, studentID, roleID)).parse();
            }
//            for (int i=0; i < srtmanager.testings.size(); i++){
//                System.out.println();
//                new Parser(new FileReader("studyroom/content/testing.item.txt").read(servletContext), pw,
//                        new TestingHandler(srtmanager.testings.get(i), lang, trsf, roleID)).parse();
//            }

        }
//        else if (name.equals("items")){
//            for (int i=0; i < testings.size(); i++){
//                System.out.println();
//                new Parser(new FileReader("studyroom/content/testing.item.txt").read(servletContext), pw,
//                        new TestingHandler(testings.get(i), lang, trsf, roleID)).parse();
//            }
//        }
//        else if (name.equals("items")){
//            for (int i=0; i < srtmanager.testings.size(); i++){
//                System.out.println();
//                new Parser(new FileReader("studyroom/content/testing.item.txt").read(servletContext), pw,
//                        new TestingHandler(srtmanager.testings.get(i), lang, trsf, roleID)).parse();
//            }
//        }
    }
}
