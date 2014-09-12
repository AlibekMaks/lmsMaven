package arta.scheduled.tests.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.login.logic.Access;
import arta.scheduled.tests.html.schStudentCardMain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class schStudentServlet extends HttpServlet {
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

            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            Student student = new Student();
            student.setPersonID(extractor.getInteger(request.getParameter("studentID")));
            student.setLastname(extractor.getRequestString(request.getParameter("lastname")));
            student.setFirstname(extractor.getRequestString(request.getParameter("firstname")));
            student.setPatronymic(extractor.getRequestString(request.getParameter("patronymic")));
            student.setAdress(extractor.getRequestString(request.getParameter("adress")));
            student.setParents(extractor.getRequestString(request.getParameter("parents")));
            student.setPhone(extractor.getRequestString(request.getParameter("phone")));
            student.setClassID(extractor.getInteger(request.getParameter("class")));
            student.getBirthdate().loadDate(request.getParameter("birth"), Date.FROM_INPUT);
            student.getStartdate().loadDate(request.getParameter("start"), Date.FROM_INPUT);
            student.getStazOverallStart().loadDate(request.getParameter("staz_o"), Date.FROM_INPUT);
            student.getStazSocietyStart().loadDate(request.getParameter("staz_s"), Date.FROM_INPUT);
            student.getStazPostStart().loadDate(request.getParameter("staz_p"), Date.FROM_INPUT);
            student.setEducationUZ(extractor.getRequestString(request.getParameter("eduUZ")));
            student.setEducationProfession(extractor.getRequestString(request.getParameter("eduProfession")));
            student.setEducationQualification(extractor.getRequestString(request.getParameter("eduQualification")));
            student.setHadUpgradedQualification(request.getParameter("upgrade") != null);
            student.setDirector(request.getParameter("isdir") != null);
            student.setInMaternityLeave(request.getParameter("ismat") != null);


            Message message = new Message();
            student.save(message,  lang,  params, person.getPersonID());

            new PageGenerator().writeHtmlPage(new schStudentCardMain(lang, getServletContext(), params,
                    student, message, person), pw, getServletContext());

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

            StudentSearchParams params = new StudentSearchParams();
            params.extractParameterValues(request, extractor);

            Student student = new Student();

            if (request.getParameter("studentID")!=null){
                int id = extractor.getInteger(request.getParameter("studentID"));
                student.loadById(id);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                student.loadByRecordNumber(recordNumber, params);
            }
            
            new PageGenerator().writeHtmlPage(new schStudentCardMain(lang, getServletContext(), params,
                    student, null, person), pw, getServletContext());

            pw.flush();
            pw.close();

        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
