package arta.welcome.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.filecabinet.logic.Person;
import arta.settings.logic.Settings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class RecommendStudents {

    public boolean recommendStudents(Person person, Settings settings){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        boolean found = false;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String tutor_condition = "";
            if (person.getRoleID() == Constants.ADMIN){
                tutor_condition = " (s.departmentid = 3) AND ";
            }


            String condition = "";
            if(settings.recommend_candidates_month == 12){
                condition = " WHERE (DATE(tb.lasttestingdate) <= DATE_SUB(DATE(NOW()), INTERVAL 1 YEAR)) \n";
            } else {
                //condition = " WHERE (DATE(tb.lasttestingdate) BETWEEN DATE_SUB(DATE(NOW()), INTERVAL "+settings.recommend_candidates_month+" MONTH) AND DATE(NOW())) \n";
                condition = " WHERE (DATE(tb.lasttestingdate) <= DATE_SUB(DATE(NOW()), INTERVAL "+settings.recommend_candidates_month+" MONTH)) \n";
            }

            res = st.executeQuery("SELECT tb.* \n" +
                    "  FROM (SELECT \n" +
                    "          s.studentid AS studentid, \n" +
                    "          s.lastname as lastname,  \n" +
                    "          s.firstname as firstname,  \n" +
                    "          s.patronymic as patronymic, \n" +
                    "          c.classname as classname, \n" +
                    "          s.startdate as startdate, \n" +
                    "          IF(r.modified IS NULL, s.startdate, DATE(r.modified)) AS lasttestingdate, \n" +
                    "          r.mark as mark, \n" +
                    "          r.isPassed AS isPassed \n" +
//                    "          TIMESTAMPDIFF(YEAR, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS years, \n" +
//                    "          TIMESTAMPDIFF(MONTH, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS months, \n" +
//                    "          TIMESTAMPDIFF(DAY, DATE(IF(r.modified IS NULL, s.startdate, r.modified)), DATE(NOW())) AS days \n" +
                    "          FROM students s \n" +
                    "          LEFT JOIN registrar r ON  s.studentid = r.studentid \n" +
                    "          INNER JOIN classes c ON s.classID = c.classid \n" +
                    "          WHERE "+tutor_condition+" (s.deleted <> 1) AND (s.studentid NOT IN( SELECT t1.studentID \n" +
                    "                                                            FROM testings t  \n" +
                    "                                                            LEFT JOIN testingstudents t1 ON t.testingID = t1.testingID  \n" +
                    "                                                            WHERE (t.testingDate = DATE(NOW())) AND (t1.status = 0)  \n" +
                    "                                                            GROUP BY t.mainTestingID, t1.studentID  \n" +
                    "                                                            HAVING COUNT(*)>1) )   \n" +
                    "          ORDER BY r.modified DESC) tb \n" +
                    "\n" +
                    condition +
                    " GROUP BY tb.studentid \n" +
                    " ORDER BY tb.lastname, tb.firstname, tb.patronymic, tb.classname \n" +
                    " LIMIT 0, 1");
            while (res.next()) {
                found = true;
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
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return found;
    }

}
