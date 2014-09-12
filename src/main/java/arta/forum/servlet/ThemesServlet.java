package arta.forum.servlet;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.PartManager;
import arta.forum.logic.Theme;
import arta.forum.logic.ThemeManager;
import arta.forum.html.ThemeListMain;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class ThemesServlet extends HttpServlet {

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

             int partid  = 0;
             String option = "";

             try {
                 partid = extractor.getInteger(httpServletRequest.getParameter("partid"));
                 option = extractor.getRequestString(httpServletRequest.getParameter("option name"));
             } catch(Exception e) {
                 Log.writeLog(e);
             }

             Message message = null;
             int themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));
             Theme theme = new Theme(themeid);

             if (option.equals("1")) {
                 message = new Message();
                 theme.delete(message, lang);
             } else {
                 ThemeManager tm = new ThemeManager();
                 tm.search(params, person.getPersonID(), partid, person.getRoleID());
             }

             new PageGenerator().writeHtmlPage(new ThemeListMain(person, params, getServletContext(), lang, partid, option, message ),
                 pw, getServletContext());
             
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

             int partid = 0;

             String option = "";
             int themeid;

             Message message = null;

             try {
                 partid = extractor.getInteger(httpServletRequest.getParameter("partid"));
                 option = extractor.getRequestString(httpServletRequest.getParameter("option"));
                 themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));
                 if (option.equals("1")) {
                     message = new Message();
                    Theme theme = new Theme(themeid);
                    theme.delete(message, lang);
                 }  else if (option.equals("3")) {
                     PartManager p = new PartManager();
                     p.updateVisitedDataForParts(partid, person);
                 } else {
                     ThemeManager tm = new ThemeManager();
                     tm.search(params, person.getPersonID(), partid, person.getRoleID());
                 }
             } catch(Exception e) {
                 Log.writeLog(e);
             }
             
             new PageGenerator().writeHtmlPage(new ThemeListMain(person, params, getServletContext(), lang, partid, option, message),
                              pw, getServletContext());
             pw.flush();
             pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    
    }
}
