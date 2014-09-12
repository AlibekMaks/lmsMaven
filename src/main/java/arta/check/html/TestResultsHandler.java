package arta.check.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.messages.MessageManager;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class TestResultsHandler extends TemplateHandler {

    int mark;
    int studentID;
    int lang;


    public TestResultsHandler(int studentID, int lang, int testingID) {
        this.studentID = studentID;
        this.lang = lang;
        getMark(testingID);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("test finished")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_HAS_BEEN_FINISHED, null));
        } else if (name.equals("ur mark is")){
            pw.print(MessageManager.getMessage(lang, TestMessages.UR_MARK_IS, null));
            pw.print(": ");
            pw.print(mark);
        }
    }

    public void getMark(int testingID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT mark FROM testreports WHERE testingID="+testingID+" " +
                    " AND studentID="+studentID+"");
            if (res.next())
                mark = res.getInt(1);

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
