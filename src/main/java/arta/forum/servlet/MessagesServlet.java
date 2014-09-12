package arta.forum.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.html.handler.PageGenerator;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.ForumMessageManager;
import arta.forum.logic.ForumMessage;
import arta.forum.logic.ThemeManager;
import arta.forum.html.MessagesListMain;
import arta.login.logic.Access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class MessagesServlet extends HttpServlet {


    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

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

            int themeid = 0;
            String option= "";
            int msID = 0;

            Message informationMessage = new Message();

            try {
                themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));
                option = extractor.getRequestString(httpServletRequest.getParameter("option"));
                msID = extractor.getInteger(httpServletRequest.getParameter("messageid"));
            } catch(Exception exc) {
                Log.writeLog(exc);
            }

            if (option.equals("2")) {
               ForumMessage message = new ForumMessage(msID);
               message.delete(lang, informationMessage);
            }  else {
                ForumMessageManager pm = new ForumMessageManager();
                pm.search(params, themeid, person.getPersonID(), person.getRoleID(), lang);
            }

            new PageGenerator().writeHtmlPage(new MessagesListMain(params, lang,  person, themeid,
                    getServletContext(), option, informationMessage), pw, getServletContext());

            pw.flush();
            pw.close();

       } catch (Exception exc){
           Log.writeLog(exc);
       }

   }


   protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

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

           Message informationMessage = new Message();

           int themeid = 0;
           String option="";
           int msID = 0;
           try {
               option = extractor.getRequestString(httpServletRequest.getParameter("option"));
               msID = extractor.getInteger(httpServletRequest.getParameter("messageid"));
               themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));
           } catch(Exception exc) {
               Log.writeLog(exc);
           }
           if (option.equals("2")) {
               ForumMessage message = new ForumMessage(msID);
               message.delete(lang, informationMessage);
           } else if (option.equals("3")) {
               ThemeManager manager = new ThemeManager();
               manager.updateVisitedDataForThemes(themeid, person);
           }  else {
               ForumMessageManager pm = new ForumMessageManager();
               pm.search(params, themeid,  person.getPersonID(), person.getRoleID(), lang);
           }

           new PageGenerator().writeHtmlPage(new MessagesListMain(params,  lang,  person, themeid,
                   getServletContext(), option, informationMessage), pw, getServletContext());
           pw.flush();
           pw.close();
       } catch (Exception exc){
           Log.writeLog(exc);
       }
   }

}
