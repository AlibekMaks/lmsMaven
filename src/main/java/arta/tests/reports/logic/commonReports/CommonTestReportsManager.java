package arta.tests.reports.logic.commonReports;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.tests.testing.logic.SimpleExaminer;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Менеджер сводных ведомостей тестирования
 */

public class CommonTestReportsManager {

    public ArrayList <CommonTestReport> reports = new ArrayList <CommonTestReport> ();
    public HashMap<Integer, SimpleExaminer> examiners = new HashMap <Integer, SimpleExaminer> ();
    Person person;

    public void search(CommonTestSearchParams params, int lang, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            //examiners.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
//            StringBuffer sql = new StringBuffer("SELECT DISTINCT testings.name as name, " +
//                    " testings.testingID as id, " +
//                    " testings.testingDate as td  ");

            StringBuffer sql = new StringBuffer("SELECT DISTINCT testings.name as name, " +
                    " testings.tutorID as tid, " +
                    " testings.testingID as id, " +
                    " testings.testingDate as td  ");

            StringBuffer countSql = new StringBuffer("SELECT COUNT(*) ");

            StringBuffer condition = new StringBuffer("");

//            condition.append(" FROM testings LEFT JOIN registrar ON registrar.testingID=testings.testingID " +
//                    " JOIN subgroups ON subgroups.groupID=registrar.groupID WHERE (testings.testingID>0 ");

            condition.append(" FROM testings LEFT JOIN registrar ON registrar.testingID=testings.testingID " +
                    " JOIN subgroups ON subgroups.groupID=registrar.groupID WHERE (testings.testingID>0 ");

            if (params.search!=null && params.search.length()>0){
                SearchParser parser = new SearchParser();
                parser.parseSearchString(new StringBuffer(params.search));
                condition.append(" AND( ");
                condition.append(parser.getCondition(new String[]{"testings.name"}));
                condition.append(")");
            }
            if (params.startDate!=null && params.finishDate!=null){
                condition.append(" AND( ");
                condition.append(" (testings.testingDate>='");
                condition.append(params.startDate.getDate());
                condition.append("' AND testings.testingDate<='");
                condition.append(params.finishDate.getDate());
                condition.append("') ");
                condition.append(") ");
            }
            if (tutorID != 0){
                condition.append(" AND ");
                condition.append(" subgroups.tutorID=");
                condition.append(tutorID);
            }
            condition.append(")");

            sql.append(condition);
            countSql.append(" FROM ("+sql+") as t");

            sql.append(" ORDER BY testings.testingDate, testings.name ");

            res = st.executeQuery(countSql.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart> 0){
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount)
                    params.partNumber = partsCount - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                sql.append(" LIMIT  "+params.partNumber*params.countInPart+","+params.countInPart);
            }

            res = st.executeQuery(sql.toString());

