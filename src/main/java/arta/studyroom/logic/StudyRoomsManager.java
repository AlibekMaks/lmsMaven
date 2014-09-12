package arta.studyroom.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.tutors.Tutor;
import arta.chat.logic.ChatRooms;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.03.2008
 * Time: 16:13:28
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoomsManager {


    private int roleID;
    private int personID;


    public ArrayList<StudyRoom> studyRooms = new ArrayList <StudyRoom> ();

    public StudyRoomsManager(int roleID, int personID) {
        this.roleID = roleID;
        this.personID = personID;
    }

    public void search(int lang, ChatRooms rooms){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con  = new ConnectionPool().getConnection();
            st = con.createStatement();

            if (roleID == Constants.STUDENT){

                res = st.executeQuery("SELECT subjects.name" + Languages.getLang(lang) + " as name, " +
                        " subgroups.subgroupID as id, " +
                        " subgroups.groupNumber as number, " +
                        " tutors.lastname as ln, " +
                        " tutors.firstname as fn, " +
                        " tutors.patronymic as p " +
                        " FROM subgroups JOIN studygroups ON subgroups.groupID=studygroups.groupID " +
                        " JOIN students ON students.classID=studygroups.classID " +
                        " LEFT JOIN tutors ON tutors.tutorID=subgroups.tutorID  " +
                        " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                        " join studentgroup on studentgroup.studentid=students.studentid and studentgroup.groupid=subgroups.subgroupid " +
                        " WHERE students.studentID=" + personID + " " +
                        " ORDER BY subjects.name" + Languages.getLang(lang) + ", subjects.subjectID ");
                while (res.next()){
                    StudyRoom studyRoom = new StudyRoom();
                    studyRoom.setSubGroupID(res.getInt("id"));
                    studyRoom.setSubGroupNumber(res.getInt("number"));
                    studyRoom.setSubjectName(res.getString("name"));
                    studyRoom.setAdditionalField(Tutor.extractName(res.getString("ln"), res.getString("fn"), res.getString("p")));
                    studyRooms.add(studyRoom);
                }

            } else {

                res = st.executeQuery("SELECT subjects.name" + Languages.getLang(lang) + " as name, " +
                        " subgroups.subgroupID as id, " +
                        " subgroups.groupnumber as number, " +
                        " classes.classname as classname  " +
                        " FROM subgroups JOIN studygroups ON studygroups.groupID=subgroups.groupID " +
                        " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                        " JOIN classes ON classes.classID=studygroups.classID " +
                        " WHERE subgroups.tutorID="+personID+" " +
                        " ORDER BY subjects.name" + Languages.getLang(lang) + ", subjects.subjectID ");
                while (res.next()){
                    StudyRoom studyRoom = new StudyRoom();
                    studyRoom.setSubGroupID(res.getInt("id"));
                    studyRoom.setSubGroupNumber(res.getInt("number"));
                    studyRoom.setSubjectName(res.getString("name"));
                    studyRoom.setAdditionalField(res.getString("classname"));
                    studyRooms.add(studyRoom);
                }

            }

            for (int i = 0; i < studyRooms.size(); i ++){
                for (int j = 0; j < rooms.rooms.size(); j ++){
                    if (studyRooms.get(i).getSubGroupID() == rooms.rooms.get(j).roomID){
                        studyRooms.get(i).setPersonsCount(rooms.rooms.get(j).getPersonsCount());
                        break;
                    }
                }
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

}
