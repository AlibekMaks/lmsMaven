package arta.tests.test.view;

import arta.tests.test.Test;
import arta.common.logic.util.Date;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;

public class ViewTestsManager {

    public static final int NAME_SORT = 0;
    public static final int CREATE_SORT = 1;
    public static final int MODIFY_SORT = 2;

    public int recordsCount;
    
    public ArrayList <Test> tests = new ArrayList <Test> ();
    
    public void search(String search, Date modifyStart, Date modifyFinish, Date createStart, Date createFinish,
                       int sort, int partNumber, int countInPart){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String sql = "SELECT tests.testName as name, " +
                    " tests.testID as tID, " +
                    " tests.created as cr, " +
                    " tests.modified as md, " +
                    " tests.tutorID as id  ";
            String countSQL = "SELECT COUNT(*) ";

            String condition = " FROM tests WHERE  (testID>0 ";

            if (modifyFinish!=null && modifyStart!=null){
                condition += " AND tests.modified>='"+modifyStart.getDate()+"' AND tests.modified<='"+modifyFinish.getDate()+"' ";
            }

            if (createStart!=null && createFinish!=null){
                condition += " AND tests.created>='"+createStart.getDate()+"' AND " +
                        " tests.created<='"+createFinish.getDate()+"' ";
            }

            if (search != null){
                StringTransform stringTransform = new StringTransform();
                condition += " AND tests.testName LIKE '%"+stringTransform.getDBString(search)+"%' ";
            }

            condition += " ) ";

            sql += condition;
            countSQL += condition;

            if (sort == NAME_SORT){
                sql += " ORDER by tests.testName ";
            } else if (sort == CREATE_SORT){
                sql += " ORDER BY tests.created ";
            } else if (sort == MODIFY_SORT){
                sql += " ORDER BY tests.modified ";
            }

            if (countInPart>0){
                sql += " limit "+countInPart*partNumber+","+countInPart;
            }
            
            res = st.executeQuery(countSQL);
            if (res.next()){
                recordsCount = res.getInt(1);
            }

            res = st.executeQuery(sql);
            while (res.next()){
                Test test = new Test(res.getInt("tID"));
                test.testName = res.getString("name");
                test.created.loadDate(res.getString("cr"), Date.FROM_DB_CONVERT);
                test.modified.loadDate(res.getString("md"), Date.FROM_DB_CONVERT);
                test.tutorID = res.getInt("id");
                tests.add(test);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }

        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
            if (res!=null) res.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }
}
