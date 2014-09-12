package arta.forum.servlet;

import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.html.PartsListMain;
import arta.forum.logic.Part;
import arta.forum.logic.PartManager;
import arta.forum.logic.ThemeManager;
import arta.forum.ForumMessages;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class PartsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {

        try{

            HttpSession session = httpServletRequest.getSession();
            httpServletResponse.setContentType("text/html; charset=utf-8");
            PrintWriter pw = httpServletResponse.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserAuthorized(session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(httpServletRequest, extractor);

            Message message = null;

            String option = "";
            String title = extractor.getRequestString(httpServletRequest.getParameter("newPartName"));
            
            int partid = extractor.getInteger(httpServletRequest.getParameter("partid"));

            try {

                if (httpServletRequest.getParameter("option name")!=null && !httpServletRequest.getParameter("option name").equals("") )
                    option = httpServletRequest.getParameter("option name");
            } catch(Exception e) {
                Log.writeLog(e);
                e.printStackTrace();
            }

            if (option.equals("1")) {
                Part part = new Part (partid);
                if (part.getClassID(partid) != 0) {
                    message.setMessage(MessageManager.getMessage(lang, ForumMessages.PART_WAS_CREATED_BY_SYSTEM));
                } else {
                    message = new Message();
                    part.delete(message, lang);    
                }

            } else if (option.equals("2")) {
                message = new Message();
                if (title != null && !title.equals("")) {
                    Part part = new Part(title, person.getPersonID(), person.getRoleID(), 0);
                    part.create(lang, message);
                    option = "";
                    params.search = "";
                } else {
                    message.setMessage(MessageManager.getMessage(lang, ForumMessages.EMPTY_PART_TITLE));
                    option = "2";
                }
            } else {
                PartManager pm = new PartManager();
                pm.search(params, person, lang);
            }

            new PageGenerator().writeHtmlPage(new PartsListMain(person, params, getServletContext(), 
                    lang, option, message), pw, getServletContext());
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {

        try {

            HttpSession session = httpServletRequest.getSession();
            httpServletResponse.setContentType("text/html; charset=utf-8");
            PrintWriter pw = httpServletResponse.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserAuthorized(session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            SearchParams params = new SearchParams();
            params.extractParameterValues(httpServletRequest, extractor);

            String option = "";
            int partID  = extractor.getInteger(httpServletRequest.getParameter("partid"));

            try {

                if (httpServletRequest.getParameter("option") != null
                        && !httpServletRequest.getParameter("option").equals("") )
                    option = httpServletRequest.getParameter("option");
            } catch(Exception e) {
                 e.printStackTrace();
            }

            Message message = new Message();

            Part part = new Part (partID);

            if (option.equals("1")) {
                if (part.getClassID(partID) != 0) {
                    message.setMessage(MessageManager.getMessage(lang, ForumMessages.PART_WAS_CREATED_BY_SYSTEM));
                } else {
                    message = new Message();
                    part.delete(message, lang);
                }
            }

            new PageGenerator().writeHtmlPage(new PartsListMain(person, params, getServletContext(),
                    lang, option, message), pw, getServletContext());
            
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
