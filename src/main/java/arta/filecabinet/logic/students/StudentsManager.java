package arta.filecabinet.logic.students;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import arta.common.logic.util.Constants;
import arta.settings.logic.Settings;
import arta.tests.testing.logic.Testing;
import arta.common.db.connection.PooledConnection;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.StringTransform;
import kz.arta.plt.common.Person;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class StudentsManager {


    private arta.filecabinet.logic.Person person;
    private int departmentID;
    Settings settings;

    //public ArrayList<SearchStudent> students = new ArrayList<SearchStudent>();
    public HashMap<Integer, SearchStudent> students = new HashMap<Integer, SearchStudent>();

    public String appooint_students_getCondition(StudentSearchParams params, Testing testing){

        StringBuffer additional_query = new StringBuffer();
        if(!person.isAdministrator){
            additional_query.append(" (s.departmentid = "+this.departmentID+") AND ");
        }

        additional_query.append(" (s.deleted <> 1) AND (s.studentid NOT IN( ");
        additional_query.append("SELECT t1.studentID FROM testings t " +
                                " LEFT JOIN testingstudents t1 ON t.testingID = t1.testingID " +
                                " WHERE (t.testingDate = DATE('"+testing.getTestingDate().getDate()+"')) AND (t1.status = 0) " +
                                " GROUP BY t.mainTestingID, t1.studentID " +
                                " HAVING COUNT(*)>1) ");
        additional_query.append(") ");

        StringBuffer condition = new StringBuffer(" FROM students s \n" +
                                                    "          LEFT JOIN registrar r ON  s.studentid = r.studentid \n" +
                                                    "          INNER JOIN classes c ON s.classID = c.classid \n");
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
                    innerCondition.append("s.lastname LIKE '%");
                    innerCondition.append(trsf.getDBString(tokens.get(i)));
                    innerCondition.append("%'");
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
            innerCondition.append(" s.classID=" + params.classID);
        }
        if(innerCondition.length()==0){
            innerCondition.append(" WHERE "+additional_query.toString()+" ");
        }
        condition.append(innerCondition);
        return condition.toString();
    }

    public void appooint_students_search(arta.filecabinet.logic.Person person, StudentSearchParams params, Testing testing) {

        this.person = person;
        this.departmentID = person.getDepartmentID();
        //this.isAdministrator = ((person.getRoleID() & Constants.ADMIN) > 0);

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT SQL_CALC_FOUND_ROWS tb.* \n" +
                                                    "  FROM (SELECT \n" +
                                                    "          s.studentid AS studentid, \n" +
                                                    "          s.lastname as lastname,  \n" +
                                                    "          s.firstname as firstname,  \n" +
                                                    "          s.patronymic as patronymic, \n" +
                                                    "          c.classname as classname, \n" +
                                                    "          s.startdate as startdate, \n" +
                                                    "          IF(r.modified IS NULL, s.startdate, DATE(r.modified)) AS lasttestingdate, \n" +
                                                    "          r.mark as mark, \n" +
                                                    "          r.isPassed AS isPassed \n");
//                                                    "          TIMESTAMPDIFF(YEAR, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS years, \n" +
//                                                    "          TIMESTAMPDIFF(MONTH, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS months, \n" +
//                                                    "          TIMESTAMPDIFF(DAY, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS days \n");
            String condition = appooint_students_getCondition(params, testing);

            query.append(condition);

            //query.append(" GROUP BY s.studentid \n");
            query.append(" ORDER BY r.modified DESC) tb \n");


            if(params.timeHasPassed>0 & params.timeHasPassed<=13){
                if(params.timeHasPassed <= 11){
                    query.append(" WHERE (DATE(tb.lasttestingdate) <= DATE_SUB(DATE(NOW()), INTERVAL "+params.timeHasPassed+" MONTH)) \n");
                } else {
                    //query.append(" WHERE (tb.years >= 1) \n");
                    query.append(" WHERE (DATE(tb.lasttestingdate) <= DATE_SUB(DATE(NOW()), INTERVAL 1 YEAR)) \n");
                }
            }

            query.append(" GROUP BY tb.studentid \n");

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

