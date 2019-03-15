package arta.settings.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.login.logic.Access;
import arta.settings.html.SettingsMainHandler;
import arta.settings.logic.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class SettingsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.ADMIN, session)) {
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }
            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            String return_link = request.getHeader("referer");

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);
            Settings settings = new Settings();

            settings.setUsetotalball(request.getParameter("usb_total") != null);

            pw.flush();
            pw.close();
        } catch (Exception exc) {
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            Message message = new Message();

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Settings settings = new Settings();

            if (request.getParameter("save") != null){
                settings.maxMarkValue = extractor.getInteger(request.getParameter("maxmark"));
                settings.excellent = extractor.getInteger(request.getParameter("excellent"));
                settings.good = extractor.getInteger(request.getParameter("good"));
                settings.satisfactory = extractor.getInteger(request.getParameter("satisfactory"));
                settings.attestatThresholdForDirectors = extractor.getInteger(request.getParameter("att_d"));
                settings.attestatThresholdForEmployee = extractor.getInteger(request.getParameter("att_e"));
                settings.usesubjectball = (request.getParameter("usb") != null && request.getParameter("usb").equals("on"));
                settings.usetotalball = (request.getParameter("usb_total") != null && request.getParameter("usb_total").equals("on"));
                settings.show_report = (request.getParameter("show_report") != null && request.getParameter("show_report").equals("on"));
                settings.show_answers = (request.getParameter("show_answers") != null && request.getParameter("show_answers").equals("on"));
                settings.recommend_candidates = (request.getParameter("recommend_candidates") != null && request.getParameter("recommend_candidates").equals("on"));
                settings.student_test_access = (request.getParameter("student_test_access") != null && request.getParameter("student_test_access").equals("on"));
                settings.recommend_candidates_month = extractor.getInteger(request.getParameter("recommend_candidates_month"));

                settings.save(message, lang);
            } else {
                settings.load();
            }

            new PageGenerator().writeHtmlPage(new SettingsMainHandler(lang, person.getRoleID(),
                    getServletContext(), message, settings),
                        pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
