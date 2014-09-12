package arta.tests.testing.servlet;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingStudent;
import arta.tests.testing.html.TestingCardMainHandler;
import arta.tests.testing.html.TestsListMain;
import arta.tests.testing.html.AppointmentResultsMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;


public class TestingServlet extends HttpServlet {

    public static final int LOAD_OPTION = 2;
    public static final int DELETE_OPTION = -1;
    public static final int SAVE_OPTION = 3;
    public static final int TESTS_OPEN_OPTION = 1;
    public static final int ACCEPT_OPTION = 4;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Testing testing = null;
            System.out.println("TestingServlet:  option="+option);

            if (option == LOAD_OPTION){  // Первоначальная загрузка страницы
                System.out.println("option == LOAD_OPTION");
                testing = new Testing();
                int testingID = extractor.getInteger(request.getParameter("testingID"));
                if (testingID == 0){
                    testing.setTutorID(person.getPersonID());
                } else {
                    testing.setTestingID(testingID);
                    testing.setTutorID(person.getPersonID());
                    testing.load(lang);
                }
                new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
                        testing, null), pw, getServletContext());
                session.setAttribute("testing", testing);
                pw.flush();
                pw.close();
                return;
            } else if (option == SAVE_OPTION){ // Сохранение теста и его параметров
                System.out.println("option == SAVE_OPTION");
                Message message = new Message();
                testing = (Testing) session.getAttribute("testing");
                
                testing.save(message, lang, person.getPersonID());

                new PageGenerator().writeHtmlPage(
                        new AppointmentResultsMainHandler(getServletContext(), lang, person.getRoleID(),
                            message, testing), pw, getServletContext());
                
                pw.flush();
                pw.close();
            } else if (option == TESTS_OPEN_OPTION){
                System.out.println("option == TESTS_OPEN_OPTION");
                testing = (Testing) session.getAttribute("testing");
                testing.setTestingName(extractor.getRequestString(request.getParameter("name")));
                testing.setTestingTime(extractor.getInteger(request.getParameter("duration")));
                new PageGenerator().writeHtmlPage(new TestsListMain(person, lang, new SearchParams(), getServletContext(),
                       testing), pw, getServletContext());
               pw.flush();
               pw.close();
            } else if (option == ACCEPT_OPTION){ // + saveNewStudentsList сохранение первой страницы
                System.out.println("option == ACCEPT_OPTION");
                testing = (Testing) session.getAttribute("testing");

                testing.getTestingDate().loadDate(request.getParameter("date"), Date.FROM_INPUT);
                testing.getTestingStartTime().hour = extractor.getInteger(request.getParameter("hour"));
                testing.getTestingStartTime().minute = extractor.getInteger(request.getParameter("minute"));
                testing.getTestingFinishTime().hour = extractor.getInteger(request.getParameter("hourfinish"));
                testing.getTestingFinishTime().minute = extractor.getInteger(request.getParameter("minutefinish"));
                testing.setTestingTime(extractor.getInteger(request.getParameter("duration")));
                testing.setTestingName(extractor.getRequestString(request.getParameter("name")));
                
                saveNewStudentsList(request, testing);
                
                
                new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
                    testing, null), pw, getServletContext());
                session.setAttribute("testing", testing);
                pw.flush();
                pw.close();
            } else {
                System.out.println("option == else");
                testing = (Testing) session.getAttribute("testing");
                new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
                    testing, null), pw, getServletContext());
                session.setAttribute("testing", testing);
                pw.flush();
                pw.close();
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

	private void saveNewStudentsList(HttpServletRequest request, Testing testing) {
        System.out.println("saveNewStudentsList");
		ArrayList <Integer> oldTestingStudentsList = testing.students;
		
		ArrayList <Integer> newTestingStudentsList = new ArrayList <Integer> ();
		for (Object paramObj : request.getParameterMap().keySet()) {
			String param = (String)paramObj;
			if (param.startsWith("check_std_")) {
				String studentIdStr = param.substring(10);
                System.out.println("param="+param);
				int studentId = 0;
				try{
					studentId = Integer.parseInt(studentIdStr);
                    System.out.println("studentId="+studentId);
				}catch (Exception e) {}
				
				if (studentId > 0) {
                    System.out.println("newTestingStudentsList.add("+studentId+")");
					newTestingStudentsList.add(studentId);
				}
			}
		}
		ArrayList <Integer> studentsToAdd = new ArrayList <Integer> ();
		
		for (int newStud : newTestingStudentsList) {
			boolean found = Collections.binarySearch(oldTestingStudentsList, newStud) >= 0;
			if (!found) {
				studentsToAdd.add(newStud);
                System.out.println("newStud="+newStud);
			}
		}
		
		ArrayList <Integer> studentsToDelete = new ArrayList <Integer> ();
		
		for (int oldStud : oldTestingStudentsList) {
			boolean found = Collections.binarySearch(newTestingStudentsList, oldStud) >= 0;
			if (!found) {
				studentsToDelete.add(oldStud);
                System.out.println("oldStud="+oldStud);
			}
		}
		
		for (int studToDelete : studentsToDelete) {
			testing.deleteStudent(studToDelete);
            System.out.println("studToDelete="+studToDelete);
		}
		
		for (int studToAdd : studentsToAdd) {
			testing.addStudent(studToAdd);
            System.out.println("studToAdd="+studToAdd);
		}
	}
}
