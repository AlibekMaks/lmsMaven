package arta.common.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.messages.MessageManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class ShowMessageServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();

        int lang = extractor.getInteger(request.getParameter("lang"));
        int code = extractor.getInteger(request.getParameter("code"));

        pw.print("<html>\n" +
                "<head>\n" +
                "<META HTTP-Equiv=\"Cache-Control\" Content=\"no-cache\">\n" +
                "<META HTTP-Equiv=\"Pragma\" Content=\"no-cache\">\n" +
                "<META HTTP-Equiv=\"Expires\" Content=\"Tue, 01 Jan 1980 1:00:00 GMT\">\n" +
                "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                "<LINK TYPE=\"text/css\" REL=\"stylesheet\" \n" +
                "      HREF=\"css/common.css\">\n" +
                "</head>\n" +
                "<body topmargin=10px bottommargin=10px leftmargin=10px rightmargin=10px>");

        pw.print("<table border=0 width=\"100%\" class=\"table\">");
            pw.print("<tr>");
                pw.print("<td class=\"header1\" align=\"center\">");
                        pw.print(MessageManager.getMessage(lang, code, null));
                pw.print("</td>");
            pw.print("</tr>");
            pw.print("<tr>");
                pw.print("<td>");
                    pw.print(extractor.getRequestString(request.getParameter("msg")));
                pw.print("</td>");
            pw.print("</tr>");
            pw.print("<tr>");
                pw.print("<td>");
                    pw.print("<input style=\"width:80px\" type=\"button\" value=\"Ok\" onClick='self.close();' class=\"button\">");
                pw.print("</td>");
            pw.print("</tr>");
        pw.print("</table>");
        pw.print("</body></html>");
    }
}
