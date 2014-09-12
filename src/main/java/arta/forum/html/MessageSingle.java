package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.forum.logic.ForumMessage;
import arta.forum.ForumMessages;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;

public class MessageSingle extends TemplateHandler {

    ForumMessage message;
    int recordNumber;
    int lang;
    SearchParams params;
    int roleid;
    int themeid;
    StringTransform trsf = new StringTransform();

    public MessageSingle (int lang, SearchParams params, int roleid, int themeid) {
        this.lang = lang;
        this.params = params;
        this.roleid = roleid;
        this.themeid = themeid;
    }

    public void set(ForumMessage message, int recordNumber){
        this.message = message;
        this.recordNumber = recordNumber;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("author")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.AUTHOR));
        } else if (name.equals("name")) {
            pw.print(trsf.getHTMLString("  " + message.getAuthor()));
        } else if (name.equals("job")) {
            if ((message.getRoleID() & Constants.ADMIN) > 0) {
                pw.print(MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE));
            }
            else
                pw.print(trsf.getHTMLString(message.getJob()));
        } else if (name.equals("theme")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.THEME));
        } else if (name.equals("theme name")) {
            pw.print(trsf.getHTMLString(message.getTitle()));
        } else if (name.equals("body")) {
            pw.print(message.getBody());
        } else if (name.equals("delete")) {
            if ((roleid & Constants.ADMIN) > 0) {
                pw.print("<td width=\"16px\"><a href=\"messageslist?option=2&messageid="
                        + message.getMessageID() + "&" + params.getFullParams() + "&themeid=" + themeid
                        + "\" onclick='return confirm(\""
                        + MessageManager.getMessage(lang, ForumMessages.DO_YOU_REALLY_WANT_TO_DELETE_MESSAGE)
                        + "\");' class=\"href\"><img src=\"images/icon.delete.gif\" width=\"16px\""
                        + " border=\"0\" height=\"16px\"></a></td>");
            }
        } else if (name.equals("date")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.DATE));
        } else if (name.equals("date name")) {
            pw.print(message.getDate());
        }
    }

}
