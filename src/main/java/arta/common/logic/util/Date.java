package arta.common.logic.util;

import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.timetable.designer.logic.LessonConstants;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Properties;
import java.io.PrintWriter;

public class Date implements Serializable {
    public static final int FROM_DB_CONVERT = 0;
    public static final int FROM_INPUT = 1;

    public int year;
    public int month;
    public int day;


    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date() {
        GregorianCalendar gc = new GregorianCalendar();
        year = gc.get(Calendar.YEAR);
        month = gc.get(Calendar.MONTH) + 1;
        day = gc.get(Calendar.DAY_OF_MONTH);
    }

    public Date(Date date) {
        year = date.year;
        month = date.month;
        day = date.day;
    }

    /**
     * yyyy-m(m)-d(d)
     *
     * @param date
     * @param type
     */
    public Date(String date, int type) {
        GregorianCalendar gc = new GregorianCalendar();
        year = gc.get(Calendar.YEAR);
        month = gc.get(Calendar.MONTH) + 1;
        day = gc.get(Calendar.DAY_OF_MONTH);
        if (date != null)
            parseString(date, type);
    }

    public void loadDate(String date, int type) {
        if (date == null)
            return;
        parseString(date, type);
    }

    /**
     * gets the number of weekday starts by 0  as Monday
     * @return
     */
    public int getWeekDay(){
        GregorianCalendar cal=new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        int d=cal.get(Calendar.DAY_OF_WEEK);
        d -= 1;
        if(d == 0) d = 7;
        return d;
    }

    private void parseString(String date, int type) {

        DataExtractor extractor = new DataExtractor();

        if (type == FROM_DB_CONVERT) {
            if (date.length() != 10)
                return;
            if (date.indexOf("-") > 0) {

                String str = date.substring(0, date.indexOf("-"));
                year = extractor.getYear(str);
                date = date.substring(date.indexOf("-") + 1, date.length());
                if (date.indexOf("-") > 0) {
                    str = date.substring(0, date.indexOf("-"));
                    month = extractor.getDateMonth(str);
                    date = date.substring(date.indexOf("-") + 1, date.length());
                }
                day = extractor.getDateMonth(date);

            }
        } else if (type == FROM_INPUT) {
            if (date.length() != 10)
                return;
            if (date.indexOf("-") > 0) {
                String str = date.substring(0, date.indexOf("-"));
                day = extractor.getDateMonth(str);
                date = date.substring(date.indexOf("-") + 1, date.length());
                if (date.indexOf("-") > 0) {
                    str = date.substring(0, date.indexOf("-"));
                    month = extractor.getDateMonth(str);
                    date = date.substring(date.indexOf("-") + 1, date.length());
                }
                year = extractor.getYear(date);
            }
        }
    }

    /**
     * @return yyyy-mm-dd
     */
    public String getDate() {
        if (year < 1000)
            year = 1000;
        String date = year + "-";
        if (month <= 0 || month > 12)
            month = 1;                                                                                                                                  
        if (month < 10) {
            date += "0" + month + "-";
        } else {
            date += month + "-";
        }

        if (day <= 0)
            day = 1;

        if (day > getNumberOfDaysAMonth())
            day = getNumberOfDaysAMonth();

        if (day < 10) {
            date += "0" + day;
        } else {
            date += day;
        }
        return date;
    }


    /**
     * 1 if this date is larger than anotherDate
     * 0 if they are equal
     * -1 if ths date is smaller than anotherDate
     *
     * @param anotherDate
     * @return int
     */
    public int compareTo(Date anotherDate) {
        if (anotherDate == null) return 0;
        if (anotherDate.year > year)
            return -1;
        if (anotherDate.year < year)
            return 1;
        if (anotherDate.month > month)
            return -1;
        if (anotherDate.month < month)
            return 1;
        if (anotherDate.day > day)
            return -1;
        if (anotherDate.day < day)
            return 1;
        return 0;
    }

