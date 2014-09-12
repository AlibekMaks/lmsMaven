package arta.filecabinet.logic.students;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.lock.LockManager;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;
import arta.books.logic.Book;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.ArrayList;

import org.adl.samplerte.server.CourseService;
import org.adl.samplerte.util.RTEFileHandler;


public class Student extends Person{

    private String parents = null;
    private int classID = 0;
    private String className;
    public int defaultSitting;
    private Date stazOverallStart;
    private Date stazSocietyStart;
    private Date stazPostStart;
    private String educationUZ;
    private String educationProfession;
    private String educationQualification;
    private boolean hadUpgradedQualification;
    private boolean isDirector;
    private boolean isInMaternityLeave;
    private int departmentID;

    public Student() {
        birthdate = new Date();
        startdate = new Date();
        stazOverallStart = new Date();
        stazSocietyStart = new Date();
        stazPostStart = new Date();
    }


    public static LockManager studentsLockManager = new LockManager();

    public static String getLockString(int studentID, int personID){
        return studentID + "_" + personID;
    }

    public boolean save(Message message, int lang, StudentSearchParams params, int tutorID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(personID, tutorID);

        if (!check(message, lang)){
            return false;
        }

        if (classID == 0){
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.CLASS_NOT_SPECIFIED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }

        try{

            studentsLockManager.execute(lockString);

            StringTransform trsf = new StringTransform();
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            int currentClassID = 0;
            res = st.executeQuery("SELECT classID FROM students WHERE studentID="+personID);
            if (res.next()){
                currentClassID = res.getInt(1);
            }

            if (personID>0){

                st.execute("UPDATE students SET " +
                        " lastname='"+trsf.getDBString(lastname)+"', " +
                        " firstname='"+trsf.getDBString(firstname)+"', " +
                        " patronymic='"+trsf.getDBString(patronymic)+"', " +
                        " birthdate='"+birthdate.getDate()+"', " +
                        " startdate='"+startdate.getDate()+"', " +
                        " staz_overall_startdate='"+stazOverallStart.getDate()+"', " +
                        " staz_society_startdate='"+stazSocietyStart.getDate()+"', " +
                        " staz_post_startdate='"+stazPostStart.getDate()+"', " +
                        " adress='"+trsf.getDBString(adress)+"', " +
                        " phone='"+trsf.getDBString(phone)+"', " +
                        " parents='"+trsf.getDBString(parents)+"', " +
                        " edu_uz='"+trsf.getDBString(educationUZ)+"', " +
                        " edu_profession='"+trsf.getDBString(educationProfession)+"', " +
                        " edu_qualification='"+trsf.getDBString(educationQualification)+"', " +
                        " isUpgrade=" + hadUpgradedQualification + "," +
                        " isDirector=" + isDirector + "," +
                        " isInMaternityLeave=" + isInMaternityLeave + "," +
                        " departmentid=" + departmentID + "," +
                        " classID="+classID+" WHERE studentID="+personID+" ");
                message.setMessageType(Message.INFORMATION_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));

            } else {

                if (Constants.IS_DEMO_VERSION){
                    res = st.executeQuery("SELECT COUNT(*) FROM students");
                    if (res.next() && res.getInt(1) >= 4){
                        message.setMessage(MessageManager.getMessage(lang,
                                FileCabinetMessages.YOU_HAVE_EXCEEDED_THE_MAXIMUM_NUMBER_OF_STUDENTS, null));
                        message.setMessageType(Message.ERROR_MESSAGE);
                        return false;
                    }
                }

                st.execute("INSERT INTO students (lastname, firstname, patronymic, birthdate, " +
                		"staz_overall_startdate, staz_society_startdate, staz_post_startdate, " +
                		"edu_uz, edu_profession, edu_qualification, " +
                		"isUpgrade, isDirector, isInMaternityLeave, departmentid, " +
                		"adress, phone, parents, startdate, classID) " +
                        " VALUES ('"+trsf.getDBString(lastname)+"', '"+trsf.getDBString(firstname)+"', '"+trsf.getDBString(patronymic)+"', " +
                        " '"+birthdate.getDate()+"', "+
                        " '"+stazOverallStart.getDate()+"', "+
                        " '"+stazSocietyStart.getDate()+"', "+
                        " '"+stazPostStart.getDate()+"', "+
                        " '"+ trsf.getDBString(educationUZ) +"'," +
                        " '"+ trsf.getDBString(educationProfession) +"'," +
                        " '"+ trsf.getDBString(educationQualification) +"', " +
                        " " + hadUpgradedQualification + ", " + isDirector + ", " + isInMaternityLeave + ", " +
                        " " + departmentID + ", " +
                        " '" + trsf.getDBString(adress)+"', '"+trsf.getDBString(phone)+"', '"+trsf.getDBString(parents)+"', " +
                        " '"+startdate.getDate()+"', "+classID+")", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    personID = res.getInt(1);
                }
                if (personID>0){
                    String condition = new StudentsManager().getCondition(params);
                    res = st.executeQuery("SELECT count(*) "+condition);
                    if (res.next())
                        params.recordsCount = res.getInt(1);
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
                } else{
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                    return false;
                }
            }


            if(currentClassID != classID){


                ArrayList<Integer> previousCourses = new ArrayList <Integer> ();
                ArrayList <Integer> futureCourses = new ArrayList <Integer> ();

                res = st.executeQuery("SELECT courseinfo.courseid FROM courseinfo " +
                        " JOIN resources ON resources.resourceID=courseinfo.resourceID  " +
                        " JOIN studygroups ON studygroups.subjectID=resources.subjectID " +
                        " WHERE studygroups.classID=" + currentClassID + "  " +
                        " AND resources.type="+ Constants.SUBJECT_BOOK+" AND " +
                        " resources.booktype=" + Book.SCORM_TYPE + " ");
                while (res.next()){
                    previousCourses.add(res.getInt(1));
                }

                res = st.executeQuery("SELECT courseinfo.courseid FROM courseinfo " +
                        " JOIN resources ON resources.resourceID=courseinfo.resourceID  " +
                        " JOIN studygroups ON studygroups.subjectID=resources.subjectID " +
                        " WHERE studygroups.classID=" + classID + "  " +
                        " AND resources.type="+ Constants.SUBJECT_BOOK+" AND " +
                        " resources.booktype=" + Book.SCORM_TYPE + " ");
                while(res.next()){
                    futureCourses.add(res.getInt(1));
                }

                for (int i = futureCourses.size() - 1; i>=0 ; i--){
                    boolean met = false;
                    for (int j = 0; j < previousCourses.size(); j ++){
                        if (futureCourses.get(i) == previousCourses.get(j)){
                            met = true;
                            previousCourses.remove(j);
                            break;
                        }
                    }
                    if (met){
                        futureCourses.remove(i);
                    }
                }

                CourseService courseService = new CourseService();
                RTEFileHandler handler = new RTEFileHandler();

                Message tmpMsg = new Message();

                for (int i = 0; i < previousCourses.size(); i ++){
                    handler.deleteCourseFiles(previousCourses.get(i) + "", personID + "");
                    st.execute("DELETE FROM usercourseinfo WHERE userID=" + personID + " " +
                            " AND courseID=" + previousCourses.get(i) + "");
                }

                for (int i = 0; i < futureCourses.size(); i ++){
                    courseService.regForACourse(personID + "", futureCourses.get(i) + "", tmpMsg,
                            lang, st, res);
                }

                st.execute("DELETE FROM studentgroup WHERE studentID="+personID);
                
                st.execute("INSERT INTO studentgroup(studentID, groupID) SELECT "+personID+", subgroups.subgroupID " +
                        " FROM classes LEFT JOIN studygroups ON classes.classID=studygroups.classID " +
                        " LEFT JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                        " WHERE classes.classID=" + classID + " AND subgroups.subgroupID IS NOT NULL " +
                        " AND subgroups.groupnumber=1");


            }

            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
            return false;
        } finally {
            studentsLockManager.finished(lockString);
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void loadByRecordNumber(int recordNumber, StudentSearchParams params){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st  = con.createStatement();

            String condition = new StudentsManager().getCondition(params);
            res = st.executeQuery("SELECT studentID "+condition+" ORDER BY lastname, firstname, patronymic, classname " +
                    " LIMIT "+recordNumber+", 1");
            if (res.next())
                personID = res.getInt(1);

            load(st, res);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void loadById(int id){

        this.personID = id;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st  = con.createStatement();

            load(st, res);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private void load(Statement st, ResultSet res) throws Exception {

        res = st.executeQuery("SELECT lastname as ln, " +
                " firstname as fn, " +
                " patronymic as p, " +
                " startdate as sd, " +
                " birthdate as bd, " +
                " staz_overall_startdate as staz_o, " +
                " staz_society_startdate as staz_s, " +
                " staz_post_startdate as staz_p, " +
                " edu_uz," +
                " edu_profession as edu_p," +
                " edu_qualification as edu_q," +
                " isUpgrade," +
                " isDirector as isdir," +
                " isInMaternityLeave as ismat," +
                " departmentid as depid, " +
                " adress as adr, " +
                " phone as ph, " +
                " students.classID as cid, " +
                " classname, " +
                " parents as par FROM students LEFT JOIN classes ON students.classid = classes.classid WHERE studentID="+personID);

        if (res.next()){
            lastname = res.getString("ln");
            firstname = res.getString("fn");
            parents = res.getString("par");
            patronymic = res.getString("p");
            startdate.loadDate(res.getString("sd"), Date.FROM_DB_CONVERT);
            birthdate.loadDate(res.getString("bd"), Date.FROM_DB_CONVERT);
            stazOverallStart.loadDate(res.getString("staz_o"), Date.FROM_DB_CONVERT);
            stazSocietyStart.loadDate(res.getString("staz_s"), Date.FROM_DB_CONVERT);
            stazPostStart.loadDate(res.getString("staz_p"), Date.FROM_DB_CONVERT);
            educationUZ = res.getString("edu_uz");
            educationProfession = res.getString("edu_p");
            educationQualification = res.getString("edu_q");
            hadUpgradedQualification = res.getBoolean("isUpgrade");
            isDirector = res.getBoolean("isdir");
            isInMaternityLeave= res.getBoolean("ismat");

            departmentID = res.getInt("depid");
            adress = res.getString("adr");
            phone = res.getString("ph");
            classID = res.getInt("cid");
            className = res.getString("classname");
        }
    }

    protected boolean check(Message message, int lang) {
        boolean result = super.check(message, lang);
        if (parents!=null && parents.length()>Varchar.DESCRIPTION/2){
            result = false;
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.PARENTS, null));
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
        }
        return result;
    }

    public boolean delete(Message message, int lang, int tutorID){

        boolean deleted = false;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        String lockString = getLockString(personID, tutorID);

        try{
            studentsLockManager.execute(lockString);

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT COUNT(*) FROM registrar r " +
                                  " JOIN students s ON r.studentid = s.studentid " +
                                  " WHERE (s.deleted <> 1) AND (r.studentid = "+personID+")");
            if(res.next() && res.getInt(1) > 0){
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, Constants.AS_PART_OF_THE_STUDENT_HAS_A_DESIGNATED_TEST, null));
                message.setMessageHeader(MessageManager.getMessage(lang,  Constants.FAILED_TO_DELETE_THE_CURRENT_STUDENT, null));
                return deleted;
            }

