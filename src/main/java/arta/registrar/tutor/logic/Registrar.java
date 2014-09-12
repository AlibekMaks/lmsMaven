package arta.registrar.tutor.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.Person;
import arta.registrar.tutor.html.RegistrarComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class Registrar {

    public int studygroupID;
    public int subgroupID;

    public String className;
    public String subject;

    public JournalHeader journalHeader = new JournalHeader();

    public ArrayList<RegistrarStudent> students = new ArrayList<RegistrarStudent>();


    public Registrar(int studygroupID, int subgroupID) {
        this.studygroupID = studygroupID;
        this.subgroupID = subgroupID;
    }

    public void load(int month, int year){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            Date date = new Date();
            date.month = month;
            date.year = year;
            int daysInMonthCount = date.getNumberOfDaysAMonth();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            journalHeader.fill(daysInMonthCount);

            // заполнение хедера таблицы колонками для оценок за тестирования

            res = st.executeQuery("SELECT DISTINCT testings.name as name, " +
                    " testings.testingID as id, " +
                    " DAY(registrar.markdate) as mark_day " +
                    " FROM testings JOIN registrar ON registrar.testingID=testings.testingID " +
                    " WHERE registrar.groupID=" + studygroupID + " AND " +
                    " MONTH(registrar.markdate)=" + month + " AND " +
                    " YEAR(registrar.markdate)=" + year + " " +
                    " ORDER BY registrar.markdate, registrar.testingID");
            while (res.next()){

                journalHeader.insert(res.getInt("mark_day"), res.getString("name"), res.getInt("id"));

            }

            res = st.executeQuery("SELECT students.lastname as ln,  " +
                    " students.firstname as fn, " +
                    " students.patronymic as p, " +
                    " students.studentID as id " +
                    " FROM students LEFT JOIN studentgroup ON studentgroup.studentID=students.studentID " +
                    " WHERE studentgroup.groupID="+subgroupID);
            while (res.next()){
                RegistrarStudent student = new RegistrarStudent();
                student.studentID = res.getInt("id");
                student.studentName = Person.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
                students.add(student);
            }

            for (int i=0; i<students.size(); i++){

                res = st.executeQuery("SELECT registrar.mark as mark, " +
                        " DAY(registrar.markdate) as mark_day " +
                        " FROM (registrar LEFT JOIN subgroups ON subgroups.groupID=registrar.groupID)  " +
                        " WHERE registrar.studentID="+students.get(i).studentID+" AND " +
                        " year(registrar.markdate)="+year+" AND month(registrar.markdate)="+month+"  " +
                        " AND subgroups.subgroupID="+subgroupID+" AND registrar.marktype="+Constants.SIMPLE_MARK+" " +
                        " group by markdate ");
                while (res.next()){
                    students.get(i).setMark(res.getInt("mark_day"), res.getInt("mark"));
                }

                res = st.executeQuery("SELECT registrar.mark as mark, " +
                        " DAY(registrar.markdate) as mark_day, " +
                        " registrar.testingID as id " +
                        " FROM (registrar LEFT JOIN subgroups ON subgroups.groupID=registrar.groupID) "+
                        " WHERE registrar.studentID="+students.get(i).studentID+" AND " +
                        " year(registrar.markdate)="+year+" AND month(registrar.markdate)="+month+"  " +
                        " AND subgroups.subgroupID="+subgroupID+" AND registrar.marktype="+Constants.TEST_MARK+" " +
                        " ORDER BY registrar.markdate, registrar.testingID ");
                while (res.next()){
                    students.get(i).setTestMark(res.getInt("id"), res.getInt("mark"), res.getInt("mark_day"));
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

    public void putMarks(ArrayList<SimpleMark> marks, int studygroupID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            for (int i=0; i<marks.size(); i++){
                st.execute("INSERT INTO registrar(studentID, groupID, markdate, mark, marktype, testingID) " +
                        " VALUES("+marks.get(i).studentID+", "+studygroupID+", '"+marks.get(i).date+"', " +
                        " "+marks.get(i).mark+",  "+Constants.SIMPLE_MARK+", 0 ) ");

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

    public void delete(Date date, int studentID, int studyGroupID, Message message, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            st.execute("DELETE FROM registrar WHERE groupID="+studyGroupID+" " +
                    " AND studentID="+studentID+" AND markdate='"+date.getDate()+"' " +
                    " AND marktype="+Constants.SIMPLE_MARK+" ");

            message.setMessageHeader(MessageManager.getMessage(lang, RegistrarMessages.MARK_HAS_BEEN_DELETED));
            message.setMessageType(Message.INFORMATION_MESSAGE);

        } catch(Exception exc){
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang, RegistrarMessages.MARK_HAS_NOT_BEEN_DELETED));
            message.setMessageType(Message.ERROR_MESSAGE);
        } finally{
            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

}
