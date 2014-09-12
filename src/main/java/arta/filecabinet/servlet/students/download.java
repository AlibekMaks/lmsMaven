package arta.filecabinet.servlet.students;

import arta.common.html.handler.PageGenerator;
import arta.common.http.HttpRequestParser;
import arta.common.http.SimpleHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.servlet.students.upload.file.MainPageHandler;
import arta.login.logic.Access;
import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;
import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class download extends HttpServlet {

    public byte[] getFile(String filename)
    {
        byte[] bytes = null;
        try
        {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            fis.read(bytes);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return bytes;
    }

    public void sendFile(HttpServletResponse resp, byte[] bytes, String name) throws IOException
    {
        ServletOutputStream stream = null;
        stream = resp.getOutputStream();
        resp.setContentType("application/xls");
        resp.addHeader("Content-Type", "application/xls");
        resp.addHeader("Content-Disposition", "attachment;filename=\"" + name + "\"");
        resp.setContentLength((int) bytes.length);
        stream.write(bytes);
        stream.flush();
        stream.close();
//      httpServletResponse.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "." + ext + "\"");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            ImportTemplate(response);
        } catch (Exception exc){

        }
    }

    public boolean ImportTemplate(HttpServletResponse resp) throws Exception{//(String FileName) throws Exception{
        try{
            String FileName = "template.xls";
            InputStream fis = getClass().getClassLoader().getResourceAsStream(FileName);
            resp.setHeader("Content-Type","application/octet-stream;"); //octet-stream
            resp.setHeader("Content-Disposition", "filename=\"" + FileName + "\"");
            try{
                BufferedInputStream in = new BufferedInputStream(fis);
                BufferedOutputStream binout = new BufferedOutputStream(resp.getOutputStream());
                int ch=in.read();
                while(ch!=-1)
                {
                    binout.write(ch);
                    ch=in.read();
                }
                binout.flush();
                binout.close();
                in.close();
            }
            catch(IOException ioe)
            {
                //out.println("Unable to get access");
            }
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return false;
    }

}
