package arta.classes.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.classes.logic.StudyClass;
import arta.classes.html.ClassCardMain;
import arta.groups.logic.StudyGroup;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class ClassServlet extends HttpServlet {

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

            Person person = (Person) session.getAttribute("person");
            Integer lang = extractor.getInteger(session.getAttribute("lang"));
            Integer examID = extractor.getInteger(request.getParameter("examID"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            StudyClass studyClass = new StudyClass();
            studyClass.setClassID(extractor.getInteger(request.getParameter("classID")));
            studyClass.setExamID(extractor.getInteger(request.getParameter("examID")));
            studyClass.setClassNameru(extractor.getRequestString(request.getParameter("nameru")));
            studyClass.setClassNamekz(extractor.getRequestString(request.getParameter("namekz")));

            Message message = new Message();
            studyClass.save(message, lang, params, person.getPersonID());            

            new PageGenerator().writeHtmlPage(new ClassCardMain(lang, person, studyClass, params, getServletContext(), message),
                    pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

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

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            Message message = null;

            if (request.getParameter("option") != null){
                if (extractor.getInteger(request.getParameter("option")) == -1){
                    message = new Message();
                    int groupID = extractor.getInteger(request.getParameter("groupID"));
                    StudyGroup studyGroup = new StudyGroup();
                    studyGroup.setStudyGroupID(groupID);
                    studyGroup.delete(message, lang, person.getPersonID());
                }
            }            

            StudyClass studyClass = new StudyClass();
            if (request.getParameter("classID") != null){
                int classID = extractor.getInteger(request.getParameter("classID"));
                studyClass.loadById(classID, lang);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                studyClass.loadByRecordNumber(params, recordNumber, lang);
            }

            new PageGenerator().writeHtmlPage(new ClassCardMain(lang, person, studyClass, params, getServletContext(), message),
                    pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
