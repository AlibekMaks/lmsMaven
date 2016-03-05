package arta.classes.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.io.PrintWriter;

import arta.common.logic.util.Log;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.sql.ConnectionPool;
import arta.common.db.connection.PooledConnection;
import arta.common.html.util.Select;


public class ClassesSelect {

    int tutorID;

    ArrayList <SimpleObject> classes = new ArrayList <SimpleObject> ();


    public ClassesSelect(int tutorID) {
        this.tutorID = tutorID;
    }

    private void search(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();


            if (tutorID != 0){
                res = st.executeQuery("SELECT DISTINCT classes.classname as name, " +
                        " classes.classID as id FROM " +
                        " classes LEFT JOIN studygroups ON studygroups.classID=classes.classID " +
                        " LEFT JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                        " WHERE subgroups.tutorID=" + tutorID + " " +
                        " ORDER BY classes.classname, classes.classid");
            } else {
                res = st.executeQuery("SELECT classes.classID as id, " +
                        " classes.classname as name " +
                        " FROM classes ORDER BY classname, classID");
            }

            while (res.next()){
                SimpleObject classObject = new SimpleObject();
                classObject.nameru = res.getString("name");
                classObject.id = res.getInt("id");
                classes.add(classObject);
            }

        } catch(Exception exc){
        	try {
                if (con != null && !con.isClosed()) {
                	con.close();
                	((PooledConnection)con).checkMe();
                }
            } catch (Exception exc1) {
                Log.writeLog(exc);
            }
            Log.writeLog(exc);
        } finally{
            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            } catch(Exception  exc){
                Log.writeLog(exc);
            }
        }

    }


    public void writePostSelect(String name, int width, PrintWriter pw, int value, boolean includeAll){

        search();

        Select select = new Select(Select.POST_SELECT);
        pw.print(select.startSelect(name));

        if (includeAll){
            pw.print(select.addOption(0, value == 0, ""));
        }

        for (int i = 0; i < classes.size(); i ++){
            pw.print(select.addOption(classes.get(i).id, classes.get(i).id == value,
                    classes.get(i).nameru));
        }

        pw.print(select.endSelect());
    }

}
