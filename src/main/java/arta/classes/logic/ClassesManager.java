package arta.classes.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.SearchParams;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class ClassesManager {

    public static String ORDER = " ORDER BY classname ";

    public ArrayList<SimpleObject> classes = new ArrayList<SimpleObject>();

    public String getCondition(SearchParams params) {
        StringBuffer condition = new StringBuffer(" FROM classes ");
        if (params.search != null && params.search.length() > 0) {
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            if (tokens != null && tokens.size() > 0) {
                condition.append(" WHERE( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0)
                        condition.append(" OR ");
                    condition.append(" classname LIKE '%");
                    condition.append(tokens.get(i));
                    condition.append("%' ");
                }
                condition.append(")");
            }
        }
        return condition.toString();
    }

    public void search(SearchParams params) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT classID as id, " +
                    " classname as name ");
            StringBuffer queryCount = new StringBuffer("SELECT COUNT(*) ");

            String condition = getCondition(params);

            query.append(condition);
            queryCount.append(condition);

            query.append(ORDER);

            res = st.executeQuery(queryCount.toString());
            if (res.next())
                params.recordsCount = res.getInt(1);

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
                SimpleObject object = new SimpleObject();
                object.id = res.getInt("id");
                object.name = res.getString("name");
                classes.add(object);
            }

        } catch (Exception exc) {
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
}
