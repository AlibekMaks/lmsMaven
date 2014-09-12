package arta.registrar.student.logic;

import arta.common.logic.util.Log;
import arta.common.logic.util.Date;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Constants;
import arta.common.logic.sql.ConnectionPool;
import arta.registrar.tutor.logic.JournalHeaderItem;
import arta.registrar.tutor.logic.JournalHeader;
import arta.registrar.tutor.logic.RegistrarDay;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class StudentRegistrar {

    public  int studentID;


    public StudentRegistrar(int studentID) {
        this.studentID = studentID;
    }

    public JournalHeader journalHeader = new JournalHeader();
    public ArrayList<StudentRegistrarSubject> subjects = new ArrayList<StudentRegistrarSubject>();

    public void load(int month, int year, int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            Date date = new Date();
            date.month = month;
            date.year = year;

            journalHeader.fill(date.getNumberOfDaysAMonth());

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT subjects.name"+ Languages.getLang(lang)+" as sn, " +
                    " studygroups.groupID as studyID, " +
                    " subgroups.subgroupID as subID  " +
                    " FROM (((studygroups LEFT JOIN subgroups ON studygroups.groupID=subgroups.groupID) " +
                    " LEFT JOIN studentgroup ON studentgroup.groupID=subgroups.subgroupID) " +
                    " LEFT JOIN subjects ON subjects.subjectID=studygroups.subjectID) " +
                    " WHERE (studentgroup.studentID="+studentID+") ");
            while (res.next()){
                StudentRegistrarSubject subject = new StudentRegistrarSubject();
                subject.studygroupID = res.getInt("studyID");
                subject.subgroupID = res.getInt("subID");
                subject.subjectName = res.getString("sn");
                subjects.add(subject);
            }

            res = st.executeQuery("SELECT testings.name as name, " +
                    " testings.testingID as id, " +
                    " DAY(registrar.markdate) as mark_day  " +
                    " FROM testings JOIN registrar ON testings.testingID=registrar.testingID  " +
                    " WHERE registrar.studentID="+studentID+" AND MONTH(registrar.markdate)="+month+" " +
                    " AND YEAR(registrar.markdate)="+year+" " +
                    " ORDER BY registrar.markdate, registrar.testingID ");
            while (res.next()){
                journalHeader.insert(res.getInt("mark_day"), res.getString("name"), res.getInt("id"));
            }

            for (int i=0; i<subjects.size(); i++){

                res = st.executeQuery("SELECT registrar.mark as mark, " +
                        " registrar.marktype as type, " +
                        " DAY(registrar.markdate) as mark_day, " +
                        " registrar.testingID as id " +
                        " FROM registrar WHERE studentID="+studentID+" AND registrar.groupID="+subjects.get(i).studygroupID+" " +
                        " AND month(markdate)="+month+" AND year(markdate)="+year+" " +
                        " ORDER BY registrar.markdate, registrar.marktype, registrar.testingID ");

                while (res.next()){

                    int mark = res.getInt("mark");
                    int type = res.getInt("type");
                    int day = res.getInt("mark_day");

                    subjects.get(i).setMark(day, mark, res.getInt("id"), type);
                                        
                }
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
