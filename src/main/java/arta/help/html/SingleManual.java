package arta.help.html;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.help.HelpMessages;

import java.io.PrintWriter;

public class SingleManual {


    public void writeTutorManual(PrintWriter pw, int lang){               
        String langValue = Languages.getManualLanguageValue(lang);
        pw.print("<table border=0 width=\"100%\" border=0 class=\"table\" >");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/tutor/1."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_1_FILECABINET, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_1_FILECABINET, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/tutor/2."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_2_STUDYPCLASSES, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_2_STUDYPCLASSES, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/tutor/3."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_3_STUDYPROCESS, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_3_STUDYPROCESS, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/tutor/4."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_4_TESTING, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_4_TESTING, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/tutor/5."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_5_SETTINGS, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.TUTOR_MANUAL_5_SETTINGS, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("</table>");
    }

    public void writeStudentManual(PrintWriter pw, int lang){
        pw.print("<table border=0 width=\"100%\" border=0 class=\"table\">");
        String langValue = Languages.getManualLanguageValue(lang);
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/student/1."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.STUDENT_MANUAL_1_STUDYPOCESS, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.STUDENT_MANUAL_1_STUDYPOCESS, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("<tr>");
            pw.print("<td width=\"100%\">");
                pw.print("<a href=\"manual/student/2."+langValue+".htm\" target=\"content\" title=\""+
                        MessageManager.getMessage(lang, HelpMessages.STUDENT_MANUAL_2_STUDYROOM, null) +
                        "\" class=\"href\">");
                pw.print(MessageManager.getMessage(lang, HelpMessages.STUDENT_MANUAL_2_STUDYROOM, null));
                pw.print("</a>");
            pw.print("</td>");
        pw.print("</tr>");
        pw.print("</table>");
    }

}
