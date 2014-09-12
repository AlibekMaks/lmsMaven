package arta.common.servlet;

import arta.common.logic.util.Log;
import arta.common.logic.db.DBSchema;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;



public class DBGenerator extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            PrintWriter  pw = response.getWriter();
            DBSchema dbSchema = new DBSchema();
            dbSchema.init();
            ArrayList <StringBuffer> report = dbSchema.check();
            pw.print("<table border=1 style=\"border-collapse:collapse\">");
            for (int i=0; i<report.size(); i++){
                pw.print("<tr>");
                pw.print("<td style=\"width:20px\" align=\"center\">");
                pw.print((i+1)+"");
                pw.print("</td>");
                pw.print("<td width=*>");
                pw.print(report.get(i).toString());
                pw.print("</td></tr>");
                pw.flush();
            }
            pw.print("</table>");
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
