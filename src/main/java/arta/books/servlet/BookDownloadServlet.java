package arta.books.servlet;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.download.ResourceDownloader;
import arta.books.logic.BookResource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;


public class BookDownloadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            DataExtractor extractor = new DataExtractor();

       /*     if (!Access.isUserAuthorized(session)){
                PrintWriter pw = response.getWriter();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }             */

            boolean inline = request.getParameter("inline") != null;
            int bookID = extractor.getInteger(request.getParameter("bookID"));

            BookResource bookResource = new BookResource(bookID);
            ResourceDownloader resourceDownloader = new ResourceDownloader(bookResource);
            resourceDownloader.download(request, response, inline);

        } catch (Exception exc){
//            Log.writeLog(exc);
        }
    }
}
