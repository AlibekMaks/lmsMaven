package arta.check.html;

import arta.check.logic.Testing;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.students.Student;
import arta.settings.logic.Settings;
import arta.tests.common.TestMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TestCheckSingleHandler2 extends TemplateHandler {

    Testing testing;
    int lang;
    Student student;
    Settings settings;

    public TestCheckSingleHandler2(Testing testing, int lang, Settings settings) {
        this.testing = testing;
        this.lang = lang;
        this.settings = settings;
    }

    public void replace(String name, PrintWriter pw) {
        if(name.equals("testing results")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_RESULTS, null));
        } else if (name.equals("total questions count")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TOTAL_QUESTIONS_COUNT, null));
        } else if (name.equals("questions count")){
            pw.print(testing.questions.size());
        } else if (name.equals("right answers count")){
            pw.print(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWERS_COUNT, null));
        } else if (name.equals("answers count")){
            pw.print(testing.rightAnswersCount);
        } else if (name.equals("ur mark is")){
            pw.print(MessageManager.getMessage(lang, TestMessages.UR_MARK_IS, null));
        } else if (name.equals("mark")){
            pw.print(testing.mark);
        } else if (name.equals("fio")){
            Student student = new Student();
            student.loadById(testing.studentID);
            System.out.println("student = " + student);
            pw.print(student.getFullName());
        } else if (name.equals("result.header")){
            pw.print(MessageManager.getMessage(lang, Constants.TESTING_VERDICT_HEADER, null));
        } else if (name.equals("result")){
            testing.getPreferredMark(testing.testingID, testing.studentID);
            int attestatThreshold = 0;
            if(settings.usesubjectball){ // Рекомендуемый балл (В придмете)
                attestatThreshold = testing.preferredMark;
            } else { //Использовать из общих настроек
                Student student = new Student();
                student.loadById(testing.studentID);
                attestatThreshold = student.isDirector() ? settings.attestatThresholdForDirectors : settings.attestatThresholdForEmployee;
            }

            if (testing.mark >= attestatThreshold) {
                pw.print(MessageManager.getMessage(lang, Constants.TESTING_VERDICT_GOOD, null));
            } else {
                pw.print(MessageManager.getMessage(lang, Constants.TESTING_VERDICT_BAD, null));
            }
        }
    }
}
