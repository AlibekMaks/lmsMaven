package arta.forum.html;

import arta.forum.logic.PartManager;
import arta.forum.logic.ThemeManager;
import arta.forum.logic.Theme;
import arta.forum.ForumMessages;
import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ThemesList extends TemplateHandler {

    ThemeManager manager;
    int partsNumber;
    int roleid;

    int lang;
    SearchParams params;
    ServletContext servletContext;
    int userid;
    String option;
    int partid;
    Message message;
    StringTransform trsf = new StringTransform();

    public ThemesList (ServletContext servletContext, SearchParams params, int lang, int roleid,
                  int userid, int partid, String option, Message message) {
        this.servletContext = servletContext;
        this.params = params;
        this.lang = lang;
        manager = new ThemeManager();
        manager.search(params, userid, partid, roleid);
        partsNumber = params.getPanelPartsNumber();
        this.roleid = roleid;
        this.userid = userid;
        this.partid = partid;
        this.option = option;
        this.message = message;
    }


    public void replace(String name, PrintWriter pw) {

        if (name.equals("theme list")) {
            pw.print(new PartManager().getPartName(partid));
        } else if (name.equals("search value")) {
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("search")) {
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("return link")) {
            pw.print("forum?" + params.getFullParams());
        } else if (name.equals("return title")) {
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("size")) {
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("add theme link")) {
            if ((roleid & Constants.ADMIN) > 0) {
                 pw.print("<td valign=\"top\">" +
                         "<a class=\"href\" href=\"addmessage?partid=" + partid + "&nocache="+ Rand.getRandString()
                         + "\" title=\"" + MessageManager.getMessage(lang, Constants.ADD, null) +"\">"
                         + "<img src=\"images/buttons.plus.gif\" width=\"" + Constants.MENU_IMAGE_SIZE
                         + "px\" height=\"" + Constants.MENU_IMAGE_SIZE +"px\" border=0/></a></td>");
            }
        } else if (name.equals("found")) {
            pw.print(MessageManager.getMessage(lang, Constants.FOUND));
        } else if (name.equals("number of found records")) {
            pw.print(params.recordsCount);
        } else if (name.equals("roleID")) {
            pw.print(roleid);
        } else if (name.equals("userid")) {
            pw.print(userid);
        } else if (name.equals("theme title")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.THEMES));
        } else if (name.equals("author")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.AUTHOR));
        } else if (name.equals("answers count")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.ANSWERS_COUNT));
        } else if (name.equals("last answer")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.LAST_ANSWER));
        } else if (name.equals("delete column")) {
            if ((roleid & Constants.ADMIN) > 0)
                pw.print("<td class=\"coloredheader\" width='16px'></td>");
        } else if (name.equals("records")) {
            StringBuffer str = new FileReader("forum/themesingle.txt").read(servletContext);
            ThemeSingle hanlder = new ThemeSingle(lang, params, roleid, partid);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(hanlder);
            for (int i = 0; i < manager.themes.size(); i ++) {
                hanlder.set(manager.themes.get(i), params.partNumber*params.countInPart + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("option value")) {
            pw.print(option);
        } else if (name.equals("partid")) {
            pw.print(partid);
        } else if (name.equals("parts")) {
            new PartsHandler(params.getPartsNumber(), params.partNumber,
                    lang, "themes?partid=" + partid + "&" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("message")) {
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        }
    }
}
