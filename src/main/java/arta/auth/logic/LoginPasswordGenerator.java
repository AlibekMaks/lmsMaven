package arta.auth.logic;

import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.tutors.TutorsManager;
import arta.filecabinet.logic.students.StudentsManager;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class LoginPasswordGenerator {

    public void generate(StudentSearchParams params, int roleID, PrintWriter pw){
        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet res = null;
        ResultSet res1 = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            StringTransform trsf = new StringTransform();

            if (roleID == Constants.STUDENT){
                res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p, " +
                        " substring(rand(), 3, 4) as rnd, studentID  as id  " +
                        " "+new StudentsManager().getCondition(params));
                while (res.next()){
                    String lastname = res.getString("ln");
                    String firstname = res.getString("fn");
                    String patronymic = res.getString("p");
                    String login = lastname+"_"+firstname;
                    String password = res.getString("rnd");
                    int studentID = res.getInt("id");

                    res1 = st1.executeQuery("SELECT COUNT(*) FROM students WHERE login like '"+
                            trsf.getDBString(login)+"' AND studentID<>"+studentID+" ");
                    if (res1.next() && res1.getInt(1) > 0){
                        login += "_" + studentID;
                    }

                    st1.execute("UPDATE students SET login='"+trsf.getDBString(login)+"', " +
                            " password=md5('"+password+"') WHERE studentID="+studentID+" ");

                    pw.print("<tr>");
                        pw.print("<td>");
                            pw.print(lastname);
                            pw.print(" ");
                            pw.print(firstname);
                            pw.print(" ");
                            if (patronymic != null)
                                pw.print(patronymic);
                        pw.print("</td>");
                        pw.print("<td>");
                            pw.print(login);
                        pw.print("</td>");
                        pw.print("<td>");
                            pw.print(password);
                        pw.print("</td>");
                    pw.print("</tr>");
                }
            } else{
                res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p, " +
                        " substring(rand(), 3, 4) as rnd, tutorID  as id  " +
                        " "+new TutorsManager().getCondition(params));
                while (res.next()){
                    String lastname = res.getString("ln");
                    String firstname = res.getString("fn");
                    String patronymic = res.getString("p");
                    String login = lastname+"_"+firstname;
                    String password = res.getString("rnd");
                    int studentID = res.getInt("id");

                    int count = 0;

                    res1 = st1.executeQuery("SELECT COUNT(*) FROM tutors WHERE login like '"+
                            trsf.getDBString(login)+"' AND tutorID<>"+studentID+" ");
                    if (res1.next() && res1.getInt(1) > 0){
                        login += "_" + studentID;
                    }

                    st1.execute("UPDATE tutors SET login='"+trsf.getDBString(login)+"', " +
                            " password=md5('"+password+"') WHERE tutorID="+studentID+" ");

                    pw.print("<tr>");
                        pw.print("<td>");
                            pw.print(lastname);
                            pw.print(" ");
                            pw.print(firstname);
                            pw.print(" ");
                            if (patronymic != null)
                                pw.print(patronymic);
                        pw.print("</td>");
                        pw.print("<td>");
                            pw.print(login);
                        pw.print("</td>");
                        pw.print("<td>");
                            pw.print(password);
                        pw.print("</td>");
                    pw.print("</tr>");
                }

            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void generate(ArrayList<Integer> ids, int roleID, PrintWriter pw){
        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet res = null;
        ResultSet res1 = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            st1 = con.createStatement();
            StringTransform trsf = new StringTransform();

            if(roleID == Constants.STUDENT){

                for (int i=0; i<ids.size(); i++){
                    res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p, " +
                            " substring(rand(), 3, 4) as rnd FROM students WHERE studentID="+ids.get(i)+" ");
                    if (res.next()){

                        String lastname = res.getString("ln");
                        String firstname = res.getString("fn");
                        String patronymic = res.getString("p");
                        String password = res.getString("rnd");
                        String login = lastname + "_" +  firstname;



                        res1 = st1.executeQuery("SELECT COUNT(*) FROM students WHERE login like '"+
                                trsf.getDBString(login)+"' AND studentID<>"+ids.get(i)+" ");
                        if (res1.next() && res1.getInt(1) > 0){
                            login += "_" + ids.get(i);
                        }

                        st1.execute("UPDATE students SET login='"+trsf.getDBString(login)+"', " +
                                " password=md5('"+password+"') WHERE studentID="+ids.get(i)+" ");



                        pw.print("<tr>");
                            pw.print("<td>");
                                pw.print(lastname);
                                pw.print(" ");
                                pw.print(firstname);
                                pw.print(" ");
                                if (patronymic != null)
                                    pw.print(patronymic);
                            pw.print("</td>");
                            pw.print("<td>");
                                pw.print(login);
                            pw.print("</td>");
                            pw.print("<td>");
                                pw.print(password);
                            pw.print("</td>");
                        pw.print("</tr>");

                    }
                }

            } else {
                for (int i=0; i<ids.size(); i++){
                    res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p, " +
                            " substring(rand(), 3, 4) as rnd FROM tutors WHERE tutorID="+ids.get(i)+" ");
                    if (res.next()){
                        String lastname = res.getString("ln");
                        String firstname = res.getString("fn");
                        String patronymic = res.getString("p");
                        String password = res.getString("rnd");
                        String login = lastname + "_" +  firstname;

                        res1 = st1.executeQuery("SELECT COUNT(*) FROM tutors WHERE login like '"+
                                trsf.getDBString(login)+"' AND tutorID<>" + ids.get(i) + " ");
                        if (res1.next() && res1.getInt(1) > 0){
                            login += "_" + ids.get(i);
                        }

                        st1.execute("UPDATE tutors SET login='"+trsf.getDBString(login)+"', " +
                                " password=md5('"+password+"') WHERE tutorID="+ids.get(i)+" ");

                        pw.print("<tr>");
                            pw.print("<td>");
                                pw.print(lastname);
                                pw.print(" ");
                                pw.print(firstname);
                                pw.print(" ");
                                if (patronymic != null)
                                    pw.print(patronymic);
                            pw.print("</td>");
                            pw.print("<td>");
                                pw.print(login);
                            pw.print("</td>");
                            pw.print("<td>");
                                pw.print(password);
                            pw.print("</td>");
                        pw.print("</tr>");
                    }
                }
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }
}
