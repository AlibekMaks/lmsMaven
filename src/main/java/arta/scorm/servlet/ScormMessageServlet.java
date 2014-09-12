package arta.scorm.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Rand;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.server.Server;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.SCORMMessages;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.scorm.html.ScormMessageHandler;
import arta.scorm.html.ScormMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 10.04.2008
 * Time: 8:54:27
 * To change this template use File | Settings | File Templates.
 */

public class ScormMessageServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserAuthorized(session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            
            int lang = extractor.getInteger(session.getAttribute("lang"));

            HashMap <String, String> messages = new HashMap <String, String> ();

            if (request.getParameter("startcontent") != null){
                writeStartCoursePage(pw, lang, extractor.getInteger(request.getParameter("courseID")), session);
            } else {
                messages.put("message", extractor.getInteger(request.getParameter("id")) + "");

                new Parser(new FileReader("scorm/scorm.main.txt").read(getServletContext()),
                        pw,
                        new ScormMainHandler(getServletContext(), messages, lang)).parse();
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        }
    }

    private void writeStartCoursePage(PrintWriter pw, int lang, int courseID, HttpSession session){
        pw.print("    <head>\n" +
                "        <title>Testing System</title>\n" +
                "        <META HTTP-Equiv=\"Cache-Control\" Content=\"no-cache\">\n" +
                "        <META HTTP-Equiv=\"Pragma\" Content=\"no-cache\">\n" +
                "        <META HTTP-Equiv=\"Expires\" Content=\"Tue, 01 Jan 1980 1:00:00 GMT\">\n" +
                "        <META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                "        <LINK TYPE=\"text/css\" REL=\"stylesheet\" HREF=\"css/common.css\">\n" +
                "        <LINK TYPE=\"text/css\" REL=\"stylesheet\" HREF=\"css/common.page.css\">\n" +
                "        <script type=\"text/javascript\" src=\"jscripts/common.js\"></script>\n" +
                "        <script language=\"JavaScript\" src=\"runtime/APIWrapper.js\"></script>" +
                "    </head>\n" +
                "\n" +
                "    <script language=\"JavaScript\">\n" +
                "      var API_1484_11 = null;\n" +
                "      function initFrame(){\n" +
                "          initAPI();\n" +
                "          API_1484_11 = API;\n" +
                "          document.location.href=\""+ Server.MAIN_URL+"runtime/sequencingEngine.jsp?courseID="
                        + courseID +"&nocache="+Rand.getRandString()+"\";\n" +
                "      }\n" +
                "    </script>\n" +
                "\n" +
                "    <body topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0 onload=\"initFrame();\">");

            HashMap<String, String> messages = new HashMap<String, String>();
            messages.put("additional", "<table border=0 width=\"100%\"><tr><td class=\"scormmsg\" align=\"center\">"
                    + MessageManager.getMessage(lang, SCORMMessages.TO_START_FOLLOW_THIS_LINK)
                    + "</td></tr><tr><td class=\"scormmsg\" align=\"center\">"
                    + "<a class=\"scormmsg\" href=\"#\" onclick='document.location.href=\""+Server.MAIN_URL+"runtime/sequencingEngine.jsp?courseID="
                    + courseID + "&nocache=" + Rand.getRandString() + "\"; return false;' >"
                    + MessageManager.getMessage(lang, SCORMMessages.START)
                    + "</a>"
                    + "</td></tr></table>"
            );
            new Parser(new FileReader("scorm/message.txt").read(getServletConfig().getServletContext()),
                    pw,
                    new ScormMessageHandler(messages, lang)).parse();

        pw.print("" +
                //"<input type=\"button\" value=\"Start\" >\n" +
                "      <form> " +
                "         <input type=\"hidden\" name=\"courseID\" " +
                "          value=\""+ (String)session.getAttribute( "COURSEID" ) + "\" /> " +
                "         <input type=\"hidden\" name=\"stateID\" " +
                "          value=\""+(String)session.getAttribute( "SCOID" ) + "\" />\n" +
                "         <input type=\"hidden\" name=\"activityID\"\n" +
                "          value=\""+ (String)session.getAttribute( "ACTIVITYID" ) +"\" />\n" +
                "         <input type=\"hidden\" name=\"userID\"\n" +
                "          value=\"" + (String)session.getAttribute( "USERID" ) +"\" />\n" +
                "         <input type=\"hidden\" name=\"numAttempts\"\n" +
                "          value=\"" + (String)session.getAttribute("NUMATTEMPTS" ) +"\" />\n" +
                "         <input type=\"hidden\" name=\"userName\"\n" +
                "          value=\"" + (String)session.getAttribute( "USERNAME" ) +"\" />\n" +
                "      </form>\n" +
                "  </body>\n" +
                "</html>");
    }
}
