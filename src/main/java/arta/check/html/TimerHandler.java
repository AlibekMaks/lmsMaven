package arta.check.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;


public class TimerHandler extends TemplateHandler {

    int started;
    int all;
    int lang;


    public TimerHandler(int started, int all, int lang) {
        this.started = started;
        this.all = all;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("start")){
            pw.print(started);
        } else if (name.equals("all")){
            pw.print(all);
        } else if (name.equals("passed time")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TIME_PASSED, null));
        } else if (name.equals("minutes")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MINUTES, null));
        } else if (name.equals("time remaining")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TIME_REMAINING, null));
        } else if (name.equals("testing time")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_TIME, null));
        } else if (name.equals("time")){
            pw.print(all);
        }
    }
}
