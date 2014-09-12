package arta.chat.servlet;

import arta.chat.logic.*;
import arta.chat.html.ChatEnterHandler;
import arta.chat.html.MessagesHandler;
import arta.chat.html.ChatEnterMainHandler;
import arta.common.logic.util.*;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.common.lock.LockManager;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.students.Student;
import arta.login.logic.Access;
import arta.studyroom.logic.StudyRoomsManager;
import arta.studyroom.html.StudyRoomsListMainHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.bentofw.mime.ParsedData;
import com.bentofw.mime.MimeParser;

public class ChatServlet extends HttpServlet {

    public static final int SEND_MESSAGE_OPTION = 2;
    public static final int GET_IN_CHAT_OPTION = 3;
    public static final int GET_MESSAGES_OPTION = 1;
    public static final int ENTER_CHAT = 9;
    public static final int PRINT_MESSAGES = 10;
    public static final int GET_STUDY_ROOMS_LIST = 11;
    public static final int GET_IFRAMES = 12;
    public static final int CHANGE_STUDENT_STATUS_OPTION = 13;

    public static final int GET_IMAGE_DATA_FROM_BOARD = 6;
    public static final int GET_SIMPLE_ELEMENT_FROM_BOARD = 4;
    public static final int SEND_ELEMENTS_TO_USER_OPTION = 5;

    ChatRooms rooms;
    StringTransform trsf;

    public static LockManager chatLocker = new LockManager();

    boolean debug = false;


