package arta.books.logic;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.common.http.FileParser;
import arta.common.SCORMMessages;
import arta.subjects.logic.SubjectMessages;
import arta.filecabinet.logic.FileCabinetMessages;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.ArrayList;

import org.adl.samplerte.server.CourseService;
import org.adl.samplerte.server.ValidationResults;
import org.adl.samplerte.util.RTEFileHandler;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;


public class Book {

    public static final int FILE_TYPE = 0;
    public static final int SCORM_TYPE = 1;

    protected String name = null;
    protected String fileName = "";
    protected int id = 0;
    protected int lang = 0;
    protected String mimeType = "";
    protected int fileSize = 0;
    protected int type = Constants.SUBJECT_BOOK;
    protected int tutorID = 0;

    private int contentID;

    private int bookTypeID = -1;

    public Book(String name) {
        this.name = name;
    }


    public Book() {        
    }

    public void startSave(byte [] resource,Connection con, ResultSet res, Statement st)
            throws Exception{

        FileParser parser = new FileParser(fileName);
        parser.parse();

        st.execute("INSERT INTO contents(resourceID) VALUES (0) ",
                Statement.RETURN_GENERATED_KEYS);
        res = st.getGeneratedKeys();
        if (res.next())
            contentID = res.getInt(1);

        if (resource != null){
            ByteArrayInputStream bin = new ByteArrayInputStream(resource);
            PreparedStatement p;
            p = con.prepareStatement("INSERT INTO contentparts (contentID, partNumber, content)" +
                    " VALUES ("+contentID+", 1, ?) ");
            p.setBinaryStream(1, bin, resource.length);
            p.execute();
            p.clearParameters();
            p = null;
            bin = null;
        }
        fileSize += resource.length;
    }

    public void append(int partNumber, byte [] b, Connection con, PreparedStatement prst) throws Exception{
        ByteArrayInputStream bin = new ByteArrayInputStream(b);
        PreparedStatement p ;
        p = con.prepareStatement("INSERT INTO contentparts (contentID, partNumber, content)" +
                " VALUES ("+contentID+", "+partNumber+", ?) ");
        p.setBinaryStream(1, bin, b.length);
        p.execute();
        p.clearParameters();
        p = null;
        bin = null;
        fileSize += b.length;
        System.gc();
    }

    public void dataParsed(Statement st, ResultSet res, int bookID, String fileName, String bookName,
                           String mimeType, int language, int subjectID, StringTransform trsf) throws Exception{
        name = bookName;
        if (bookID == 0){
            if (fileSize == 0){
                throw new FileNotFoundException("No file present");
            } else {
                st.execute("INSERT INTO resources(subjectID, name, language, filename, mimetype, type, " +
                        "tutorID, contentID, length) VALUES ("+subjectID+", '"+trsf.getDBString(bookName, Varchar.NAME)+"', " +
                        " "+language+", '"+trsf.getDBString(fileName, Varchar.NAME)+"', '"+trsf.getDBString(mimeType, Varchar.NAME)+"', " +
                        " "+ type +", "+tutorID+", "+contentID+" ,"+fileSize+")", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    bookID = res.getInt(1);
                    id = bookID;
                }
                st.execute("UPDATE contents SET resourceID="+bookID+" WHERE contentID="+contentID);
            }
        } else {
            id = bookID;            
            if (fileSize != 0){
                st.execute("DELETE FROM contentparts USING contents, contentparts " +
                        " WHERE contents.contentID=contentparts.contentID AND contents.resourceID="+bookID);
                st.execute("DELETE FROM contents WHERE resourceID="+bookID);
                st.execute("UPDATE resources SET name='"+trsf.getDBString(bookName, Varchar.NAME)+"', " +
                        " language='"+language+"', " +
                        " filename='"+trsf.getDBString(fileName, Varchar.NAME)+"', " +
                        " mimetype='"+trsf.getDBString(mimeType, Varchar.NAME)+"', " +
                        " contentID="+contentID+", " +
                        " length="+fileSize+" " +
                        " WHERE resourceID="+bookID);
                st.execute("UPDATE contents SET resourceID="+bookID+" WHERE contentID="+contentID);
            } else {
                st.execute("DELETE FROM contents WHERE contentID="+contentID);
                st.execute("DELETE FROM contentparts WHERE contentID="+contentID);
                st.execute("UPDATE resources SET name='"+trsf.getDBString(bookName, Varchar.NAME)+"', " +
                        " language='"+language+"' " +
                        " WHERE resourceID="+bookID);
            }
        }
    }

