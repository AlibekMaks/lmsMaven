package arta.tests.imageExchange;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.http.MimeTypes;
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

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 25.03.2008
 * Time: 14:16:01
 * To change this template use File | Settings | File Templates.
 */

public class InsertObjectServlet extends HttpServlet {

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
            int lang = Access.hasLanguage(session);

            MimeParser parser=MimeParser.getInstance();
            ParsedData data=parser.parseOnly(request);

            byte [] file = data.getBinaryContents("file");

            int width = extractor.getInteger(data.getParameter("width"));
            int height = extractor.getInteger(data.getParameter("height"));
            String signature = extractor.getRequestString(data.getParameter("signature"));
            int testID = extractor.getInteger(data.getParameter("testID"));
            int type = extractor.getInteger(data.getParameter("type"));


            ImageUpload upload = new ImageUpload();
            int newObjectID = upload.uploadImage(file, person.getPersonID(), testID, type,
                    width, height, signature, MimeTypes.getMimeType(data.getOriginalFileName("file")));

            new Parser(new FileReader("tests/objects/object.card.html").read(getServletContext()),
                    pw, new InsertObjectHandler(lang, testID, newObjectID != 0,
                    MimeTypes.getMimeType(data.getOriginalFileName("file")),
                    newObjectID, width, height)).parse();


        } catch(Exception exc){

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

            int lang = Access.hasLanguage(session);
            int testingID = extractor.getInteger(request.getParameter("testingID"));

            new Parser(new FileReader("tests/objects/object.card.html").read(getServletContext()),
                    pw, new InsertObjectHandler(lang, testingID, false)).parse();

        } catch(Exception exc){
            Log.writeLog(exc);
        }

    }
}
