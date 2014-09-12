package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.forum.logic.Part;
import arta.forum.logic.Theme;
import arta.forum.ForumMessages;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;
import java.util.Properties;

public class ThemeSingle extends TemplateHandler {

    Theme theme;
    int recordNumber;

    int lang;
    SearchParams params;
    int roleid;
    int partid;
    
    StringTransform trsf = new StringTransform();

    public ThemeSingle(int lang, SearchParams params, int roleid, int partid) {
        this.lang = lang;
        this.params = params;
        this.roleid = roleid;
        this.partid = partid;
    }

    public void set(Theme theme, int recordNumber){
        this.theme = theme;
        this.recordNumber = recordNumber;
    }
    
    public void replace(String name, PrintWriter pw) {
        if ( name.equals("isNew")) {
            if (theme.isNew()) {
                pw.print("<img src=\"images/message.unread.gif\" width=\"16px\" border=0 height=\"16px\">");
            }
        } else if (name.equals("theme title")) {
            pw.print(trsf.getHTMLString(theme.getTitle()));
        } else if (name.equals("href")) {
            pw.print("messageslist?option=3&themeid=" + theme.getThemeID() + "&nocache=" + Rand.getRandString());
        } else if (name.equals("author")) {
            pw.print(trsf.getHTMLString(theme.getAuthor()));
        } else if (name.equals("answers count")) {
            pw.print(theme.getAnswersCount());
        } else if (name.equals("last date")) {
            pw.print(trsf.getHTMLString(theme.getLastDate()));
        } else if (name.equals("last author")) {
            pw.print(trsf.getHTMLString(theme.getLastAuthor()));
        } else if (name.equals("delete")) {
            if ((roleid & Constants.ADMIN) > 0) {
                pw.print("<td width='16px'>" +
                        "<a href=\"themes?partid=" + partid +"&option=1&themeid=" + theme.getThemeID() + "&"
                        + params.getFullParams() + "\" onclick='return confirm(\""
                        + MessageManager.getMessage(lang, ForumMessages.DO_YOU_REALLY_WANT_TO_DELETE_THEME)
                        + "\");' class='href'><img src=\"images/icon.delete.gif\" width=\"16px\" "
                        + "border=0 height=\"16px\"></a></td>");
            }
        }
    }
}
