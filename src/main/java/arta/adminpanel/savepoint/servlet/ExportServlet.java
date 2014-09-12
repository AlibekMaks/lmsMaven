package arta.adminpanel.savepoint.servlet;

import arta.adminpanel.savepoint.SavePoint;
import arta.adminpanel.savepoint.html.ExportMainHandler;
import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.*;
import arta.common.logic.util.DataExtractor;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;



public class ExportServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();

            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(arta.common.logic.util.Constants.ADMIN, session)){
                pw.print(arta.common.logic.util.Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

        try{

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Message message = null;
            String name = request.getParameter("export");


            if (name!=null)
                name = new String(name.getBytes(Encoding.ISO), Encoding.UTF);

            if (name == null){
                message = new Message();
                message.setMessage("export folder can not be null");
            }

            if (name!=null){
                message = new Message();
                File file = new File(name);
                if (!file.exists()){
                    message.setMessage("derictory "+name+" does not exist.");
                } else {
                    SavePoint savePoint = new SavePoint();
                    savePoint.setFolderName(name);
                    savePoint.makeCopy(message, true);
                }
            }

            ExportMainHandler handler = new ExportMainHandler(lang, person.getRoleID(), name, message, getServletContext());
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
                exc.printStackTrace();
            Log.writeLog(exc);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();        

            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(arta.common.logic.util.Constants.ADMIN, session)){
                pw.print(arta.common.logic.util.Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            try{

                Person person = (Person) session.getAttribute("person");
                int lang = extractor.getInteger(session.getAttribute("lang"));

            ExportMainHandler handler = new ExportMainHandler(lang, person.getRoleID(), null, null, getServletContext());
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
