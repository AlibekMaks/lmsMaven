package arta.exams.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.SimpleObject;
import arta.filecabinet.logic.SearchParams;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class ExamsManager {

    public static String ORDER = " ORDER BY e.examname ";

    public ArrayList<SimpleExam> exams = new ArrayList<SimpleExam>();

    public String getCondition(SearchParams params) {
        StringBuffer condition = new StringBuffer(" FROM exams e ");
        if (params.search != null && params.search.length() > 0) {
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            if (tokens != null && tokens.size() > 0) {
                condition.append(" WHERE( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0)
                        condition.append(" OR ");
                    condition.append(" e.examname LIKE '%");
                    condition.append(tokens.get(i));
                    condition.append("%' ");
                }
                condition.append(")");
            }
        }
        return condition.toString();
    }

    public void search(int examID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            this.exams.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String sql;

            if(examID == Constants.SELECT_ALL){
                sql = "";
            } else {
                sql = " WHERE e.examID = " + examID;
            }

            StringBuffer query = new StringBuffer("SELECT e.examID as id, "+
                                                  " e.examname as name, "+
                                                  " e.question_count as qcount, "+
                                                  " IF((SELECT COUNT(*) FROM registrar r WHERE r.examID = e.examID)>0, TRUE, FALSE) AS isUsed "+
                                                  " FROM exams e " + sql);
            query.append(ORDER);

            res = st.executeQuery(query.toString());
            while (res.next()){
                SimpleExam exam = new SimpleExam();
                exam.examID = res.getInt("id");
                exam.examName = res.getString("name");
                exam.questionCount = res.getInt("qcount");
                exam.isUsed = res.getBoolean("isUsed");
                exams.add(exam);
            }
        } catch (Exception exc) {
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

    public void search(SearchParams params) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            this.exams.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT e.examID as id, "+
                                                        " e.examname as name, "+
                                                        " e.question_count as qcount, "+
                                                        " IF((SELECT COUNT(*) FROM registrar r WHERE r.examID = e.examID)>0, TRUE, FALSE) AS isUsed ");
            query.append(getCondition(params));
            query.append(ORDER);

            StringBuffer count_query = new StringBuffer("SELECT COUNT(*) FROM (");
            count_query.append(query.toString());
            count_query.append(") as tb ");

            res = st.executeQuery(count_query.toString());
            if (res.next()) params.recordsCount = res.getInt(1);

            if (params.countInPart>0){
                int partsNumber = params.getPartsNumber();
                if (params.partNumber >= partsNumber)
                    params.partNumber = partsNumber - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                query.append(" LIMIT "+params.countInPart*params.partNumber+","+params.countInPart+" ");
            }

            res = st.executeQuery(query.toString());
            while (res.next()){
                SimpleExam exam = new SimpleExam();
                exam.examID = res.getInt("id");
                exam.examName = res.getString("name");
                exam.questionCount = res.getInt("qcount");
                exam.isUsed = res.getBoolean("isUsed");
                exams.add(exam);
            }
        } catch (Exception exc) {
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
