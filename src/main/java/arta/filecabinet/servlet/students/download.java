package arta.filecabinet.servlet.students;

import arta.common.logic.util.Log;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class download extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            ImportTemplate(response);
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public boolean ImportTemplate(HttpServletResponse resp) throws Exception{
        try{

            String FileName = "template.xls";
            OutputStream outputStream = resp.getOutputStream();
            resp.setHeader("Content-Type","application/octet-stream;"); //octet-stream
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + FileName + "\"");


            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(FileName).getFile());
            InputStream fis = new FileInputStream(file);

            try{
                IOUtils.copy(fis, outputStream);
            }catch(Exception exp){
                exp.printStackTrace();
            }
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return false;
    }

}
