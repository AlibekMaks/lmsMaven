package arta.check.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;


public class TestResultsEmptyHandler extends TemplateHandler {

    int lang;


    public TestResultsEmptyHandler(int lang) {
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("testing finished")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_HAS_BEEN_FINISHED, null));
        }
    }
}
