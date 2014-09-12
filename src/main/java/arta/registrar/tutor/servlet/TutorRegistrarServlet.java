package arta.registrar.tutor.servlet;

import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import arta.registrar.tutor.logic.Registrar;
import arta.registrar.tutor.logic.SimpleMark;
import arta.registrar.tutor.logic.RegistrarMessages;
import arta.registrar.tutor.html.RegistrarCardMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ArrayList;

public class TutorRegistrarServlet extends HttpServlet {

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

            int studygroupID = extractor.getInteger(request.getParameter("studygroupID"));
            int subgroupID = extractor.getInteger(request.getParameter("subgroupID"));

            int month = extractor.getInteger(request.getParameter("month"));
            int year = extractor.getInteger(request.getParameter("year"));

            Message message = new Message();

            ArrayList<SimpleMark> marks = new ArrayList<SimpleMark>();

            Enumeration en = request.getParameterNames();
            String absent = RegistrarMessages.ABSENT_RU;
            if (lang == Languages.KAZAKH)
                absent = RegistrarMessages.ABSENT_KZ;
            else if (lang == Languages.ENGLISH)
                absent = RegistrarMessages.ABSENT_EN;

            while (en.hasMoreElements()){

                String name = (String) en.nextElement();
                if (name.length() > 6 && name.substring(0,4).equals("mark")){
                    int mark = -2;
                    if (request.getParameter(name) == null || request.getParameter(name).length() == 0)
                        continue;
                    if (extractor.getRequestString(request.getParameter(name)).equals(absent))
                        mark = -1;
                    else {
                        mark = extractor.getInteger(request.getParameter(name));
                        if (mark < 0 || mark > Constants.MAX_MARK_VALUE)
                            continue;
                    }
                    name = name.substring(name.indexOf("_")+1, name.length());
                    int studentID = extractor.getInteger(name.substring(0, name.indexOf("_")));
                    name = name.substring(name.indexOf("_")+1, name.length());
                    SimpleMark simpleMark = new SimpleMark();
                    simpleMark.date = name;
                    simpleMark.mark = mark;
                    simpleMark.studentID = studentID;
                    marks.add(simpleMark);
                }
            }

            Registrar registrar = new Registrar(studygroupID, subgroupID);
            registrar.putMarks(marks, studygroupID);

            int action = extractor.getInteger(request.getParameter("action"));

            if (action == -1){
                if (month == 1){
                    month = 12;
                    year -- ;
                } else {
                    month --;
                }
            }
            if (action == 1){
                if (month == 12){
                    year ++;
                    month = 1;
                } else {
                    month ++;
                }
            }
            if (action == -2){
                month = extractor.getInteger(request.getParameter("new_month"));
                year = extractor.getInteger(request.getParameter("new_year"));
            }


            registrar.load(month, year);

            new PageGenerator().writeHtmlPage(new RegistrarCardMainHandler(month, year, registrar,
                    lang, person, getServletContext(), studygroupID, subgroupID, message),
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

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            int studygroupID = extractor.getInteger(request.getParameter("studygroupID"));
            int subgroupID = extractor.getInteger(request.getParameter("subgroupID"));

            int month = extractor.getInteger(request.getParameter("month"));
            int year = extractor.getInteger(request.getParameter("year"));

            if (month == 0){
                month = new Date().month;
            }
            if (year == 0){
                year = new Date().year;
            }

            int action = extractor.getInteger(request.getParameter("action"));
            Registrar registrar = new Registrar(studygroupID, subgroupID);

            Message message = new Message();

            if (action == -3){
                int day = extractor.getInteger(request.getParameter("day"));
                Date date = new Date(year, month, day);
                int studentID = extractor.getInteger(request.getParameter("studentID"));
                registrar.delete(date, studentID, studygroupID, message, lang);
            }


            registrar.load(month, year);

            new PageGenerator().writeHtmlPage(new RegistrarCardMainHandler(month, year, registrar,
                    lang, person, getServletContext(), studygroupID, subgroupID, message),
                    pw, getServletContext());
            pw.flush();                                   
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
