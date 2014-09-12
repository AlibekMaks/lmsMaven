package arta.tests.testing.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingStudentsSearchParams;
import arta.tests.testing.logic.TestingStudent;
import arta.tests.testing.html.AddedStudentsListHandler;
import arta.tests.testing.html.AddedStudentsListMainHandler;
import arta.tests.testing.html.StudentsToAddListMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:19:14
 * To change this template use File | Settings | File Templates.
 */
public class TestingStudentsServlet extends HttpServlet {

    public static final int SEARCH_TO_ADD_OPTION = 1;
    public static final int ADD_TO_TESTING_OPTION = 2;
    public static final int DELETE_OPTION = -1;
    public static final int CHANGE_OPTION = 3;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*try {

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.TUTOR, session)) {
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }


            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));


            Testing testing = (Testing) session.getAttribute("testing");

            int option = extractor.getInteger(request.getParameter("option"));

            if (option == CHANGE_OPTION){
                new PageGenerator().writeHtmlPage(new AddedStudentsListMainHandler(lang, person.getRoleID(),
                        getServletContext(), testing),
                        pw,
                        getServletContext());
            } else if (option == SEARCH_TO_ADD_OPTION ){

                TestingStudentsSearchParams params = new TestingStudentsSearchParams();
                params.extractParameterValues(request, extractor);

                new PageGenerator().writeHtmlPage(new StudentsToAddListMainHandler(lang, person,
                        getServletContext(), testing, params), pw, getServletContext());

            } else if (option == ADD_TO_TESTING_OPTION){

                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()){
                    String name = (String)en.nextElement();
                    if (name.length() >  4 && name.substring(0, 4).equals("std_")){
                        int studentID = extractor.getInteger(name.substring(4, name.length()));
                        if (studentID > 0){
                            TestingStudent student = new TestingStudent();
                            student.studentID = studentID;
                            student.studentName = extractor.getRequestString(request.getParameter("std_"+
                                    studentID+"_name"));
                            student.className = extractor.getRequestString(request.getParameter("std_"+
                                    studentID+"_class"));
                            student.subjectName = extractor.getRequestString(request.getParameter("std_"+
                                    studentID+"_subject"));
                            student.subgroupID = extractor.getInteger(request.getParameter("std_"+
                                    studentID+"_subgroupID"));
                            testing.addStudent(student);
                        }
                    }
                }


                TestingStudentsSearchParams params = new TestingStudentsSearchParams();
                params.extractParameterValues(request, extractor);

                new PageGenerator().writeHtmlPage(new StudentsToAddListMainHandler(lang, person,
                        getServletContext(), testing, params), pw, getServletContext());

                new PageGenerator().writeHtmlPage(new AddedStudentsListMainHandler(lang, person.getRoleID(),
                        getServletContext(), testing),
                        pw,
                        getServletContext());
            } else if (option == DELETE_OPTION){

                int studentID = extractor.getInteger(request.getParameter("studentID"));
                testing.deleteStudent(studentID);
                new PageGenerator().writeHtmlPage(new AddedStudentsListMainHandler(lang, person.getRoleID(),
                        getServletContext(), testing),
                        pw,
                        getServletContext());
                
            }


        } catch (Exception exc) {
            Log.writeLog(exc);
        }*/
    }
}
