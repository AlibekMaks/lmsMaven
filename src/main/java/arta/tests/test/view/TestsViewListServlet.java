package arta.tests.test.view;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;
import arta.common.logic.util.*;
import arta.common.html.handler.PageGenerator;
import arta.login.logic.Access;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;

public class TestsViewListServlet extends HttpServlet {

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
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            MimeParser parser = MimeParser.getInstance();
            ParsedData data = parser.parseOnly(request);

            String search = null;
            Date createStart = null;
            Date  createFinish = null;
            Date modifyStart = null;
            Date modifyFinish = null;
            int partNumber = 0;
            int countInPart = 30;
            int sort = 0;

            String tmp = data.getParameter("search");
            if (tmp!=null){
                search = new String (tmp.getBytes(Encoding.ISO), Encoding.UTF);
            }
            tmp = data.getParameter("createStart");
            if (tmp!=null){
                createStart = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = data.getParameter("createFinish");
            if (tmp!=null){
                createFinish = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = data.getParameter("modifyStart");
            if (tmp!=null){
                modifyStart = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = data.getParameter("modifyFinish");
            if (tmp!=null){
                modifyFinish = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = data.getParameter("sort");
            if (tmp!=null){
                sort = extractor.getInteger(tmp);
            }
            tmp = data.getParameter("partNumber");
            if (tmp!=null)
                partNumber = extractor.getInteger(tmp);
            tmp = data.getParameter("countInPart");
            if (tmp!=null)
                countInPart = extractor.getInteger(tmp);

            TestsListMainHandler handler = new TestsListMainHandler(lang, person.getRoleID(), sort,
                    partNumber, countInPart, createStart, createFinish, modifyStart, modifyFinish,
                    search, getServletContext());
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        }catch (Exception exc){
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
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            String search = null;
            Date createStart = null;
            Date  createFinish = null;
            Date modifyStart = null;
            Date modifyFinish = null;
            int partNumber = 0;
            int countInPart = 30;
            int sort = 0;

            String tmp = request.getParameter("search");
            if (tmp!=null){
                search = new String (tmp.getBytes(Encoding.ISO), Encoding.UTF);
            }
            tmp = request.getParameter("createStart");
            if (tmp!=null){
                createStart = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = request.getParameter("createFinish");
            if (tmp!=null){
                createFinish = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = request.getParameter("modifyStart");
            if (tmp!=null){
                modifyStart = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = request.getParameter("modifyFinish");
            if (tmp!=null){
                modifyFinish = new Date(tmp, Date.FROM_INPUT);
            }
            tmp = request.getParameter("sort");
            if (tmp!=null){
                sort = extractor.getInteger(tmp);
            }
            tmp = request.getParameter("partNumber");
            if (tmp!=null)
                partNumber = extractor.getInteger(tmp);
            tmp = request.getParameter("countInPart");
            if (tmp!=null)
                countInPart = extractor.getInteger(tmp);

            TestsListMainHandler handler = new TestsListMainHandler(lang, person.getRoleID(), sort,
                    partNumber, countInPart, createStart, createFinish, modifyStart, modifyFinish,
                    search, getServletContext());
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
