package arta.login.servlet;

import arta.common.logic.util.Log;
import arta.common.logic.server.Server;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.html.IndexPageHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class Index extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();

            Server.readMainURL(request, getServletContext());
            if (!Server.isURLCorrect){
                pw.print("Start with HTTP!!!");
                return;
            }

            new Parser(new FileReader("login/index.txt").read(getServletContext()), pw,
                    new IndexPageHandler()).parse();
            
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

}
