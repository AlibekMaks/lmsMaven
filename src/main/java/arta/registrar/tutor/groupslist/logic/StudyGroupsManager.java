package arta.registrar.tutor.groupslist.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Languages;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class StudyGroupsManager {

    public ArrayList<StudyGroup> groups = new ArrayList<StudyGroup>();

    public void search(int tutorID, int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            
            res = st.executeQuery("SELECT subjects.name"+ Languages.getLang(lang)+" as sn, " +
                    " classes.classname as cn , " +
                    " subgroups.groupNumber as gn, " +
                    " subgroups.subgroupID as subID, " +
                    " studygroups.groupID as studyID " +
                    " FROM subgroups LEFT JOIN studygroups ON studygroups.groupID=subgroups.groupID  " +
                    " LEFT JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                    " LEFT JOIN classes ON classes.classID=studygroups.classID  " +
                    " WHERE subgroups.tutorID="+tutorID+" " +
                    " ORDER BY subjects.name"+Languages.getLang(lang)+", classes.classname ");
            while(res.next()){
                StudyGroup group = new StudyGroup();
                group.className = res.getString("cn");
                group.groupNumber = res.getInt("gn");
                group.studyGroupID = res.getInt("studyID");
                group.subgroupID = res.getInt("subID");
                group.subject = res.getString("sn");
                groups.add(group);
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
