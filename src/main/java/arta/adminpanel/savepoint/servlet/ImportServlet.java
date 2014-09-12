package arta.adminpanel.savepoint.servlet;

import arta.adminpanel.savepoint.SavePoint;
import arta.adminpanel.savepoint.html.ImportMainHandler;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
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


public class ImportServlet extends HttpServlet {

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
            String name = request.getParameter("import");
            if (name==null){
                message = new Message();
                message.setMessage("Directory name can not be null");
            }
            if (name!=null){
                message = new Message();
                File f = new File(name);
                if (!f.exists()){
                    message.setMessage("Derictory "+name+" does not exist.");
                } else {
                    SavePoint savePoint = new SavePoint();
                    savePoint.goToSavePoint(name, message);
                }
            }

            ImportMainHandler handler = new ImportMainHandler(lang,
                    person.getRoleID(), name, message, getServletContext());
            PageGenerator generator = new PageGenerator();
            generator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
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
            ImportMainHandler handler = new ImportMainHandler(lang, person.getRoleID(), null, null, getServletContext());
            PageGenerator generator = new PageGenerator();
            generator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
