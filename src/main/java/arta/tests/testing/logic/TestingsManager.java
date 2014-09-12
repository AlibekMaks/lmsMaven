package arta.tests.testing.logic;

import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.common.logic.sql.ConnectionPool;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 11:19:57
 * To change this template use File | Settings | File Templates.
 */
public class TestingsManager {


    public ArrayList<SimpleTesting> testings = new ArrayList <SimpleTesting> ();
    public HashMap<Integer, SimpleExaminer> examiners = new HashMap <Integer, SimpleExaminer> ();
    public int lang;

    TestingsSearchParams params;
    Person person;


    public TestingsManager(TestingsSearchParams params, Person person) {
        this.params = params;
        this.person = person;
    }

    public void search(int tutorID, TestingsSearchParams params){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        try{
            examiners.clear();

            con = new ConnectionPool().getConnection();
            st = con.createStatement();
//            StringBuffer query = new StringBuffer(
//                    "SELECT testings.name as name, " +
//                            " testings.testingID as id, " +
//                            " testings.testingdate as tdate, " +
//                            " testings.starttime as starttime, " +
//                            " testings.testingtime as testingtime " +
//                            " FROM testings " +
//                            " WHERE testings.testingdate>=current_date AND testings.tutorID="+tutorID+" "
//            );

            String sql = "";
            if(!person.isAdministrator){
                sql ="SELECT \n"+
                        " t.tutorID as tid, \n" +
                        " t.lastname, \n" +
                        " t.firstname, \n" +
                        " t.patronymic \n" +
                        " FROM tutors t \n"+
                        " WHERE t.tutorID = " + tutorID +
                        " LIMIT 1";
            } else {
                sql ="SELECT \n"+
                        " t.tutorID as tid, \n" +
                        " t1.lastname, \n" +
                        " t1.firstname, \n" +
                        " t1.patronymic \n" +
                        " FROM testings t \n"+
                        " INNER JOIN tutors t1 ON t.tutorID = t1.tutorID \n" +
                        " WHERE t.tutorID = " + tutorID +
                        " GROUP BY t.tutorID \n"+
                        " ORDER BY t1.lastname ASC, t1.firstname ASC, t1.patronymic ASC \n";
            }

            res = st.executeQuery(sql);
            while(res.next()){
                SimpleExaminer examiner = new SimpleExaminer();
                examiner.examinerID = res.getInt("tid");
                examiner.lastname = res.getString("lastname");
                examiner.firstname = res.getString("firstname");
                examiner.patronymic = res.getString("patronymic");
                examiners.put(examiner.examinerID, examiner);
            }

            sql = "";
            if(person.isAdministrator){
                if(params.examinerID == 0){
                    sql = " ";
                } else {
                    sql = " AND (t.tutorID="+params.examinerID+") ";
                }
            } else {
                sql = " AND (t.tutorID="+tutorID+") ";
            }

            //TestingMessages.TESTING_LABEL
            StringBuffer query = new StringBuffer(
                    "SELECT t.mainTestingID as mid, \n"+
                            " t.name as name, \n" +
                            " t.testingID as id, \n" +
                            " t.tutorID as tid, \n" +
                            " t.testingdate as tdate, \n" +
                            " t.starttime as starttime, \n" +
                            " t.testingtime as testingtime \n" +
                            " FROM testings t \n" +
                            " INNER JOIN tutors t1 ON t.tutorID = t1.tutorID \n" +
                            "  JOIN testingstudents ts ON t.testingID = ts.testingID \n" +
                            "  JOIN students s ON ts.studentID = s.studentid \n"+
                            " WHERE (1=1) "+sql); //WHERE t.testingdate>=current_date

            if (params.search != null && params.search.length() > 0){
                query.append(" AND (t.name LIKE '%"+trsf.getDBString(params.search)+"%') ");
            }

            if (params.fio!=null && params.fio.length()>0){
                query.append(" AND (");
                query.append("(s.lastname LIKE '%"+params.fio+"%') OR ");
                query.append("(s.firstname LIKE '%"+params.fio+"%') OR ");
                query.append("(s.patronymic LIKE '%"+params.fio+"%')");
                query.append(")");
            }

            if(params.departmentID>0){
                query.append(" AND (s.departmentid = "+params.departmentID+") ");
            }

            if (params.startDate!=null && params.finishDate!=null){
                query.append(" AND (");
                query.append("(t.testingDate>='");
                query.append(params.startDate.getDate());
                query.append("' AND t.testingDate<='");
                query.append(params.finishDate.getDate());
                query.append("')");
                query.append(") ");
            }

            query.append(" GROUP BY t.mainTestingID \n");
            query.append(" ORDER BY t.testingdate DESC, t.modified DESC \n");

            if (params.countInPart != 0){
                query.append(" LIMIT " + params.partNumber*params.countInPart+", "+ params.countInPart);
            }

//            System.out.println("query.toString() = " + query.toString());


            params.recordsCount = 0;
            res = st.executeQuery(query.toString());
            while (res.next()){
                params.recordsCount ++;
                SimpleTesting testing = new SimpleTesting();
                testing.setMainTestingID(res.getInt("mid"));
                testing.setTestingID(res.getInt("id"));
                testing.setTestingDate(new Date(res.getString("tdate"), Date.FROM_DB_CONVERT));
                testing.setTestingName(res.getString("name"));
                testing.setTestingStartTime(new Time(res.getString("starttime")));
                testing.setTestingTime(res.getInt("testingtime"));
                testing.setTutorID(res.getInt("tid"));
                testings.add(testing);
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }


}
