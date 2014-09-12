package arta.login.logic;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.Person;
import arta.settings.logic.Settings;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Hashtable;


public class Access {

    public static int USER_ID_INDEX = 0;
    public static int USER_ROLE_ID_INDEX = 1;
    public static int USER_LANG_INDEX = 2;

    public static boolean read = false;

    public static void loginUser(HttpServletRequest req, HttpServletResponse res,
                                 ServletContext servletContext, int userid, int roleid, int lang){

        //ServletContext servletContext = req.getSession().getServletContext().getContext("/");
        HttpSession session = req.getSession();
        Cookie ssoCookie = new Cookie("sessionid", session.getId());
        //ssoCookie.setPath("/");
        res.addCookie(ssoCookie);
        Object obj = servletContext.getAttribute("users");
        Hashtable <String, int[]> hashtable = null;
        if (obj == null || !(obj instanceof Hashtable )){
            hashtable = new Hashtable <String, int[]> ();
        } else {
            hashtable = (Hashtable <String, int[]>) obj;
        }
        hashtable.put(session.getId(), new int[]{userid, roleid, lang});
        servletContext.setAttribute("users", hashtable);

    }

    public static void setLanguage(HttpServletRequest req, HttpServletResponse res,
                                   ServletContext servletContext, int lang){

        HttpSession session = req.getSession();
        Cookie ssoCookie = new Cookie("sessionid", session.getId());
        res.addCookie(ssoCookie);
        Object obj = servletContext.getAttribute("users");
        Hashtable <String, int[]> hashtable = null;
        if (obj == null || !(obj instanceof Hashtable )){
            hashtable = new Hashtable <String, int[]> ();
        } else {
            hashtable = (Hashtable <String, int[]>) obj;
        }
        hashtable.put(session.getId(), new int[]{0, 0, lang});
        servletContext.setAttribute("users", hashtable);
    }

    public static int getLanguage(HttpServletRequest request, ServletContext servletContext){

        String sessionid = null;
        if (servletContext == null)
            return 0;

        Cookie [] cookies = request.getCookies();
        if (cookies == null)
            return 0;

        for (int i=0 ; i <cookies.length; i++){
            if (cookies[i].getName().equals("sessionid")){
                sessionid = cookies[i].getValue();
                break;
            }
        }

        if (sessionid == null)
            return 0;

        Hashtable hashtable = (Hashtable) servletContext.getAttribute("users");
        if (hashtable == null)
            return 0;

        Object o = hashtable.get(sessionid);
        if (!(o instanceof int[]))
            return 0;

        if (((int[])o).length != 3)
            return 0;

        return (((int[])o)[2]);
    }

    public static int[] getLoggedInUserParams(HttpServletRequest request, ServletContext servletContext){

//        ServletContext context = request.getSession().getServletContext().getContext("/");
        String sessionid = null;
        if (servletContext == null)
            return null;

        Cookie [] cookies = request.getCookies();
        if (cookies == null)
            return null;

        for (int i=0 ; i <cookies.length; i++){
            if (cookies[i].getName().equals("sessionid")){
                sessionid = cookies[i].getValue();
                break;
            }
        }

        if (sessionid == null)
            return null;

        Hashtable hashtable = (Hashtable) servletContext.getAttribute("users");
        if (hashtable == null)
            return null;

        Object o = hashtable.get(sessionid);
        if (!(o instanceof int[]))
            return null;

        if (((int[])o).length != 3)
            return null;

        return ((int[])o);
    }

    public static int hasLanguage(HttpSession session){
        if (session.getAttribute("lang") == null)
            return 0;
        int lang = new DataExtractor().getInteger(session.getAttribute("lang"));
        if (lang!=Languages.ENGLISH && lang!=Languages.KAZAKH && lang!=Languages.RUSSIAN)
            return 0;
        return lang;
    }

    public static boolean isUserInRole(int roleID, HttpSession session){
        if (roleID == Constants.TUTOR || roleID == Constants.ADMIN){
            if (session.getAttribute("person")==null)
                return false;
            if ((((Person)session.getAttribute("person")).getRoleID()&roleID)==0)
                return false;
        } else if (roleID == Constants.STUDENT){
            if (session.getAttribute("person")==null)
                return false;
        }
        if (hasLanguage(session) == 0)
            session.setAttribute("lang", Languages.RUSSIAN);
        return true;
    }

    public static boolean isUserAuthorized(HttpSession session){
        if (session.getAttribute("person") == null)
            return false;
        if (hasLanguage(session) == 0)
            session.setAttribute("lang", Languages.RUSSIAN);
        return true;
    }

    public static boolean CheckkOnTestHandling(){
        return true;
    }

    public static Student authorizeStudent(HttpServletRequest req, String login, String password){

        if (!read){
            Settings settings = new Settings();
            settings.load();
            read = true;
        }

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();
        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con  = connectionPool.getConnection();
            st = con.createStatement();
            Student student = null;
            int studentIsTestingCount = 0;

            if (password == null) password = "";

            res = st.executeQuery("SELECT studentID, lastname, firstname, patronymic, classes.classname  " +
                    " FROM students LEFT JOIN classes ON classes.classID=students.classID " +
                    " WHERE login LIKE '"+trsf.getDBString(login)+"' AND password LIKE '"+MD5.crypt(password)+"' ");
            if (res.next()){
                student = new Student();
                student.setPersonID(res.getInt("studentID"));
                student.setFirstname(res.getString("firstname"));
                student.setLastname(res.getString("lastname"));
                student.setPatronymic(res.getString("patronymic"));
                student.setClassName(res.getString("classname"));
                student.setRoleID(Constants.STUDENT);
            }



            // ====== Проверка на сдачу тестирования данного студента
            if(student != null){
            // Удаляем старые записи
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = sdf.format(dt);
            st.execute("DELETE FROM handing_over_testing WHERE enddatetime < '"+currentDateTime+"'");


            Settings setting = new Settings();
            setting.load();
            if(!setting.student_test_access){

                // Получение IP адреса
                String ipAddress = req.getHeader("x-forwarded-for");
                if (ipAddress == null) {
                    ipAddress = req.getHeader("X_FORWARDED_FOR");
                    if (ipAddress == null){
                        ipAddress = req.getRemoteAddr();
                    }
                }

                res = st.executeQuery("SELECT COUNT(*) FROM handing_over_testing WHERE StudentID = "+ student.getPersonID() + " and IP <> '"+ipAddress+"'");
                if (res.next()){
                    studentIsTestingCount = res.getInt(1);
                }
            }
            }
            if(studentIsTestingCount == 0) return student;
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return null;
    }

    public static Tutor authorizeTutor(String login, String password){

        if (!read){
            Settings settings = new Settings();
            settings.load();
            read = true;
        }
        
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();
        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con  = connectionPool.getConnection();
            st = con.createStatement();


            res = st.executeQuery("SELECT tutorID, lastname, firstname, patronymic, roleID FROM tutors " +
                    " WHERE login LIKE '"+trsf.getDBString(login)+"' AND password LIKE '"+MD5.crypt(password)+"' ");
            if (res.next()){
                Tutor tutor = new Tutor();
                tutor.setPersonID(res.getInt("tutorID"));
                tutor.setFirstname(res.getString("firstname"));
                tutor.setLastname(res.getString("lastname"));
                tutor.setPatronymic(res.getString("patronymic"));
                tutor.setRoleID(res.getInt("roleID"));
                return tutor;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return null;
    }
}
