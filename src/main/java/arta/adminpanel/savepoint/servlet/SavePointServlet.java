package arta.adminpanel.savepoint.servlet;

import arta.adminpanel.savepoint.SavePoint;
import arta.adminpanel.savepoint.SavePointsManager;
import arta.adminpanel.savepoint.html.SavePointsListMainHandler;
import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class SavePointServlet extends HttpServlet {

    String folderName = "";

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

            int option = 0;      
            String name = "";

            Message message = null;
            option = extractor.getInteger(request.getParameter("option"));
            name = request.getParameter("name");
            if (name!=null){
                name = new String(name.getBytes(Encoding.ISO), Encoding.UTF);
            }

            if (option == 1){
                message = new Message();
                SavePoint savePoint = new SavePoint();
                savePoint.setFolderName(folderName);
                savePoint.rollback(name, message);
            } else if (option == -1){
                message = new Message();
                SavePointsManager manager = new SavePointsManager();
                manager.setCopiesFolder(folderName);
                manager.deleteCopy(name, message);
            } else if (option == 2){
                message = new Message();
                SavePoint savePoint = new SavePoint();
                savePoint.setFolderName(folderName);
                savePoint.makeCopy(message, request.getParameter("full") != null);
            }

            SavePointsListMainHandler handler = new SavePointsListMainHandler(folderName, getServletContext(),
                    message, lang,
                    person.getRoleID(), option);
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
                exc.printStackTrace();
        }
    }

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

            String name = request.getParameter("folder");
            if (name!=null){
                name = new String(name.getBytes(Encoding.ISO), Encoding.UTF);
                folderName = name;
                message = new Message();
                message.setMessage("Текущая директория изменена.");
            }

            SavePointsListMainHandler handler = new SavePointsListMainHandler(folderName,
                    getServletContext(), message,
                    lang, person.getRoleID(), 0);
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
