package arta.filecabinet.servlet.tutors;

import arta.common.logic.util.Log;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.tutors.TutorsManager;
import arta.filecabinet.html.tutors.TutorsListMain;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class TutorsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.ADMIN, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Tutor tutor = (Tutor) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new TutorsListMain(tutor, lang, getServletContext(), params, null), pw,
                    getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();

        if (!Access.isUserInRole(Constants.ADMIN, session)){
            pw.print(Constants.RETURN_TO_MAIN_PAGE);
            pw.flush();
            pw.close();
            return;
        }

        Message message = null;
        Tutor tutor = (Tutor) session.getAttribute("person");
        int lang = extractor.getInteger(session.getAttribute("lang"));

        SearchParams params = new SearchParams();
        params.extractParameterValues(request, extractor);

        if(extractor.getInteger(request.getParameter("option")) == -1){     //Удаление
            int tutorID = extractor.getInteger(request.getParameter("tutorID"));
            message = new Message();
            Tutor t = new Tutor();
            t.setPersonID(tutorID);
            t.delete(message, lang, tutor.getPersonID());
        }

        new PageGenerator().writeHtmlPage(new TutorsListMain(tutor, lang, getServletContext(), params, message), pw,
                getServletContext());

        pw.flush();
        pw.close();
    }
}
