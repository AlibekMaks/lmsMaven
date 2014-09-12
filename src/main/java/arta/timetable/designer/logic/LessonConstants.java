package arta.timetable.designer.logic;

import arta.common.logic.messages.MessageManager;


public class LessonConstants {

    public static final int COMMON = 1;
    public static final int SHORTENED = 2;

    public static final int MONDAY = 1;
    public static final int TUESDAY= 2;
    public static final int WEDNSDAY= 3;
    public static final int THURSDAY= 4;
    public static final int FRIDAY= 5;
    public static final int SATURDAY= 6;
    public static final int SUNDAY = 7;

    public static int BELLS_SCHEDULER = 139;
    public static int SHOERTENED_DAY = 140;
    public static int BELLS_SCHEDULER_ON_FULL_DAY = 152;

    public static final int MONDAY_MSG = 141;
    public static final int TUESDAY_MSG = 142;
    public static final int WEDNESDAY_MSG = 143;
    public static final int THURSDAY_MSG = 144;
    public static final int FRIDAY_MSG = 145;
    public static final int SATURDAY_MSG = 146;

    public static final int MONDAY_MSG_SHORT = 197;
    public static final int TUESDAY_MSG_SHORT = 198;
    public static final int WEDNESDAY_MSG_SHORT = 199;
    public static final int THURSDAY_MSG_SHORT = 200;
    public static final int FRIDAY_MSG_SHORT = 201;
    public static final int SATURDAY_MSG_SHORT = 202;
    public static final int SUNDAY_MSG_SHORT = 203;

    public static final int CLASS_ROOM = 153;
    public static final int SCHEDULE_EDITTOR = 154;
    public static final int ADDING_LESSON = 155;
    public static final int ENTER_CLASS_ROOM_NUMBER = 156;
    public static final int SUBGROUP_NUMBER = 157;
    public static final int SITTING = 158;
    public static final int TIME_TABLE = 160;

    public static final int JANUARY_WITH_DAYS = 161;
    public static final int FEBRUARY_WITH_DAYS = 162;
    public static final int MARCH_WITH_DAYS = 163;
    public static final int APRIL_WITH_DAYS = 164;
    public static final int MAY_WITH_DAYS = 165;
    public static final int JUNE_WITH_DAYS = 166;
    public static final int JULY_WITH_DAYS = 167;
    public static final int AUGUST_WITH_DAYS = 168;
    public static final int SEPTEMBER_WITH_DAYS = 169;
    public static final int OCTOBER_WITH_DAYS = 170;
    public static final int NOVEMBER_WITH_DAYS = 171;
    public static final int DECEMBER_WITH_DAYS = 172;

    public static final int STUDY_MATERIALS = 173;
    public static final int EDIT_STUDY_MATERIAL = 176;

    public static final int LESSON_MATERIAL_IS_USED_CANNOT_BE_DELETED = 239;
    public static final int CONFIRM_DELETE_STUDY_MATERIAL = 242;


    public static String getDayOfWeeShortkName(int dayNumber, int lang){
        if (dayNumber == MONDAY){
            return MessageManager.getMessage(lang, MONDAY_MSG_SHORT, null);
        } else if (dayNumber == TUESDAY){
            return MessageManager.getMessage(lang, TUESDAY_MSG_SHORT, null);
        } else if (dayNumber == WEDNSDAY){
            return MessageManager.getMessage(lang, WEDNESDAY_MSG_SHORT, null);
        } else if (dayNumber == THURSDAY){
            return MessageManager.getMessage(lang, THURSDAY_MSG_SHORT, null);
        } else if (dayNumber == FRIDAY){
            return MessageManager.getMessage(lang, FRIDAY_MSG_SHORT, null);
        } else if (dayNumber == SATURDAY){
            return MessageManager.getMessage(lang, SATURDAY_MSG_SHORT, null);
        } else if (dayNumber == SUNDAY){
            return MessageManager.getMessage(lang, SUNDAY_MSG_SHORT, null);
        }
        return "";
    }

    public static String getDayOfWeekName(int dayNumber, int lang){
        if (dayNumber == MONDAY){
            return MessageManager.getMessage(lang, MONDAY_MSG, null);
        } else if (dayNumber == TUESDAY){
            return MessageManager.getMessage(lang, TUESDAY_MSG, null);
        } else if (dayNumber == WEDNSDAY){
            return MessageManager.getMessage(lang, WEDNESDAY_MSG, null);
        } else if (dayNumber == THURSDAY){
            return MessageManager.getMessage(lang, THURSDAY_MSG, null);
        } else if (dayNumber == FRIDAY){
            return MessageManager.getMessage(lang, FRIDAY_MSG, null);
        } else if (dayNumber == SATURDAY){
            return MessageManager.getMessage(lang, SATURDAY_MSG, null);
        }
        return "";
    }

}
