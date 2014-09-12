package arta.forum.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.forum.logic.PartManager;
import arta.forum.ForumMessages;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class PartsList extends TemplateHandler {

    PartManager manager;
    int partsNumber;
    Person person;
    int lang;
    SearchParams params;
    ServletContext servletContext;
    String option;
    Message message;
    StringTransform trsf = new StringTransform();

    PartsList(ServletContext servletContext, SearchParams params, int lang, Person person, String option, Message message) {
        this.servletContext = servletContext;
        this.params = params;
        this.lang = lang;
        this.person = person;
        manager = new PartManager();
        manager.search(params, person, lang);
        partsNumber = params.getPanelPartsNumber();
        this.option = option;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("parts list")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.FORUM, null));
        } else if (name.equals("number of found records")) {
            pw.print(params.recordsCount);
        } else if (name.equals("found")) {
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("part title")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.PARTS));
        } else if (name.equals("author")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.AUTHOR));
        } else if (name.equals("created date")) {
            pw.print(MessageManager.getMessage(lang, ForumMessages.CREATED_DATE));
        } else if (name.equals("records")) {
            StringBuffer str = new FileReader("forum/partsingle.txt").read(servletContext);
            PartSingle hanlder = new PartSingle(lang, params, person.getRoleID());
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(hanlder);
            for (int i = 0; i < manager.parts.size(); i ++) {
                hanlder.set(manager.parts.get(i), params.partNumber*params.countInPart + i);
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang,Constants.SEARCH));
        } else if (name.equals("delete")) {
            pw.print(MessageManager.getMessage(lang, Constants.DELETE));
        }  else if (name.equals("return link")){
            pw.print("main?nocache=" + Rand.getRandString());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, ForumMessages.MAIN, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("add part")) {
            if ((person.getRoleID() & Constants.ADMIN)>0) {
                if (option.equals("2")) {
                    pw.print("<td align='left'><table width='80%'><tr><td width='80%'><input type=\"text\"" +
                            " name='newPartName' class=\"input\" style='width:100%'>" +
                            "</td><td width='100px'><input class=\"button\" type=\"submit\" value=\""+
                            MessageManager.getMessage(lang, Constants.ADD) +"\">" +
                            "</td><td><input type=\"text\" style=\"visibility:hidden\"  value=" +
                            partsNumber + " name='partnumber'></td></tr></table></td>");
                }
            }
        } else if (name.equals("add part link")) {
            if ((person.getRoleID() & Constants.ADMIN) > 0) {
                 pw.print("<td valign=\"top\">" +
                         "<a class=\"href\" href=\"forum?userid=" + person.getPersonID() + "&roleid="
                         + person.getRoleID() + "&option=2&" + params.getParams() + "\" title=\""
                         + MessageManager.getMessage(lang, Constants.ADD, null) +"\">"
                         + "<img src=\"images/buttons.plus.gif\" width=\"" + Constants.MENU_IMAGE_SIZE 
                         + "px\" height=\"" + Constants.MENU_IMAGE_SIZE +"px\" border=0/></a></td>");
            }
        } else if (name.equals("delete column")) {
            if ((person.getRoleID() & Constants.ADMIN) > 0)
                pw.print("<td width='16px' class=\"coloredheader\"></td>");
        } else if (name.equals("search value")) {
            pw.print(trsf.getHTMLString(params.search));
        } else if (name.equals("option value")) {
            pw.print(option);
        } else if (name.equals("roleid")) {
            pw.print(person.getRoleID());
        } else if (name.equals("userid")) {
            pw.print(person.getRoleID());
        } else if (name.equals("parts")) {
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "forum?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        } else if (name.equals("message")) {
             if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        }
    }
}