            while (res.next()){
                CommonTestReport report = new CommonTestReport();
                report.date.loadDate(res.getString("td"), Date.FROM_DB_CONVERT);
                report.tutorID = res.getInt("tid");
                report.name = res.getString("name");
                report.testingID = res.getInt("id");                
                reports.add(report);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void mysearch(Person person, CommonTestSearchParams params, int lang, int tutorID){

        this.person = person;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            if(!person.isAdministrator) {
                params.departmentID = person.getDepartmentID();
            }


            examiners.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String examinersID = "";
            String _sql = "";
            if(!person.isAdministrator){
                _sql = "SELECT "+
                        " t.tutorID as tid," +
                        " t.lastname, " +
                        " t.firstname, " +
                        " t.patronymic " +
                        " FROM tutors t "+
                        " WHERE t.departmentID = " + params.departmentID;
            } else {
                _sql = "SELECT "+
                        " t.tutorID as tid, " +
                        " t1.lastname, " +
                        " t1.firstname, " +
                        " t1.patronymic " +
                        " FROM testings t "+
                        " INNER JOIN tutors t1 ON t.tutorID = t1.tutorID " +
                        " WHERE t.tutorID = " + tutorID +
                        " GROUP BY t.tutorID "+
                        " ORDER BY t1.lastname ASC, t1.firstname ASC, t1.patronymic ASC ";
            }

            res = st.executeQuery(_sql);
            while(res.next()){
                SimpleExaminer examiner = new SimpleExaminer();
                examiner.examinerID = res.getInt("tid");
                examiner.lastname = res.getString("lastname");
                examiner.firstname = res.getString("firstname");
                examiner.patronymic = res.getString("patronymic");
                examiners.put(examiner.examinerID, examiner);

                if(examinersID.length() > 0) {
                    examinersID += ", ";
                }
                examinersID += examiner.examinerID;
            }

//            System.out.println(_sql);


            StringBuffer sql = new StringBuffer("SELECT \n"+
                                                " t.tutorID as tid, \n"+
                                                " t1.lastname, \n"+
                                                " t1.firstname, \n"+
                                                " t1.patronymic, \n"+
                                                " t.name as name, \n"+
                                                " t.mainTestingID AS mid, \n"+
                                                " t.testingID as id, \n"+
                                                " t.testingDate as td, \n"+
                                                " IF(DATE(t.testingDate)<DATE(NOW()), TRUE, FALSE) as timeIsUp \n");

            StringBuffer countSql = new StringBuffer("SELECT COUNT(*) \n");

            StringBuffer condition = new StringBuffer("");

            condition.append(" FROM testings t \n");
            condition.append(" INNER JOIN tutors t1 ON t.tutorID = t1.tutorID \n");
            condition.append("  JOIN testingstudents ts ON t.testingID = ts.testingID \n");
            condition.append("  JOIN students s ON ts.studentID = s.studentid \n");
            condition.append(" WHERE (t.mainTestingID>0 ");

            if (params.fio!=null && params.fio.length()>0){
                condition.append(" AND (");
                condition.append("(s.lastname LIKE '%"+params.fio+"%') OR ");
                condition.append("(s.firstname LIKE '%"+params.fio+"%') OR ");
                condition.append("(s.patronymic LIKE '%"+params.fio+"%')");
                condition.append(")");
            }

            if(params.departmentID>0){
                condition.append(" AND (s.departmentid = "+params.departmentID+")");
            }

            if (params.search!=null && params.search.length()>0){
                SearchParser parser = new SearchParser();
                parser.parseSearchString(new StringBuffer(params.search));
                condition.append(" AND (");
                condition.append(parser.getCondition(new String[]{"t.name"}));
                condition.append(") ");
            }

            if (params.startDate!=null && params.finishDate!=null){
                condition.append(" AND (");
                condition.append("(t.testingDate>='");
                condition.append(params.startDate.getDate());
                condition.append("' AND t.testingDate<='");
                condition.append(params.finishDate.getDate());
                condition.append("')");
                condition.append(")");
            }

            if(!person.isAdministrator){
                condition.append(" AND (");
                condition.append("t.tutorID IN (" + examinersID + ")");
                //condition.append(person.getPersonID());
                condition.append(") ");
            } else {
                if(params.examinerID > 0){
                    condition.append(" AND (");
                    condition.append("t.tutorID=");
                    condition.append(params.examinerID);
                    condition.append(") ");
                }
            }
//            if (tutorID != 0){
////                System.out.println("tutorID != 0 = " + (tutorID != 0));
////                condition.append(" AND (");
////                condition.append("t.tutorID=");
////                condition.append(tutorID);
////                condition.append(") ");
//            }
            condition.append(")");

            sql.append(condition);
            sql.append(" GROUP BY t.mainTestingID \n");
            sql.append(" ORDER BY t.testingDate DESC, t.modified DESC \n");
            countSql.append(" FROM ("+sql+") as t");

            res = st.executeQuery(countSql.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart> 0){
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount)
                    params.partNumber = partsCount - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                sql.append(" LIMIT  "+params.partNumber*params.countInPart+","+params.countInPart);
            }

//            System.out.println("+sql.toString() = " + sql.toString());

            res = st.executeQuery(sql.toString());
            while (res.next()){
                CommonTestReport report = new CommonTestReport();
                report.tutorID = res.getInt("tid");
                report.tutor_lastname = res.getString("lastname");
                report.tutor_firstname = res.getString("firstname");
                report.tutor_patronymic = res.getString("patronymic");
                report.name = res.getString("name");
                report.mainTestingID = res.getInt("mid");
                report.testingID = res.getInt("id");
                report.date.loadDate(res.getString("td"), Date.FROM_DB_CONVERT);
                report.timeIsUp = res.getBoolean("timeIsUp");
                reports.add(report);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
