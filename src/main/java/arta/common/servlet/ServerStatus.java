package arta.common.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerStatus extends HttpServlet {

    public static final long temp = 1024*1024;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter pw = response.getWriter();
        pw.print("<html>\n" +
                "\t<head>\n" +
                "\t\t<script language=javascript>\n" +
                "\t\tsetTimeout(\"top.location='http://127.0.0.1/LMS/serverStatus'\", 1000)\n" +
                "\t\t</script>\n" +
                "\t</head>");
        pw.print("MaxMemory=" + (Runtime.getRuntime().maxMemory()/temp));
        pw.print("<br>");
        pw.print("FreeMemory=" + (Runtime.getRuntime().freeMemory()/temp));
        pw.print("<br>");
        pw.print("occupied=" + ((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory())/temp));
        pw.print("</html>");
    }
}