    public void init() throws ServletException {
        super.init();
        rooms = new ChatRooms();
        trsf = new StringTransform();

        new Timer(120000, new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                rooms.performCheck(Date.getCurrentTimeInMills());
            }

        }).start();


    }

    protected void doPost(HttpServletRequest req, HttpServletResponse response)
            throws ServletException, IOException {

        try{

            HttpSession session = req.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            MimeParser dataParser=MimeParser.getInstance();
            ParsedData data=dataParser.parseOnly(req);
            DataExtractor extractor = new DataExtractor();

            Person person = null; 
            int lang = 0;

            if (debug){

                int roleID = extractor.getInteger(data.getParameter("roleID"));
                int personID = extractor.getInteger(data.getParameter("personID"));
                lang = extractor.getInteger(data.getParameter("lang"));
                String lastname = extractor.getRequestString(data.getParameter("name"));

                if (roleID == Constants.STUDENT){
                    person = new Student();
                } else {
                    person = new Tutor();
                }

                person.setPersonID(personID);
                person.setRoleID(roleID);
                person.setLastname(lastname);

            } else {

                if (!Access.isUserAuthorized(session)){
                    session.invalidate();
                    pw.print(Constants.RETURN_TO_MAIN_PAGE);
                    pw.flush();
                    pw.close();
                    return;
                }

                person = (Person) session.getAttribute("person");
                lang = extractor.getInteger(session.getAttribute("lang"));

            }



            StringTransform stringTransform = new StringTransform();

            int roomID = extractor.getInteger(data.getParameter("roomID"));
            int option = extractor.getInteger(data.getParameter("option"));

            if (option == 0){
                MessageExtractor messageExtractor = new MessageExtractor();
                ChatMessage message = messageExtractor.extract(data);

                if (message!=null){
                    Time t = new Time();
                    message.senderName = person.getShortName();
                    message.time = Date.getCurrentTimeInMills();
                    message.messageTime = t.getValue();
                    message.senderPersonID = person.getPersonID();
                    message.senderRoleID = person.getRoleID();

                    int lockInt = roomID;
                    try{
                        chatLocker.execute(lockInt);
                        rooms.addMessage(message, Date.getCurrentTimeInMills(), roomID);
                    } catch(Exception exc){
                        Log.writeLog(exc);
                    } finally {
                        chatLocker.finished(lockInt);
                    }

                }
            } else if (option == GET_IMAGE_DATA_FROM_BOARD){

                int lockInt = roomID;
                try{
                    chatLocker.execute(lockInt);
                    rooms.addBBImage(data.getBinaryContents("element"), Date.getCurrentTimeInMills(), person.getRoleID(),
                            person.getPersonID(), roomID);
                } catch(Exception exc){
                    Log.writeLog(exc);
                } finally{
                    chatLocker.finished(lockInt);
                }

            }


        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            DataExtractor extractor = new DataExtractor();

            Person person = null;
            int lang = 0;

            if (debug){

                int roleID = extractor.getInteger(request.getParameter("roleID"));
                int personID = extractor.getInteger(request.getParameter("personID"));
                lang = extractor.getInteger(request.getParameter("lang"));
                String lastname = extractor.getRequestString(request.getParameter("name"));

                if (roleID == Constants.STUDENT){
                    person = new Student();
                } else {
                    person = new Tutor();
                }

                person.setPersonID(personID);
                person.setRoleID(roleID);
                person.setLastname(lastname);

            } else {

                if (!Access.isUserAuthorized(session)){
                    PrintWriter pw = response.getWriter();
                    session.invalidate();
                    pw.print(Constants.RETURN_TO_MAIN_PAGE);
                    pw.flush();
                    pw.close();
                    return;
                }

                person = (Person) session.getAttribute("person");
                lang = extractor.getInteger(session.getAttribute("lang"));

            }

            StringTransform stringTransform = new StringTransform();
            int roomID = extractor.getInteger(request.getParameter("roomID"));
            int option = extractor.getInteger(request.getParameter("option"));
            String id = extractor.getRequestString(request.getParameter("JsHttpRequest"));

//===============================================================================================
            // GET MESSAGES
//===============================================================================================
            if (option == GET_MESSAGES_OPTION){
                PrintWriter pw = response.getWriter();
                id = id.substring(0, id.indexOf("-"));

                String messages = rooms.getUserNewMessages(person.getPersonID(), roomID , person.getRoleID(),
                        Date.getCurrentTimeInMills());
                messages = stringTransform.makeChatSting(messages);
                if (messages != null){
                    pw.print("JsHttpRequest.dataReady( " +
                            "  "+id+" ," +
                            "  'messages'," +
                            "   {  " +
                            "   messages : '"+messages+"' " +
                            "   }" +
                            ")");
                }
                pw.flush();
                pw.close();
                return;
//===============================================================================================
            // GET IN CHAT
//===============================================================================================
            } else if (option == GET_IN_CHAT_OPTION){
                PrintWriter pw = response.getWriter();
                ArrayList<ChatPerson> persons = rooms.getRoomUsers(roomID);
                id = id.substring(0, id.indexOf("-"));
                pw.print("JsHttpRequest.dataReady( " +
                        "  "+id+" ," +
                        "  'messages'," +
                        "   {  " +
                        "   inchat : '" +
                        "<table border=0 width=100% >");
                for (int i=0; i<persons.size(); i++){
                    persons.get(i).writeUser(pw, person.getRoleID());
                }
                pw.print("</table>' }" +
                            ")");
                pw.flush();
                pw.close();
                return;
//===============================================================================================
            // ENTER CHAT
//===============================================================================================
            } else if (option == ENTER_CHAT){
                PrintWriter pw = response.getWriter();
                rooms.enterRoom(roomID, person.getPersonID(), person.getRoleID(), Date.getCurrentTimeInMills(),
                        new StringTransform().getHTMLString(person.getShortName()));
                if (roomID >= 0){
                    PageGenerator pageGenerator = new PageGenerator();
                    pageGenerator.filename = "common/chat.common.page.txt";
                    pageGenerator.writeHtmlPage(new ChatEnterMainHandler(roomID, lang, person.getRoleID(),
                            getServletContext()), pw, getServletContext());
                } /*else {
                    new Parser(new FileReader("chat/chat.main.txt").read(getServletContext()), pw,
                            new NoStudyRoomHandler(lang)).parse();                    
                }*/
            } else if (option == PRINT_MESSAGES){
                PrintWriter pw = response.getWriter();
                ChatPerson chatPerson = rooms.getRoomUser(roomID, person.getPersonID(), person.getRoleID());
                if (chatPerson != null){
                    new Parser(new FileReader("chat/chat.message.txt").read(getServletContext()), pw,
                            new MessagesHandler(roomID, chatPerson, lang)).parse();
                    pw.flush();
                    pw.close();
                }
            } else if (option == GET_STUDY_ROOMS_LIST){
                PrintWriter pw = response.getWriter();
                /*
                * Получение списка доступных учебных аудиторий
                * с количеством присутствующих в аудитории человек
                */

                StudyRoomsManager manager = new StudyRoomsManager(person.getRoleID(), person.getPersonID());
                manager.search(lang, rooms);

                new PageGenerator().writeHtmlPage(new StudyRoomsListMainHandler(person.getRoleID(), lang, 
                        manager.studyRooms, getServletContext()), pw, getServletContext());

            } else if (option == GET_IFRAMES){
               PrintWriter pw = response.getWriter();
                new Parser(new FileReader("chat/chat.main.txt").read(getServletContext()), pw,
                    new ChatEnterHandler(roomID)).parse();

            } else if (option == SEND_ELEMENTS_TO_USER_OPTION){



                ChatPerson currentPerson = rooms.getChatPerson(roomID, person.getRoleID(), person.getPersonID());
                long time = Date.getCurrentTimeInMills();

                if (currentPerson == null){
                    /*OutputStream out = response.getOutputStream();
                    out.write(BBElement.EMPTY_HEADER.getBytes());
                    out.flush();
                    out.close();*/
                    return;
                }

                byte [] lastImage = null;

                if ((lastImage = rooms.getLastImage(currentPerson, roomID)) != null){

                    OutputStream out = response.getOutputStream();

                    out.write(BBElement.getImageDataHeader(lastImage.length).getBytes());

                    out.write(lastImage);

                    out.flush();
                    out.close();

                    currentPerson.lastGetElements = time;

                    return;

                } else {
                    ArrayList <String> newElements = rooms.getElements(currentPerson, roomID, time);
                    if (newElements != null && newElements.size() > 0){
                        PrintWriter pw = response.getWriter();
                        pw.print(BBElement.ARRAY_LIST_HEADER);
//                        XStream xStream = new XStream();
//                        String array = xStream .toXML(newElements);
//                        pw.print(array);
                    }
                    currentPerson.lastGetElements = time;
                }


                 

            } else if (option == GET_SIMPLE_ELEMENT_FROM_BOARD){
                PrintWriter pw = response.getWriter();
                String element = extractor.getRequestString(request.getParameter("element"));


                if (element != null){
                    rooms.addBBElement(element, Date.getCurrentTimeInMills(),
                            person.getRoleID(), person.getPersonID(), roomID);
                }
            } else if (option == CHANGE_STUDENT_STATUS_OPTION){
                PrintWriter pw = response.getWriter();
                id = id.substring(0, id.indexOf("-"));

                int studentID = extractor.getInteger(request.getParameter("studentID"));
                int status = extractor.getInteger(request.getParameter("status"));

                rooms.changeStudentStatus(studentID, roomID, status == 1);

                pw.print("JsHttpRequest.dataReady( " +
                        "  "+id+" ," +
                        "  'messages'," +
                        "   {  " +
                        "   somemessage : 'asd' " +
                        "    }" +
                        ")");
                pw.flush();
                pw.close();
                return;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
