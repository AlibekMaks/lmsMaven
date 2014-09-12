package arta.common.html.menu;

import java.util.ArrayList;
import java.io.PrintWriter;


public class HTMLMenu {

    private String title = null;

    private ArrayList <HTMLMenuItem> items = new ArrayList <HTMLMenuItem> ();

    public HTMLMenu(String title) {
        this.title = title;
    }

    public void addItem(HTMLMenuItem item){
        items.add(item);
    }

    public HTMLMenuItem getItem(int index){
        return items.get(index);
    }

    public int getItemsCount(){
        return items.size();
    }

    public String getTitle(){
        return title;
    }

    public void writeHTML(PrintWriter pw){

        pw.print("<tr>");
            pw.print("<td>");
                pw.print("<table border=0 width=\"100%\" cellpadding=0 cellspacing=0 " +
                        " style=\"border-style:none;border-collapse:collapse\">");
                    pw.print("<tr>");
                        pw.print("<td width=* class=\"menuHeader\" >");
                            pw.print(getTitle());
                        pw.print("</td>");
                    pw.print("</tr>");
                pw.print("</table>");
            pw.print("</td>");
        pw.print("</tr>");

        pw.print("<tr>");
            pw.print("<td>");
                pw.print("<table border=0 width=\"100%\" cellpadding=0 cellspacing=0" +
                        "  style=\"border-style:none;border-collapse:collapse\" >");
                    for (int i = 0; i < items.size(); i ++){
                        pw.print("<tr><td onmouseover='mouseEntered(this)' onmouseout='mouseExited(this)'  " +
                            " style=\"background-repeat:no-repeat; cursor:pointer;\" " +
                            " "+items.get(i).getOnClick()+" >");
                        pw.print(items.get(i).getHTML());
                        pw.print("</td></tr>");
                    }
                pw.print("</table>");
            pw.print("</td>");
        pw.print("</tr>");

    }

    public void writeHorizontalMenu(PrintWriter pw){

        pw.print("<table border=0 class=\"commonPageTable\">");
            pw.print("<tr>");
                for (int i = 0; i < items.size(); i ++){
                    pw.print(items.get(i).getHorizontalHTML(i == 0));
                }
            pw.print("</tr>");
        pw.print("</table>");

    }

}
