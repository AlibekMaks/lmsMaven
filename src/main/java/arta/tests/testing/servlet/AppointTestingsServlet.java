package arta.tests.testing.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.login.logic.Access;
import arta.tests.testing.html.AppointmentResultsMainHandler;
import arta.tests.testing.html.TestingCardMainHandler;
import arta.tests.testing.html.TestsListMain;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;


public class AppointTestingsServlet extends HttpServlet {

    public static final int LOAD_OPTION = 2;
    public static final int DELETE_OPTION = -1;
    public static final int SAVE_OPTION = 3;
    public static final int TESTS_OPEN_OPTION = 1;
    public static final int ACCEPT_OPTION = 4;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();


            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            String testingName = extractor.getRequestString( request.getParameter("testingName") );
            String appointbutton = extractor.getRequestString( request.getParameter("appointbutton") );

            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            Testing testing = (Testing) session.getAttribute("testing");
            int testingID = extractor.getInteger(request.getParameter("testingID"));
            if (testingID == 0){
                testing.setTutorID(person.getPersonID());
                testing.setRoleID(person.getRoleID());
            } else {
                /**
                testing.setTestingID(testingID);
                testing.setTutorID(person.getPersonID());
                testing.setRoleID(person.getRoleID());
                testing.load(lang);
                **/
            }

            testing.getTestingDate().loadDate(request.getParameter("date"), Date.FROM_INPUT);
//            testing.getTestingStartTime().hour = extractor.getInteger(request.getParameter("hour"));
//            testing.getTestingStartTime().minute = extractor.getInteger(request.getParameter("minute"));
//            testing.getTestingFinishTime().hour = extractor.getInteger(request.getParameter("hourfinish"));
//            testing.getTestingFinishTime().minute = extractor.getInteger(request.getParameter("minutefinish"));
            testing.setTestingTime(extractor.getInteger(request.getParameter("duration")));
            testing.setTestingName(testingName);
            //testing.setTestingName(extractor.getRequestString(request.getParameter("name")));
            saveNewStudentsList(request, testing, params);
//            new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
//                    testing, null), pw, getServletContext());
//            session.setAttribute("testing", testing);
//            pw.flush();
//            pw.close();
            if(appointbutton.equals(MessageManager.getMessage(lang, Constants.APPOINT_BUTTON_VALUE, null))){  // Была нажата кнопка "Назначить"
                testing.lang = lang;
                testing.person = person;
                testing.message = new Message();
                testing.saveTesting();

                params.partNumber = 0;
            }

