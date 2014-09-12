package arta.check.servlet;

import arta.check.html.TimerHandler;
import arta.check.logic.Testing;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class SaveTimerServlet extends HttpServlet {

    int OPTION_UPDATE = 0;
    int OPTION_GET = 1;

    int all = 0;
    int start = 0;
    boolean found = false;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            pw.print("SESSION="+session.getMaxInactiveInterval()+"\n");
            pw.print("getSessionContext="+session.getSessionContext().toString()+"\n");
            pw.print("getId="+session.getId()+"\n");
            pw.print("getValueNames="+session.getValueNames().toString()+"\n");
            for(String str : session.getValueNames()){
                pw.print("str="+str+"="+session.getAttribute(str)+"\n");
            }




            if (!Access.isUserInRole(Constants.STUDENT, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));
            int all = extractor.getInteger(request.getParameter("all"));
            int start = extractor.getInteger(request.getParameter("start"));
            int option = extractor.getInteger(request.getParameter("option"));

            Testing testing = (Testing) session.getAttribute("studenttesting");

            if(getTime(person, testing)){
                if(option == OPTION_UPDATE){
                    if(updateTime(person, testing, start)){
                        pw.print("OK");
                    } else {
                        pw.print("ERROR");
                    }
                } else {
                    pw.print("OK");
                }
            } else {
                if(insertTime(person, testing)){
                    pw.print("OK");
                } else {
                    pw.print("ERROR");
                }
            }

            pw.flush();
            pw.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }


    public boolean getTime(Person person, Testing testing){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        all = 0;
        start = 0;
        found = false;

        if(person == null) return false;
        if(testing == null) return false;
        if(testing.mainTestingID <= 0) return false;
        if(testing.testingID<=0) return false;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.alltime, t.start \n" +
                    " FROM testingtime t \n" +
                    " WHERE (t.studentID = "+person.getPersonID()+") AND (t.testingID = "+testing.testingID+") AND (t.mainTestingID = "+testing.mainTestingID+") \n"+
                    " LIMIT 1");
            if (res.next()){
                all = res.getInt("alltime");
                start = res.getInt("start");
                found = true;

                return true;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return false;
    }

    public boolean updateTime(Person person, Testing testing, int start){

        this.all = 0;
        this.start = 0;
        this.found = false;

        if(person == null) return false;
        if(testing == null) return false;
        if(testing.mainTestingID <= 0) return false;
        if(testing.testingID<=0) return false;


        Connection con = null;
        Statement st = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            st.execute("UPDATE testingtime t \n" +
                       " SET t.start = " +start+
                       " WHERE (t.studentID = "+person.getPersonID()+") AND (t.testingID = "+testing.testingID+") AND (t.mainTestingID = "+testing.mainTestingID+")");
            return true;

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

        return false;
    }

    public boolean insertTime(Person person, Testing testing){

        if(person == null) return false;
        if(testing == null) return false;
        if(testing.mainTestingID <= 0) return false;
        if(testing.testingID<=0) return false;


        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();

            con.setAutoCommit(false);

            res = st.executeQuery("SELECT t.testingTime as tt \n" +
                                  "  FROM testings t \n" +
                                  "  INNER JOIN testingstudents t1 ON t.testingID = t1.testingID \n" +
                                  "  WHERE (t.mainTestingID = "+testing.mainTestingID+") AND \n" +
                                  "         (t.testingID = "+testing.testingID+") AND \n" +
                                  "         (DATE(t.testingDate) = DATE(NOW()) AND \n" +
                                  "         (t1.studentID = "+person.getPersonID()+"))");
            if(res.next()){
                st.execute("INSERT INTO testingtime(studentID, mainTestingID, testingID, alltime, start) \n" +
                           " VALUES("+person.getPersonID()+", "+testing.mainTestingID+", "+testing.testingID+", "+res.getInt("tt")+", 1)");

                con.commit();
                return true;
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (st1!=null) st1.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

        return false;
    }

}
