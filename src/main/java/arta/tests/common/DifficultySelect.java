package arta.tests.common;

import arta.common.logic.messages.MessageManager;
import arta.common.html.util.Select;

import java.io.PrintWriter;

public class DifficultySelect {

    public static final int EASY = 1;
    public static final int MIDDLE = 2;
    public static final int DIFFICULT = 3;

    int lang;

    public DifficultySelect(int lang) {
        this.lang = lang;
    }

    public void writeSelect(String name, int value, int width, PrintWriter pw){
        Select select = new Select(Select.POST_SELECT);
        select.width = width;
        pw.print(select.startSelect(name));
        pw.print(select.addOption("1", value == EASY, MessageManager.getMessage(lang, TestMessages.EASY_QUESTION, null)));
        pw.print(select.addOption("2", value == MIDDLE, MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTION, null)));
        pw.print(select.addOption("3", value == DIFFICULT, MessageManager.getMessage(lang, TestMessages.DIFFICULT_QUESTION, null)));
        pw.print(select.endSelect());
    }

    public String getDifficultyName(int difficulty){
        if (difficulty == EASY){
            return MessageManager.getMessage(lang, TestMessages.EASY_QUESTION, null);
        } else if (difficulty == MIDDLE){
            return MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTION, null);
        } else if (difficulty == DIFFICULT){
            return MessageManager.getMessage(lang, TestMessages.DIFFICULT_QUESTION, null);           
        }
        return "";
    }
}
