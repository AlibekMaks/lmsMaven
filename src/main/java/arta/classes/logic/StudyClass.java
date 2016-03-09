package arta.classes.logic;

import arta.groups.logic.StudyGroup;
import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.common.lock.LockManager;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.classes.ClassMessages;
import arta.forum.logic.Part;

import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class StudyClass {

    private int classID;
    private String classNameru;
    private String classNamekz;
    private Integer examID;

    public ArrayList<StudyGroup> groups = new ArrayList<StudyGroup>();

    /*
    * Объект предназначен для синхронизации следующих действий
    * Сохранение (создание) класса  StudyClass.save(...)
    * Удаление класса StudyClass.delete(...)
    * Добавление (редактирование) учебной группы класса StudyGroup.create(...)
    * Удаление учебной группы класса StudyGroup.delete(...)
    * Удаление, создание подгрупп StudyGroup.addSubGroup(), StudyGroup.deleteSubGroup(int subGroupID)
    * Перемещение между подгруппами студентов StudyGroup.moveStudents(...)
    * */
    public static LockManager classesManager = new LockManager();

    public static String getLockString (int classID, int personID){
        return classID + "_" + personID;
    }

    public void loadByRecordNumber(SearchParams params, int recordNumber, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT classID "+new ClassesManager().getCondition(params)+ClassesManager.ORDER+
                                        "LIMIT "+recordNumber+", 1");
            if (res.next()){
                classID = res.getInt(1);
            }

            load(lang, st, res);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void loadById(int id, int lang){

        this.classID = id;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            load(lang,  st, res);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private void load(int lang, Statement st, ResultSet res) throws Exception{

            res = st.executeQuery("SELECT classnamekz,classname, examID FROM classes WHERE classID="+classID);
            if (res.next()){
                classNamekz = res.getString("classnamekz");
                classNameru = res.getString("classname");
                examID = res.getInt("examID");
            }

            res = st.executeQuery("SELECT subjects.subjectID AS sid, "+
                                    " subjects.name"+ Languages.getLang(lang)+" as subj, " +
                                    " studygroups.groupID  as gID  " +
                                    " FROM studygroups LEFT JOIN subjects ON studygroups.subjectID=subjects.subjectID   " +
                                    " WHERE studygroups.classID="+classID+" ORDER BY subjects.name"+Languages.getLang(lang));
            while (res.next()){
                StudyGroup group = new StudyGroup();
                group.setSubjectID(res.getInt("sid"));
                group.setStudyGroupID(res.getInt("gID"));
                group.setSubjectName(res.getString("subj"));
                groups.add(group);
            }            

    }

    public SimpleObject loadAsSimpleObject(int classID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
    //    SimpleObject studyClass = new SimpleObject();
        SimpleObject studyClass = new SimpleObject();
        SimpleObjectKZ studyClasskz = new SimpleObjectKZ();
        studyClass.id = classID;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT classname,classnamekz FROM classes WHERE classID="+classID);
            if (res.next()){
                studyClass.name = res.getString(1);
                studyClasskz.namekz = res.getString(2);
            } else {
                studyClass.name = "";
                studyClasskz.namekz = "";
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return studyClass;
    }

    public boolean save(Message message, int lang, SearchParams params, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(classID, personID);

        classesManager.execute(lockString);


        if (!check(message, lang)){
            classesManager.finished(lockString);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            return false;
        }

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (classID > 0){
                st.execute("UPDATE classes SET examID="+examID+", classname='"+trsf.getDBString(classNameru)+"' " +
                        ", classnamekz='"+trsf.getDBString(classNamekz)+"' WHERE classID="+classID);
            } else {
                st.execute("INSERT INTO classes (examID, classname, classnamekz) " +
                                "VALUES ("+examID+", '"+trsf.getDBString(classNameru)+"', '"+trsf.getDBString(classNamekz)+"')",
                         Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    classID = res.getInt(1);
                }
                res = st.executeQuery("SELECT count(*) "+new ClassesManager().getCondition(params));
                if (res.next()){
                    params.recordsCount = res.getInt(1);
                }
                Part part = new Part(classNameru, 0, 0, classID);
                part.create(lang, new Message());
                
            }

            load(lang, st, res );

            message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            classesManager.finished(lockString);
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete(Message message, int lang, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(classID, personID);
        classesManager.execute(lockString);

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT count(*) FROM students s WHERE (s.classID="+classID+") AND (s.deleted <> 1)");
            if (res.next() && res.getInt(1) > 0){
                message.setMessage(MessageManager.getMessage(lang, ClassMessages.THERE_ARE_STUDENTS_IN_A_CLASS, null));
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }

            con.setAutoCommit(false);

            st.execute("DELETE FROM studygroups, subgroups USING studygroups, subgroups, classes " +
                    " WHERE classes.classID=studygroups.groupID AND subgroups.groupID=studygroups.groupID " +
                    " AND classes.classID="+classID);

            st.execute("DELETE FROM classes WHERE classID="+classID);

            con.commit();

            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return false;
        } catch (Exception exc){
            try{
                if (con!=null && !con.getAutoCommit())
                    con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_DELETED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            classesManager.finished(lockString);
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private boolean check(Message message, int lang){
        boolean checked = true;
        if (classNameru == null || classNameru.length() == 0){
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, Constants.NAMERU, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (classNameru.length()> Varchar.NAME/2){
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, Constants.NAMERU, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }

//        if(examID <= 0){
//            message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.THE_EXAM_IS_NOT_SELECTED, null));
//            checked = false;
//        }
        return checked;
    }

    public int getClassID() {
        return classID;
    }

    public Integer getExamID(){
        return examID;
    }

    public String getClassNameru() {
        if (classNameru == null) return "";
        return classNameru;
    }
    public String getClassNamekz() {
        if (classNamekz == null) return "";
        return classNamekz;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public void setExamID(Integer examID){
        this.examID = examID;
    }

    public void setClassNameru(String classNameru) {
        this.classNameru = classNameru;
    }
    public void setClassNamekz(String classNamekz) {
        this.classNamekz = classNamekz;
    }
}
