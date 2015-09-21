package arta.tests.parser.servlet;

import arta.tests.parser.logic.ParseData;
import arta.tests.parser.html.ImportPageMainHandler;
import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.PageGenerator;
import arta.common.http.HttpRequestParser;
import arta.common.http.SimpleHandler;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;
import kz.arta.plt.common.IntegerGetter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;


public class TestImportServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();
        try{
            if (!Access.isUserInRole(Constants.TUTOR, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }
            int contentLength = extractor.getInteger(request.getHeader("Content-Length"));
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Person person = (Person) session.getAttribute("person");
            Message message = new Message();
            String testName = "";
            int testSubjectID = 0;
            int type = 0;

            TestsSearchParams params = new TestsSearchParams();

            if (contentLength > 14680064){
                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_HAS_NOT_BEEN_IMPORTED, null));
                message.setMessage(MessageManager.getMessage(lang, TestMessages.TOO_LARGE_SIZE, null));
                message.messageType = Message.ERROR_MESSAGE;
                SimpleHandler handler = new SimpleHandler();
                HttpRequestParser parser = new HttpRequestParser(handler, request);
                parser.parse();
                params.extractParameterValues(handler, extractor);
                testName = extractor.getRequestString(handler.getParameter("name"));
                testSubjectID = extractor.getInteger(handler.getParameter("subject"));
                type = extractor.getInteger(handler.getParameter("type"));
            } else {
                IntegerGetter integerGetter = new IntegerGetter();
                String fileName = null;
                ParseData parseData;
                InputStream inputStream = null;
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();
                    String name = item.getFieldName();

                    if (item.isFormField()) //your code for getting form fields
                    {
                        String value = item.getString();
                        if (name.equals("type")) {
                            type = integerGetter.getInteger(value);
                        } else if (name.equals("subject")) {
                            testSubjectID = integerGetter.getInteger(value);
                        } else if (name.equals("name")) {
                            testName = integerGetter.getRequestString(value);
                        }
                    }

                    if (!item.isFormField()) {
                        fileName = FilenameUtils.getName(item.getName());
                        inputStream = item.getInputStream();
                    }
                }

                parseData = new ParseData(inputStream, type, fileName);

                if (!parseData.check(message, lang)) {
                    new PageGenerator().writeHtmlPage(new ImportPageMainHandler(person, lang, message,
                            getServletContext(), testName, testSubjectID, params), pw, getServletContext());
                    pw.flush();
                    pw.close();
                    return;
                }

                boolean saved = parseData.parserAndSave(person.getPersonID(), person.getPersonID(), request.getRemoteHost(), message, lang, testName, testSubjectID);

                if (saved) {
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.TEST_HAS_BEEN_SAVED_SUCCESSFULLY, null));
                }
            }

            new PageGenerator().writeHtmlPage(new ImportPageMainHandler(person, lang, message, getServletContext(),
                    testName, testSubjectID, params), pw, getServletContext());
            pw.flush();
            pw.close();
            return;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();

        try{
            if (!Access.isUserInRole(Constants.TUTOR, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Person person = (Person) session.getAttribute("person");

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new ImportPageMainHandler(person, lang, null, getServletContext(), "", 0, params),
                    pw, getServletContext());
            pw.flush();
            pw.close();
            return;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
