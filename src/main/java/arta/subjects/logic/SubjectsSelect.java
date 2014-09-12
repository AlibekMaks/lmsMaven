package arta.subjects.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.util.Languages;
import arta.common.logic.sql.ConnectionPool;
import arta.common.html.util.Select;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 19:01:29
 * To change this template use File | Settings | File Templates.
 */
public class SubjectsSelect {

    public int tutorID;
    public int classID;
    int lang;
    
    public SubjectsSelect(int lang) {
        this(0, 0, lang);
    }

    public SubjectsSelect(int tutorID, int classID, int lang) {
        this.tutorID = tutorID;
        this.classID = classID;
        this.lang = lang;
    }

    ArrayList<SimpleObject> subjects = new ArrayList <SimpleObject>();

    private void search(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            
            String sql = "SELECT subjects.name"+ Languages.getLang(lang) +" as name, " +
                    " subjects.subjectID as id " +
                    " FROM subjects JOIN studygroups ON studygroups.subjectID=subjects.subjectID " +
                    " JOIN subgroups ON subgroups.groupID=studygroups.groupID ";
            
            if (tutorID > 0 || classID > 0) {
            	sql += " WHERE subgroups.tutorID="+tutorID+" AND studygroups.classID="+classID+" ";
            }

            res = st.executeQuery(sql);

            while (res.next()){
                subjects.add(new SimpleObject(res.getInt("id"), res.getString("name")));
            }


        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{

                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void writePostSelect(String name, int width, int value, PrintWriter pw, boolean includeAll){

        search();

        Select select = new Select(Select.POST_SELECT);
        pw.print(select.startSelect(name));

        if (includeAll){
            pw.print(select.addOption(0, value == 0, ""));
        }

        for (int i = 0; i < subjects.size(); i ++){
            pw.print(select.addOption(subjects.get(i).id, subjects.get(i).id == value,
                    subjects.get(i).name));
        }
        pw.print(select.endSelect());

    }

}
