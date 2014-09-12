package arta.tests.testing.logic;

import arta.filecabinet.logic.SearchParams;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class TestsManager {

    public ArrayList<TestForTesting> tests = new ArrayList<TestForTesting>();

    public void search(SearchParams params, ArrayList<TestForTesting> tests){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer sql = new StringBuffer("SELECT testID as id, " +
                    " testname as name, " +
                    " easy as e, " +
                    " middle as m , " +
                    " difficult as d ");
            StringBuffer countSQL = new StringBuffer("SELECT count(*) ");

            StringBuffer condition = new StringBuffer("FROM tests WHERE testID>0  ");
            if (params.search != null){
                SearchParser parser = new SearchParser();
                ArrayList<String> tokens =  parser.parseSearchString(new StringBuffer(params.search));
                if (tokens.size() > 0){
                    condition.append(" AND (");
                    for (int i=0; i<tokens.size(); i++){
                        if (i>0)
                            condition.append(" OR ");
                        condition.append(" tests.testname LIKE '%");
                        condition.append(tokens.get(i));
                        condition.append("%' ");
                    }
                    condition.append(") ");
                }
            }

            if (tests.size() > 0){
                condition.append(" AND (");
                for (int i=0; i<tests.size(); i++){
                    if (i > 0)
                        condition.append(" AND ");
                    condition.append("tests.testID<>");
                    condition.append(tests.get(i).testID);
                }
                condition.append(")");
            }

            sql.append(condition);
            countSQL.append(condition);

            sql.append(" ORDER BY testname, testID ");

            res = st.executeQuery(countSQL.toString());
            if (res.next())
                params.recordsCount = res.getInt(1);

            if (params.countInPart != 0){

                int partsNumber = params.getPartsNumber();
                if (params.partNumber >= partsNumber)
                    params.partNumber = partsNumber - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;

                sql.append(" LIMIT ");
                sql.append(params.countInPart*params.partNumber);
                sql.append(", ");
                sql.append(params.countInPart);
            }

            res = st.executeQuery(sql.toString());
            while (res.next()){
                TestForTesting test = new TestForTesting();
                test.alldifficult = res.getInt("d");
                test.alleasy = res.getInt("e");
                test.allmiddle = res.getInt("m");
                test.testID = res.getInt("id");
                test.name = res.getString("name");
                this.tests.add(test);
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