    /**
     * compares without year
     *
     * @param anotherDate
     * @return
     */
    public int compare(Date anotherDate) {
        if (anotherDate == null) return 0;
        if (anotherDate.month > month)
            return -1;
        if (anotherDate.month < month)
            return 1;
        if (anotherDate.day > day)
            return -1;
        if (anotherDate.day < day)
            return 1;
        return 0;
    }

    public boolean shortEquals(Date anotherDate){
        if (day!=anotherDate.day || month!=anotherDate.month)
            return false;
        return true;
    }
    /**
     * Возвращает разницу между датами в днях
     * типа Date-anotherDate
     * результат может быть отрицательным
     *
     * @param anotherDate
     * @return int
     */
    public int getDifference(Date anotherDate) {

        int result = 0;

        GregorianCalendar gc = new GregorianCalendar();

        if (year == anotherDate.year) {
            gc.set(year, month - 1, day);
            result = gc.get(Calendar.DAY_OF_YEAR);
            gc.set(anotherDate.year, anotherDate.month - 1, anotherDate.day);
            result -= gc.get(Calendar.DAY_OF_YEAR);
            return result;
        } else {
            if (Math.abs(year - anotherDate.year) > 1) {
                int startYear = Math.min(year, anotherDate.year);
                int endYear = Math.max(year, anotherDate.year);
                for (int i = startYear + 1; i < endYear; i++) {
                    if (i % 4 == 0)
                        result += 366;
                    else
                        result += 365;
                }
            }

            gc.set(year, month - 1, day);
            if (year % 4 == 0)
                result += 366 - gc.get(Calendar.DAY_OF_YEAR);
            else
                result += 365 - gc.get(Calendar.DAY_OF_YEAR);
            gc.set(anotherDate.year, anotherDate.month - 1, anotherDate.day);
            result += gc.get(Calendar.DAY_OF_YEAR);

            if (year < anotherDate.year)
                result = -result;
            return result;

        }


    }

    /**
     * returns time from the brgining of 2003.01.01 int seconds
     */
    public static long getCurrentTime() {
        return new java.util.Date().getTime();
    }

    public void writeDayComboBox(PrintWriter pw, String name) {
        writeDayComboBox(pw, name, null);
    }
    public void writeDayComboBox(PrintWriter pw, String name, String id) {
        Select sel = new Select(Select.SIMPLE_SELECT);
        pw.print(sel.startSelect(name, id));
        for (int i = 1; i < 32; i++) {
            pw.print(sel.addOption(String.valueOf(i), i == day, String.valueOf(i)));
        }
        pw.print(sel.endSelect());
    }

    public void writeMonthComboBox(PrintWriter pw, int language, String name) {
        writeMonthComboBox(pw, language, name, null);
    }

    public void writeMonthComboBox(PrintWriter pw, int language, String name, String id, String function) {
        Select sel = new Select(Select.SIMPLE_SELECT);
        sel.change_function = function;
        pw.print(sel.startSelect(name, id));
        pw.print(sel.addOption(String.valueOf(1), 1 == month, MessageManager.getMessage(language, Constants.JANUARY, null)));
        pw.print(sel.addOption(String.valueOf(2), 2 == month, MessageManager.getMessage(language, Constants.FEBRUARY, null)));
        pw.print(sel.addOption(String.valueOf(3), 3 == month, MessageManager.getMessage(language, Constants.MARCH, null)));
        pw.print(sel.addOption(String.valueOf(4), 4 == month, MessageManager.getMessage(language, Constants.APRIL, null)));
        pw.print(sel.addOption(String.valueOf(5), 5 == month, MessageManager.getMessage(language, Constants.MAY, null)));
        pw.print(sel.addOption(String.valueOf(6), 6 == month, MessageManager.getMessage(language, Constants.JUNE, null)));
        pw.print(sel.addOption(String.valueOf(7), 7 == month, MessageManager.getMessage(language, Constants.JULY, null)));
        pw.print(sel.addOption(String.valueOf(8), 8 == month, MessageManager.getMessage(language, Constants.AUGUST, null)));
        pw.print(sel.addOption(String.valueOf(9), 9 == month, MessageManager.getMessage(language, Constants.SEPTEMBER, null)));
        pw.print(sel.addOption(String.valueOf(10), 10 == month, MessageManager.getMessage(language, Constants.OCTOBER, null)));
        pw.print(sel.addOption(String.valueOf(11), 11 == month, MessageManager.getMessage(language, Constants.NOVEMBER, null)));
        pw.print(sel.addOption(String.valueOf(12), 12 == month, MessageManager.getMessage(language, Constants.DECEMBER, null)));
        pw.print(sel.endSelect());
    }

