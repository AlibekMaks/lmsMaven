package arta.common.html.util;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Date;

import java.io.PrintWriter;


public class HTMLCalendar {
    private int lang;
    private int yearSelectStartOffset = 4;

    public HTMLCalendar(int lang) {
        this.lang = lang;
    }

    public void initializeCalendar(PrintWriter pw){

        pw.println("<script language=\"javaScript\">");
        //pw.println("var cal = new CalendarPopup(\"calendarDiv\");");
        pw.println("var cal = new CalendarPopup();");
        pw.println("cal.setTodayText(\""+ MessageManager.getMessage(lang, Constants.TODAY, null)+"\");");
        pw.println("cal.setWeekStartDay(1);");
        pw.println("cal.setYearSelectStartOffset("+yearSelectStartOffset+");");
        pw.println("cal.showNavigationDropdowns();");
        pw.println("cal.setMonthNames(\""+MessageManager.getMessage(lang, Constants.JANUARY, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.FEBRUARY, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.MARCH, null)+"\"," +
                " \""+MessageManager.getMessage(lang, Constants.APRIL, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.MAY, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.JUNE, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.JULY, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.AUGUST, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.SEPTEMBER, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.OCTOBER, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.NOVEMBER, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.DECEMBER, null)+"\" );");

        pw.println("cal.setMonthAbbreviations(\""+MessageManager.getMessage(lang, Constants.JANUARY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.FEBRUARY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.MARCH_SHORT, null)+"\"," +
                " \""+MessageManager.getMessage(lang, Constants.APRIL_SHORT, null)+"\" , " +
                " \""+MessageManager.getMessage(lang, Constants.MAY_SHORT, null)+"\" , " +
                " \""+MessageManager.getMessage(lang, Constants.JUNE_SHORT, null)+"\" , " +
                " \""+MessageManager.getMessage(lang, Constants.JULY_SHORT, null)+"\" , " +
                " \""+MessageManager.getMessage(lang, Constants.AUGUST_SHORT, null)+"\" , " +
                " \""+MessageManager.getMessage(lang, Constants.SEPTEMBER_SHORT, null)+"\" ,  " +
                " \""+MessageManager.getMessage(lang, Constants.OCTOBER_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.NOVEMBER_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.DECEMBER_SHORT, null)+"\");");

        pw.println("cal.setDayHeaders( " +
                " \""+MessageManager.getMessage(lang, Constants.MONDAY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.TUESDAY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.WEDNSDAY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.THUSDAY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.FRIDAY_SHORT, null)+"\", " +
                " \""+MessageManager.getMessage(lang, Constants.SATURDAY_SHORT, null)+"\" , "+
                " \""+MessageManager.getMessage(lang, Constants.SUNDAY_SHORT, null)+"\"); ");

        pw.println("</script>");
    }

    public void writeDIV(PrintWriter pw){
     /*   pw.println("<DIV id=\"calendarDiv\"");
        pw.println("style=\"VISIBILITY: hidden; POSITION: absolute; BACKGROUND-COLOR: white; layer-background-color: white\">");
        pw.println("</DIV>");      */
    }

    public void printInput(PrintWriter pw, Date date, String inputName, String formName){
        input(pw, date, inputName, formName, true);
    }

    public void printDisabledInput(PrintWriter pw, Date date, String inputName, String formName){
        input(pw, date, inputName, formName, false);
    }

    public void printInput(PrintWriter pw, Date date, String inputName, String formName, boolean enabled){
        input(pw, date, inputName, formName, enabled);
    }

    private void input(PrintWriter pw, Date date, String inputName, String formName, boolean enabled){
        if (date == null)
            date = new Date();
        pw.println("<table border=0 cellpadding=0 cellspacing=0><tr><td valign=\"middle\">");
        pw.print("<INPUT class=\"input\" name=\""+inputName+"\" id=\""+inputName+"\"");
        if (!enabled)
            pw.print(" disabled ");
        pw.print(" style=\"width:80px\" value=\""+date.getInputValue()+"\"></td>");
        pw.print("<td valign=\"middle\"><input name=\""+inputName+"Select\" ");
        if (!enabled)
            pw.print(" disabled ");
        pw.print("style=\"width:15px; height:20px\" type=\"button\" id=\"anchor"+inputName+"\" ");
        pw.print(" name=\"anchor"+inputName+"\" ");
        pw.print(" onclick=\"cal.select(document."+formName+"."+inputName+", 'anchor"+inputName+"','dd-MM-yyyy'); return false;\" ");
        pw.print(" value=\"...\" />");
        pw.print("</td></tr></table>");
    }

    public static String getScriptSRC(){
        return "<script language=\"javascript\" src=\"jscripts\\PopupWindow.js\"></script>\n" +
                "<script language=\"javascript\" src=\"jscripts\\AnchorPosition.js\"></script>\n" +
                "<script language=\"javascript\" src=\"jscripts\\date.js\"></script>\n" +
                "<script language=\"javascript\" src=\"jscripts\\calendar.js\"></script>\n" +
                "<script language=JavaScript>document.write(getCalendarStyles());</script>";
    }

    public void setYearSelectStartOffset(int yearsCount){
           yearSelectStartOffset = yearsCount;
    }
}