            new PageGenerator().writeHtmlPage(new AppointTestingsStudentsListMain(person, testing.message, testing, params, getServletContext(), lang),
                    pw, getServletContext());
            session.setAttribute("testing", testing);
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

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            int option = extractor.getInteger(request.getParameter("option"));
            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);
            Message message = new Message();//null;

            Testing testing = null;

            if (option == LOAD_OPTION){  // Первоначальная загрузка страницы
                testing = new Testing();
                int testingID = extractor.getInteger(request.getParameter("testingID"));
                if (testingID == 0){
                    testing.setTutorID(person.getPersonID());
                    testing.setRoleID(person.getRoleID());
                }

                new PageGenerator().writeHtmlPage(new AppointTestingsStudentsListMain(person, message, testing, params, getServletContext(), lang),
                        pw, getServletContext());
                session.setAttribute("testing", testing);
//                pw.flush();
//                pw.close();
//                return;
//            } else if (option == SAVE_OPTION){ // Сохранение теста и его параметров
//                //Message message = new Message();
//                testing = (Testing) session.getAttribute("testing");
//
//                testing.save(message, lang, person.getPersonID());
//
//                new PageGenerator().writeHtmlPage(
//                        new AppointmentResultsMainHandler(getServletContext(), lang, person.getRoleID(),
//                                message, testing), pw, getServletContext());
//
//                pw.flush();
//                pw.close();
//            } else if (option == TESTS_OPEN_OPTION){
//                testing = (Testing) session.getAttribute("testing");
//                testing.setTestingName(extractor.getRequestString(request.getParameter("name")));
//                testing.setTestingTime(extractor.getInteger(request.getParameter("duration")));
//                new PageGenerator().writeHtmlPage(new TestsListMain(person, lang, new SearchParams(), getServletContext(),
//                        testing), pw, getServletContext());
//                session.setAttribute("testing", testing);
//                pw.flush();
//                pw.close();
//            } else if (option == ACCEPT_OPTION){ // + saveNewStudentsList сохранение первой страницы
//                testing = (Testing) session.getAttribute("testing");
//
//                testing.getTestingDate().loadDate(request.getParameter("date"), Date.FROM_INPUT);
//                testing.getTestingStartTime().hour = extractor.getInteger(request.getParameter("hour"));
//                testing.getTestingStartTime().minute = extractor.getInteger(request.getParameter("minute"));
//                testing.getTestingFinishTime().hour = extractor.getInteger(request.getParameter("hourfinish"));
//                testing.getTestingFinishTime().minute = extractor.getInteger(request.getParameter("minutefinish"));
//                testing.setTestingTime(extractor.getInteger(request.getParameter("duration")));
//                testing.setTestingName(extractor.getRequestString(request.getParameter("name")));
//
//                saveNewStudentsList(request, testing, params);
//
//
//                new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
//                        testing, null), pw, getServletContext());
//                session.setAttribute("testing", testing);
//                pw.flush();
//                pw.close();
            } else { // Переход по страницам
                testing = (Testing) session.getAttribute("testing");
                saveNewStudentsList(request, testing, params);
                new PageGenerator().writeHtmlPage(new AppointTestingsStudentsListMain(person, testing.message, testing, params, getServletContext(), lang),
                        pw, getServletContext());
                session.setAttribute("testing", testing);
//                pw.flush();
//                pw.close();
            }



            //=====================
//            StudentSearchParams params = new StudentSearchParams();
//            params.extractParameterValues(request, extractor);

//            if (extractor.getInteger(request.getParameter("option")) == -1){
//                message = new Message();
//                int studentID = extractor.getInteger(request.getParameter("studentID"));
//                Student student = new Student();
//                student.setPersonID(studentID);
//                student.delete(message, lang, person.getPersonID());
//            }

//            new PageGenerator().writeHtmlPage(new AppointTestingsStudentsListMain(person, message, params, getServletContext(), lang),
//                    pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    private void saveNewStudentsList(HttpServletRequest request, Testing testing, StudentSearchParams params) {
        ArrayList<Integer> oldTestingStudentsList = testing.students;

        ArrayList <Integer> newTestingStudentsList = new ArrayList <Integer> ();
        testing.selectedStudents.clear();

        //
        if(!params.studentList.equals("")){
//            if(params.studentList.lastIndexOf("_") >= params.studentList.length()-1){
//                params.studentList = params.studentList.substring(0, params.studentList.length()-1);
//                System.out.println("params.studentList = " + params.studentList);
//            }
            String[] stdList = params.studentList.split("_");
            for(int i=0; i< stdList.length; i++) {
                String[] param = stdList[i].split("=");
                int studentID = Integer.parseInt(param[0].replace("std", ""));
                int checked = Integer.parseInt(param[1]);

                if(checked==1){
                    testing.selectedStudents.add(studentID);
                }
            }
            testing.students = testing.selectedStudents;
        }

        //
//        for (Object paramObj : request.getParameterMap().keySet()) {
//            String param = (String)paramObj;
//            System.out.println("param = " + param);
//            if (param.startsWith("std")) {
//                String studentIdStr = param.substring(3);
//                int studentId = 0;
//                try{
//                    studentId = Integer.parseInt(studentIdStr);
//                } catch (Exception e) {}
//
//                if (studentId > 0) {
//                    newTestingStudentsList.add(studentId);
//                }
//            }
//        }

    }

}