    public void writeMonthComboBox(PrintWriter pw, int language, String name, String id) {
        writeMonthComboBox(pw, language, name, id, null);
    }

    public void writeYearComboBox(PrintWriter pw, String name, String id, int yearBackward, int yearForward, String function) {
        Select sel = new Select(Select.SIMPLE_SELECT);
        sel.change_function = function;
        pw.println(sel.startSelect(name, id));
        int beg = 0;
        int end = 0;
        beg = year - yearBackward;
        end = year + yearForward;
        for (int i = beg; i < end; i++) {
            pw.println(sel.addOption(String.valueOf(i), i == year, String.valueOf(i)));
        }
        pw.println(sel.endSelect());
    }


    public void writeYearServletSelect(PrintWriter pw, String name, int yearBackward, int yearForward, String link) {
        Select sel = new Select(Select.SERVLET_SELECT);
        pw.println(sel.startSelect(name));
        int beg = 0;
        int end = 0;
        beg = year - yearBackward;
        end = year + yearForward;
        for (int i = beg; i < end; i++) {
            pw.println(sel.addOption(link + "&" + name + "=" + i,
                    i == year, String.valueOf(i)));
        }
        pw.println(sel.endSelect());
    }


    public void writeStudyYearComboBox(PrintWriter pw, String name) {
        Select sel = new Select(Select.SIMPLE_SELECT);
        pw.println(sel.startSelect(name));
        int beg = 0;
        int end = 0;
        Date date = new Date();
        date.loadDate(Date.getCurrentDate(), 0);
        beg = date.year - 20;
        end = date.year + 10;
        for (int i = beg; i < end; i++) {
            pw.println(sel.addOption(String.valueOf(i), i == year, i + "-" + (i + 1)));
        }
        pw.println(sel.endSelect());
    }


    public void writeStudyYearComboBox(PrintWriter pw, String name, int backward, int forward) {
        Select sel = new Select(Select.SIMPLE_SELECT);
        pw.println(sel.startSelect(name));
        int beg = 0;
        int end = 0;
        Date date = new Date();
        date.loadDate(Date.getCurrentDate(), 0);

        for (int i = backward; i < forward; i++) {
            pw.println(sel.addOption(String.valueOf(i), i == year, i + "-" + (i + 1)));
        }
        pw.println(sel.endSelect());
    }

