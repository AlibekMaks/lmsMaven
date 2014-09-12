package arta.common.logic.util;

import arta.common.logic.messages.MessageManager;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 12.03.2008
 * Time: 17:20:12
 * To change this template use File | Settings | File Templates.
 */
public class WeekDays {

    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNSDAY = 3;
    public static final int THIRSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;
    public static final int SUNDAY = 7;

    public String getWeekDayFullName(int dayNumber){
        if (dayNumber == MONDAY){

        }
        return "";
    }

    public String getWeekDayShortName(int dayNumber, int lang){
        if (dayNumber == MONDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.MONDAY_SHORT, null);
        } else if (dayNumber == TUESDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.TUESDYA_SHORT, null);
        } else if (dayNumber == WEDNSDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.WEDNSDAY_SHORT, null);
        } else if (dayNumber == THIRSDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.THUSDAY_SHORT, null);
        } else if (dayNumber == FRIDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.FRIDAY_SHORT, null);
        } else if (dayNumber == SATURDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.SATURDAY_SHORT, null);
        } else if (dayNumber == SUNDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.SUBDAY_SHORT, null);
        }
        return "";
    }

    public static String getWeekDaytName(int dayNumber, int lang){
        if (dayNumber == MONDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.MONDAY_FULL, null);
        } else if (dayNumber == TUESDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.TUESDYA_FULL, null);
        } else if (dayNumber == WEDNSDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.WEDNSDAY_FULL, null);
        } else if (dayNumber == THIRSDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.THUSDAY_FULL, null);
        } else if (dayNumber == FRIDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.FRIDAY_FULL, null);
        } else if (dayNumber == SATURDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.SATURDAY_FULL, null);
        } else if (dayNumber == SUNDAY){
            return MessageManager.getMessage(lang, WeekDayMessages.SUBDAY_FULL, null);
        }
        return "";
    }


}
