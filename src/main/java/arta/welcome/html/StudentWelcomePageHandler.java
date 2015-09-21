package arta.welcome.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.settings.logic.Settings;
import arta.studyroom.logic.StudyRoomTestingsManager;
import arta.welcome.WelcomeMessages;
import arta.welcome.logic.RecommendStudents;
import arta.welcome.logic.StudentTestingsManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class StudentWelcomePageHandler extends TemplateHandler {

    Person person;
    int lang;
    ServletContext servletContext;
    StringTransform trsf = new StringTransform();
    StudyRoomTestingsManager srtmanager;
    int roleID;

    public StudentWelcomePageHandler(Person person, int lang, ServletContext servletContext,  int roleID,  int personID) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
//        srtmanager = new StudyRoomTestingsManager();
//        srtmanager.search(roleID, personID);
        this.roleID = roleID;

        if ( person.getRoleID() >= Constants.TUTOR){
            SheduledTesting testing = new SheduledTesting(0, null, 0);
            testing.CheckAndUpdateTestings();
        }

    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("welcome")){
            Properties prop = new Properties();
            prop.setProperty("name", trsf.getHTMLString(person.getFullName()));
            pw.print(MessageManager.getMessageNative(lang, WelcomeMessages.WELCOME, prop));
        } else if (name.equals("testings")){
            if (person.getRoleID() == Constants.STUDENT){
                StudentTestingsManager manager = new StudentTestingsManager();
                manager.mysearch(person.getPersonID(), lang);

                if (manager.testings.size() > 0 || manager.finishTestings.size() > 0){
                    new Parser(new FileReader("welcome/mytestings.txt").read(servletContext),
                               pw,
                               new StudentTestingsHandler(lang, person.getPersonID(), manager.testings, manager.finishTestings, null, person.getRoleID(),servletContext)).parse();
                }
            }
        } else if (name.equals("recommend students")){
            if (person.getRoleID() >= Constants.ADMIN){
                Settings settings = new Settings();
                settings.load();
                if(settings.recommend_candidates){
                    RecommendStudents recommendStudents = new RecommendStudents();

                    if(recommendStudents.recommendStudents(person, settings)){
                        pw.print("<table border=0 cellpadding=\"0\" cellspacing=\"0\" align=\"left\">");
                        pw.print("<tr><td height=\"30px\"></td></tr>");
                        pw.print("<tr><td align=\"left\">");

                        Message message = new Message();
                        //message.setMessageType(Message.INFORMATION_MESSAGE);
                        message.setMessageHeader(MessageManager.getMessageNative(lang, WelcomeMessages.FOUND_APPLICANTS_FOR_ATTESTATION, null));
                        message.addReason(MessageManager.getMessageNative(lang, WelcomeMessages.TO_SEE_GO_TO, null),
                                "appointtests?option=2&time="+settings.recommend_candidates_month+"&" + Rand.getRandString());
                        message.writeMessage(pw);

                        pw.print("</td></tr>");
                        pw.print("</table>");
                    }
                }
            }
        }
    }
}
