package arta.groups.logic;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.common.lock.LockManager;
import arta.classes.ClassMessages;
import arta.classes.logic.StudyClass;
import arta.books.logic.Book;

import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import org.adl.samplerte.server.CourseService;
import org.adl.samplerte.util.RTEFileHandler;


public class StudyGroup {

    public ArrayList <SubGroup> subgroups = new ArrayList <SubGroup> ();
    private String subjectName;
    private int subjectID;
    private int studyGroupID;


    public String getSubjectName() {
        if (subjectName == null) return "";
        return subjectName;
    }

    public int getStudyGroupID() {
        return studyGroupID;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setStudyGroupID(int studyGroupID) {
        this.studyGroupID = studyGroupID;
    }

    public boolean create(Message message, int lang, int classID, int tutorID){

        String lockString = StudyClass.getLockString(classID, tutorID);
        StudyClass.classesManager.execute(lockString);

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        if (classID == 0 || subjectID == 0){
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_NOT_BEEN_CREATED, null));
            message.addReason("Invalid values (classID="+classID+"; subjectID="+subjectID+")");
            return false;
        }

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT subjects.name"+Languages.getLang(lang)+" as subject, " +
                    " classes.classname as class FROM studygroups JOIN classes ON classes.classID=studygroups.classID " +
                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID " +
                    " WHERE studygroups.classID="+classID+" AND studygroups.subjectID="+subjectID+" ");
            if (res.next()){
                Properties prop = new Properties();
                String subject = res.getString("subject");
                String className = res.getString("class");
                prop.setProperty("class", className != null ? className : "");
                prop.setProperty("subject", subject != null ? subject : "");                
                message.addReason(MessageManager.getMessage(lang, ClassMessages.CLASS_STUDY_THIS_SUBJECT, prop));
                return false;
            }

            st.execute("INSERT INTO studygroups(classID, subjectID) VALUES ("+classID+", "+subjectID+")",
                    Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next()){
                studyGroupID = res.getInt(1);
            }

            int subgroupsCount = subgroups.size();
            int studentsCount = res.getInt(1);

            res = st.executeQuery("SELECT COUNT(*) FROM students WHERE classID="+classID);
            if (res.next())
                studentsCount = res.getInt(1);

            ArrayList<Integer> studentsCounts = new ArrayList<Integer>();

            for (int i=0; i<subgroups.size(); i++){
                studentsCounts.add(studentsCount/subgroupsCount);
            }
            int remainder = studentsCount % subgroupsCount;
            for (int i=0; i<remainder; i++){
                studentsCounts.set(i, studentsCounts.get(i)+1);
            }
            int limitStart = 0;
            for (int i=0; i<subgroups.size(); i++){

                st.execute("INSERT INTO subgroups(groupID, tutorID,groupNumber ) VALUES " +
                        " ("+studyGroupID+", "+subgroups.get(i).getTutorID()+", "+(i+1)+")",
                        Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    subgroups.get(i).setSubGroupID(res.getInt(1));
                }
                st.execute("INSERT INTO studentgroup (groupID, studentID) " +
                        " SELECT "+subgroups.get(i).getSubGroupID()+", students.studentID FROM students WHERE classID="+classID+" " +
                        " ORDER BY lastname, firstname, patronymic, studentID LIMIT "+limitStart+", "+studentsCounts.get(i));
                limitStart += studentsCounts.get(i);
            }

            ArrayList <String> SCORMCourses = new ArrayList <String> ();
            ArrayList <Integer> students = new ArrayList <Integer> ();

            res = st.executeQuery("SELECT studentID FROM studentgroup LEFT JOIN subgroups " +
                    " ON subgroups.subgroupID=studentgroup.groupID " +
                    " WHERE subgroups.groupID=" + studyGroupID);
            while (res.next()){
                students.add(res.getInt(1));
            }

            res = st.executeQuery("SELECT courseinfo.courseID FROM courseinfo LEFT JOIN resources " +
                    " ON courseinfo.resourceID=resources.resourceID " +
                    " WHERE type="+Constants.SUBJECT_BOOK+" AND booktype="+ Book.SCORM_TYPE+" " +
                    " AND subjectID="+subjectID+"");
            while (res.next()){
                SCORMCourses.add(res.getString(1));
            }

            CourseService courseService = new CourseService();
            Message msg = new Message();
            for (int i = 0; i < SCORMCourses.size(); i ++){
                for (int j = 0; j < students.size(); j ++){
                    courseService.regForACourse(students.get(j) + "", SCORMCourses.get(i),
                            msg, lang, st, res);
                }
            }
             

            con.commit();
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con!=null && !con.getAutoCommit())
                    con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, ClassMessages.GROUP_HAS_BEEN_CREATED, null));
            return false;
        } finally{
            StudyClass.classesManager.finished(lockString);
            try{
                if (con!=null)con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete(Message message, int lang, int personID){

        Connection con =null;
        Statement st = null;
        ResultSet res = null;

        ArrayList <Integer> students = new ArrayList <Integer> ();
        ArrayList <String> courses = new ArrayList <String> ();

        int classID = 0;
        String lockString = "";

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT classID FROM studygroups WHERE groupID=" + studyGroupID);
            if (res.next()){
                classID = res.getInt(1);
            }

            lockString = StudyClass.getLockString(classID, personID);
            StudyClass.classesManager.execute(lockString);

            st.execute("DELETE FROM groupmaterials USING groupmaterials, subgroups " +
                    " WHERE subgroups.subgroupID=groupmaterials.subgroupID " +
                    " AND subgroups.groupID="+studyGroupID);

            st.execute("DELETE FROM registrar USING registrar, subgroups WHERE subgroups.subgroupID=registrar.groupID " +
                    " AND subgroups.groupID="+studyGroupID);

            st.execute("DELETE FROM testings, testingstudents, testsfortesting, testreports " +
                    " USING testings, testingstudents, testsfortesting, testreports, registrar " +
                    " WHERE testings.testingID=testsfortesting.testingID AND testings.testingID=testingstudents.testingID " +
                    " AND testings.testingID=testreports.testingID AND registrar.testingID=testings.testingID " +
                    " AND registrar.groupID="+studyGroupID);

            res = st.executeQuery("SELECT courseinfo.courseID FROM courseinfo " +
                    " JOIN resources ON courseinfo.resourceID=resources.resourceID " +
                    " JOIN studygroups ON studygroups.subjectID=resources.subjectID  " +
                    " WHERE studygroups.groupID="+studyGroupID+" ");
            while (res.next()){
                courses.add(res.getString(1));
            }


            if (courses.size() > 0){
                res = st.executeQuery("SELECT studentgroup.studentID  " +
                        " FROM studentgroup LEFT JOIN subgroups ON studentgroup.groupID=subgroups.subgroupID " +
                        " WHERE subgroups.groupID=" + studyGroupID);
                while (res.next()){
                    students.add(res.getInt(1));
                }

                RTEFileHandler fileHandler = new RTEFileHandler();

                for (int i=0; i < courses.size(); i ++){
                    for (int j = 0; j < students.size(); j ++){
                        fileHandler.deleteCourseFiles(courses.get(i), students.get(j) + "");
                        st.execute("DELETE FROM usercourseinfo WHERE userID=" + students.get(j) + " " +
                                " AND courseID=" + courses.get(i) + " ");
                    }
                }
            }


            st.execute("DELETE FROM studentgroup USING subgroups,studentgroup WHERE subgroups.subgroupID=studentgroup.groupID " +
                    " AND subgroups.groupID="+studyGroupID);

            st.execute("DELETE FROM subgroups WHERE groupID="+studyGroupID);

            st.execute("DELETE FROM studygroups WHERE groupID="+studyGroupID);

            con.commit();

            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con!=null && !con.getAutoCommit())
                    con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            StudyClass.classesManager.finished(lockString);
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void loadById(int id, int lang){
        this.studyGroupID = id;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con =  connectionPool.getConnection();
            st = con.createStatement();

            load(st, res, lang);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void loadByRecordNumber(int recordNumber, int lang, int classID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT groupID FROM studygroups LEFT JOIN " +
                    " subjects ON subjects.subjectID=studygroups.subjectID WHERE studygroups.classID="+classID+" " +
                    " ORDER BY subjects.name"+ Languages.getLang(lang) +", subjects.subjectID LIMIT "+recordNumber+", 1");
            if (res.next())
                studyGroupID = res.getInt(1);
            load(st, res, lang);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private void load(Statement st, ResultSet res, int lang) throws Exception{
        res = st.executeQuery("SELECT subjects.name"+Languages.getLang(lang)+", " +
                " subjects.subjectID FROM subjects LEFT JOIN studygroups ON studygroups.subjectID=subjects.subjectID " +
                " WHERE studygroups.groupID="+studyGroupID);
        if (res.next()){
            subjectName = res.getString(1);
            subjectID = res.getInt(2);
        }
        res = st.executeQuery("SELECT subgroups.subgroupID as id, " +
                " subgroups.tutorID as tutID, " +
                " subgroups.groupNumber, " +
                " tutors.lastname as ln, " +
                " tutors.firstname as fn, " +
                " tutors.patronymic as p , " +
                " subgroups.groupNumber as n " +
                " FROM subgroups LEFT JOIN tutors ON tutors.tutorID=subgroups.tutorID " +
                " WHERE subgroups.groupID="+studyGroupID+" ORDER BY groupnumber ");
        while (res.next()){
            SubGroup group = new SubGroup();
            group.setGroupNumber(res.getInt("n"));
            group.setSubGroupID(res.getInt("id"));
            group.setTutorID(res.getInt("tutID"));
            String tutorName = "";
            if (group.getTutorID() == 0){
                tutorName = MessageManager.getMessage(lang,  ClassMessages.SELECT_TUTOR, null);                   
            } else {
                tutorName = res.getString("ln")+" "+res.getString("fn");
                if (res.getString("p")!= null)
                    tutorName += " " + res.getString("p");
            }
            group.setTutorName(tutorName);
            subgroups.add(group);
        }
        for (int i=0; i<subgroups.size(); i++){
            res = st.executeQuery("SELECT students.lastname as ln, " +
                    " students.firstname as fn , " +
                    " students.studentID as id FROM " +
                    " students LEFT JOIN studentgroup ON studentgroup.studentID=students.studentID " +
                    " WHERE studentgroup.groupID="+subgroups.get(i).getSubGroupID() +
                    " ORDER BY students.lastname, students.firstname, students.patronymic, students.studentID");
            while (res.next()){
                SimpleObject student = new SimpleObject();
                student.id = res.getInt("id");
                student.name = res.getString("ln");
                String name = res.getString("fn");
                if (name != null && name.length()>0){
                    student.name += " " + name.substring(0,1) + ".";
                }
                subgroups.get(i).students.add(student);
            }
        }
    }

    public boolean addSubGroup(int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        int classID = 0;
        String lockString = "";

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT classID FROM studygroups WHERE groupID=" + studyGroupID);
            if (res.next()){
                classID = res.getInt(1);
            }

            lockString = StudyClass.getLockString(classID, tutorID);
            StudyClass.classesManager.execute(lockString);

            int number = 0;
            res = st.executeQuery("SELECT MAX(groupNumber) FROM subgroups WHERE groupID="+studyGroupID );
            if (res.next())
                number = res.getInt(1) + 1;

            st.execute("INSERT INTO subgroups (groupID, tutorID, groupNumber) VALUES ("+studyGroupID+", 0, "+number+")",
                    Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next()){
                int subgroupID = res.getInt(1);
            }
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            StudyClass.classesManager.finished(lockString);
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean deleteSubGroup(int subGroupID, int tutorID){
        
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = "";
        int classID = 0;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT classID FROM studygroups WHERE groupID=" + studyGroupID);
            if (res.next()){
                classID = res.getInt(1);
            }

            lockString = StudyClass.getLockString(classID, tutorID);
            StudyClass.classesManager.execute(lockString);

            res = st.executeQuery("SELECT COUNT(*) FROM subgroups WHERE subgroupID=" + subGroupID + " ");
            if (res.next() && res.getInt(1) == 0)
                return false;

            res = st.executeQuery("SELECT COUNT(*) FROM studentgroup WHERE groupID="+subGroupID);
            if (res.next() && res.getInt(1) >  0)
                return false;

            st.execute("DELETE FROM groupmaterials WHERE subgroupID="+subGroupID);

            int number = 0;

            res = st.executeQuery("SELECT groupNumber FROM subgroups WHERE subgroupID="+subGroupID);
            if (res.next())
                number = res.getInt(1);

            st.execute("DELETE FROM subgroups WHERE subgroupID="+subGroupID);
            st.execute("UPDATE subgroups SET groupNumber=groupNumber-1 WHERE groupNumber>"+number+" " +
                    " AND groupID="+studyGroupID);
            
            con.commit();
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            StudyClass.classesManager.finished(lockString);
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    /**
     * the first element is the id of ubgroup
     * the second element is studentID
     * @param data data[0] - subgroupID, data[1] - studentID
     * @return
     */
    public boolean moveStudents(ArrayList <int[]> data, int tutorID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = "";
        int classID = 0;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT classID FROM studygroups WHERE groupID=" + studyGroupID);
            if (res.next()){
                classID = res.getInt(1);
            }

            lockString = StudyClass.getLockString(classID, tutorID);
            StudyClass.classesManager.execute(lockString);

            for (int i = 0; i<data.size(); i++){
                st.execute("DELETE FROM studentgroup USING studentgroup, subgroups " +
                        " WHERE studentgroup.groupID=subgroups.subgroupID AND subgroups.groupID="+studyGroupID+" " +
                        " AND studentID="+data.get(i)[1]);
                st.execute("INSERT INTO studentgroup (groupID, studentID) VALUES("+
                        data.get(i)[0]+", "+data.get(i)[1]+")");
            }
            con.commit();            
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            StudyClass.classesManager.finished(lockString);
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void changeTutor(int subgroupID, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = "";
        int classID = 0;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            con.setAutoCommit(false);

            res = st.executeQuery("SELECT classID FROM studygroups WHERE groupID=" + classID);
            if (res.next()){
                classID = res.getInt(1);
            }

            lockString = StudyClass.getLockString(classID, tutorID);
            StudyClass.classesManager.execute(lockString);

            st.execute("UPDATE subgroups SET tutorID="+tutorID+" WHERE subgroupID="+subgroupID);
            st.execute("DELETE FROM groupmaterials USING groupmaterials, subgroups  " +
                    " WHERE groupmaterials.subgroupID=subgroups.subgroupID AND subgroups.subgroupID="+subgroupID);
            
            con.commit();
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {

            StudyClass.classesManager.finished(lockString);

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
