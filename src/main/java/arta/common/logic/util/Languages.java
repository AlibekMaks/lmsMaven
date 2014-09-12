package arta.common.logic.util;

import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.library.logic.LibraryMessages;

import java.io.PrintWriter;


public class Languages {
    public static final int KAZAKH = 1;
    public static final int RUSSIAN = 2;
    public static final int ENGLISH = 3;

    public static final String KAZ = "kz";
    public static final String ENG = "en";
    public static final String RUS = "ru";

    public static final int RUSSIAN_MSG = 117;
    public static final int KAZAKH_MSG = 118;
    public static final int ENGLISH_MSG = 119;
    public static final int ANY_MSG = 120;

    /**
     * Метод возвращает суффикс используемый в полях БД
     * для обозначения языка.   
     * @param lang
     * @return
     */
    public static String getLang(int lang) {
        if (lang == KAZAKH)
            return KAZ;
        if (lang == RUSSIAN)
            return RUS;
        if (lang == ENGLISH)
            return ENG;
        return RUS;
    }

    public static void writeLanguageSelect(int lang, int width, String name, PrintWriter pw,
                                           boolean includeAny, int value) {
        writeLanguageSelect(lang, width, name, pw, includeAny, value, null);
    }

    public static void writeLanguageSelect(int lang, int width, String name, PrintWriter pw,
                                           boolean includeAny, int value, String id) {
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        pw.print(select.startSelect(name, id));
        pw.print(select.addOption(KAZAKH + "", KAZAKH == value, MessageManager.getMessage(lang, KAZAKH_MSG, null)));
        pw.print(select.addOption(RUSSIAN + "", RUSSIAN == value, MessageManager.getMessage(lang, RUSSIAN_MSG, null)));
        pw.print(select.addOption(ENGLISH + "", ENGLISH == value, MessageManager.getMessage(lang, ENGLISH_MSG, null)));
        if (includeAny)
            pw.print(select.addOption("0", 0 == value, MessageManager.getMessage(lang, ANY_MSG, null)));
        pw.print(select.endSelect());
    }

    public static void writeLanguageSelectAny(int lang, int width, String name, PrintWriter pw,
                                           boolean includeAny, int value) {
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        pw.print(select.startSelect(name));
        pw.print(select.addOption(KAZAKH + "", KAZAKH == value, MessageManager.getMessage(lang, KAZAKH_MSG, null)));
        pw.print(select.addOption(RUSSIAN + "", RUSSIAN == value, MessageManager.getMessage(lang, RUSSIAN_MSG, null)));
        pw.print(select.addOption(ENGLISH + "", ENGLISH == value, MessageManager.getMessage(lang, ENGLISH_MSG, null)));
        if (includeAny)
            pw.print(select.addOption("0", 0 == value, MessageManager.getMessage(lang, LibraryMessages.ANY, null)));
        pw.print(select.endSelect());
    }

    public static String getManualLanguageValue(int lang){
        if (lang == Languages.RUSSIAN){
            return "ru";
        } else if (lang == Languages.ENGLISH){
            return "en";
        } else if (lang == Languages.KAZAKH){
            return "kz";
        }
        return "ru";
    }
}
