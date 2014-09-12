package arta.welcome.servlet;

import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.html.IndexPageHandler;
import arta.common.logic.util.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class ExitServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            session.invalidate();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            new Parser(new FileReader("login/index.txt").read(getServletContext()), pw,
                    new IndexPageHandler()).parse();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
