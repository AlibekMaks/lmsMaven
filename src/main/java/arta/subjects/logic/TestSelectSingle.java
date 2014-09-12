package arta.subjects.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Dida
 * Date: 6/27/14
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSelectSingle {

    public ArrayList<TestsSelect> tests = new ArrayList<TestsSelect>();

    public void GetAllTest(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT * FROM tests t ORDER BY t.testName ASC");
            while(res.next()) {
                TestsSelect ts = new TestsSelect();
                    ts.setTestID(res.getInt("testID"));
                    ts.setTestName(res.getString("testName"));
                    ts.setTutorID(res.getInt("tutorID"));
                    ts.setSubjectID(res.getInt("subjectID"));
                    ts.setCreated(res.getDate("created"));
                    ts.setModified(res.getDate("modified"));
                    ts.setEasy(res.getInt("easy"));
                    ts.setMiddle(res.getInt("middle"));
                    ts.setDifficult(res.getInt("difficult"));
                tests.add(ts);
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
