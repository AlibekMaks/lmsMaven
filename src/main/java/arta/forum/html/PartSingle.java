package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.forum.logic.Part;
import arta.forum.ForumMessages;

import java.io.PrintWriter;
import java.util.Properties;

public class PartSingle extends TemplateHandler {

    Part part;
    int lang;
    int recordNumber;
    SearchParams params;
    StringTransform trsf = new StringTransform();
    int roleid;

    public PartSingle(int lang, SearchParams params, int roleid) {
        this.lang = lang;
        this.params = params;
        this.roleid = roleid;
    }

    public void set(Part part, int recordNumber){
        this.part = part;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("isNew")) {
            if (part.getIsNew())
                pw.print("<img src=\"images/message.unread.gif\" width=\"16px\" border=0 height=\"16px\">"); 
        }  else if (name.equals("part title")) {
            pw.print(trsf.getHTMLString(part.getTitle()));
        } else if (name.equals("href")) {
            pw.print("themes?option=3&partid=" + part.getPartID() + "&nocache=" + Rand.getRandString());
        } else if (name.equals("author")) {
            pw.print(part.getAuthor());
        } else if (name.equals("date")) {
            pw.print(part.getDateString());
        } else if (name.equals("delete")) {
            if ((roleid & Constants.ADMIN) > 0) {
                if (!part.getAuthor().equals(MessageManager.getMessage(lang, ForumMessages.PART_OF_GROUP))) {
                    pw.print("<td width='16px'>" +
                        "<a href=\"forum?option=1&partid=" + part.getPartID() + "&"
                        + params.getFullParams() + "\" onclick='return confirm(\"" +
                        MessageManager.getMessage(lang, ForumMessages.DO_YOU_REALLY_WANT_TO_DELETE_PART)
                        + "\");' class='href'><img src=\"images/icon.delete.gif\" width=\"16px\""
                        + " border=0 height=\"16px\"></a></td>");
                }
                else {
                    pw.print("<td width='16px'></td>");
                }

            }
        }
    }
}
