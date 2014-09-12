package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.TinyMce;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.forum.ForumMessages;
import arta.forum.logic.ForumParams;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;

import java.io.PrintWriter;

public class AddMessage extends TemplateHandler {

    int lang;
    TinyMce tinyMce;
    Person person;
    int partid;
    ForumParams params;
    int themeid;
    Message message;
    SearchParams searchParams;

    public AddMessage(int lang, TinyMce tinyMce, Person person, int partid, int themeid,
                      ForumParams params, Message message, SearchParams searchParams) {
        this.lang = lang;
        this.tinyMce = tinyMce;
        this.person = person;
        this.partid = partid;
        this.themeid = themeid;
        this.params = params;
        this.message = message;
        this.searchParams = searchParams;
    }

    public void replace(String name, PrintWriter pw) {

        if (name.equals("message")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.MESSAGE));
        } else if (name.equals("theme")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.MESSAGE_THEME));
        } else if (name.equals("body")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.MESSAGE_BODY));
        } else if (name.equals("input body")) {
            tinyMce.writeTinyMceInput(pw, "body", 100, 15, TinyMce.DIMENSION_TYPE__PERCENT_ROW, params.getBody());
        } else if (name.equals("save")) {
            pw.print(MessageManager.getMessage(lang, Constants.SAVE));
        } else if (name.equals("partid")) {
            pw.print(partid);
        } else if (name.equals("themeid")) {
            pw.print(themeid);
        } else if (name.equals("size")) {
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("theme value")) {
            pw.print(params.getTitle());
        } else if (name.equals("showmessage")) {
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("return link")) {
            if (partid==0) {
                pw.print("messageslist?themeid=" + themeid +"&userid=" + person.getPersonID() +"&roleid="
                    + person.getRoleID() + "&themeid=" + themeid + searchParams.getFullParams());
            }
            if (themeid == 0) {
                pw.print("themes?partid=" + partid +"&userid=" + person.getPersonID() +"&roleid="
                    + person.getRoleID() + "&themeid=" + themeid + searchParams.getFullParams());
            }
        } else if (name.equals("return title")) {
            pw.print(MessageManager.getMessage(lang, Constants.BACK));
        }
    }
}
