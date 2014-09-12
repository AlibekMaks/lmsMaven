package arta.common.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.menu.MenuGenerator;
import arta.common.html.menu.MenuCommonHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;


public class MenuServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserAuthorized(session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            int menuItemId = extractor.getInteger(request.getParameter("id"));
            if (menuItemId == 0) {
                if (person.getRoleID() != Constants.STUDENT && (person.getRoleID() & Constants.ADMIN)==0 ){
                    //menuItemId = MenuGenerator.TESTING_ITEM;
                } else {
                    menuItemId = 1;                    
                }
            }

            if (request.getParameter("JsHttpRequest") != null){
                String id = extractor.getRequestString(request.getParameter("JsHttpRequest"));
                id = id.substring(0, id.indexOf("-"));

                pw.print("JsHttpRequest.dataReady( " +
                        "  "+id+" ," +
                        "  'messages'," +
                        "   {  " +
                        "   menu : '");

                MenuGenerator generator = new MenuGenerator();
                generator.writeUserMenu(pw, lang, person.getRoleID());

                pw.print("' " +
                    "   }" +
                    ")");

                pw.flush();
                pw.close();
                return;
            } else {
                new Parser(new FileReader("common/menu.txt").read(getServletContext()), pw,
                        new MenuCommonHandler(lang, person.getRoleID(), menuItemId)).parse();
            }

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
