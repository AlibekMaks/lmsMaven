package arta.exams.servlet;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.exams.html.TicketCardMain;
import arta.exams.logic.Ticket;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.login.logic.Access;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class TicketServlet extends HttpServlet {

    int OPTION_OPEN_EXISTS_TICKET = 1;
    int OPTION_CREATE_NEW_TICKET = 2;
    int OPTION_UPDATE_TICKET = 0;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            int option = extractor.getInteger(request.getParameter("option"));
            int examID = extractor.getInteger(request.getParameter("examID"));
            int ticketID = extractor.getInteger(request.getParameter("ticketID"));
            int ticketNumber = extractor.getInteger(request.getParameter("ticketNumber"));
            boolean btnSave = ((request.getParameter("save") != null) & (!extractor.getRequestString(request.getParameter("save")).equals("")));
            boolean isNew = (examID>0 & ticketID == 0);

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);
            Message message = new Message();


            Ticket ticket = new Ticket();

            if(option == OPTION_OPEN_EXISTS_TICKET){
                ticket.loadById(ticketID, lang, true);
                option = OPTION_UPDATE_TICKET;

            } else if(option == OPTION_CREATE_NEW_TICKET){
                ticket.loadById(ticketID, lang, false);
                ticket.extractQuestionsWithParams(extractor, request, true);
                option = OPTION_UPDATE_TICKET;

            } else if(option == OPTION_UPDATE_TICKET){
                ticket.loadById(ticketID, lang, false);
                ticket.setTicketNumber(ticketNumber);
                ticket.extractQuestionsWithParams(extractor, request, false);
            }

            if(btnSave){
                ticket.save(examID, ticketNumber, isNew, message, lang);
            }

            new PageGenerator().writeHtmlPage(new TicketCardMain(lang, person, examID, ticket, params, getServletContext(), message, option),
                    pw, getServletContext());

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

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
//            int examID = extractor.getInteger(request.getParameter("examID"));
//            int ticketID = extractor.getInteger(request.getParameter("ticketID"));
//            int ticketNumber = extractor.getInteger(request.getParameter("ticketNumber"));
//            boolean btnSave = ((request.getParameter("save") != null) & (!extractor.getRequestString(request.getParameter("save")).equals("")));
//            boolean isNew = (examID>0 & ticketID == 0);
//
//
//            SearchParams params = new SearchParams();
//            params.extractParameterValues(request, extractor);
//
//
//            Message message = new Message();
//
//            Ticket ticket = new Ticket();
//              ticket.loadById(ticketID, lang);
//              ticket.extractQuestionsWithParams(extractor, request, isNew);
//
//            if(btnSave){
//                ticket.save(examID, ticketNumber, isNew, message, lang);
//                if(examID>0 & ticketID==0){ // Новый билет
//                } else if(examID>0 & ticketID>0){ // Открыт существующий билет
//                }
//            }
//
//            new PageGenerator().writeHtmlPage(new TicketCardMain(lang, person, examID, ticket, params, getServletContext(), message),
//                    pw, getServletContext());
//
//            pw.flush();
//            pw.close();
//        } catch (Exception exc){
//            Log.writeLog(exc);
//        }
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
//            int examID = extractor.getInteger(request.getParameter("examID"));
//            int ticketID = extractor.getInteger(request.getParameter("ticketID"));
//
//            SearchParams params = new SearchParams();
//            params.extractParameterValues(request, extractor);
//
//            Message message = new Message();
//
//            Ticket ticket = new Ticket();
//             ticket.loadById(ticketID, lang);
//
//            new PageGenerator().writeHtmlPage(new TicketCardMain(lang, person, examID, ticket, params, getServletContext(), message),
//                    pw, getServletContext());
//
//            pw.flush();
//            pw.close();
//
//        } catch (Exception exc){
//            Log.writeLog(exc);
//        }
//    }

}
