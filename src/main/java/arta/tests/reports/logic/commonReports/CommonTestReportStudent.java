package arta.tests.reports.logic.commonReports;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Languages;
import arta.tests.common.TestMessages;

/**
 * Single testing student
 */
public class CommonTestReportStudent {

    public int lang;

    public String name;
    public int easy;
    public int difficult;
    public int middle;
    public int mark;
    public int studentID;
    public int testingID;
    public int testingLanguage;
    public boolean timeIsUp = false;

    public int classID;
    public String className;
    public boolean testingIsPassed = false;

    public CommonTestReportStudent(int lang){
        this.lang = lang;
    }

    public String getLanguageName(){
        if(testingLanguage == Languages.KAZAKH){
            return MessageManager.getMessage(lang, TestMessages.KAZ_MSG, null);
        } else if(testingLanguage == Languages.RUSSIAN){
            return MessageManager.getMessage(lang, TestMessages.RUS_MSG, null);
        } else return "";
    }

}
