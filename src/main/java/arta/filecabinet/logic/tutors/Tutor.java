package arta.filecabinet.logic.tutors;

import arta.common.logic.util.*;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.lock.LockManager;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;

import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class Tutor extends Person{

    private String education = null;


    public Tutor() {
        birthdate = new Date();
        startdate = new Date();
    }

    public static LockManager tutorsLockManager = new LockManager();

    public static String getLockString(int tutorID, int personID){
        return tutorID + "_" + personID;
    }

    public boolean save(Message message, int lang, SearchParams params, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(personID, tutorID);

        if (!check(message, lang)){
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            return false;
        }

        try{

            tutorsLockManager.execute(lockString);

            StringTransform trsf = new StringTransform();
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (personID>0){
                res = st.executeQuery("SELECT COUNT(*) FROM tutors WHERE tutorID="+personID);
                if (res.next() && res.getInt(1) == 0){
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                    message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.COULD_NOT_FIND_USER_INFO, null)+"<br>" +
                                MessageManager.getMessage(lang, FileCabinetMessages.WHILE_SAVING_TUTORINFO_HAS_BEEN_DELETED, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;
                }
                if ((roleID & Constants.ADMIN) == 0){
                    res = st.executeQuery("SELECT COUNT(*) FROM tutors WHERE tutorID<>"+personID+" " +
                            " AND roleID&"+Constants.ADMIN+">0 ");
                    if (res.next() && res.getInt(1) == 0){
                        message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                        message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.ADMIN_MUST_EXIST, null));
                        message.setMessageType(Message.ERROR_MESSAGE);
                        return false;
                    }
                }
                st.execute("UPDATE tutors SET " +
                        " lastname='"+trsf.getDBString(lastname)+"', " +
                        " firstname='"+trsf.getDBString(firstname)+"', " +
                        " patronymic='"+trsf.getDBString(patronymic)+"', " +
                        " adress='"+trsf.getHTMLString(adress)+"',  " +
                        " phone='"+trsf.getDBString(phone)+"', " +
                        " education='"+trsf.getDBString(education)+"',  " +
                        " birthdate='"+birthdate.getDate()+"', " +
                        " roleID="+roleID+", " +
                        " ischairman=" + ischairman+", "+
                        " isvicechairman=" + isvicechairman+", "+
                        " ismembers=" + ismembers+", "+
                        " issecretary=" + issecretary+", "+
                        " departmentID="+departmentID+", " +
                        " startdate='"+startdate.getDate()+"' WHERE tutorID="+personID+"");
                message.setMessageType(Message.INFORMATION_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
                return true;
            } else {
                st.execute("INSERT INTO tutors(lastname, firstname, patronymic, " +
                        " adress, phone, education, roleID, ischairman, departmentID, " +
                        " birthdate, startdate) " +
                        " VALUES ('"+trsf.getDBString(lastname)+"', '"+trsf.getDBString(firstname)+"', '"+trsf.getDBString(patronymic)+"', " +
                        " '"+trsf.getDBString(adress)+"', '"+trsf.getDBString(phone)+"', '"+trsf.getDBString(education)+"', "+roleID+", "+ischairman+", "
                        +isvicechairman+", "+ismembers+", "+issecretary+", "+ departmentID + ", " +
                        " '"+birthdate.getDate()+"', '"+startdate.getDate()+"')", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    personID = res.getInt(1);
                }
                if (personID>0){
                    String condition = new TutorsManager().getCondition(params);
                    res = st.executeQuery("SELECT COUNT(*) "+condition);
                    if (res.next())
                        params.recordsCount = res.getInt(1);
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    return true;
                } else {
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;
                }
            }

        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(   MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            return false;
        } finally{
            tutorsLockManager.finished(lockString);
            try {
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }        
    }

    public boolean loadByRecordNumber(int recordNumber, SearchParams params){
        Log.writeLog("loadByRecordNumber");

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            TutorsManager manager = new TutorsManager();
            String condition = manager.getCondition(params);

            res = st.executeQuery("SELECT count(*) "+condition+" ORDER BY lastname, firstname, patronymic, tutorID ");
            if (res.next())
                params.recordsCount = res.getInt(1);

            if (params.recordsCount == 0)
                return false;

            if (recordNumber >= params.recordsCount){
                recordNumber = params.recordsCount - 1;
                params.recordNumber = recordNumber;
            }

            res = st.executeQuery("SELECT tutors.tutorID "+condition+" " +
                    " ORDER BY lastname, firstname, patronymic, tutorID LIMIT "+recordNumber+", 1 ");
            if (res.next())
                personID = res.getInt(1);
            else
                return false;

            load(st, res);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try {
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return false;
    }

    private void load(Statement st, ResultSet res) throws Exception{
        Log.writeLog("load");
        res = st.executeQuery("SELECT lastname as ln, " +
                " firstname as fn, " +
                " patronymic as p, " +
                " birthdate as bd, " +
                " adress as adr, " +
                " phone as ph, " +
                " education as ed,  " +
                " roleID as role, " +
                " departmentID as department, " +
                " startdate as sd, "+
                " ischairman as chm, " +
                " isvicechairman as vichm, " +
                " ismembers as memb, " +
                " issecretary as secr " +
                " FROM tutors "+
                " WHERE tutorid="+personID);
        if (res.next()){
            lastname = res.getString("ln");
            firstname = res.getString("fn");
            patronymic = res.getString("p");
            birthdate.loadDate(res.getString("bd"), Date.FROM_DB_CONVERT);
            adress = res.getString("adr");
            phone = res.getString("ph");
            education = res.getString("ed");
            roleID = res.getInt("role");
            ischairman = res.getInt("chm");
            isvicechairman = res.getInt("vichm");
            ismembers = res.getInt("memb");
            issecretary = res.getInt("secr");
            departmentID = res.getInt("department");
            startdate.loadDate(res.getString("sd"), Date.FROM_DB_CONVERT);
        }
    }

    public boolean loadById(int id){
        this.personID = id;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT COUNT(*) FROM tutors WHERE (deleted <> 1) and (tutorID="+personID+") ");
            if(res.next())
                if (res.getInt(1) == 0)
                    return false;
            
            load(st, res);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try {
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return true;
    }

    protected boolean check(Message message, int lang) {
        boolean result = super.check(message, lang);
        if (education!=null && education.length()>Varchar.DESCRIPTION/2){
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.EDUCATION, null));
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
            result = false;
        }
        return result;
    }

    public int getRoleID() {
        return roleID;
    }

    public int getChairman() {
        return ischairman;
    }
    public int getViceChairman() {
        return isvicechairman;
    }
    public int getMembers() {
        return ismembers;
    }
    public int getSecretary() {
        return issecretary;
    }
    public String getFullName() {
        StringBuffer fullName = new StringBuffer();
        if (lastname != null){
            fullName.append(lastname);
            fullName.append(" ");
        }
        if (firstname!=null){
            fullName.append(firstname);
            fullName.append(" ");
            if (patronymic!=null){
                fullName.append(patronymic);
            }
        }
        return fullName.toString();
    }

    public String getEducation() {
        if (education == null) return "";
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getShortName() {
        if (lastname == null)
            return "";
        StringBuffer res = new StringBuffer(lastname);
        if (firstname != null && firstname.length() > 0){
            res.append(" ");
            res.append(firstname.substring(0,1));
            res.append(".");
            if (patronymic != null && patronymic.length() > 0){
                res.append(patronymic.substring(0,1));
                res.append(".");
            }
        }
        return res.toString();
    }

    public boolean delete(Message message, int lang, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(personID, tutorID);

        try{
            tutorsLockManager.execute(lockString);

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT t.roleID FROM tutors t WHERE (t.deleted <> 1) AND (t.tutorID = "+personID+")");
            if (res.next() && (res.getInt(1) & Constants.ADMIN)>0){
                res = st.executeQuery("SELECT count(*) FROM tutors t WHERE (t.deleted <> 1) AND (t.tutorID <> "+personID+") " +
                        " AND (t.roleID&"+Constants.ADMIN+">0) ");
                if (res.next() && res.getInt(1) == 0){
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                    message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.ADMIN_MUST_EXIST, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;                    
                }
            }

            res = st.executeQuery("SELECT COUNT(*) FROM subgroups WHERE tutorID = "+personID);
            if (res.next() && res.getInt(1) > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.TUTOR_CAN_NOT_BE_DELETED,  null));
                return false;
            }

            res = st.executeQuery("SELECT COUNT(*) FROM registrar r " +
                                  " JOIN testings t ON r.mainTestingID = t.mainTestingID " +
                                  " JOIN tutors t1 ON t.tutorID = t1.tutorID " +
                                  " WHERE (r.status IS NULL) AND (t.tutorID = "+personID+") AND (t1.deleted <> 1) " +
                                  " GROUP BY r.mainTestingID ");
            if (res.next() && res.getInt(1) > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.TUTOR_CAN_NOT_BE_DELETED,  null));
                return false;
            }

            con.setAutoCommit(false);
            /**
            st.execute("DELETE FROM tutors WHERE tutorID=" + personID);
            st.execute("DELETE FROM contentparts, contents  USING contents, resources, contentparts " +
                    " WHERE contentparts.contentID=contents.contentID AND contents.resourceID=resources.resourceID " +
                    " AND resources.tutorID="+personID+" AND resources.type="+Constants.TUTOR_BOOK+" ");
            st.execute("DELETE FROM resources WHERE tutorID=" + personID + " AND type=" + Constants.TUTOR_BOOK);
            st.execute("DELETE FROM testings WHERE tutorID=" + personID);
            /**/
            st.execute("UPDATE tutors SET deleted = 1 WHERE tutorID = " + personID );
            con.commit();
            message.setMessage(MessageManager.getMessage(lang, Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con != null && !con.getAutoCommit()){
                    con.rollback();
                }
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessage(MessageManager.getMessage(lang, Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return false;
        } finally {
            tutorsLockManager.finished(lockString);
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }


    public void loadName() {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        if (lastname != null && lastname.length() > 0)
            return;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p, departmentID as did " +
                    " FROM tutors WHERE tutorID=" + personID+" ");
            if (res.next()){
                lastname = res.getString("ln");
                firstname = res.getString("fn");
                patronymic = res.getString("p");
                departmentID = res.getInt("did");
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


    public int getDepartmentID(){
        return this.departmentID;
    }

    public void setDepartmentID(int departmentID){
        this.departmentID = departmentID;
    }
}

