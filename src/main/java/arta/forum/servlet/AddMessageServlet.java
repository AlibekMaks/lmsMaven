package arta.forum.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.forum.logic.ForumMessage;
import arta.forum.logic.Theme;
import arta.forum.logic.ForumParams;
import arta.forum.logic.ThemeManager;
import arta.forum.html.AddMessageMain;
import arta.forum.html.MessagesListMain;
import arta.forum.html.PartsListMain;
import arta.forum.html.ThemeListMain;
import arta.forum.ForumMessages;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class AddMessageServlet extends HttpServlet {

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

            ForumParams params = new ForumParams();
            params.saveMsgParams(httpServletRequest, extractor);
            SearchParams searchParams = new SearchParams();
            searchParams.extractParameterValues(httpServletRequest, extractor);
            searchParams.getSearch();

            int partid = extractor.getInteger(httpServletRequest.getParameter("partid"));
            int themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));
            String title = extractor.getRequestString(httpServletRequest.getParameter("theme"));
            String body = extractor.getRequestString(httpServletRequest.getParameter("body"));

            Message informationMessage;

            if (partid == 0) {
                if (title.equals("") || title == null) {
                    informationMessage = new Message();
                    informationMessage.setMessage(MessageManager.getMessage(lang,
                            ForumMessages.MESSAGE_WITHOUT_TITLE));
                    new PageGenerator().writeHtmlPage(new AddMessageMain(person, getServletContext(),
                            lang, 0, themeid, params, informationMessage, searchParams), pw, getServletContext());
                }
                if (body.equals("") || body == null) {
                    informationMessage = new Message();
                    informationMessage.setMessage(MessageManager.getMessage(lang,
                            ForumMessages.CANNOT_CREATE_EMPTY_MESSAGE));
                    new PageGenerator().writeHtmlPage(new AddMessageMain(person, getServletContext(),
                            lang, 0, themeid, params, informationMessage, searchParams), pw, getServletContext());
                }
                if (!title.equals("") && title != null && !body.equals("") && body != null)  {
                    ForumMessage message = new ForumMessage(person.getPersonID(), person.getRoleID(),
                            themeid, body, title);
                    Theme theme = new Theme(themeid);
                    informationMessage = new Message();
                    message.create(lang, informationMessage, "0");
                    theme.changeLastMsgData(message.getMessageID(), themeid);
                    ThemeManager th = new ThemeManager();
                    th.updateVisitedDataForThemes(themeid, person);
                    new PageGenerator().writeHtmlPage(new MessagesListMain(searchParams, lang, person,
                            themeid, getServletContext(), "", informationMessage), pw, getServletContext());
                }
            }
            if (themeid == 0) {
                Theme theme = new Theme(partid, params.getTitle(), person.getPersonID(),
                        person.getRoleID(), lang);
                ThemeManager tm = new ThemeManager();
                informationMessage = new Message();
                if (title.equals("") || title == null) {
                    informationMessage.setMessage(MessageManager.getMessage(lang,
                            ForumMessages.EMPTY_THEME_TITLE));
                    new PageGenerator().writeHtmlPage(new AddMessageMain(person, getServletContext(),
                            lang, partid, 0, params, informationMessage, searchParams), pw, getServletContext());
                }
                if (body.equals("") || body == null) {
                    informationMessage.setMessage(MessageManager.getMessage(lang,
                            ForumMessages.CANNOT_CREATE_EMPTY_MESSAGE));
                    new PageGenerator().writeHtmlPage(new AddMessageMain(person, getServletContext(),
                            lang, partid, 0, params, informationMessage, searchParams), pw, getServletContext());
                }
                if (!title.equals("") && title != null && !body.equals("") && body != null)  {

                    if (theme.create(informationMessage)) {
                       ForumMessage message = new ForumMessage(person.getPersonID(), person.getRoleID(),
                               theme.getThemeID(), body, title);
                       message.create(lang, informationMessage, "1");
                       theme.changeLastMsgData(message.getMessageID(), theme.getThemeID());
                       tm.updateVisitedDataForThemes(theme.getThemeID(), person);
                       new PageGenerator().writeHtmlPage(new ThemeListMain(person, searchParams,
                               getServletContext(), lang, partid, "", informationMessage),
                               pw, getServletContext()); 
                    }
                }
            }

            pw.flush();
            pw.close();
        } catch(Exception exc) {
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

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

            int partid = extractor.getInteger(httpServletRequest.getParameter("partid"));
            int themeid = extractor.getInteger(httpServletRequest.getParameter("themeid"));

            ForumParams params = new ForumParams();
            params.saveMsgParams(httpServletRequest, extractor);

            SearchParams searchParams = new SearchParams();
            searchParams.extractParameterValues(httpServletRequest, extractor);

            Message message = new Message();
            
            new PageGenerator().writeHtmlPage(new AddMessageMain(person, getServletContext(), lang, partid,
                     themeid, params, message, searchParams), pw, getServletContext());

            pw.flush();
            pw.close();
        } catch(Exception exc) {
            Log.writeLog(exc);
        }

    }
}
