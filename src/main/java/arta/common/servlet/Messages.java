package arta.common.servlet;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;



public class Messages extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        try{
            MessageManager manager = new MessageManager();
            InputStream inputStream = getServletContext().getResourceAsStream("templates/messages.txt");
            manager.checkMessages(inputStream);
            pw.print("OK");
        } catch (Exception exc){
            pw.print("FAIL");
            Log.writeLog(exc);
        }
    }
}
