package arta.subjects.logic;

import arta.books.logic.Book;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.lock.LockManager;

import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class Subject {

    private String nameRU = null;
    private String nameKZ = null;
    private String nameEN = null;
    private int preferredMark;
    private int subjectID = 0;
    private int kaz_test_ID = 0;
    private int rus_test_ID = 0;
    public ArrayList<Book> books = new ArrayList<Book>();

    public static LockManager subjectsLockManager = new LockManager();

    private String getLockString(int tutorID){
        return subjectID + "_" + tutorID;
    }

    public void loadByRecordNumber(SearchParams params, int recordNumber, int lang) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT subjectID " + new SubjectsManager().getCondition(params, lang)+" ORDER BY name"+Languages.getLang(lang)+
                    " LIMIT "+recordNumber+", 1");
            if (res.next()) {
                subjectID = res.getInt(1);
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
        loadById(subjectID);
    }

    public void loadById(int id) {

        this.subjectID = id;
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
        res = st.executeQuery("SELECT nameru as nr, " +
                " nameen as ne, namekz as nk, preferredMark as pm, kaz_test_id, rus_test_id "+
                " FROM subjects WHERE subjectID=" + subjectID);
        if (res.next()) {
            nameRU = res.getString("nr");
            nameEN = "";//res.getString("ne");
            nameKZ = res.getString("nk");
            preferredMark = res.getInt("pm");
            kaz_test_ID = res.getInt("kaz_test_id");
            rus_test_ID = res.getInt("rus_test_id");
        }

        res = st.executeQuery("SELECT name as n, resourceID as id, booktype FROM resources " +
                " WHERE subjectID=" + subjectID + " AND type="+Constants.SUBJECT_BOOK+"");
        while (res.next()) {
            Book book = new Book("");
            book.setId(res.getInt("id"));
            book.setName(res.getString("n"));
            book.setBookTypeID(res.getInt("booktype"));
            books.add(book);
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

            if(kaz_test_ID == 0 | rus_test_ID == 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                if(kaz_test_ID == 0 & rus_test_ID == 0){
                    message.setMessage(MessageManager.getMessage(lang, SubjectMessages.YOU_FORGET_TO_SELECT_A_TEST_FOR_THE_KAZAKH_AND_RUSSIAN_VERSION, null));
                } else if(kaz_test_ID == 0){
                    message.setMessage(MessageManager.getMessage(lang, SubjectMessages.YOU_FORGET_TO_SELECT_A_TEST_FOR_THE_KAZAKH_VERSION, null));
                } else {
                    message.setMessage(MessageManager.getMessage(lang, SubjectMessages.YOU_FORGET_TO_SELECT_A_TEST_FOR_THE_RUSSIAN_VERSION, null));
                }
                return false;
            }

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (subjectID > 0) {
                st.execute("UPDATE subjects SET nameru='" + trsf.getDBString(nameRU) + "', " +
                        " namekz='" + trsf.getDBString(nameKZ) + "', " +
                        //" nameen='" + trsf.getDBString(nameEN) + "', " +
                        " preferredMark = " + preferredMark + ", " +
                        " kaz_test_id = " + kaz_test_ID + ", " +
                        " rus_test_id = " + rus_test_ID +
                        " WHERE subjectID=" + subjectID + "");
            } else {
                //st.execute("INSERT INTO subjects (nameru, namekz, nameen, preferredMark) " +
                st.execute("INSERT INTO subjects (nameru, namekz, preferredMark, kaz_test_id, rus_test_id) " +
                        " VALUES ('" + trsf.getDBString(nameRU) + "', '" + trsf.getDBString(nameKZ) + "', " +
                        preferredMark+", "+kaz_test_ID+", " +rus_test_ID+")", Statement.RETURN_GENERATED_KEYS);
                        //" '" + trsf.getDBString(nameEN) + "', "+preferredMark+")", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next())
                    subjectID = res.getInt(1);
                res = st.executeQuery("SELECT COUNT(*) "+new SubjectsManager().getCondition(params, lang));
                if (res.next())
                    params.recordsCount = res.getInt(1);
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
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
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
            con.setAutoCommit(false);

            StringBuffer classes = new StringBuffer();
            res = st.executeQuery("SELECT DISTINCT classes.classname " +
                    " FROM studygroups JOIN classes ON classes.classID=studygroups.classID " +
                    " WHERE subjectID=" + subjectID);
            while (res.next()){
                if (classes.length() > 0)
                    classes.append(", ");
                classes.append(res.getString(1));
            }
            if (classes.length() > 0) {
                Properties prop = new Properties();
                prop.setProperty("classes", classes.toString());
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, SubjectMessages.CANNOT_DELETE_SUBJECT_GROUPS_EXIST, prop));
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                return false;
            }

            ArrayList <Integer> SCORMBooks = new ArrayList <Integer> ();

            res = st.executeQuery("SELECT resources.resourceID FROM resources WHERE " +
                    " subjectID=" + subjectID + " AND booktype=" + Book.SCORM_TYPE + "");
            while(res.next()){
                SCORMBooks.add(res.getInt(1));
            }

            for (int i = 0; i < SCORMBooks.size(); i ++){
                Book book = new Book();
                book.delete(st, res);
            }
            

            st.execute("DELETE FROM resources WHERE subjectID=" + subjectID);
            st.execute("DELETE FROM subjects WHERE subjectID=" + subjectID);
            con.commit();
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
            return true;
        } catch (Exception exc) {
            Log.writeLog(exc);
            try {
                if (!con.getAutoCommit())
                    con.rollback();
            } catch (Exception e) {
                Log.writeLog(e);
            }
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            return false;
        } finally {
            subjectsLockManager.finished(lockString);
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }

    private boolean check(Message message, int lang) {
        boolean checked = true;
        if (nameRU == null || nameRU.length() == 0) {
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_RUSSAIN, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (nameRU.length() > Varchar.NAME ) {
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_RUSSAIN, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }
        if (nameKZ == null || nameKZ.length() == 0) {
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_KAZAKH, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (nameKZ.length() > Varchar.NAME ) {
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_KAZAKH, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }
        /**
        if (nameEN == null || nameEN.length() == 0) {
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_ENGLISH, null));
            message.addReason(MessageManager.getMessage(lang, Constants.ENTER_FIELD_VALUE, prop));
            checked = false;
        } else {
            if (nameEN.length() > Varchar.NAME ) {
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, SubjectMessages.SUBJECT_NAME_IN_RUSSAIN, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }
        /**/
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

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

	public int getPreferredMark() {
		return preferredMark;
	}

    public int getKaz_test_ID(){
        return kaz_test_ID;
    }

    public int getRus_test_ID(){
        return rus_test_ID;
    }

	public void setPreferredMark(int preferredMark) {
		this.preferredMark = preferredMark;
	}

    public void setKaz_test_ID(int kaz_test_ID){
        this.kaz_test_ID = kaz_test_ID;
    }

    public void setRus_test_ID(int rus_test_id){
        this.rus_test_ID = rus_test_id;
    }
}
