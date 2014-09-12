package arta.scheduled.tests.logic.students;

import arta.common.db.connection.PooledConnection;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.students.SearchStudent;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.scheduled.tests.logic.SheduledTesting;
import arta.tests.testing.logic.Testing;
import kz.arta.plt.common.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class schStudentsManager {

    public ArrayList<SearchStudent> students = new ArrayList<SearchStudent>();

    public String appooint_students_getCondition(int mainTestingID, int testingID, SheduledTesting testing, StudentSearchParams params){
        StringBuffer additional_query = new StringBuffer();
        additional_query.append(" (s.deleted <> 1) AND (t.mainTestingID = "+mainTestingID+") ");

        StringBuffer condition = new StringBuffer(" FROM testings t " +
                                                  " JOIN testingstudents t1 ON t.testingID = t1.testingID " +
                                                  " JOIN students s ON t1.studentID = s.studentid " +
                                                  " JOIN classes c ON c.classid = s.classID ");
        StringBuffer innerCondition = new StringBuffer();
        if (params.search != null) {
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            StringTransform trsf = new StringTransform();
            if (tokens != null && tokens.size() > 0) {
                innerCondition.append(" WHERE ( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0){
                        innerCondition.append(" OR ");
                    }
                    innerCondition.append(" (s.lastname LIKE '%");
                    innerCondition.append(trsf.getDBString(tokens.get(i)));
                    innerCondition.append("%') ");

                    innerCondition.append(" OR ");

                    innerCondition.append(" (s.firstname LIKE '%");
                    innerCondition.append(trsf.getDBString(tokens.get(i)));
                    innerCondition.append("%') ");
                }
                innerCondition.append(") AND "+additional_query.toString());
            }
        }

        if (params.classID != 0){
            if (innerCondition.length() > 0){
                innerCondition.append(" AND ");
            } else {
                innerCondition.append(" WHERE "+additional_query.toString()+" AND "); //innerCondition.append(" WHERE ");
            }
            innerCondition.append(" (s.classID = " + params.classID + ") ");
        }
        if(innerCondition.length()==0){
            innerCondition.append(" WHERE "+additional_query.toString()+" ");
        }
        condition.append(innerCondition);
        condition.append(" GROUP BY t1.studentID ");
        return condition.toString();
    }

    public void appooint_students_search(int mainTestingID, int testingID, SheduledTesting testing, StudentSearchParams params) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT s.lastname as ln, " +
                                                  " s.firstname as fn," +
                                                  " s.patronymic as p, " +
                                                  " c.classname as cn , " +
                                                  " s.studentid as id ");

            String condition = appooint_students_getCondition(mainTestingID, testingID, testing, params);
            query.append(condition);

            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) FROM (");
            countQuery.append(query.toString());
            countQuery.append(") as tab ");

            query.append(" ORDER BY s.lastname, s.firstname, s.patronymic, c.classname ");
            res = st.executeQuery(countQuery.toString());
            if (res.next()) {
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart > 0) {
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount){
                    params.partNumber = partsCount - 1;
                    if (params.partNumber < 0)
                        params.partNumber = 0;
                }
                query.append(" LIMIT ");
                query.append(params.countInPart * params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }


            res = st.executeQuery(query.toString());
            while (res.next()) {
                SearchStudent student = new SearchStudent();
                student.id = res.getInt("id");
                student.name = Person.getFullName(res.getString("ln"), res.getString("fn"), res.getString("p"));
                student.className = res.getString("cn");
                if (student.className == null) student.className = ""; 
                students.add(student);
            }

        } catch (Exception exc) {
        	try {
                if (con != null && !con.isClosed()) {
                	con.close();
                	((PooledConnection)con).checkMe();
                }
            } catch (Exception exc1) {
                Log.writeLog(exc);
            }
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }



    public String getCondition(StudentSearchParams params){
        StringBuffer condition = new StringBuffer(" FROM students LEFT JOIN classes ON classes.classID=students.classID");
        StringBuffer innerCondition = new StringBuffer();
        if (params.search != null) {
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            StringTransform trsf = new StringTransform();
            if (tokens != null && tokens.size() > 0) {
                innerCondition.append(" WHERE ( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0){
                        innerCondition.append(" OR ");
                    }
                    innerCondition.append(" lastname LIKE '%");
                    innerCondition.append(trsf.getDBString(tokens.get(i)));
                    innerCondition.append("%' ");
                }
                innerCondition.append(") AND (deleted <> 1)");
            }
        }

        if (params.classID != 0){
            if (innerCondition.length() > 0){
                innerCondition.append(" AND ");
            } else {
                innerCondition.append(" WHERE (deleted <> 1) AND "); //innerCondition.append(" WHERE ");
            }
            innerCondition.append(" students.classID=" + params.classID);
        }
        if(innerCondition.length()==0){
            innerCondition.append(" WHERE (deleted <> 1) ");
        }
        condition.append(innerCondition);
        return condition.toString();
    }

    public void search(int mainTestingID, int testingID, SheduledTesting testing, StudentSearchParams params) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT lastname as ln, " +
                    " firstname as fn," +
                    " patronymic as p, " +
                    " classname as cn , " +
                    " studentid as id ");
            StringBuffer countQuery = new StringBuffer("SELECT count(*) ");

            String condition = getCondition(params);

            query.append(condition);
            countQuery.append(condition);

            query.append(" ORDER BY lastname, firstname, patronymic, classname ");
            res = st.executeQuery(countQuery.toString());
            if (res.next()) {
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart > 0) {
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount){
                    params.partNumber = partsCount - 1;
                    if (params.partNumber < 0)
                        params.partNumber = 0;
                }
                query.append(" LIMIT ");
                query.append(params.countInPart * params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }

            res = st.executeQuery(query.toString());
            while (res.next()) {
                SearchStudent student = new SearchStudent();
                student.id = res.getInt("id");
                student.name = Person.getFullName(res.getString("ln"), res.getString("fn"), res.getString("p"));
                student.className = res.getString("cn");
                if (student.className == null) student.className = "";
                students.add(student);
            }

        } catch (Exception exc) {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                    ((PooledConnection)con).checkMe();
                }
            } catch (Exception exc1) {
                Log.writeLog(exc);
            }
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public void getStudentsTestingStatus(int mainTestingID) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            for(int i=0; i<students.size(); i++){
                SearchStudent student = (SearchStudent)students.get(i);
                res = st.executeQuery("SELECT t1.status AS st, t.testingDate AS testingDate, IF(DATE(t.testingDate) < DATE(NOW()), TRUE, FALSE) as isEnded \n" +
                                      " FROM testings t \n" +
                                      " JOIN testingstudents t1 ON t.testingID = t1.testingID \n" +
                                      " WHERE (t1.studentID = "+student.id+") AND (t.mainTestingID = "+mainTestingID+") \n" +
                                      " ORDER BY t1.status DESC \n" +
                                      " LIMIT 1");
                if(res.next()){
                    student.testingStatus = res.getInt("st");
                    student.testingDate = res.getDate("testingDate");
                    student.testingIsEnded = res.getBoolean("isEnded");
                }
            }
        } catch (Exception exc) {
            try {
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc1) {
                Log.writeLog(exc);
            }
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }
}
