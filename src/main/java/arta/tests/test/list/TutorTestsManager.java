package arta.tests.test.list;

import arta.tests.test.Test;
import arta.common.logic.util.Date;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.SearchParams;


import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class TutorTestsManager {

    public static String TESTS_ORDER = " tests.testname, tests.created, tests.modified, tests.testID ";
    public ArrayList <Test> tests = new ArrayList <Test> ();

    public String getCondition(TestsSearchParams params){
        StringBuffer condition = new StringBuffer(" FROM tests LEFT JOIN tutors ON tutors.tutorID=tests.tutorID ");
        condition.append(" WHERE( testID>0 ");
        if (params.search != null){
            SearchParser parser = new SearchParser();
            parser.parseSearchString(new StringBuffer(params.search));
            if (parser.getTokens() != null && parser.getTokens().size()>0){
                condition.append(" AND(");
                condition.append(parser.getCondition(new String[]{"tests.testname", "tutors.lastname", "tutors.firstname",
                "tutors.patronymic"}));
                condition.append(") ");
            }
            /*if (params.tutorID > 0){
                condition.append(" AND tests.tutorID=");
                condition.append(params.tutorID);
            }*/
            condition.append(")");
        }
        return condition.toString();
    }

    public void search(TestsSearchParams params){
        
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform stringTransform = new StringTransform();

        try{

            StringBuffer countSQL = new StringBuffer("SELECT COUNT(*) ");
            StringBuffer sql = new StringBuffer("SELECT tests.testname as tn, " +
                    " tests.testID as tID, " +
                    " tests.created as cr, " +
                    " tests.modified as md, " +
                    " tests.tutorID as tutorID ");

            String condition = getCondition(params);

            countSQL.append(condition);
            sql.append(condition);
            sql.append(" ORDER BY ");
            sql.append(TESTS_ORDER);

            ConnectionPool connectionPool = new ConnectionPool();
            con  = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery(countSQL.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart > 0 && params.partNumber >= 0){
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount)
                    params.partNumber = partsCount - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                sql.append(" LIMIT ");
                sql.append(params.countInPart*params.partNumber);
                sql.append(",");
                sql.append(params.countInPart);
            }


            res = st.executeQuery(sql.toString());

            while (res.next()){
                Test test = new Test(res.getInt("tID"));
                test.testName = res.getString("tn");
                test.created.loadDate(res.getString("cr"), Date.FROM_DB_CONVERT);
                test.created.loadDate(res.getString("md"), Date.FROM_DB_CONVERT);
                test.tutorID = res.getInt("tutorID");
                tests.add(test);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
