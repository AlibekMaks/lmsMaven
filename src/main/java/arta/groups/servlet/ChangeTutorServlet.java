package arta.groups.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.html.TutorAdd;
import arta.groups.html.TutorChange;
import arta.groups.logic.StudyGroup;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class ChangeTutorServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            String option = extractor.getRequestString(request.getParameter("option"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);
            int subgroupID = extractor.getInteger(request.getParameter("subgroupID"));

            if (option.equals("show")){
                TutorChange handler = new TutorChange(lang, getServletContext(), params, subgroupID);
                new Parser(new FileReader("groups/tutor.select.txt").read(getServletContext()), pw,
                    handler).parse();
            } else if (option.equals("change")){
                int tutorID = extractor.getInteger(request.getParameter("tutorID"));
                StudyGroup studyGroup = new StudyGroup();
                studyGroup.changeTutor(subgroupID, tutorID);
                String id = extractor.getRequestString(request.getParameter("JsHttpRequest"));
                id = id.substring(0, id.indexOf("-"));
                pw.print("JsHttpRequest.dataReady( " +
                        "  "+id+" ," +
                        "  'messages'," +
                        "   {  " +
                        "   messages : 'OK' " +
                        "   }" +
                        ")");
            }

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
