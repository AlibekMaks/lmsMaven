package arta.groups.servlet;

import arta.classes.html.ClassCardMain;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.StudyGroup;
import arta.groups.logic.SubGroup;
import arta.classes.logic.StudyClass;
import arta.classes.ClassMessages;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ArrayList;


public class StudyGroupAddServlet extends HttpServlet {
    
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try{
//            HttpSession session = request.getSession();
//            response.setContentType("text/html; charset=utf-8");
//            PrintWriter pw = response.getWriter();
//            DataExtractor extractor = new DataExtractor();
//
//            if (!Access.isUserInRole(Constants.ADMIN, session)){
//                pw.print(Constants.RETURN_TO_MAIN_PAGE);
//                pw.flush();
//                pw.close();
//                return;
//            }
//
//            Person person = (Person) session.getAttribute("person");
//            int lang = extractor.getInteger(session.getAttribute("lang"));
//
//            SearchParams params = new SearchParams();
//            params.extractParameterValues(request, extractor);
//
//            ArrayList<StudyGroup> studyGroups = new ArrayList<StudyGroup> ();
//
//            Enumeration en = request.getParameterNames();
//            while (en.hasMoreElements()){
//                String name = (String) en.nextElement();
//                String value = request.getParameter(name);
//                if (name.length() > 6 && name.substring(0,6).equals("subsel")){
//                    int subjectID = extractor.getInteger(name.substring(6, name.length()));
//                    int count = extractor.getInteger(value);
//                    if (subjectID > 0){
//                        StudyGroup studyGroup = new StudyGroup();
//                        studyGroup.setSubjectID(subjectID);
//                        for (int i=0; i<count; i++){
//                            SubGroup subGroup = new SubGroup();
//                            int tutorID = extractor.getInteger(request.getParameter("sub="+subjectID+"ind="+i));
//                            if (tutorID > 0){
//                                subGroup.setTutorID(tutorID);
//                                studyGroup.subgroups.add(subGroup);
//                            }
//                        }
//                        if (studyGroup.subgroups.size() > 0){
//                            studyGroups.add(studyGroup);
//                        }
//                    }
//                }
//            }
//
//            int classID = extractor.getInteger(request.getParameter("classID"));
//
//            Message message = new Message();
//
//            for (int i=0; i<studyGroups.size(); i++){
//                boolean all = true;
//                Message tmp = new Message();
//                if (!studyGroups.get(i).create(tmp, lang, classID, person.getPersonID())){
//                    for (int k=0; k<tmp.reasons.size(); k++){
//                        message.addReason(tmp.reasons.get(k));
//                    }
//                    all = false;
//                }
//                if (all){
//                    message.setMessageType(Message.INFORMATION_MESSAGE);
//                    message.setMessage(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_BEEN_CREATED));
//                } else {
//                    message.setMessageType(Message.ERROR_MESSAGE);
//                    message.setMessage(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_NOT_BEEN_CREATED));
//                }
//            }
//
//            StudyClass studyClass = new StudyClass();
//            studyClass.loadById(classID, lang);
//
//            new PageGenerator().writeHtmlPage( new ClassCardMain(lang, person, studyClass, params, getServletContext(), message),
//                    pw, getServletContext());
//            pw.flush();
//            pw.close();
//
//        } catch (Exception exc){
//            Log.writeLog(exc);
//        }
//    }

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

            ArrayList<StudyGroup> studyGroups = new ArrayList<StudyGroup> ();

            Enumeration en = request.getParameterNames();
            while (en.hasMoreElements()){
                String name = (String) en.nextElement();
                String value = request.getParameter(name);
                System.out.println("\n");
                System.out.println("name = " + name);
                System.out.println("value = " + value);

                if (name.length() > 3 && name.substring(0,3).equals("sub")){
                    int subjectID = extractor.getInteger(name.substring(3, name.length()));
                    System.out.println("subjectID = " + subjectID);

                    if (subjectID > 0){
                        StudyGroup studyGroup = new StudyGroup();
                        studyGroup.setSubjectID(subjectID);
                        SubGroup subGroup = new SubGroup();
                        subGroup.setTutorID(0);
                        studyGroup.subgroups.add(subGroup);
                        if (studyGroup.subgroups.size() > 0){
                            studyGroups.add(studyGroup);
                        }
                    }
                }
            }

            int classID = extractor.getInteger(request.getParameter("classID"));

            Message message = new Message();

            for (int i=0; i<studyGroups.size(); i++){
                boolean all = true;
                Message tmp = new Message();
                if (!studyGroups.get(i).create(tmp, lang, classID, person.getPersonID())){
                    for (int k=0; k<tmp.reasons.size(); k++){
                        message.addReason(tmp.reasons.get(k));
                    }
                    all = false;
                }
                if (all){
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessage(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_BEEN_CREATED));
                } else {
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessage(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_NOT_BEEN_CREATED));
                }
            }

            StudyClass studyClass = new StudyClass();
            studyClass.loadById(classID, lang);

            new PageGenerator().writeHtmlPage( new ClassCardMain(lang, person, studyClass, params, getServletContext(), message),
                    pw, getServletContext());
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
