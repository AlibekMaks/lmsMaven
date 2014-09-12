package arta.log.servlet;

import arta.common.logic.util.Log;
import arta.common.logic.download.ResourceDownloader;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

public class GetLogServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            response.addHeader(ResourceDownloader.CONTENT_DISPOSITON, ResourceDownloader.ATTACHMENT +
                    ResourceDownloader.FILE + "logs.txt\"");
            OutputStream out = response.getOutputStream();
            Log.writeLogs(out);
            out.flush();
            out.close();
        } catch (Exception exc){
            exc.printStackTrace();
        }
    }
}
