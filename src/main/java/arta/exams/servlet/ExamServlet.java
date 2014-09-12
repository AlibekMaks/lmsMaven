package arta.exams.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.exams.html.ExamCardMain;
import arta.exams.logic.Exam;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.StudyGroup;
import arta.login.logic.Access;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class ExamServlet extends HttpServlet {

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

            Exam exam = new Exam();
              exam.setExamID(extractor.getInteger(request.getParameter("examID")));
              exam.setExamName(extractor.getRequestString(request.getParameter("examName")));
              exam.setQuestionCount(extractor.getInteger(request.getParameter("questionCount")));

            Message message = new Message();
            exam.save(message, lang, params, person.getPersonID());

            new PageGenerator().writeHtmlPage(new ExamCardMain(lang, person, exam, params, getServletContext(), message),
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
                    int examID = extractor.getInteger(request.getParameter("examID"));
                    int ticketID = extractor.getInteger(request.getParameter("ticketID"));
                    Ticket ticket = new Ticket();
                      ticket.setTicketID(ticketID);
                      ticket.delete(examID, message, lang);
                }
            }            

            Exam exam = new Exam();
            if (request.getParameter("examID") != null){
                int examID = extractor.getInteger(request.getParameter("examID"));
                exam.loadById(examID, lang, true);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                exam.loadByRecordNumber(params, recordNumber, lang);
            }

            new PageGenerator().writeHtmlPage(new ExamCardMain(lang, person, exam, params, getServletContext(), message),
                    pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
