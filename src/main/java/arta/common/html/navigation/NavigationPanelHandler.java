package arta.common.html.navigation;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;

import java.io.PrintWriter;


/**
 * Класс печатает панель слева.
 * Панель полная с поиском и кнопками для перемещения между
 * страницами (карточками)
 * Файл - common/navigate.txt
 * TutorsList, SubjectsList, StudentsList, Help, CommonTestReoprtsList, CommonTestReportCard,
 * LessonMaterialsList, TutorTimeTable, GroupsaddList, TestingAddTest  
 * 
 */
public abstract class NavigationPanelHandler extends TemplateHandler {


    public void replace(String name, PrintWriter pw) {
        if (name.equals("img")){
            pw.print(getImage());
        } else if (name.equals("header")){
            pw.print(getHeader());
        } else if (name.equals("search")){
            writeSearch(pw);
        } else if (name.equals("first href")){
            pw.print(getFirstLink());
        } else if (name.equals("first title")){
            pw.print(getFirstTitle());
        } else if (name.equals("previous href")){
            pw.print(getPreviousLink());
        } else if (name.equals("previous title")){
            pw.print(getPreviousTitle());
        } else if (name.equals("next href")){
            pw.print(getNextLink());
        } else if (name.equals("next title")){
            pw.print(getNextTitle());
        } else if (name.equals("last href")){
            pw.print(getLastLink());
        } else if (name.equals("last title")){
            pw.print(getLastTitle());
        } else if (name.equals("info")){
            String[] info = getInfo();
            for (int i = 0; i < info.length; i++){
                if (i > 0)
                    pw.print("<br>");
                pw.print("<img src=\"images/check.gif\" width=\"16px\" height=\"16px\" border=0>");
                pw.print(info[i]);
            }
        } else if (name.equals("navigate title")){
            pw.print(getNavigationTitle());            
        } else if (name.equals("arrows description")){
            String description = getArrowsDescription();
            if (description != null){
                pw.print("<tr>");
                    pw.print("<td class=\"table\" colspan=\"9\" align=\"center\">");
                        pw.print(description);
                    pw.print("</td>");
                pw.print("</tr>");
            }
        }
    }

    public void writeSearch(PrintWriter pw){
        pw.print("<table border=0 width=\"100%\" cellpadding=0 cellspacing=0>");
            pw.print("<tr>");
                pw.print("<td width=\"100%\" style=\"padding-left:8px;\">");
                    pw.print("<input class=\"input\" name=\"search\" value=\""+getSearch()+"\" style=\"width=:100%\">");
               pw.print("</td>");
                pw.print("<td style=\"padding-right:8px;\">");
                    pw.print("<input type=\"submit\" class=\"button\" value=\""+MessageManager.getMessage(getLanguage(),
                    Constants.SEARCH, null)+"\">");
                pw.print("</td>");
            pw.print("<tr>");
            if (getArrowsDescription() != null){
                pw.print("<tr>");
                    pw.print("<td colspan=\"2\" class=\"table\">");
                        getArrowsDescription();
                    pw.print("</td>");
                pw.print("</tr>");
            }
        pw.print("</table>");

    }

    public abstract String getSearch();

    public abstract int getLanguage();

    public abstract String getImage();

    public abstract String getHeader();

    public abstract String[] getInfo();

    public abstract String getFirstLink();

    public abstract String getLastLink();

    public abstract String getPreviousLink();

    public abstract String getNextLink();

    public String getNavigationTitle(){
        return "";
    }

    public String getFirstTitle(){
        return MessageManager.getMessage(getLanguage(), Constants.FIRST, null);
    }

    public String getPreviousTitle(){
        return MessageManager.getMessage(getLanguage(), Constants.PREVIOUS, null);
    }

    public String getNextTitle(){
        return MessageManager.getMessage(getLanguage(), Constants.NEXT, null);
    }

    public String getLastTitle(){
        return MessageManager.getMessage(getLanguage(), Constants.LAST, null);
    }

    public String getArrowsDescription(){
        return null;
    }
}
