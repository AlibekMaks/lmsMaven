package arta.tests.servlets;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.tests.imageExchange.ImageUpload;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class CreateNewImage extends HttpServlet {

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


            MimeParser parser=MimeParser.getInstance();
            ParsedData data=parser.parseOnly(request);

            byte[] image = data.getBinaryContents("img");
            ImageUpload imageUpload = new ImageUpload();

            int width = extractor.getInteger(data.getParameter("width"));
            int height = extractor.getInteger(data.getParameter("height"));
            String signature = extractor.getRequestString(data.getParameter("timestamp"));
            int testID = extractor.getInteger(data.getParameter("testid"));
            int type = extractor.getInteger(data.getParameter("type"));

            int newImageID = imageUpload.uploadImage(Base64.decode(new String(image)), person.getPersonID(),
                    testID, type, width, height, signature);

            pw.print(newImageID);
            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
