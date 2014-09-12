package arta.departments.logic;

import arta.books.logic.Book;
import arta.common.lock.LockManager;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;


public class Department {

    private String nameRU = null;
    private String nameKZ = null;
    private String nameEN = null;
    private int departmentID = 0;

    public static LockManager subjectsLockManager = new LockManager();

    private String getLockString(int tutorID){
        return departmentID + "_" + tutorID;
    }

    public void loadByRecordNumber(SearchParams params, int recordNumber, int lang) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT departmentID " + new DepartmentsManager().getCondition(params, lang)+" ORDER BY name"+Languages.getLang(lang)+
                    " LIMIT "+recordNumber+", 1");
            if (res.next()) {
                departmentID = res.getInt(1);
            }

            load(st, res);

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    public void load(){
        loadById(departmentID);
    }

    public void loadById(int id) {

        this.departmentID = id;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            load(st, res);

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    private void load(Statement st, ResultSet res) throws Exception {
        res = st.executeQuery("SELECT nameru as nr, namekz as nk "+
                              " FROM departments d "+
                              " WHERE d.departmentID = " + departmentID);
        if (res.next()) {
            nameRU = res.getString("nr");
            nameKZ = res.getString("nk");
        }
    }

    public boolean save(Message message, int lang, SearchParams params, int tutorID) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(tutorID);

        if (!check(message, lang)) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            return false;
        }

        try {

            subjectsLockManager.execute(lockString);

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            int count = 0;
            res = st.executeQuery("SELECT COUNT(*) FROM departments d " +
                                  " WHERE (d.namekz = '"+trsf.getDBString(nameKZ)+"') OR (d.nameru = '"+trsf.getDBString(nameRU)+"')");
            if(res.next()){
                count = res.getInt(1);
            }

            if(count > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.REGION_WITH_THE_SAME_NAME_ALREDY_EXISTS, null));
                return false;
            }

            if (departmentID > 0) {
                st.execute("UPDATE departments "+
                           " SET nameru='" + trsf.getDBString(nameRU) + "', " +
                               " namekz='" + trsf.getDBString(nameKZ) + "' " +
                           " WHERE departmentID=" + departmentID);
            } else {
                st.execute("INSERT INTO departments (nameru, namekz) " +
                           " VALUES ('" + trsf.getDBString(nameRU) + "', '" + trsf.getDBString(nameKZ) + "')",
                           Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()) departmentID = res.getInt(1);

                res = st.executeQuery("SELECT COUNT(*) "+new DepartmentsManager().getCondition(params, lang));
                if (res.next()) params.recordsCount = res.getInt(1);
            }

            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
            return true;

        } catch (Exception exc) {
            Log.writeLog(exc);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            return false;
        } finally {
            subjectsLockManager.finished(lockString);
            try {
                if (con != null && !con.isClosed()) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }       

    public boolean delete(Message message, int lang, int tutorID) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(tutorID);
        try {

            subjectsLockManager.execute(lockString);
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            boolean isExists = false;
            res = st.executeQuery("SELECT COUNT(*) FROM (" +
                                  " SELECT s.studentid as id FROM students s WHERE (s.departmentid = "+ departmentID +") AND (s.deleted <> 1) "+
                                  " UNION (SELECT t.tutorID AS id FROM tutors t WHERE (t.departmentID = "+departmentID+") AND (t.deleted <> 1)  )) AS t");
            if(res.next()){
                isExists = (res.getInt(1) > 0);
            }

            if(isExists){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                message.setMessage(MessageManager.getMessage(lang, Constants.SINCE_THIS_REGION_IS_NEED, null));
                return false;
            }

            st.execute("DELETE FROM departments WHERE departmentID = " + departmentID);

            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
            return true;
        } catch (Exception exc) {
            Log.writeLog(exc);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            return false;
        } finally {
            subjectsLockManager.finished(lockString);
            try {
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    private boolean check(Message message, int lang) {
        boolean checked = true;
        if (nameRU == null || nameRU.length() == 0) {
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, DepartmentMessages.REGION_IN_RUSSIIAN, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (nameRU.length() > Varchar.NAME ) {
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, DepartmentMessages.REGION_IN_RUSSIIAN, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }
        if (nameKZ == null || nameKZ.length() == 0) {
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, DepartmentMessages.REGION_KAZAKH, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (nameKZ.length() > Varchar.NAME ) {
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, DepartmentMessages.REGION_KAZAKH, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }
        return checked;
    }

    public void setName(int lang, String name) {
        if (lang == Languages.ENGLISH) {
            nameEN = name;
        } else if (lang == Languages.KAZAKH) {
            nameKZ = name;
        } else if (lang == Languages.RUSSIAN) {
            nameRU = name;
        }
    }

    public String getName(int lang) {
        if (lang == Languages.ENGLISH) {
            if (nameEN != null)
                return nameEN;
        } else if (lang == Languages.KAZAKH) {
            if (nameKZ != null)
                return nameKZ;
        } else if (lang == Languages.RUSSIAN) {
            if (nameRU != null)
                return nameRU;
        }
        return "";
    }

    public int getDepartmentID() {
        return this.departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

}
