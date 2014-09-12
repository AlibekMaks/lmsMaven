package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.*;
import arta.forum.ForumMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class MessagesList extends TemplateHandler {

    ServletContext servletContext;
    ForumMessageManager mm;
    SearchParams params;
    Person person;
    int lang;
    int themeid;
    int partsNumber;
    String option;
    StringTransform trsf = new StringTransform();
    Message message;

    public MessagesList(SearchParams params, Person person, int lang,
                        ServletContext servletContext, int themeid, String option, Message message) {
        this.params = params;
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
        this.themeid = themeid;
        partsNumber = params.getPanelPartsNumber();
        mm = new ForumMessageManager();
        mm.search(params, themeid, person.getPersonID(), person.getRoleID(), lang);
        this.option = option;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        ThemeManager theme = new ThemeManager();
        if (name.equals("theme")) {
            pw.print(trsf.getHTMLString(theme.search(themeid)));
        } else if (name.equals("return link")) {
            pw.print("themes?partid=" + theme.getPartID(themeid) +"&userid=" + person.getPersonID() +"&roleid="
                    + person.getRoleID() + "&themeid=" + themeid + params.getFullParams());
        } else if (name.equals("return title")) {
            pw.print(MessageManager.getMessage(lang, Constants.BACK));
        } else if (name.equals("add message link")) {
            pw.print("<td valign=\"top\"><a class=\"href\" href=\"addmessage?option=1&themeid="
                    + themeid + "&nocache="+ Rand.getRandString() + "\" title=\""
                    + MessageManager.getMessage(lang, Constants.ADD) +"\">"
                    + "<img src=\"images/buttons.plus.gif\" width=\"" + Constants.MENU_IMAGE_SIZE
                    + "px\" height=\"" + Constants.MENU_IMAGE_SIZE +"px\" border=\"0\"/></a></td>");
        } else if (name.equals("author")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.AUTHOR));
        } else if (name.equals("messages list")) {
            StringBuffer str = new FileReader("forum/messagesingle.txt").read(servletContext);
            MessageSingle hanlder = new MessageSingle(lang, params, person.getRoleID(), themeid);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(hanlder);
            for (int i = 0; i < mm.messages.size(); i ++) {
                hanlder.set(mm.messages.get(i), params.partNumber*params.countInPart + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("size")) {
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("option")) {
            pw.print(option);
        } else if (name.equals("found")) {
            pw.print(MessageManager.getMessage(lang, Constants.FOUND));
        } else if (name.equals("number of found records")) {
            pw.print(params.recordsCount);
        } else if (name.equals("parts")) {
            new PartsHandler (params.getPartsNumber(), params.partNumber, lang,
                    
                    "messageslist?themeid="+ themeid+ "&" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("show message")) {
             if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        }
    }
}
