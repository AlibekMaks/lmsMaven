package arta.filecabinet.logic.tutors;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.StringTransform;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.SearchParams;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class TutorsManager {

    public ArrayList<SimpleObject> tutors = new ArrayList<SimpleObject>();

    public String getCondition(SearchParams params){
        StringBuffer condition = new StringBuffer(" FROM tutors ");

        if (params.search != null) {
            SearchParser parser = new SearchParser();
            StringTransform trsf = new StringTransform();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            if (tokens != null && tokens.size() > 0) {
                condition.append(" WHERE ( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0) condition.append(" OR ");
                    condition.append(" lastname LIKE '%");
                    condition.append(trsf.getDBString(tokens.get(i)));
                    condition.append("%' OR firstname LIKE '%");
                    condition.append(trsf.getDBString(tokens.get(i)));
                    condition.append("%' OR patronymic LIKE '%");
                    condition.append(trsf.getDBString(tokens.get(i)));
                    condition.append("%' ");
                }
                condition.append(") AND (deleted <> 1) ");
            } else {
                condition.append(" WHERE (deleted <> 1) ");
            }
        }
        return condition.toString();
    }    

    public boolean search(SearchParams params) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT lastname as ln, " +
                    " firstname as fn, " +
                    " patronymic as p, " +
                    " tutorID as id ");
            StringBuffer queryCount = new StringBuffer("SELECT count(*) ");

            String condition = getCondition(params);

            query.append(condition);
            queryCount.append(condition);

            query.append(" ORDER BY lastname, firstname, patronymic ");

            res = st.executeQuery(queryCount.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }          

            if (params.countInPart > 0) {

                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount)
                    params.partNumber = partsCount - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                query.append(" LIMIT ");
                query.append(params.countInPart * params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }

            res = st.executeQuery(query.toString());
            while (res.next()) {
                SimpleObject tutor = new SimpleObject();
                tutor.name = res.getString("ln");
                if (res.getString("fn") != null) {
                    tutor.name += " " + res.getString("fn");
                    if (res.getString("p") != null) {
                        tutor.name += " " + res.getString("p");
                    }
                }
                tutor.id = res.getInt("id");
                tutors.add(tutor);
            }

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
        return true;
    }

}
