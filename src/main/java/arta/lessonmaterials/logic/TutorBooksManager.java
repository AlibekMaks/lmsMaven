package arta.lessonmaterials.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.Constants;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.SearchParams;

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;


public class TutorBooksManager {

    public ArrayList<SimpleObject> books = new ArrayList<SimpleObject>();    

    public String getCondition(SearchParams params, int tutorID){
        StringBuffer result = new StringBuffer(" FROM resources ");
        result.append(" WHERE( tutorID="+tutorID+" AND type="+ Constants.TUTOR_BOOK + " ");
        if (params.search != null){
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens =  parser.parseSearchString(new StringBuffer(params.search));
            if (tokens.size() > 0){
                result.append(" AND (");
                for (int i=0; i<tokens.size(); i++){
                    if (i > 0)
                        result.append(" OR ");
                    result.append("resources.name LIKE '%");
                    result.append(tokens.get(i));
                    result.append("%' ");
                }
                result.append(")");
            }
        }
        result.append(")");
        return result.toString();
    }

    public void search (SearchParams params, int tutorID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer sql = new StringBuffer(" SELECT resources.name as name, " +
                    " resources.resourceID as id ");
            StringBuffer countSQL = new StringBuffer(" SELECT count(*) ");

            String condition = getCondition(params, tutorID);

            sql.append(condition);
            countSQL.append(condition);

            sql.append(" ORDER BY name, resourceID ");

            if(params.countInPart != 0){
                sql.append(" LIMIT ");
                sql.append(params.countInPart*params.partNumber);
                sql.append(", ");
                sql.append(params.countInPart);
                sql.append(params.countInPart * params.partNumber);
            }

            res = st.executeQuery(countSQL.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }

            res = st.executeQuery(sql.toString());
            while (res.next()){
                SimpleObject book = new SimpleObject();
                book.name = res.getString("name");
                book.id = res.getInt("id");
                books.add(book);
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

}
