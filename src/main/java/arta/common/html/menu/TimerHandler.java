package arta.common.html.menu;

import arta.common.logic.util.WeekDays;
import arta.common.logic.util.Date;
import arta.common.logic.util.Time;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 12.03.2008
 * Time: 17:18:05
 * To change this template use File | Settings | File Templates.
 */
public class TimerHandler {
    Date date = new Date();
    Time time = new Time();

    int lang;


    public TimerHandler(int lang) {
        this.lang = lang;
    }


    public void write(PrintWriter pw){
        String hour = time.hour + "";
        if (time.hour < 10)
            hour = "0" + hour;
        String minute = time.minute + "";
        if (time.minute < 10)
            minute = "0" + minute;
        pw.print("<script language=\"javascript\"> " +
                " setInterval('addMinute()', 60000);\n" +
                " function addMinute(){" +
                " var h =document.getElementById(\"hour\").innerHTML * 1;\n" +
                " var m = document.getElementById(\"minute\").innerHTML*1;\n" +
                " m ++;\n" +
                " if (m == 60){\n" +
                " m = 0;\n" +
                " h ++;\n" +
                " }\n" +
                " if (h == 24)\n" +
                " h = 0;" +
                "    if (h < 10)" +
                "       document.getElementById(\"hour\").innerHTML = \"0\" + h;" +
                "   else" +
                "       document.getElementById(\"hour\").innerHTML = h;" +
                "   if (m < 10)" +
                "       document.getElementById(\"minute\").innerHTML = \"0\" + m;" +
                "   else " +
                "       document.getElementById(\"minute\").innerHTML = m;" +
                " }\n" +
                "</script>\n" +
                "<table border=0 cellspacing=0 cellpadding=0>\n" +
                " <tr>\n" +
                " <td width=\"100px\" align=\"left\">\n" +
                "\t\t\t<table border=0  style=\"padding-left:2px; padding-right:2px; padding-top:0px; padding-bottom:0px;\" cellspacing=0>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td id=\"hour\" class=\"time\">\n" +
                "\t\t\t\t\t\t"+hour+"\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td class=\"time\">\n" +
                "\t\t\t\t\t\t:\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td id=\"minute\" class=\"time\">\n" +
                "\t\t\t\t\t\t"+minute+"\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t</table>\n" +
                "\t\t</td>" +
                "<td width=\"5px\">" +
                "</td>\n" +
                "\t\t<td>\n" +
                "\t\t\t<table border=0 >\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td class=\"date\" nowrap>\n" +
                "\t\t\t\t\t\t"+ WeekDays.getWeekDaytName(date.getWeekDay(), lang)+"\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td class=\"date\" nowrap>\n" +
                "\t\t\t\t\t\t"+ date.getStringDateValue(lang) +"\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t</table>\n" +
                "\t\t</td>\n" +
                "\t</tr>\n" +
                "</table>");
    }
}
