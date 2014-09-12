package arta.auth.servlet;

import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 12.03.2008
 * Time: 15:33:32
 * To change this template use File | Settings | File Templates.
 */

public class InvalidURL extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();

        request.getSession().invalidate();

        pw.print("start with HTTP !!!");

    }

}
