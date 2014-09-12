package arta.chat.logic.studyRooms;

import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 */

public class StudyRoomsManager {

    int lang;
    int personID;
    public int roomID;
    public String roomName;
    int dayNumber;
    int type;

    public StudyRoomsManager(int lang, int studentID, int type, int dayNumber) {
        this.lang = lang;
        this.personID = studentID;
        this.dayNumber = dayNumber;
        this.type = type;                
    }

    public void searchForStudent(){

        Connection  con = null;
        Statement st = null;
        ResultSet res = null;
        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            GregorianCalendar calendar = new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int lessonNumber = -1;
            int sitting = -1;

            res = st.executeQuery("SELECT lessonNumber, sitting FROM " +
                    " bells WHERE daynumber="+dayNumber+" AND  " +
                    " (starthour<"+hour+" OR (starthour="+hour+" AND startminute<="+minute+") )" +
                    " AND (endhour>"+hour+" OR (endhour="+hour+" AND endminute>="+minute+")) AND type="+type+"");
            if (res.next()){
                lessonNumber = res.getInt(1);
                sitting = res.getInt(2);
            }

            res = st.executeQuery("SELECT timetable.lessonID as id, " +
                    " subjects.name"+Languages.getLang(lang)+" as sn " +
                    " FROM ((((timetable LEFT JOIN subgroups ON subgroups.subgroupID=timetable.groupID )" +
                    " LEFT JOIN studentgroup ON studentgroup.groupID=subgroups.subgroupID) " +
                    " LEFT JOIN studygroups ON studygroups.groupID=subgroups.groupID) " +
                    " LEFT JOIN subjects ON subjects.subjectID=studygroups.subjectID) " +
                    " WHERE (timetable.lesson="+lessonNumber+" AND studentgroup.studentID="+ personID +" " +
                    " AND timetable.sitting="+sitting+" AND timetable.day="+dayNumber+" ) ");
            if (res.next()){
                roomID = res.getInt(1);
                roomName = res.getString(2);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void searchForTutor(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            GregorianCalendar calendar = new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int lessonNumber = -1;
            int sitting = -1;

            res = st.executeQuery("SELECT lessonNumber, sitting FROM " +
                    " bells WHERE daynumber="+dayNumber+" AND type="+type+" AND  " +
                    " (starthour<"+hour+" OR (starthour="+hour+" AND startminute<="+minute+") )" +
                    " AND (endhour>"+hour+" OR (endhour="+hour+" AND endminute>="+minute+"))");
            if (res.next()){
                lessonNumber = res.getInt(1);
                sitting = res.getInt(2);
            }

            res = st.executeQuery("SELECT timetable.lessonID as lID, " +
                    " subjects.name" + Languages.getLang(lang)+" as sn " +
                    " FROM timetable LEFT JOIN subgroups ON subgroups.subgroupID=timetable.groupID " +
                    " LEFT JOIN studygroups ON studygroups.groupID=subgroups.groupID " +
                    " LEFT JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                    " WHERE timetable.lesson="+lessonNumber+" AND timetable.sitting="+sitting+" " +
                    " AND subgroups.tutorID=" + personID + " AND timetable.day="+dayNumber);
            if (res.next()){
                roomID = res.getInt(1);
                roomName = res.getString(2);
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
