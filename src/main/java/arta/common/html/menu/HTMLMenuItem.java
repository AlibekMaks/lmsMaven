package arta.common.html.menu;

import arta.common.logic.util.Rand;


public class HTMLMenuItem {

    private String title = null;
    private String icon = null;
    private String link = null;
    private String description = null;
    private boolean selected = true;

    public static final String DOCUMENT_TOP = "document.top";
    public static final String BLANK_WINDOW = "_blank";

    private String target;

    public void setEnabled(boolean selected) {
        this.selected = selected;
    }

    public HTMLMenuItem() {
    }

    public HTMLMenuItem(String title, String icon, String link, boolean selected, String description) {
        this.title = title;
        this.icon = icon;
        this.selected = selected;
        this.link = link;
        this.description = description;
    }

    public HTMLMenuItem(String title, String icon, String link, boolean selected, String description, String target) {
        this.title = title;
        this.icon = icon;
        this.selected = selected;
        this.link = link;
        this.description = description;
        this.target = target;
    }

    public String getIcon() {
        return icon;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getRandomizedLink(){
        String tmp = "";
        String newLink = "";
        if (link.indexOf("?") >= 0){
            newLink =  link + "&" + Rand.getRandString() + "";
        } else{
            newLink = link + "?" + Rand.getRandString();
        }
        tmp = " href=\"" + newLink + "\" ";
        if (target != null){
            if (target.equals(DOCUMENT_TOP)){
                tmp += " onclick='top.location.href=\""+newLink+"\"; return false;' ";
            } else {
                tmp += " target=\""+target+"\" ";
            }
        }

        return tmp;
    }

    public String getOnClick(){
        String tmp = "";
        String newLink = "";

        if (link.indexOf("?") >= 0){
            newLink =  link + "&" + Rand.getRandString() + "";
        } else{
            newLink = link + "?" + Rand.getRandString();
        }
        if (target == null || target.equals(DOCUMENT_TOP)){
            tmp += " onclick='top.location.href=\""+newLink+"\"; return false;' ";
        } else {
            tmp += " onclick='window.open(\""+newLink+"\", \"\", \"height=\"+window.screen.availHeight*0.8+\", " +
                    " width=\"+window.screen.availWidth*0.8+\"," +
                    "  left=\"+window.screen.availWidth*0.1+\"," +
                    "  top=\"+window.screen.availHeight*0.1+\", resizable=1, scrollbars=1\");' ";
        }
        return tmp;
    }

    public boolean isSelected() {
        return selected;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getHTML(){
        StringBuffer html = new StringBuffer();        
        html.append("<table border=0 class=\"menuItemTable\" width=\"100%\" height=\""+MenuGenerator.BUTTON_HEIGHT+"px\" " +
                " cellspacing=0 cellpadding=0 >");
            html.append("<tr >");
                html.append("<td width=\""+(MenuGenerator.BUTTON_HEIGHT + 8)+"px\" class=\"menuIcon\"" +
                        " align=\"center\" valign=\"middle\">");
                        html.append("<img src=\"images/menu/items/"+getIcon()+"\" " +
                                " width=\""+MenuGenerator.ICON_WIDTH+"px\" height=\""+MenuGenerator.ICON_WIDTH+"px\" " +
                                " border=0 >");
                html.append("</td>");
                html.append("<td class=\"menuItem\" style=\"padding-left:10px;\"        height=\"100%\" >");
                    html.append(getTitle());
                html.append("</td>");
            html.append("</tr>");
        html.append("</table>");
        return html.toString();
    }

    public String getHorizontalHTML(boolean isFirst){
        StringBuffer html = new StringBuffer();

        if (!isFirst)
            html.append("<td class=\"menuItem\">|</td>");

        html.append("<td class=\"menuItem\">");
        html.append(" <a class=\"menuLink1\" "+getRandomizedLink()+" >");
        html.append(getTitle());
        html.append("</a> ");
        html.append("</td>");

        return html.toString();
    }
}
