package arta.registrar.tutor.logic;

import arta.common.logic.util.Languages;


public class RegistrarMessages {

    public static final int REGISTRAR_HEADER = 196;
    public static final int REGISTRAR = 204;
    public static final int GROUPS_LIST = 205;

    public static final String ABSENT_RU = "н";
    public static final String ABSENT_KZ = "ж";
    public static final String ABSENT_EN = "a";


    public static final int STUDENT_JOURNAL_HEADER =207;

    public static final int CONFIRM_DELETE_MARK  = 439;

    public static final int MARK_HAS_BEEN_DELETED  = 440;

    public static final int MARK_HAS_NOT_BEEN_DELETED  = 441;

    public static final int NEXT_MONTH  = 457;

    public static final int PREVIOUS_MONTH  = 456;


    public static String getAbsent(int lang){
        if (lang == Languages.RUSSIAN)
            return ABSENT_RU;
        if (lang == Languages.KAZAKH)
            return ABSENT_KZ;
        if (lang == Languages.ENGLISH)
            return ABSENT_EN;
        return ABSENT_RU;
    }

}