//            System.out.println("query.toString() = " + query.toString());

            res = st.executeQuery(query.toString());
            while (res.next()) {
                SearchStudent student = new SearchStudent();
                student.id = res.getInt("studentid");
                student.name = Person.getFullName(res.getString("lastname"), res.getString("firstname"), res.getString("patronymic"));
                student.className = res.getString("classname");
                student.startdate = res.getDate("startdate");
                student.lastTestingDate = res.getDate("lasttestingdate");
                student.mark = res.getInt("mark");
                student.isPassed = res.getBoolean("isPassed");

                DateTime now = new DateTime();
                DateTime lastTestingDate = new DateTime(student.lastTestingDate);

                Period p = new Period(lastTestingDate, now, PeriodType.yearMonthDayTime());
                student.year = p.getYears();
                student.month = p.getMonths();
                student.day = p.getDays();
                student.hour = p.getHours();
                student.minute = p.getMinutes();
                student.week = p.getWeeks();
                students.put(student.id, student);
            }

            res = st.executeQuery("SELECT FOUND_ROWS();");
            if(res.next()){
                params.recordsCount = res.getInt(1);
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
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
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

    public void search(StudentSearchParams params) {

        students.clear();

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
                    " startdate as st, " +
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
                //student.startdate = res.getDate("st");
                //student.lastTestingDate = student.startdate;
                if (student.className == null) student.className = "";
                //students.add(student);
                students.put(student.id, student);
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
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    public void getLastTestingDate(SearchStudent student){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT " +
                                 "  r.studentid AS sid, " +
                                 "  r.mark AS mark, " +
                                 "  r.marktype AS marktype, " +
                                 "  r.mainTestingID AS mainTestingID, " +
                                 "  r.testingID AS testingID, " +
                                 "  r.modified as modified, " +
                                 "  r.status AS status  " +
                                 "  FROM registrar r " +
                                 "  WHERE (r.studentid = "+student.id+") AND (r.status = 2) AND (r.modified > '"+formatter.format(student.startdate)+"') " +
                                 "  ORDER BY r.modified DESC " +
                                 "  LIMIT 1 ");
            if(res.next()) {
                student.mark = res.getInt("mark");
                student.checkMark = true;
                student.lastTestingDate = res.getDate("modified");
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


    public void checkRecommendStudent(SearchStudent student){

        if(student == null) return;
        if(student.startdate == null){
            student.testing_status_type = student.ERROR_START_DATE;
            return;
        }

//        Date now = new Date();
//        int day = (int)getDateDiff(student.startdate, now, TimeUnit.DAYS);
//        System.out.println(student.id +"   startdate="+student.startdate.toString()+"    now="+now.toString()+ "     day = " + day);
//
//
//        getLastTestingDate(student);
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(student.lastTestingDate);
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DATE);
//
//        DateTime now = new DateTime();
//        DateTime lastTestingDate = new DateTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0, 0);
//
//        Period p = new Period(lastTestingDate, now, PeriodType.yearMonthDayTime());
//
////        student.year = p.getYears();
////        student.month = p.getMonths();
////        student.day = p.getDays();
////        student.hour = p.getHours();
////        student.minute = p.getMinutes();
////        student.week = p.getWeeks();
//
//        if(settings.recommend_candidates_month <= p.getMonths() & p.getDays()>=0 & p.getHours()>=0 & p.getMinutes()>=0 & p.getSeconds()>=0){
//            student.isRecommend = true;
//        }
//
//        if(p.getYears()>=1 & p.getMonths()>=0 & p.getDays()>=0 & p.getHours()>=0 & p.getMinutes()>=0 & p.getSeconds()>=0){
//            student.passedTextType = Constants.MORE_THAN_A_YEAR; // 1 Больше года
//        } else if(p.getYears()==0 & p.getMonths()>0 & p.getDays()>=0 & p.getHours()>=0 & p.getMinutes()>=0 & p.getSeconds()>=0){
//            student.passedTextType = Constants.X_MONTHS_AND_X_DAYS; // 2 X месяцев X дней
//        }
//
//        if(student.checkMark){
//
//        }

        if(!student.isPassed){ // Если не прошел аттестацию
            student.isRecommend = true;
            if(student.lastTestingDate.toString().equals(student.startdate.toString())){
                student.testing_status_type = student.NEVER_PASSED;
            } else {
                student.testing_status_type = student.FAILED_TEST;
            }
        } else {
            if(settings.recommend_candidates){
                if(student.year>0 || (student.month> settings.recommend_candidates_month & student.day==0) || (student.month == settings.recommend_candidates_month & student.day > 0)){
                    student.isRecommend = true;
                    student.testing_status_type = student.NEED_TO_BE_TESTED;
                } else {
                    student.testing_status_type = student.SUCCUESSFULLY_PASSED_THE_TEST;
                }
            } else {
                student.testing_status_type = student.SUCCUESSFULLY_PASSED_THE_TEST;
            }
        }

        if(student.year>=1 & student.month>=0 & student.day>=0){
            student.passedTextType = Constants.MORE_THAN_A_YEAR; // 1 Больше года
        } else if(student.month>0 & student.day==0){
            student.passedTextType = Constants.X_MONTHS;
        } else if(student.month==0 & student.day>0){
            student.passedTextType = Constants.X_DAYS;
        } else if(student.month==0 & student.day==0){
            student.passedTextType = Constants.X_TODAY;
        } else {
            student.passedTextType = Constants.X_MONTHS_AND_X_DAYS; // 2 X месяцев X дней
        }


    }


    public void getRecommendStudents(){
        if(students != null && students.size()>0){
            settings = new Settings();
            settings.load();

            for(int studentID : students.keySet()){
                SearchStudent student = (SearchStudent)students.get(studentID);
                checkRecommendStudent(student);
            }
        }
    }
}
