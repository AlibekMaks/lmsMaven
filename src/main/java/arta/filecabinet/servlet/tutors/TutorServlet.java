package arta.filecabinet.servlet.tutors;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.login.logic.Access;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.html.tutors.TutorCardMain;
import arta.filecabinet.html.tutors.TutorNotFoundHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class TutorServlet extends HttpServlet {
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

            Person person= (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            Tutor tutor = new Tutor();
            tutor.setPersonID(extractor.getInteger(request.getParameter("tutorID")));
            tutor.setLastname(extractor.getRequestString(request.getParameter("lastname")));
            tutor.setFirstname(extractor.getRequestString(request.getParameter("firstname")));
            tutor.setAdress(extractor.getRequestString(request.getParameter("adress")));
            tutor.setEducation(extractor.getRequestString(request.getParameter("education")));
            tutor.setPatronymic(extractor.getRequestString(request.getParameter("patronymic")));
            tutor.setPhone(extractor.getRequestString(request.getParameter("phone")));
            tutor.setRoleID(Constants.TUTOR);
            tutor.setCheirman(Constants.ISCHAIRMAN);
            tutor.setViceCheirman(Constants.ISCHAIRMAN);
            tutor.setMembers(Constants.ISCHAIRMAN);
            tutor.setSecretary(Constants.ISCHAIRMAN);
            tutor.setDepartmentID(extractor.getInteger(request.getParameter("department")));
            if(request.getParameter("isadmin")!=null) tutor.setRoleID(tutor.getRoleID()|Constants.ADMIN);
            if(request.getParameter("ischairman")==null) tutor.setCheirman(Constants.NOTCHAIRMAN);
            if(request.getParameter("isvicechairman")==null) tutor.setViceCheirman(Constants.NOTCHAIRMAN);
            if(request.getParameter("ismembers")==null) tutor.setMembers(Constants.NOTCHAIRMAN);
            if(request.getParameter("issecretary")==null) tutor.setSecretary(Constants.NOTCHAIRMAN);

            tutor.getBirthdate().loadDate(request.getParameter("birth"), Date.FROM_INPUT);
            tutor.getStartdate().loadDate(request.getParameter("start"), Date.FROM_INPUT);
            Message message = new Message();
            tutor.save(message, lang, params, person.getPersonID());

            new PageGenerator().writeHtmlPage(new TutorCardMain(person, tutor, lang, getServletContext(),
                    params, message), pw, getServletContext());
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

            Tutor tutor = new Tutor();
            boolean loaded = false;

            if (request.getParameter("tutorID")!=null){
                int tutorID = extractor.getInteger(request.getParameter("tutorID"));
                loaded = tutor.loadById(tutorID);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                loaded = tutor.loadByRecordNumber(recordNumber, params);
            }

            if (tutor.getPersonID() == 0 || loaded){
                new PageGenerator().writeHtmlPage(new TutorCardMain(person, tutor, lang, getServletContext(),
                        params, null), pw, getServletContext());
            } else {
                new Parser(new FileReader("common/not.found.txt").read(getServletContext()), pw,
                        new TutorNotFoundHandler(lang, getServletContext(), params)).parse();
            }
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
