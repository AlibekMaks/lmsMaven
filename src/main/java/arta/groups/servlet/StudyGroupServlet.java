package arta.groups.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.html.StudyGroupCardMain;
import arta.groups.logic.StudyGroup;
import arta.groups.logic.StudyGroupsSearchParams;
import arta.classes.logic.StudyClass;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ArrayList;


public class StudyGroupServlet extends HttpServlet {

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
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            int classID = extractor.getInteger(request.getParameter("classID"));
            int groupsCount = extractor.getInteger(request.getParameter(StudyGroupsSearchParams.RECORDS_COUNT));
            int groupNumber = extractor.getInteger(request.getParameter(StudyGroupsSearchParams.RECORD_NUMBER));

            StudyGroup group = new StudyGroup();

            if (request.getParameter("save")!= null){
                Enumeration en = request.getParameterNames();
                ArrayList <int[]> data = new ArrayList<int[]>();
                while (en.hasMoreElements()){
                    String name = (String) en.nextElement();
                    if (name.length() > 6 && name.substring(0, 6).equals("stdsel")){
                        int studentID = extractor.getInteger(name.substring(6, name.length()));
                        if (studentID > 0){
                            int subgroupID = extractor.getInteger(request.getParameter(name));
                            int[] tmp = new int[2];
                            tmp[0] = subgroupID;
                            tmp[1] =studentID;
                            data.add(tmp);
                        }
                    }
                }
                int studyGroupID = extractor.getInteger(request.getParameter("studyGroupID"));
                group.setStudyGroupID(studyGroupID);
                group.moveStudents(data, person.getPersonID());
            }

            if (request.getParameter("studyGroupID") != null){
                int id = extractor.getInteger(request.getParameter("studyGroupID"));
                group.loadById(id, lang);
            } else {
                group.loadByRecordNumber(groupNumber, lang, classID);
            }

            new PageGenerator().writeHtmlPage(new StudyGroupCardMain(lang, person, getServletContext(),
                    new StudyClass().loadAsSimpleObject(classID), group, groupNumber, groupsCount, params),
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
            int classID = extractor.getInteger(request.getParameter("classID"));
            int groupsCount = extractor.getInteger(request.getParameter(StudyGroupsSearchParams.RECORDS_COUNT));
            int groupNumber = extractor.getInteger(request.getParameter(StudyGroupsSearchParams.RECORD_NUMBER));

            StudyGroup group = new StudyGroup();

            if (request.getParameter("option") != null){
                int studyGroupID = extractor.getInteger(request.getParameter("studyGroupID"));
                group.setStudyGroupID(studyGroupID);
                if (request.getParameter("option").equals("add")){
                    group.addSubGroup(person.getPersonID());
                } else if (request.getParameter("option").equals("delete")){
                    int subgroupID= extractor.getInteger(request.getParameter("subgroupid"));
                    group.deleteSubGroup(subgroupID, person.getPersonID());
                }
            }

            if (request.getParameter("studyGroupID") != null){
                int id = extractor.getInteger(request.getParameter("studyGroupID"));
                group.loadById(id, lang);
            } else {
                group.loadByRecordNumber(groupNumber, lang, classID);
            }

            new PageGenerator().writeHtmlPage(new StudyGroupCardMain(lang, person, getServletContext(),
                    new StudyClass().loadAsSimpleObject(classID), group, groupNumber, groupsCount, params),
                    pw, getServletContext());
            pw.flush();
            pw.close();            
        } catch (Exception exc){
            Log.writeLog(exc);
            }
    }
}