            /**
            st.execute("DELETE FROM registrar WHERE studentID="+personID);
            st.execute("DELETE FROM testingstudents WHERE studentID="+personID);
            st.execute("DELETE FROM testreports WHERE studentID="+personID);
            st.execute("DELETE FROM studentgroup WHERE studentID="+personID);
            st.execute("DELETE FROM students WHERE studentID="+personID);
            /**/
            st.execute("DELETE FROM registrar WHERE studentID="+personID);
            st.execute("DELETE FROM testingstudents WHERE studentID="+personID);
            st.execute("DELETE FROM testreports WHERE studentID="+personID);
            st.execute("DELETE FROM studentgroup WHERE studentID="+personID);
            st.execute("UPDATE students SET deleted = 1 WHERE studentID="+personID);
            /**
            ArrayList <Integer> SCORMBooks = new ArrayList <Integer> ();
            RTEFileHandler fileHandler = new RTEFileHandler();

            res = st.executeQuery("SELECT usercourseinfo.courseID FROM usercourseinfo " +
                    " WHERE userID=" + personID);
            while (res.next()){
                SCORMBooks.add(res.getInt(1));
            }

            for (int i = 0; i < SCORMBooks.size(); i ++){
                fileHandler.deleteCourseFiles(SCORMBooks.get(i) , personID);
            }

            st.execute("DELETE FROM usercourseinfo WHERE userID=" + personID);
            st.execute("DELETE FROM objectives WHERE learnerID=" + personID);
            /**/
            con.commit();
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.DELETD, null));
        } catch (Exception exc){
            Log.writeLog(exc);
            try{
                if (con != null && !con.getAutoCommit()) con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            deleted = false;
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_DELETED, null));
        } finally {
            studentsLockManager.finished(lockString);
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return deleted;
    }

    public int getRoleID() {
        return Constants.STUDENT;
    }

    public String getFullName() {
        return lastname + " " + firstname;  
    }

    public String getParents() {
        if (parents == null) return "";
        return parents;
    }

    public int getClassID() {
        return classID;
    }

    public void setParents(String parents) {
        this.parents = parents;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public Date getStazOverallStart() {
		return stazOverallStart;
	}

	public Date getStazSocietyStart() {
		return stazSocietyStart;
	}

	public Date getStazPostStart() {
		return stazPostStart;
	}

	public String getEducationUZ() {
		return educationUZ;
	}

	public void setEducationUZ(String educationUZ) {
		this.educationUZ = educationUZ;
	}

	public String getEducationProfession() {
		return educationProfession;
	}

	public void setEducationProfession(String educationProfession) {
		this.educationProfession = educationProfession;
	}

	public String getEducationQualification() {
		return educationQualification;
	}

	public void setEducationQualification(String educationQualification) {
		this.educationQualification = educationQualification;
	}

	public boolean isHadUpgradedQualification() {
		return hadUpgradedQualification;
	}

	public void setHadUpgradedQualification(boolean hadUpgradedQualification) {
		this.hadUpgradedQualification = hadUpgradedQualification;
	}

	public boolean isDirector() {
		return isDirector;
	}

	public void setDirector(boolean isDirector) {
		this.isDirector = isDirector;
	}

	public boolean isInMaternityLeave() {
		return isInMaternityLeave;
	}

	public void setInMaternityLeave(boolean isInMaternityLeave) {
		this.isInMaternityLeave = isInMaternityLeave;
	}

	public int getDepartmentID() {
		return departmentID;
	}

	public void setDepartmentID(int departmentID) {
		this.departmentID = departmentID;
	}

	public String getShortName() {
        StringBuffer res = new StringBuffer(lastname);
        if (firstname != null && firstname.length() > 0){
            res.append(" ");
            res.append(firstname.substring(0,1));
            res.append(".");
        }
        return res.toString();
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName(){
        if (className == null)
            return "";
        return className;
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

            res = st.executeQuery("SELECT lastname as ln, firstname as fn, patronymic as p " +
                    " FROM students WHERE studentID=" + personID);
            if (res.next()){
                lastname = res.getString("ln");
                firstname = res.getString("fn");
                patronymic = res.getString("p");
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