    public boolean importSCORMBook(HttpServletRequest request, String name, int subjectID, int langID,
                                   Message message, int userLangID, boolean validate){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();
        boolean insretFile = true;

        try{

            con = new ConnectionPool().getConnection();
            con.setAutoCommit(false);

            st = con.createStatement();


            if (id > 0)
                insretFile = false;

            if (insretFile){
                st.execute("INSERT INTO resources(subjectID, name, language, booktype, type) " +
                        " VALUES (" + subjectID + ", '" + trsf.getDBString(name) + "', " +
                        " "+langID+", " + Book.SCORM_TYPE + ", "+type+") ",
                        Statement.RETURN_GENERATED_KEYS);

                res = st.getGeneratedKeys();
                if (res.next()){
                    id = res.getInt(1);
                }


                CourseService courseService = new CourseService();

                if (!courseService.importCourse(request, con, id, request.getParameter("validate") != null)){

                    ValidationResults validationResult = courseService.validationResults;

                    DetailedLogMessageCollection dlmc = DetailedLogMessageCollection.getInstance();
                    message.setMessageType(Message.ERROR_MESSAGE);

                    boolean needFullMessage = false;

                    if (validationResult.getManifestExists().equals("false")){
                        needFullMessage = true;

                        message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.MANIFEST_FILE_DOES_NOT_EXIST));

                    } else if (validationResult.getWellFormed().equals("false")){
                        needFullMessage = true;

                        message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.MANIFEST_FILE_IS_NOT_WELL_FORMED));

                    } else if (validationResult.getValidRoot().equals("false")){
                        needFullMessage = true;

                        message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.ROOT_ELEMENTS_DOES_NOT_BELONG_TO_THE_EXPECTED_NAMESPACE));

                    } else if (validationResult.getValidation().equals("true")){
                        needFullMessage = true;
                        if (validationResult.getRequiredFiles().equals("false")){

                            message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.CONTROL_DOCUMENTS_ARE_NOT_LOCATED_AT_ROOT_PACKAGE));

                        }
                        if (validationResult.getValidToSchema().equals("false")){

                            message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.XML_FILE_NOT_VALID_AGAINST_SCHEMAS));

                        }
                        if (validationResult.getValidToProfile().equals("false")){

                            message.addReason(MessageManager.getMessage(userLangID,
                                SCORMMessages.NOT_VALID_TO_SCORM_2004_3_REQUIREMENTS));

                        }
                    }

                    message.setMessageHeader(MessageManager.getMessage(userLangID, Constants.NOT_SAVED ));

                    if (needFullMessage){
                        int collSize = dlmc.getSize();
                        LogMessage currentMessage = dlmc.getMessage();
                        for (int i = 0; i  < collSize; i ++){
                            if (currentMessage.getMessageType() == MessageType.FAILED ||
                                    currentMessage.getMessageType() == MessageType.WARNING)
                                message.addReason(currentMessage.getMessageText());
                        }
                    }


                    con.rollback();
                    return false;
                }
            } else {
                st.execute("UPDATE resources SET name='" + trsf.getDBString(name, Varchar.NAME) + "', " +
                        " language=" + langID + " WHERE resourceID=" + id + " ");
            }

            ArrayList <Integer> students = new ArrayList <Integer> ();

            res = st.executeQuery("SELECT students.studentID FROM " +
                    " students JOIN studygroups ON studygroups.classID=students.classID " +
                    " WHERE studygroups.subjectID=" + subjectID);
            while (res.next()){
                students.add(res.getInt(1));
            }

            String courseID = "";

            res = st.executeQuery("SELECT courseID FROM courseinfo WHERE resourceID=" + id);
            if (res.next()){
                courseID = res.getString(1);
            }

            CourseService courseService = new CourseService();

            Message msg = new Message();
            for (int i = 0; i < students.size(); i ++){
                courseService.regForACourse(students.get(i) + "", courseID, msg, lang, st, res);
            }

            con.commit();

            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(userLangID, Constants.SAVED));

            return true;

        } catch(Exception exc){
            Log.writeLog(exc);
            return false;
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

    public SimpleObject getSubject(int subjectID, int lang){
        SimpleObject subject = new SimpleObject();
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            subject.id = subjectID;
            res = st.executeQuery("SELECT name"+Languages.getLang(lang)+" as name " +
                    " FROM subjects WHERE subjectID="+subjectID);
            if (res.next()){
                subject.name = res.getString("name");
            } else {
                subject.name = "";
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return subject;
    }

    public void load(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT name, language, booktype  FROM " +
                    " resources WHERE resourceID="+id);
            if (res.next()){
                lang = res.getInt("language");
                name = res.getString("name");
                bookTypeID = res.getInt("booktype");
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void delete(Statement st, ResultSet res) throws Exception{

        if (bookTypeID == Book.SCORM_TYPE){

            RTEFileHandler handler = new RTEFileHandler();
            ArrayList <Integer> registeredStudents = new ArrayList <Integer> ();
            String courseID = "";

            res = st.executeQuery("SELECT courseID FROM courseinfo WHERE resourceid=" + id);
            if (res.next())
                courseID = res.getString(1);

            res = st.executeQuery("SELECT userID FROM usercourseinfo " +
                    " WHERE courseID="+courseID+" ");
            while (res.next()){
                registeredStudents.add(res.getInt(1));
            }

            for (int i = 0; i < registeredStudents.size(); i ++ ){
                handler.deleteCourseFiles(courseID , registeredStudents.get(i) + "");
            }

            st.execute("DELETE FROM usercourseinfo WHERE courseid=" + courseID);

            st.execute("DELETE FROM coursestatus WHERE courseid=" + courseID);

            st.execute("DELETE FROM courseinfo WHERE courseid=" + courseID);


        } else {

            st.execute("DELETE FROM contentparts USING contents, contentparts " +
                    " WHERE contents.contentID=contentparts.contentid AND contents.resourceid="+id);

            st.execute("DELETE FROM contents WHERE contents.resourceid="+id);
        }

        st.execute("DELETE FROM resources WHERE resourceID="+id);


    }

    public boolean delete(Message message, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            res = st.executeQuery("SELECT booktype FROM  resources WHERE resourceID=" + id);
            if (res.next())
                bookTypeID = res.getInt(1);
            
            delete(st, res);

            con.commit();
            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
            return true;
        } catch (Exception exc){
            try{
                con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            Log.writeLog(exc);
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            return true;
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private void check(Message message, int lang){
        if (name == null || name.length()==0){
            name = "Not defined";
        } else {
            if (name.length() > Varchar.NAME/2){
                name = name.substring(0, Varchar.NAME/2);
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, Constants.NAME, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
            }
        }
        if (fileName == null ){
            fileName = "";
        } else {
            if (fileName.length()>Varchar.NAME/2){
                fileName = fileName.substring(fileName.length()-Varchar.NAME/2, fileName.length());
            }
        }
    }

    public String getName() {
        if (name == null) return "";
        return name;
    }

    public int getId() { return id; }

    public int getLang() { return lang; }


    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public int getBookTypeID() {
        return bookTypeID;
    }

    public void setBookTypeID(int bookTypeID) {
        this.bookTypeID = bookTypeID;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBookTypeValue(int lang){
        if (bookTypeID == Book.FILE_TYPE){
            return MessageManager.getMessage(lang, SubjectMessages.FILE_BOOK_TYPE);
        }
        if (bookTypeID == Book.SCORM_TYPE){
            return MessageManager.getMessage(lang, SubjectMessages.SCORM_BOOK_TYPE);
        }
        return "-";
    }

    public boolean canBeViewed(){
        return bookTypeID == Book.FILE_TYPE;
    }

}


