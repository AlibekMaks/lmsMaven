package arta.chat.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.chat.servlet.ChatServlet;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 19.03.2008
 * Time: 9:51:28
 * To change this template use File | Settings | File Templates.
 */
public class ChatEnterMainHandler extends PageContentHandler {

    int roomID;
    int lang;
    int roleID;
    ServletContext servletContext;


    public ChatEnterMainHandler(int roomID, int lang, int roleID, ServletContext servletContext) {
        this.roomID = roomID;
        this.lang = lang;
        this.roleID = roleID;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) {
        out.print("<script language=\"javascript\">" +
                " var myWidth=1000; " +
                " var myHeight=1000; " +
                " function getParams(){" +
                "    if (parseInt(navigator.appVersion)>3 && navigator.appName==\"Netscape\") {\n" +
                "        myWidth = window.innerWidth;\n" +
                "        myHeight = window.innerHeight;\n" +
                "    } else {" +
                "       myHeight = window.screen.availHeight;" +
                "       myWidth = window.screen.availWidth;" +
                "       myHeight -= 100;" +
                "   } " +
                " }" +
                " getParams();" +
                "</script>");
    }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {


        pw.print(
                "<table border=0 width=\"100%\" height=\"100%\" cellpadding=0 cellspacing=0 >" +
                        "<tr>" +
                        "   <td valign=\"top\">" +
                        "       <table border=0 cellpadding=0 cellspacing=0 width=\"100%\" style='height:400px' id=\"asd\">" +
                                "<script language=\"javascript\"> " +
                                "myHeight -= 90;" +
                                "document.getElementById(\"asd\").style.height=myHeight;" +
                                "</script>" +
                        "           <tr  id=\"asdasd\">" +
                        "               <td width=\"100%\" height=\"100%\" valign=\"bottom\">" );

        pw.print("<iframe width=\"100%\" height=\"100%\" " +
                " src=\"chat?option="+ ChatServlet.GET_IFRAMES + "&roomID="+roomID+"\">");
        pw.print("</iframe>");

        pw.print(
                "                      </td>" +
                        "" +
                        "           </tr>" +
                        "        </table>" +
                        "    </td>" +
                        "</tr>" +
                        "<tr height=\"100%\">" +
                        "   <td bgcolor=\"#376B9A\"></td>" +
                    "</tr>" +
                "</table>");
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
