package arta.common.logic.util;

import arta.common.logic.messages.MessageManager;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 12.03.2008
 * Time: 17:24:33
 * To change this template use File | Settings | File Templates.
 */
public class Time {

    public int hour;
    public int minute;
    public int second;

    public Time(int hour, int minute) {
        GregorianCalendar calendar = new GregorianCalendar();
        if (hour>=0 && hour<24){
            this.hour = hour;
        } else {
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (minute>=0 && minute<60){
            this.minute = minute;
        } else {
            minute = calendar.get(Calendar.MINUTE);
        }
    }

    public Time() {
        GregorianCalendar calendar = new GregorianCalendar();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
    }

    public Time(String time){
        if (time == null || time.length() != 8){
            GregorianCalendar calendar = new GregorianCalendar();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            second = calendar.get(Calendar.SECOND);
        } else {
            GregorianCalendar calendar = new GregorianCalendar();
            String h = time.substring(0, 2);
            String m = time.substring(3, 5);
            String s = time.substring(6, 8);
            DataExtractor getter = new DataExtractor();
            hour = getter.getInteger(h);
            minute = getter.getInteger(m);
            second = getter.getInteger(s);
            if (hour < 0 || hour > 23)
                hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (minute < 0 || minute > 59)
                minute = calendar.get(Calendar.MINUTE);
            if (second < 0 || second > 59)
                second = calendar.get(Calendar.SECOND);
        }
    }


    public void writeHourSelect(PrintWriter pw, String name){
        pw.println("<SELECT width=\"35px\" style=\"text-align:center; font-family:tahoma\" " +
                " name=\""+name+"\" class=\"select\">");
        for (int i=0; i<24; i++){
            String selected = "";
            if (i == hour)
                selected = "selected";
            pw.println("<OPTION value=\""+i+"\" "+selected+" class=\"option\">"+i+"</OPTION>");
        }
        pw.print("</SELECT>");
    }

    public void writeMinuteSelect(PrintWriter pw, String name){
        pw.println("<SELECT width=\"35px\" style=\"text-align:center; font-family:tahoma\" " +
                " name=\""+name+"\" class=\"select\">");
        for (int i=0; i<60; i+=5 ){
            String selected = "";
            if (i == minute)
                selected = "selected";
            pw.println("<OPTION value=\""+i+"\" "+selected+" class=\"option\">"+i+"</OPTION>");
        }
        pw.print("</SELECT>");
    }

    public String getDBValue(){
        String result = "";
        if (hour<0 || hour>=24)
            hour = 0;
        if (hour < 10)
            result += "0";
        result += hour+":";
        if (minute < 0 || minute >= 60)
            minute = 0;
        if (minute < 10)
            result += "0";
        result += minute;
        result += ":";
        if (second<10)
            result += "0";
        result += second;
        return result;
    }

    public void loadDBTime(String str){
        DataExtractor integerGetter = new DataExtractor();
        if (str == null || str.length()<8)
            return;
        hour  = integerGetter.getInteger(str.substring(0, 2));
        minute = integerGetter.getInteger(str.substring(3, 5));
        second = integerGetter.getInteger(str.substring(6, 8));
    }

    public int getMinuteValue(){
        return hour*60 + minute;
    }

    /**
     *
     * @return hh: mm
     */
    public String getValue(){
        String result = "";
        if (hour < 10)
            result += "0";
        result += hour;
        result += ":";
        if (minute < 10)
            result += "0";
        result += minute;
        return result;
    }

    /**
     * Substracts another time value from this time value
     * @param anotherTime
     * @return difference in minutes
     */
    public int subjsract(Time anotherTime){
        return getMinuteValue() - anotherTime.getMinuteValue();
    }

    public void writeTimeSelects(int lang, PrintWriter pw, String hourName, String minName){
        pw.print("<table border=0>");
            pw.print("<tr>");
                pw.print("<td class=\"td\">");
                    writeHourSelect(pw, hourName);
                pw.print("</td>");
                pw.print("<td class=\"td\">");
                    pw.print(MessageManager.getMessage(lang, Constants.HOUR_SHORT));
                pw.print("</td>");
                pw.print("<td class=\"td\">");
                    writeMinuteSelect(pw, minName);
                pw.print("</td>");
                pw.print("<td class=\"td\">");
                    pw.print(MessageManager.getMessage(lang, Constants.MINUTE_SHORT));
                pw.print("</td>");
            pw.print("</tr>");
        pw.print("</table>");
    }

}

