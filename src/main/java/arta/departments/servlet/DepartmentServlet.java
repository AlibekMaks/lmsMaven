package arta.departments.servlet;

import arta.books.logic.Book;
import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.*;
import arta.departments.html.DepartmentCardMain;
import arta.departments.logic.Department;
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


public class DepartmentServlet extends HttpServlet {
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
            int departmentID = extractor.getInteger(request.getParameter("departmentID"));


            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            Department department = new Department();
            department.setName(Languages.KAZAKH, extractor.getRequestString(request.getParameter("namekz")));
            department.setName(Languages.RUSSIAN, extractor.getRequestString(request.getParameter("nameru")));
            department.setDepartmentID(departmentID);

            Message message = new Message();
            department.save(message, lang, params, person.getPersonID());

            if (department.getDepartmentID() > 0)
                department.load();

            new PageGenerator().writeHtmlPage(new DepartmentCardMain(person, getServletContext(), lang,
                    department, params, message), pw, getServletContext());

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
            Message message = null;

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

            Department department = new Department();

            if (request.getParameter("option") != null){
                if (extractor.getInteger(request.getParameter("option")) ==  -1){
                    message = new Message();
                    int bookID = extractor.getInteger(request.getParameter("bookID"));
                    Book book = new Book();
                    book.setId(bookID);
                    book.delete(message, lang);
                }
            }            

            if (request.getParameter("departmentID") != null){
                int departmentID = extractor.getInteger(request.getParameter("departmentID"));
                department.loadById(departmentID);
            } else {
                int recordNumber = extractor.getInteger(request.getParameter("recordNumber"));
                department.loadByRecordNumber(params, recordNumber, lang);
            }

            new PageGenerator().writeHtmlPage(new DepartmentCardMain(person, getServletContext(), lang,
                    department, params, message), pw, getServletContext());

            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
