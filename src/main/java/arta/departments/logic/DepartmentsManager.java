package arta.departments.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.SimpleObject;
import arta.filecabinet.logic.SearchParams;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class DepartmentsManager {

    public ArrayList <SimpleObject> departments = new ArrayList <SimpleObject> ();

    public String getCondition(SearchParams params, int lang){        
        StringBuffer result = new StringBuffer(" FROM departments ");
        if (params != null && params.search != null && params.search.length() > 0){
            SearchParser parser = new SearchParser();
            ArrayList <String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            if (tokens != null && tokens.size() > 0){
                result.append(" WHERE( ");
                for (int i=0; i<tokens.size(); i++){
                    if (i > 0)
                        result.append(" OR ");
                    result.append(" name");
                    result.append(Languages.getLang(lang));
                    result.append(" LIKE '%");
                    result.append(tokens.get(i));
                    result.append("%' ");
                }
                result.append(") ");                    
            }
        }
        return result.toString();
    }

    public void search(SearchParams params, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT departmentID as id, " +
                    " name"+Languages.getLang(lang)+" as name ");

            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) ");

            String condition = getCondition(params, lang);

            query.append(condition);
            countQuery.append(condition);

            query.append(" ORDER BY name");
            query.append(Languages.getLang(lang));

            res = st.executeQuery(countQuery.toString());
            if (res.next())
                params.recordsCount = res.getInt(1);

            if (params.countInPart > 0){
                int partsNumber = params.getPartsNumber();
                if (params.partNumber >= partsNumber)
                    params.partNumber = partsNumber - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                query.append(" LIMIT ");
                query.append(params.countInPart*params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }

            res = st.executeQuery(query.toString());
            while (res.next()){
                SimpleObject object = new SimpleObject();
                object.id = res.getInt("id");
                object.name = res.getString("name");
                departments.add(object);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