    public static String getCurrentDate() {
        Calendar cal = new GregorianCalendar();
        int y = cal.get(cal.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        String date = "";
        date = date + String.valueOf(y);
        date += "-";
        m++;
        if (m < 10) {
            date += "0";
        }
        date += m;
        date += "-";
        if (d < 10) {
            date += "0";
        }
        date += d;
        return date;
    }

    public String getCurrentMothStart() {
        String date = year + "-";
        if (month < 10) {
            date += "0";
        }
        date += month + "-01";
        return date;
    }

    public String getCurrentMonthFinish() {
        String date = year + "-";
        if (month < 10)
            date += "0";
        date += month + "-" + getNumberOfDaysAMonth();
        return date;
    }

    public int getDay(String date) {
        try {
            return new Integer(date.substring(8, 10)).intValue();
        } catch (Exception exc) {
            return 0;
        }
    }

    public String getStringTimePeriod(Date start, Date end, int lang) {
        if (start.getDifference(end) == 0) {
            return getMonthName(start.month, start.day + "", lang);
        }
        if (start.month == end.month) {
            String days = start.day + "-" + end.day;
            return getMonthName(start.month, days, lang);
        }
        return getMonthName(start.month, start.day + "", lang) + "-" + getMonthName(end.month, end.day + "", lang);
    }

    public String getMonthName(int monthNumber, String days, int lang) {
        Properties properties = new Properties();
        properties.setProperty("day", days);
        if (monthNumber == 1) {
            return MessageManager.getMessage(lang, LessonConstants.JANUARY_WITH_DAYS, properties);
        } else if (monthNumber == 2) {
            return MessageManager.getMessage(lang, LessonConstants.FEBRUARY_WITH_DAYS, properties);
        } else if (monthNumber == 3) {
            return MessageManager.getMessage(lang, LessonConstants.MARCH_WITH_DAYS , properties);
        } else if (monthNumber == 4) {
            return MessageManager.getMessage(lang, LessonConstants.APRIL_WITH_DAYS, properties);
        } else if (monthNumber == 5) {
            return MessageManager.getMessage(lang, LessonConstants.MAY_WITH_DAYS, properties);
        } else if (monthNumber == 6) {
            return MessageManager.getMessage(lang, LessonConstants.JUNE_WITH_DAYS, properties);
        } else if (monthNumber == 7) {
            return MessageManager.getMessage(lang, LessonConstants.JULY_WITH_DAYS, properties);
        } else if (monthNumber == 8) {
            return MessageManager.getMessage(lang, LessonConstants.AUGUST_WITH_DAYS, properties);
        } else if (monthNumber == 9) {
            return MessageManager.getMessage(lang, LessonConstants.SEPTEMBER_WITH_DAYS, properties);
        } else if (monthNumber == 10) {
            return MessageManager.getMessage(lang, LessonConstants.OCTOBER_WITH_DAYS, properties);
        } else if (monthNumber == 11) {
            return MessageManager.getMessage(lang, LessonConstants.NOVEMBER_WITH_DAYS, properties);
        } else if (monthNumber == 12) {
            return MessageManager.getMessage(lang, LessonConstants.DECEMBER_WITH_DAYS, properties);
        }
        return "";
    }

    public String getStringDateValue(int lang) {
        return getMonthName(month, day + "", lang);
    }

    public int getWeeksCount(Date start, Date end) {
        return (int) end.getDifference(start) / 7 + 1;
    }

    /**
     * start with 1
     */
    public int getDayfWeekNumber() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(year, month - 1, day);
        if (gc.get(Calendar.DAY_OF_WEEK) == 1)
            return 7;
        return gc.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public void writeServletYearSelect(PrintWriter pw, int width, String link, String template, int yearNumber) {
        Select select = new Select(Select.SERVLET_SELECT);
        select.width = width;
        pw.print(select.startSelect(""));
        int beg = 0;
        int end = 0;
        Date date = new Date();
        date.loadDate(Date.getCurrentDate(), 0);
        beg = date.year - 80;
        end = date.year + 10;
        for (int i = beg; i < end; i++) {
            pw.println(select.addOption(replace(link, template, i), i == year, String.valueOf(i)));
        }
        pw.print(select.endSelect());
    }

    public void writeServletYearSelect(PrintWriter pw, int width, String link, String template) {
        Select select = new Select(Select.SERVLET_SELECT);
        select.width = width;
        pw.print(select.startSelect(""));
        int beg = 0;
        int end = 0;
        Date date = new Date();
        date.loadDate(Date.getCurrentDate(), 0);
        beg = date.year - 80;
        end = date.year + 10;
        for (int i = beg; i < end; i++) {
            pw.println(select.addOption(replace(link, template, i), i == year, String.valueOf(i)));
        }
        pw.print(select.endSelect());
    }

    public void writePostYearSelect(PrintWriter pw, int width, String name, int yearBackward, int yearForward) {
        Select select = new Select(Select.POST_SELECT);
        select.width = width;
        pw.print(select.startSelect(name));
        int beg = 0;
        int end = 0;
        Date date = new Date();
        date.loadDate(Date.getCurrentDate(), 0);
        beg = date.year - yearBackward;
        end = date.year + yearForward;
        for (int i = beg; i < end; i++) {
            pw.print(select.addOption(i + "", i == year, i + ""));
        }
        pw.print(select.endSelect());
    }


    public void writeMonthServletSelect(PrintWriter pw, int language, String name, int width, String link) {
        Select sel = new Select(Select.SERVLET_SELECT);
        sel.width = width;        
        pw.println(sel.startSelect(name));
        pw.println(sel.addOption(link + "&" + name + "=" + 1, 1 == month, MessageManager.getMessage(language, Constants.JANUARY, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 2, 2 == month, MessageManager.getMessage(language, Constants.FEBRUARY, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 3, 3 == month, MessageManager.getMessage(language, Constants.MARCH, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 4, 4 == month, MessageManager.getMessage(language, Constants.APRIL, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 5, 5 == month, MessageManager.getMessage(language, Constants.MAY, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 6, 6 == month, MessageManager.getMessage(language, Constants.JUNE, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 7, 7 == month, MessageManager.getMessage(language, Constants.JULY, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 8, 8 == month, MessageManager.getMessage(language, Constants.AUGUST, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 9, 9 == month, MessageManager.getMessage(language, Constants.SEPTEMBER, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 10, 10 == month, MessageManager.getMessage(language, Constants.OCTOBER, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 11, 11 == month, MessageManager.getMessage(language, Constants.NOVEMBER, null)));
        pw.println(sel.addOption(link + "&" + name + "=" + 12, 12 == month, MessageManager.getMessage(language, Constants.DECEMBER, null)));
        pw.println(sel.endSelect());
    }

    public void writeDayPostSelect(PrintWriter pw, String name, int width, String formName) {
        Select sel = new Select(Select.POST_SELECT);
        sel.width = width;
        sel.formName = formName;
        pw.println(sel.startSelect(name));
        for (int i = 1; i < 32; i++) {
            pw.println(sel.addOption(String.valueOf(i), i == day, String.valueOf(i)));
        }
        pw.println(sel.endSelect());
    }

    public void writeMonthSelectServlet(PrintWriter pw, int width, String link, String template, int language) {
        Select select = new Select(Select.SERVLET_SELECT);
        select.width = width;
        pw.print(select.startSelect(""));
    /*    pw.println(select.addOption(replace(link, template, 1), 1 == month, MessageManager.getMessage(language, CommonConstants.JANUARY, null)));
        pw.println(select.addOption(replace(link, template, 2), 2 == month, MessageManager.getMessage(language, CommonConstants.FEBRUARY, null)));
        pw.println(select.addOption(replace(link, template, 3), 3 == month, MessageManager.getMessage(language, CommonConstants.MARCH, null)));
        pw.println(select.addOption(replace(link, template, 4), 4 == month, MessageManager.getMessage(language, CommonConstants.APRIL, null)));
        pw.println(select.addOption(replace(link, template, 5), 5 == month, MessageManager.getMessage(language, CommonConstants.MAY, null)));
        pw.println(select.addOption(replace(link, template, 6), 6 == month, MessageManager.getMessage(language, CommonConstants.JUNE, null)));
        pw.println(select.addOption(replace(link, template, 7), 7 == month, MessageManager.getMessage(language, CommonConstants.JULY, null)));
        pw.println(select.addOption(replace(link, template, 8), 8 == month, MessageManager.getMessage(language, CommonConstants.AUGUST, null)));
        pw.println(select.addOption(replace(link, template, 9), 9 == month, MessageManager.getMessage(language, CommonConstants.SEPTEMBER, null)));
        pw.println(select.addOption(replace(link, template, 10), 10 == month, MessageManager.getMessage(language, CommonConstants.OCTOBER, null)));
        pw.println(select.addOption(replace(link, template, 11), 11 == month, MessageManager.getMessage(language, CommonConstants.NOBERNBER, null)));
        pw.println(select.addOption(replace(link, template, 12), 12 == month, MessageManager.getMessage(language, CommonConstants.DECEMBER, null)));
        */
        pw.println(select.endSelect());
    }

    private String replace(String link, String template, int number) {
        if (link == null || template == null)
            return "";
        return link.replaceAll(template, number + "");
    }

    public static boolean isValidDate(String str) {
        if (str == null)
            return false;
        if (str.length() != 10)
            return false;
        if (!str.substring(4, 5).equals("-"))
            return false;
        if (!str.substring(7, 8).equals("-"))
            return false;
        try {
            new Integer(str.substring(0, 4));
            new Integer(str.substring(5, 7));
            new Integer(str.substring(8, 10));
        } catch (Exception exc) {
            return false;
        }
        if (new Integer(str.substring(0, 4)) < 1000)
            return false;
        return true;
    }

    public String getInputValue() {
        String result = "";
        if (day < 10)
            result += "0";
        result += day + "-";
        if (month < 10)
            result += "0";
        result += month + "-";
        result += year;
        return result;
    }

    /**
     *
     * @return
     */
    public static synchronized long getCurrentTimeInMills() {
        return new java.util.Date().getTime();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (((Date) obj).year == year && ((Date) obj).month == month && ((Date) obj).day == day)
            return true;
        return false;
    }

    public static String getMonthName(int month, int lang) {
        if (month == 1) {
            return MessageManager.getMessage(lang, Constants.JANUARY , null);
        } else if (month == 2) {
            return MessageManager.getMessage(lang, Constants.FEBRUARY, null);
        } else if (month == 3) {
            return MessageManager.getMessage(lang, Constants.MARCH, null);
        } else if (month == 4) {
            return MessageManager.getMessage(lang, Constants.APRIL, null);
        } else if (month == 5) {
            return MessageManager.getMessage(lang, Constants.MAY, null);
        } else if (month == 6) {
            return MessageManager.getMessage(lang, Constants.JUNE, null);
        } else if (month == 7) {
            return MessageManager.getMessage(lang, Constants.JULY, null);
        } else if (month == 8) {
            return MessageManager.getMessage(lang, Constants.AUGUST, null);
        } else if (month == 9) {
            return MessageManager.getMessage(lang, Constants.SEPTEMBER, null);
        } else if (month == 10) {
            return MessageManager.getMessage(lang, Constants.OCTOBER, null);
        } else if (month == 11) {
            return MessageManager.getMessage(lang, Constants.NOVEMBER, null);
        } else if (month == 12) {
            return MessageManager.getMessage(lang, Constants.DECEMBER, null);
        }
        return "";
    }

    /**
     * Добавляет к дате данное количество дней
     *
     * @param daysCount
     */
    public void addDays(int daysCount) {
        GregorianCalendar gr = new GregorianCalendar(year, month - 1, day);
        gr.add(Calendar.DAY_OF_MONTH, daysCount);
        year = gr.get(Calendar.YEAR);
        month = gr.get(Calendar.MONTH);
        day = gr.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Добавляет к дате данное количество месяцев
     *
     * @param monthsCount
     */
    public void addMonths(int monthsCount) {
        GregorianCalendar gr = new GregorianCalendar(year, month - 1, day);
        gr.add(Calendar.MONTH, monthsCount);
        year = gr.get(Calendar.YEAR);
        month = gr.get(Calendar.MONTH);
        day = gr.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayNumber(){
        GregorianCalendar calendar = new GregorianCalendar();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == 1)
            return 7;
        return day - 1;
    }

    public int getFirstDayOfMonthNumber(int dayOfWeek){
        if (dayOfWeek == 7)
            dayOfWeek = 1;
        else dayOfWeek ++ ;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.MONTH,     month-1);
        calendar.set(Calendar.YEAR, year);
        for(int i=1; i<7; i++){
            calendar.set(Calendar.DAY_OF_MONTH, i);
            if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek )
                return i;
        }
        return 0;
    }


    public int getNumberOfDaysAMonth(){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, month-1, day);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public String getFullDayValue(){
        if (day < 10)
            return "0" + day;
        return day + "";
    }

}
