/*******************************************************************************
 **
 ** Advanced Distributed Learning Co-Laboratory (ADL Co-Lab) Hub grants you
 ** ("Licensee") a non-exclusive, royalty free, license to use, modify and
 ** redistribute this software in source and binary code form, provided that
 ** i) this copyright notice and license appear on all copies of the software;
 ** and ii) Licensee does not utilize the software in a manner which is
 ** disparaging to ADL Co-Lab Hub.
 **
 ** This software is provided "AS IS," without a warranty of any kind.  ALL
 ** EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 ** ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 ** OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.  ADL Co-Lab Hub AND ITS LICENSORS
 ** SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 ** USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES.  IN NO
 ** EVENT WILL ADL Co-Lab Hub OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE,
 ** PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 ** INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE
 ** THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE
 ** SOFTWARE, EVEN IF ADL Co-Lab Hub HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 ** DAMAGES.
 **
 *******************************************************************************/

package org.adl.samplerte.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.*;

import org.adl.samplerte.util.LMSDBHandler;
import org.adl.samplerte.util.LMSDatabaseHandler;
import org.adl.samplerte.util.RTEFileHandler;
import org.adl.sequencer.ADLSeqUtilities;
import org.adl.sequencer.SeqActivityTree;
import org.adl.validator.ADLValidatorOutcome;
import org.ims.ssp.samplerte.util.SSP_DBHandler;

import java.util.zip.*;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.*;
import org.apache.commons.io.output.DeferredFileOutputStream;

import javax.servlet.http.HttpServletRequest;

import arta.common.Constants;
import arta.common.SCORMMessages;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;

/**
 *
 *
 * <strong>Filename: </strong>CourseService.java <br>
 * <br>
 * <p/>
 * <strong>Description: </strong> <br>
 * The CourseService class handles access to course information in the Sample
 * RTE database. In addition, it handles inserts and updates of new information.
 * <br>
 * <br>
 * <p/>
 * <strong>Design Issues: </strong> <br>
 * This implementation is intended to be used by the SCORM 2004 Sample RTE <br>
 * <br>
 * <p/>
 * <strong>References: </strong> <br>
 * <ul>
 * <li>IMS SS Specification</li>
 * <li>SCORM 2004</li>
 * </ul>
 * @author asdasd
 * @version 1.0.0
 */
public class CourseService {
    /**
     * The string containing the name of the SampleRTEFiles directory.
     */
    // private static final String SRTEFILESDIR = "SCORM3EDSampleRTE102Files";

    /**
     * The userID of the student.
     */
    //String mUserID = "";

    /**
     * Default Constructor
     */
    public CourseService() {
        // default constructor
    }

    /**
     * Returns a List of courses entered in the system
     *
     * @return The list of courses
     */
    public Vector getCourses() {
        String tempUserID = "";
        Vector tempVector = getCourses(tempUserID);
        return tempVector;
    }

    /**
     * This method is used to return a <code>Vector</code> of CourseData
     * objects. These objects correspond to the courses that are currently
     * imported into the Sample RTE course catalog
     *
     * @param iUserID - The ID of the desired user
     * @return <code>Vector</code> of CourseData objects corresponding to the
     *         Sample RTE's course catalog
     */
    public Vector getCourses(String iUserID) {

        // Get all of the course information out of the database
        Connection conn = null;

        PreparedStatement stmtGetCourses;
        String sqlGetCourses = "";

        if (iUserID.equals("")) {
            sqlGetCourses = "SELECT * FROM courseinfo WHERE Active = 1 ORDER BY created DESC";
        } else {
            sqlGetCourses = "SELECT courseinfo.CourseID, courseinfo.CourseTitle, courseinfo.created, "
                    + "courseinfo.Start, courseinfo.TOC, usercourseinfo.SuspendAll  FROM "
                    + "courseinfo, usercourseinfo WHERE usercourseinfo.UserID = ? AND "
                    + "courseinfo.CourseID = usercourseinfo.CourseID AND courseinfo.Active = 1 "
                    + "ORDER BY courseinfo.created DESC";
        }
        Vector courses = new Vector();

        try {
            conn = new ConnectionPool().getConnection();
            stmtGetCourses = conn.prepareStatement(sqlGetCourses);
            ResultSet rsCourses = null;

            synchronized (stmtGetCourses) {
                if (!(iUserID.equals(""))) {

                    stmtGetCourses.setString(1, iUserID);
                }

                rsCourses = stmtGetCourses.executeQuery();

            }

            // Loop through the dataset and create CourseData Objects
            // Add them to a Vector that will be sent to a view.
            while (rsCourses.next()) {
                CourseData cd = new CourseData();
                cd.mCourseID = rsCourses.getString("CourseID");
                cd.mCourseTitle = rsCourses.getString("CourseTitle");
                cd.mImportDateTime = rsCourses.getString("created");
                cd.mStart = rsCourses.getBoolean("Start");
                cd.mTOC = rsCourses.getBoolean("TOC");
                if (!(iUserID.equals(""))) {
                    cd.mSuspend = rsCourses.getBoolean("SuspendAll");
                }
                courses.add(cd);
            }

            // Clean up the database handler connections
            rsCourses.close();
            stmtGetCourses.close();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }

        return courses;
    }


    /**
     * This method is used to return a Vector of SCOData objects. These objects
     * correspond to the SCOs that are currently in the RTE's course catalog <br>
     *
     * @param iCourseID -
     *                  The String course identifier, internal to the Sample RTE. The
     *                  SCOData object returned by this method will be the SCOs
     *                  associated with this course identifier.
     * @return Vector SCOData objects corresponding to the Sample RTE's SCOs in
     *         the course catalog
     */
    public Vector getSCOs(String iCourseID) {

        // Get all of the course information out of the database
        Connection conn = null;

        PreparedStatement stmtGetSCOs;

        String sqlGetSCOs = "SELECT * FROM iteminfo WHERE CourseID = ? AND " + "Type = 'sco'";

        Vector scos = new Vector();

        try {

            conn = new ConnectionPool().getConnection();

            stmtGetSCOs = conn.prepareStatement(sqlGetSCOs);
            ResultSet rsSCOs = null;

            synchronized (stmtGetSCOs) {
                stmtGetSCOs.setString(1, iCourseID);
                rsSCOs = stmtGetSCOs.executeQuery();
            }

            // Loop through the dataset and create SCOData Objects
            // Add them to a Vector that will be sent to a view.
            while (rsSCOs.next()) {
                SCOData sd = new SCOData();
                sd = getSCO(rsSCOs.getInt("ActivityID"));
                scos.add(sd);
            }

            // Clean up the database handler connections
            rsSCOs.close();
            stmtGetSCOs.close();

        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }

        return scos;
    }

    /**
     * This method is used to return a SCOData object. This object corresponds to
     * the current SCO being modified. <br>
     *
     * @param iActivityID -
     *                    The unique identifier (internal to the Sample RTE) used to
     *                    identify a single SCO in the Sample RTE.
     * @return SCOData - object corresponding to the current SCO being modified
     */
    public SCOData getSCO(int iActivityID) {
        // Get all of the course information out of the database

        Connection conn = null;

        PreparedStatement stmtGetSCO;

        String sqlGetSCO = "SELECT * FROM iteminfo WHERE ActivityID = ?";

        SCOData sco = new SCOData();

        try {
            conn = new ConnectionPool().getConnection();
            stmtGetSCO = conn.prepareStatement(sqlGetSCO);
            ResultSet rsSCO = null;

            synchronized (stmtGetSCO) {
                stmtGetSCO.setInt(1, iActivityID);
                rsSCO = stmtGetSCO.executeQuery();
            }

            while (rsSCO.next()) {
                sco.mActivityID = rsSCO.getString("ActivityID");
                sco.mItemTitle = rsSCO.getString("Title");
                sco.mComment = new Vector();
                sco.mDateTime = new Vector();
                sco.mLocation = new Vector();

                String sqlGetComments = "SELECT * FROM scocomments WHERE " + "ActivityID = " + sco.mActivityID;

                PreparedStatement stmtGetComments;
                stmtGetComments = conn.prepareStatement(sqlGetComments);
                ResultSet rsComments = null;

                synchronized (stmtGetComments) {
                    rsComments = stmtGetComments.executeQuery();
                }

                while (rsComments.next()) {
                    sco.mComment.add(rsComments.getString("Comment"));
                    sco.mDateTime.add(rsComments.getString("CommentDateTime"));
                    sco.mLocation.add(rsComments.getString("CommentLocation"));
                }

                // Cleanup the 'comments' connections
                rsComments.close();
                stmtGetComments.close();

            }

            // Cleanup the connections
            rsSCO.close();
            stmtGetSCO.close();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }
        return sco;
    }

    /**
     * This method is used to update SCOData object and its associated comments.
     * <br>
     *
     * @param iActivityID -
     *                    The unique identifier (internal to the Sample RTE) used to
     *                    identify a single SCO in the Sample RTE.
     * @param iComments   -
     *                    A '[.]' delimited list of comments_from_lms Strings to be used
     *                    to initialize the SCO when it is launched
     * @param iUpdate     -
     *                    String representation of whether this is an update (true or false)
     * @param iLocations  -
     *                    The locations
     * @return String - indicates whether the update was successful
     */
    public String updateSCO(int iActivityID, String iComments, String iUpdate, String iLocations) {
        String result = "true";

        // Get all of the course information out of the database
        Connection conn = null;

        PreparedStatement stmtDeleteComment;
        PreparedStatement stmtSetComment;

        String sqlSetComment = "INSERT INTO scocomments(ActivityID, Comment,  "
                + "CommentDateTime, CommentLocation) VALUES(?,?,?,?)";

        String sqlDeleteComment = "DELETE FROM scocomments WHERE ActivityID " + "= ?";

        Vector comments = new Vector();
        comments = getCommentVector(iComments);

        Vector locations = new Vector();
        locations = getCommentVector(iLocations);

        try {
            conn = new ConnectionPool().getConnection();
            stmtSetComment = conn.prepareStatement(sqlSetComment);

            if (iUpdate.equals("false")) {
                // Clean out old comments then insert the new
                stmtDeleteComment = conn.prepareStatement(sqlDeleteComment);

                synchronized (stmtDeleteComment) {
                    stmtDeleteComment.setInt(1, iActivityID);
                    stmtDeleteComment.executeUpdate();
                }

                stmtDeleteComment.close();
            }

            DateFormat date = DateFormat.getDateTimeInstance();

            if (iComments != null && (!iComments.equals(""))) {

                for (int i = 0; i < comments.size(); i++) {
                    if (locations.elementAt(i).equals("")) {
                        synchronized (stmtSetComment) {
                            stmtSetComment.setInt(1, iActivityID);
                            stmtSetComment.setString(2, (String) comments.elementAt(i));
                            stmtSetComment.setString(3, date.format(new Date()));
                            stmtSetComment.executeUpdate();
                        }
                    } else {
                        synchronized (stmtSetComment) {
                            stmtSetComment.setInt(1, iActivityID);
                            stmtSetComment.setString(2, (String) comments.elementAt(i));
                            stmtSetComment.setString(3, date.format(new Date()));
                            stmtSetComment.setString(4, (String) locations.elementAt(i));
                            stmtSetComment.executeUpdate();
                        }
                    }
                }

                stmtSetComment.close();
            }

        }
        catch (Exception e) {
            result = "false";
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

        return result;
    }

    /**
     * This method deletes courses.
     *
     * @param iCourses List of courses selected to be deleted
     * @return String representation of the success of the deletion (true or false)
     */
    public String deleteCourse(Vector iCourses) {

        String result = "true";
        String courseID = "";
        Connection conn = null;
        PreparedStatement stmtUpdateCourse;
        String sqlUpdateCourse = "UPDATE courseinfo set Active = no where CourseID = ?";

        try {
            conn = new ConnectionPool().getConnection();

            stmtUpdateCourse = conn.prepareStatement(sqlUpdateCourse);
            for (int i = 0; i < iCourses.size(); i++) {

                synchronized (stmtUpdateCourse) {
                    courseID = (String) iCourses.elementAt(i);
                    stmtUpdateCourse.setString(1, courseID);
                    stmtUpdateCourse.executeUpdate();
                }
            }
            stmtUpdateCourse.close();
            conn.close();

        }
        catch (Exception e) {
            result = "false";
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }

        return result;
    }

    /**
     * This function will return the list of courses for which a user is
     * registered.
     *
     * @param iUserID ID of the user whose courses shall be returned
     * @return String list of the courses
     */
    public String getRegCourses(String iUserID) {

        String userCourses = "|";
        Connection conn = null;
        PreparedStatement stmtRegSelectUserCourse;
        String sqlSelectUserCourse = "SELECT * FROM usercourseinfo WHERE UserID = ?";

        try {
            conn = new ConnectionPool().getConnection();
            stmtRegSelectUserCourse = conn.prepareStatement(sqlSelectUserCourse);


            ResultSet userCourseRS = null;

            // returns a list of all courses for which a user is registered
            synchronized (stmtRegSelectUserCourse) {
                stmtRegSelectUserCourse.setString(1, iUserID);
                userCourseRS = stmtRegSelectUserCourse.executeQuery();
            }

            while (userCourseRS.next()) {
                userCourses += userCourseRS.getString("CourseID") + "|";
            }

            userCourseRS.close();
            stmtRegSelectUserCourse.close();            
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                conn.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

        return userCourses;

    }

    public boolean regForACourse(String userID, String courseID, Message message, int lang,
                                 Statement st, ResultSet res)
            throws Exception {
        StringTransform trsf = new StringTransform();
        SeqActivityTree mySeqActivityTree;

        //Проверяем не был ли обучающийся зарегистрирован на данный курс ранее
        res = st.executeQuery("SELECT * FROM usercourseinfo WHERE userID = '" + trsf.getDBString(userID) + "' " +
                " AND CourseID = '" + trsf.getDBString(courseID) + "' ");

        if (res.next()) {
            message.addReason(MessageManager.getMessage(lang,
                    SCORMMessages.USER_CANNNOT_BE_REGISTERED_FOR_PREVIOUSLY_REGESTERED_COURSE));
            return false;
        }

        //Если обучающийся не был зарегистрирован на данный курс ранее
        //Делаем соответствующую запись в базе
        st.execute("INSERT INTO usercourseinfo (UserID, CourseID) VALUES('" + trsf.getDBString(userID) + "', " +
                " '" + trsf.getDBString(courseID) + "')");

        st.execute("INSERT INTO coursestatus (learnerID, courseID) VALUES('" + trsf.getDBString(userID) + "', " +
                " '" + trsf.getDBString(courseID) + "' )");

        //Считываем информацию о дереве из директории куда были импортированы учебники
        String tree = Constants.getCoursesStoreDirectory() + File.separator + courseID + File.separator + "serialize.obj";
        FileInputStream in = new FileInputStream(tree);
        ObjectInputStream ie = new ObjectInputStream(in);
        mySeqActivityTree = (SeqActivityTree) ie.readObject();
        ie.close();
        in.close();

        mySeqActivityTree.setLearnerID(userID);

        String scope = mySeqActivityTree.getScopeID();

        Vector theGobalObjectiveList = mySeqActivityTree.getGlobalObjectives();

        if (theGobalObjectiveList != null) {
            ADLSeqUtilities.createGlobalObjs(userID, scope, theGobalObjectiveList, st);
        }

        //Создаем директорию куда будет складываться информация об обходе дерева данным обучающимся
        String userDir = Constants.getUsersInfoDirectory() + File.separator + userID + File.separator + courseID;
        File theRTESCODataDir = new File(userDir);
        // The course directory should not exist yet
        if (!theRTESCODataDir.isDirectory()) {
            theRTESCODataDir.mkdirs();
        }

        //Serialize the activity tree out to the user directory
        String serializeFileName = Constants.getUsersInfoDirectory() + File.separator + userID + File.separator + courseID
                + File.separator + "serialize.obj";
        FileOutputStream outFile = new FileOutputStream(serializeFileName);
        ObjectOutputStream s = new ObjectOutputStream(outFile);
        s.writeObject(mySeqActivityTree);
        s.flush();
        s.close();
        outFile.close();

        return true;
    }

    public boolean regForACourse(String userID, String courseID, Message message, int lang) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            boolean result = regForACourse(userID, courseID, message, lang, st, res);

            if (result)
                con.commit();

            return result;

        } catch (Exception exc) {
            Log.writeLog(exc);

            try {

                if (con != null && !con.getAutoCommit())
                    con.rollback();

            } catch (Exception e) {
                Log.writeLog(e);
            }

            try {

                RTEFileHandler handler = new RTEFileHandler();
                handler.deleteFile(Constants.getUsersInfoDirectory() + File.separator + userID + File.separator + courseID);

            } catch (Exception e) {
                Log.writeLog(e);
            }

            return false;
        } finally {
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

    }


    /**
     * Updates the list of courses for which a chosen user is registered.
     *
     * @param iCourseIDs The list of courses that are selected
     * @param iPath      The web path
     * @param iUserID    The ID of the user.
     * @return String representation of the success of this action (true or false)
     */
    public String updateRegCourses(Vector iCourseIDs, String iPath, String iUserID) {

        String result = "true";

        Connection conn = null;
        Connection csConn = null;
        PreparedStatement stmtSelectCourse;
        PreparedStatement stmtSelectUserCourse;
        PreparedStatement stmtInsertUserCourse;
        PreparedStatement stmtInsertCourseStatus;
        PreparedStatement stmtDeleteUserCourse;
        PreparedStatement stmtDeleteCourseStatus;

        String sqlSelectUserCourse = "SELECT * FROM usercourseinfo WHERE UserID = ? AND CourseID = ?";

        String sqlSelectCourse = "SELECT * FROM usercourseinfo WHERE UserID = ?";

        String sqlInsertUserCourse = "INSERT INTO usercourseinfo (UserID, CourseID) VALUES(?,?)";

        String sqlDeleteUserCourse = "DELETE FROM usercourseinfo WHERE UserID = ? AND CourseID = ?";

        String sqlInsertCourseStatus = "INSERT INTO coursestatus (learnerID, courseID) VALUES(?,?)";

        String sqlDeleteCourseStatus = "DELETE FROM coursestatus WHERE learnerID = ? AND courseID = ?";

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            conn = new ConnectionPool().getConnection();
            csConn = new ConnectionPool().getConnection();
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            stmtSelectCourse = conn.prepareStatement(sqlSelectCourse);
            stmtSelectUserCourse = conn.prepareStatement(sqlSelectUserCourse);
            stmtInsertUserCourse = conn.prepareStatement(sqlInsertUserCourse);
            stmtDeleteUserCourse = conn.prepareStatement(sqlDeleteUserCourse);
            stmtInsertCourseStatus = csConn.prepareStatement(sqlInsertCourseStatus);
            stmtDeleteCourseStatus = csConn.prepareStatement(sqlDeleteCourseStatus);


            String selectedCourses = "|";

            RTEFileHandler fileHandler = new RTEFileHandler();

            Message message = new Message();

            for (int i = 0; i < iCourseIDs.size(); i++) {

                String paramName = (String) iCourseIDs.elementAt(i);
                String courseID = paramName;
                selectedCourses += courseID + "|";

                try {
                    new Integer(iCourseIDs.elementAt(i) + "");
                } catch (Exception exc) {
                    continue;
                }
                regForACourse(iUserID, iCourseIDs.elementAt(i) + "", message, 0, st, res);
            }

            ResultSet courseRS = null;

            // Find what courses the user is registered for, then check
            // to see if on current list. If not, delete entries in
            // UserCourseInfo. Delete datamodel files.

            synchronized (stmtSelectCourse) {
                stmtSelectCourse.setString(1, iUserID);
                courseRS = stmtSelectCourse.executeQuery();
            }

            while (courseRS.next()) {

                String courseID = courseRS.getString("CourseID");

                // Look for courses that are not selected for the user
                if (selectedCourses.indexOf("|" + courseID + "|") == -1) {

                    synchronized (stmtDeleteUserCourse) {
                        stmtDeleteUserCourse.setString(1, iUserID);
                        stmtDeleteUserCourse.setString(2, courseID);
                        stmtDeleteUserCourse.executeUpdate();
                    }
                    synchronized (stmtDeleteCourseStatus) {
                        stmtDeleteCourseStatus.setString(1, iUserID);
                        stmtDeleteCourseStatus.setString(2, courseID);
                        stmtDeleteCourseStatus.executeUpdate();
                    }
                    fileHandler.deleteCourseFiles(courseID, iUserID);
                }
            }

            courseRS.close();
            stmtSelectCourse.close();
            stmtSelectUserCourse.close();
            stmtInsertUserCourse.close();
            stmtDeleteUserCourse.close();
            stmtInsertCourseStatus.close();
            stmtDeleteCourseStatus.close();

        } catch (Exception e) {
            result = "false";
            Log.writeLog(e);
        } finally {
            try {
                csConn.close();
                con.close();
                conn.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

        return result;

    }

    /**
     * Shows the status of a selected course for a specific user
     *
     * @param iCourseID The course ID
     * @param iUserID   ID of the user
     * @return CourseData info.
     */
    public CourseData showCourseStatus(String iCourseID, String iUserID) {
        String courseID = iCourseID;
        CourseData cs = new CourseData();

        Connection conn = null;

        try {

            conn = new ConnectionPool().getConnection();
            PreparedStatement stmtSelectStatus;

            //Query String to obtain Courses
            String sqlSelectStatus = "SELECT * FROM coursestatus where " + "learnerID = ? AND courseID = ?";

            stmtSelectStatus = conn.prepareStatement(sqlSelectStatus);

            ResultSet statusRS;

            synchronized (stmtSelectStatus) {
                stmtSelectStatus.setString(1, iUserID);
                stmtSelectStatus.setString(2, courseID);
                statusRS = stmtSelectStatus.executeQuery();
            }

            while (statusRS.next()) {
                cs.mSatisfied = statusRS.getString("satisfied");
                cs.mMeasure = statusRS.getString("measure");
                cs.mCompleted = statusRS.getString("completed");
            }

            statusRS.close();

            stmtSelectStatus.close();

        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }
        return cs;
    }

    /**
     * This is to return a string representation of the name of the chosen user.
     *
     * @param iUserID The user ID representing the name to be returned
     * @return String The name associated with the user ID passed in. Returned as
     *         "First Last".
     */
    public String getName(String iUserID) {

        String name = "";
        String lastName = "";
        String firstName = "";
        Connection conn = null;

        try {

            PreparedStatement stmtSelectUser;
            conn = new ConnectionPool().getConnection();
            String sqlSelectUser = "SELECT * FROM userinfo WHERE UserID = ?";
            stmtSelectUser = conn.prepareStatement(sqlSelectUser);
            ResultSet userRS;

            synchronized (stmtSelectUser) {
                stmtSelectUser.setString(1, iUserID);
                userRS = stmtSelectUser.executeQuery();
            }

            while (userRS.next()) {
                lastName = userRS.getString("LastName");
                firstName = userRS.getString("FirstName");
            }

            name += firstName + " " + lastName;

            userRS.close();
            stmtSelectUser.close();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }
        return name;
    }

    /**
     * This method clears the courses from the database
     *
     * @param iPath - The web path
     * @return String - indicates whether clearDatabase was successful
     */

    public String clearDatabase(String iPath) {

        String result = "true";
        Connection conn = null;
        Connection objConn = null;
        Connection sspConn = null;

        try {

            conn = new ConnectionPool().getConnection();
            objConn = new ConnectionPool().getConnection();
            sspConn = new ConnectionPool().getConnection();
            PreparedStatement stmtDeleteCourseInfo;
            PreparedStatement stmtUpdateApplicationData;
            PreparedStatement stmtGetCourseList;
            PreparedStatement stmtGetUserIDList;
            PreparedStatement stmtGetCourses;
            PreparedStatement stmtDeleteObj;
            PreparedStatement stmtDeleteStatus;

            PreparedStatement stmtDeleteSSP;

            // Set the driverName and connectionURL variables and establishes the
            // database connection. The SQL string are also assigned and converted
            // to a prepared statement.
            String sqlGetUserList = "SELECT * FROM userinfo";
            String sqlGetCourseList = "SELECT * FROM usercourseinfo WHERE UserId = ?";
            String sqlGetCourses = "SELECT * FROM courseinfo";
            String sqlDeleteCourseInfo = "Delete FROM courseinfo";
            String sqlUpdateApplicationData = "UPDATE applicationdata "
                    + "SET numberValue = '1' WHERE dataName = 'nextCourseID'";
            String sqlDeleteCourseObjs = "Delete FROM objectives";
            String sqlDeleteCourseStatus = "Delete FROM coursestatus";

            // SSP SQL Statements
            String sqlDeleteSSP = "Delete FROM SSP_BucketTbl";

            //get users and courses they are registered for and delete all the
            // course files
            stmtGetCourseList = conn.prepareStatement(sqlGetCourseList);
            stmtGetUserIDList = conn.prepareStatement(sqlGetUserList);
            stmtGetCourses = conn.prepareStatement(sqlGetCourses);
            stmtDeleteCourseInfo = conn.prepareStatement(sqlDeleteCourseInfo);
            stmtUpdateApplicationData = conn.prepareStatement(sqlUpdateApplicationData);

            RTEFileHandler fileHandler = new RTEFileHandler();

            ResultSet userRS = null;
            userRS = stmtGetUserIDList.executeQuery();
            ResultSet courseRS = null;
            String user = new String();

            while (userRS.next()) {
                user = userRS.getString("UserID");
                synchronized (stmtGetCourseList) {
                    stmtGetCourseList.setString(1, user);
                    courseRS = stmtGetCourseList.executeQuery();
                }
                while (courseRS.next()) {
                    fileHandler.deleteCourseFiles(courseRS.getString("CourseID"), user);
                }
            }

            String theWebPath = iPath;
            String mCourseDir = Constants.getCoursesStoreDirectory() + File.separator;
            ResultSet courseListRS = null;
            courseListRS = stmtGetCourses.executeQuery();
            //delete the template course files from the CourseImports folder
            while (courseListRS.next()) {
                File mRTESCODataDir = new File(mCourseDir + File.separator + courseListRS.getString("CourseID"));
                File mScoFiles[] = mRTESCODataDir.listFiles();

                for (int i = 0; i < mScoFiles.length; i++) {
                    deleteCourseFiles(mScoFiles[i]);
                }
                mRTESCODataDir.delete();
            }

            // Execute the queries to delete all records in the CourseInfo table
            // and to update the ApplicationData table.
            stmtDeleteCourseInfo.executeUpdate();
            stmtUpdateApplicationData.executeUpdate();

            // Close the statement and the database connection.
            stmtDeleteCourseInfo.close();
            stmtUpdateApplicationData.close();

            // Delete global objectives
            objConn = LMSDBHandler.getConnection();
            stmtDeleteObj = objConn.prepareStatement(sqlDeleteCourseObjs);
            stmtDeleteObj.executeUpdate();
            stmtDeleteObj.close();
            stmtDeleteStatus = objConn.prepareStatement(sqlDeleteCourseStatus);
            stmtDeleteStatus.executeUpdate();
            stmtDeleteStatus.close();

            // Delete SSP Database Table
            sspConn = SSP_DBHandler.getConnection();
            stmtDeleteSSP = sspConn.prepareStatement(sqlDeleteSSP);
            stmtDeleteSSP.executeUpdate();
            stmtDeleteSSP.close();

            fileHandler.deleteAllSSPData();
        }
        catch (SQLException e) {
            System.out.println("sql exception in");
            result = "false";
            Log.writeLog(e);
        }
        catch (Exception e) {
            result = "false";
            Log.writeLog(e);
        }
        finally{
            try{
                conn.close();
                objConn.close();
                sspConn.close();
            }catch( Exception exc){
                Log.writeLog(exc);
            }

        }

        return result;
    }

    /**
     * Deletes course files
     *
     * @param iDeleteFile - file selected for deletion
     */
    public void deleteCourseFiles(File iDeleteFile) {
        try {
            if (iDeleteFile.isDirectory()) {
                File mScoFiles[] = iDeleteFile.listFiles();
                for (int i = 0; i < mScoFiles.length; i++) {
                    deleteCourseFiles(mScoFiles[i]);
                }
            }

            iDeleteFile.delete();
        }
        catch (Exception e) {
            Log.writeLog(e);
        }
    }

    /**
     * This adds the ObjectivesData information
     *
     * @param ioObject - ObjectivesData object to be added
     * @return String - success of the action
     */
    public String addObj(ObjectivesData ioObject) {
        String result = "true";
        String userID = ioObject.mUserID;
        String objectiveID = ioObject.mObjectiveID;
        String satisfied = ioObject.mSatisfied;
        String satisfiedValue = new String(satisfied);
        String measure = ioObject.mMeasure;
        String newMeasure = "unknown";

        if (satisfied.equals("not satisfied")) {
            satisfiedValue = "notSatisfied";
        }

        Connection conn = null;

        String sqlSelectObjectives = "SELECT * FROM objectives WHERE objID = ? " + "AND learnerID = ? AND scopeID = ?";
        PreparedStatement stmtSelectObjectives;
        String sqlInsertObjective = "INSERT INTO objectives VALUES(?,?,?,?,?)";
        PreparedStatement stmtInsertObjective;
        ResultSet objectivesRS = null;

        try {
            Double doubleMeasure = new Double(measure);
            Double tempDouble = new Double(-1.0);
            int compareLower = doubleMeasure.compareTo(tempDouble);
            tempDouble = new Double(1.0);
            int compareUpper = doubleMeasure.compareTo(tempDouble);
            if ((compareLower >= 0) && (compareUpper <= 0)) {
                newMeasure = measure;
            }
        }
        catch (Exception e) {
            Log.writeLog(e);
        }


        try {
            conn = new ConnectionPool().getConnection();
            stmtSelectObjectives = conn.prepareStatement(sqlSelectObjectives);

            synchronized (stmtSelectObjectives) {
                stmtSelectObjectives.setString(1, objectiveID);
                stmtSelectObjectives.setString(2, userID + "");
                stmtSelectObjectives.setString(3, "");
                objectivesRS = stmtSelectObjectives.executeQuery();
            }

            if (objectivesRS.next()) {
                ioObject.mObjErr = "dupobjid";
                result = "false";
            } else {
                stmtInsertObjective = conn.prepareStatement(sqlInsertObjective);
                synchronized (stmtInsertObjective) {
                    stmtInsertObjective.setString(1, objectiveID);
                    stmtInsertObjective.setString(2, userID + "");
                    stmtInsertObjective.setString(3, satisfiedValue);
                    stmtInsertObjective.setString(4, newMeasure);
                    stmtInsertObjective.setString(5, "");
                    stmtInsertObjective.executeUpdate();
                }
            }
        }
        catch (Exception e) {
            result = "false";
            Log.writeLog(e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

        return result;
    }

    /**
     * Returns a list (Vector) of global objectives for the desired user
     *
     * @param iUserID - the desired user
     * @param iObjs   - the course specific objectives for the desired user
     * @return List (Vector) of global objectives
     */
    public Vector getGlobalObjs(String iUserID, Vector iObjs) {

        Vector obj = iObjs;
        Connection conn = null;

        try {

            PreparedStatement stmtSelectGlobals;

            conn = new ConnectionPool().getConnection();

            String sqlSelectGlobals = "SELECT * FROM objectives where " + "learnerID = ? and scopeID = ''";
            stmtSelectGlobals = conn.prepareStatement(sqlSelectGlobals);
            synchronized (stmtSelectGlobals) {
                stmtSelectGlobals.setString(1, iUserID);
            }

            ResultSet globalsRS;

            globalsRS = stmtSelectGlobals.executeQuery();

            boolean foundObjective = false;
            boolean firstQueryEmpty = true;
            while (globalsRS.next()) {
                ObjectivesData od = new ObjectivesData();
                od.mObjectiveID = globalsRS.getString("objID");
                od.mUserID = globalsRS.getString("learnerID");
                od.mSatisfied = globalsRS.getString("satisfied");
                od.mMeasure = globalsRS.getString("measure");

                if (obj != null) {
                    for (int i = 0; i < obj.size(); i++) {
                        firstQueryEmpty = false;
                        ObjectivesData od2 = (ObjectivesData) obj.elementAt(i);
                        if (od.mObjectiveID.equals(od2.mObjectiveID)) {
                            foundObjective = true;
                            break;
                        }
                    }
                }
                if (firstQueryEmpty || !foundObjective) {
                    obj.add(od);
                }
            }
            globalsRS.close();
            stmtSelectGlobals.close();
            LMSDBHandler.closeConnection();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }

        return obj;
    }

    /**
     * Gets a List of objectives associated with courses for which a user is registered
     *
     * @param iCourseID - The ID of the course
     * @param iUserID   - The ID of the user
     * @return List of objectives for these desired course
     */
    public Vector getObjs(String iCourseID, String iUserID) {

        String courseID = iCourseID;
        Vector obj = new Vector();
        Connection conn = null;

        try {

            conn = new ConnectionPool().getConnection();
            PreparedStatement stmtSelectObjectives;

            //Query String to obtain Courses
            String sqlSelectObjectives = "SELECT * FROM objectives where learnerID = ? AND scopeID = ?";

            stmtSelectObjectives = conn.prepareStatement(sqlSelectObjectives);

            synchronized (stmtSelectObjectives) {
                stmtSelectObjectives.setString(1, iUserID);
                stmtSelectObjectives.setString(2, courseID);
            }

            ResultSet objectivesRS = stmtSelectObjectives.executeQuery();

            if (!courseID.equals("")) {
                // Loops through all of the global objectives and outputs them in
                // the table with a radio button for selection of delete or reset.
                while (objectivesRS.next()) {
                    ObjectivesData od = new ObjectivesData();
                    od.mObjectiveID = objectivesRS.getString("objID");
                    od.mUserID = objectivesRS.getString("learnerID");
                    od.mSatisfied = objectivesRS.getString("satisfied");
                    od.mMeasure = objectivesRS.getString("measure");
                    obj.add(od);
                }
            }
            objectivesRS.close();
            stmtSelectObjectives.close();
            LMSDBHandler.closeConnection();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return obj;
    }

    /**
     * Edits the objectives based on the parameters being passed in
     *
     * @param iParams - List of parameters
     * @return String representation of the success of this operation (true or false)
     */
    public String editObjs(Vector iParams) {

        String result = "true";
        Vector requestNames = iParams;
        Connection conn = null;

        try {

            conn = new ConnectionPool().getConnection();

            PreparedStatement stmtUpdateObjective;
            PreparedStatement stmtDeleteObjective;

            String sqlUpdateObjective = "UPDATE objectives SET satisfied = 'unknown', measure = 'unknown' "
                    + "WHERE objID = ? AND learnerID = ?";

            String sqlDeleteObjective = "DELETE FROM objectives WHERE objID = ? AND learnerID = ?";

            stmtUpdateObjective = conn.prepareStatement(sqlUpdateObjective);

            stmtDeleteObjective = conn.prepareStatement(sqlDeleteObjective);

            // loop through all of the parameters.
            for (int i = 0; i < requestNames.size(); i++) {
                String param = (String) requestNames.elementAt(i);

                String paramName;
                String paramValue;
                int splitIndex;

                splitIndex = param.lastIndexOf(":");
                paramName = param.substring(0, splitIndex);
                paramValue = param.substring(splitIndex + 1, param.length());

                String objID;
                String learnerID;

                // If the parameter is not the submit button
                if (!(paramName.equals("submit"))) {
                    splitIndex = paramName.lastIndexOf(";");
                    objID = paramName.substring(0, splitIndex);
                    learnerID = paramName.substring(splitIndex + 1, paramName.length());

                    if (paramValue.equals("reset")) {
                        synchronized (stmtUpdateObjective) {
                            stmtUpdateObjective.setString(1, objID);
                            stmtUpdateObjective.setString(2, learnerID);
                            stmtUpdateObjective.executeUpdate();
                        }
                    } else if (paramValue.equals("delete")) {
                        synchronized (stmtDeleteObjective) {
                            stmtDeleteObjective.setString(1, objID);
                            stmtDeleteObjective.setString(2, learnerID);
                            stmtDeleteObjective.executeUpdate();
                        }
                    }
                }
            }
            stmtUpdateObjective.close();
            stmtDeleteObjective.close();
            LMSDBHandler.closeConnection();
        }
        catch (Exception e) {
            Log.writeLog(e);
        } finally{
            try{
                if (conn != null) conn.close();
            } catch( Exception exc){
                Log.writeLog(exc);
            }
        }
        return result;
    }

    /**
     * This method is used to convert a '\n' delimited string of comments to a
     * Vector. <br>
     *
     * @param iComments -
     *                  A String list of comments delimited by '[.]'
     * @return Vector - A <code>Vector</code> of Strings that correspond to the
     *         <code>cmi.comments_from_lms.n.comment</code> value of a SCO.
     */
    public Vector getCommentVector(String iComments) {
        String[] commentArray = iComments.split("\n");

        Vector commentVector = new Vector();

        for (int i = 0; i < commentArray.length; i++) {
            String cmt = commentArray[i].trim();
            commentVector.add(cmt);
        }

        return commentVector;
    }


    public boolean importCourse(HttpServletRequest iRequest, String webPath,
                                String sessionID, boolean needValidate) {

        Connection con = null;

        try{

            con = new ConnectionPool().getConnection();

            return importCourse(iRequest, webPath, sessionID, con, 0, needValidate);
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null)
                    con.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return false;

    }

    public boolean importCourse(HttpServletRequest iRequest, Connection con, int resourceID, boolean needValidate) {
        return importCourse(iRequest, "", "", con, resourceID, needValidate);

    }

    public boolean importCourse(HttpServletRequest iRequest, boolean needValidate) {

        Connection con = null;

        try{

            con = new ConnectionPool().getConnection();

            return importCourse(iRequest, "", "", con, 0, needValidate);
        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null)
                    con.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }
        return false;
    }

    /**
     * This method takes a course from the import page and uploads it to
     * the server.
     * <p/>
     * Метод импортирует курс.
     *
     * @param iWebPath - A String representation of the path to the server.
     * @param iSessionID - The id of the server session.
     * @return ValidationResults - Encapsulates the information about
     * the validation of the course submitted to be imported to the Sample RTE.
     */
    public ValidationResults validationResults;

    public boolean importCourse(HttpServletRequest iRequest,
                                String iWebPath, String iSessionID, Connection con,
                                int resourceID, boolean needValidate) {

        this.validationResults = new ValidationResults();
        //String sessionID = "";
        //Временная директория, куда будет загружен учебник
        String tmpZipDirectory = "";
        String fileName = "";
        String myFileName = "";     //название архива
        String courseTitle = "";
        LMSManifestHandler myManifestHandler;

        boolean validation = false;

        try {

            //sessionID = iSessionID;
            String fileSeparator = java.io.File.separator;
            String theWebPath = iWebPath;  //E:\Development\Services\tomcat\webapps\adl\

            //uploadDir = Constants.COURSES_TEMP_DIRECTTORY + fileSeparator + "tempUploads" + fileSeparator + sessionID;
            String courseIdentifier = Constants.getCourseIdentifier();
            tmpZipDirectory = Constants.getTempZipFilesDirectory(courseIdentifier);

            java.io.File theRTEUploadDir = new java.io.File(tmpZipDirectory);
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            if (!theRTEUploadDir.isDirectory()) {
                theRTEUploadDir.mkdirs();
            }

            // Parse the request
            List items = upload.parseRequest(iRequest);
            Iterator iter = items.iterator();
            FileItem item = (FileItem) iter.next();

            String name = item.getFieldName();
            if (name.equals("coursezipfile")) {
                fileName = (new File(fileName)).getName();
                int index = item.getName().lastIndexOf(fileSeparator);
                index = index + 1;
                myFileName = item.getName().substring(index);
                File fNew = new File(tmpZipDirectory, myFileName);
                courseTitle = fileName;
                item.write(fNew);
            }

            FileItem item2 = (FileItem) iter.next();
            String validationValue = item2.getString();

            validation = needValidate;

            String zipFile = tmpZipDirectory + fileSeparator + myFileName;
            String theXSDPath = theWebPath;/*.
                             substring(0, theWebPath.
                                          lastIndexOf(fileSeparator));*/ //  E:\Development\Services\tomcat\webapps\adl

            // Create a manifest handler instance
            myManifestHandler = new LMSManifestHandler(theXSDPath);
            myManifestHandler.setCourseName(courseTitle);
            myManifestHandler.setWebPath(theWebPath);

            // Parse the manifest and fill up the object structure
            ADLValidatorOutcome result = myManifestHandler.processPackage(zipFile, validation, 
                    courseIdentifier, con, resourceID);

            if ((result.getDoesIMSManifestExist() && result.getIsWellformed() && validation == false)
                    || (result.getDoesIMSManifestExist() && result.getIsWellformed() && result.getIsValidRoot()
                    && result.getIsValidToSchema() && result.getIsValidToApplicationProfile() && result
                    .getDoRequiredCPFilesExist() && myManifestHandler.getPackageProcessingStatus())) {
                if (validation) {
                    validationResults.setValidation("true");
                } else {
                    validationResults.setValidation("false");
                }

                validationResults.setRedirectView("/import/dsp_confirmImport.jsp");
                return true;
            } else {
                if (validation) {
                    validationResults.setValidation("true");
                } else {
                    validationResults.setValidation("false");
                }
                if (result.getDoesIMSManifestExist()) {
                    validationResults.setManifestExists("true");
                } else {
                    validationResults.setManifestExists("false");
                }
                if (result.getIsWellformed()) {
                    validationResults.setWellFormed("true");
                } else {
                    validationResults.setWellFormed("false");
                }
                if (result.getIsValidRoot()) {
                    validationResults.setValidRoot("true");
                } else {
                    validationResults.setValidRoot("false");
                }
                if (result.getIsValidToSchema()) {
                    validationResults.setValidToSchema("true");
                } else {
                    validationResults.setValidToSchema("false");
                }
                if (result.getIsValidToApplicationProfile()) {
                    validationResults.setValidToProfile("true");
                } else {
                    validationResults.setValidToProfile("false");
                }
                if (result.getDoRequiredCPFilesExist()) {
                    validationResults.setRequiredFiles("true");
                } else {
                    validationResults.setRequiredFiles("false");
                }

                // If the error occurred during package processing, no error will
                // be set in validationResult, but an invalid import will be reported
                validationResults.setRedirectView("/import/dsp_invalidImport.jsp");
            }

        } catch (Exception e) {
            Log.writeLog(e);
        }

        return false;
    }

    /**
     * Utility Method to copy a file
     *
     * @param inFile  - File to copy.
     * @param outFile - Destination file.
     */
    public boolean ZipCopy(File inFile, File outFile) {

        boolean success = false;
        try {
            FileInputStream fis = new FileInputStream(inFile);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            fis.close();
            fos.close();
            success = true;
        }
        catch (IOException ioe) {
            Log.writeLog(ioe);
        }
        return success;
    }

    /**
     * This method takes a all courses in a specified directory and uploads them to
     * the server.
     *
     * @param iZipFile    - A String representation of the path to the file.
     * @param iDirectory  - A file representation of the path to the folder
     * @param iWebPath    - A String representation used to determine the drive on which
     *                    the Sample RTE is installed.
     * @param iSessionID  - The id of the server session.
     * @param iValidation - String determining whether or not to perform validation.
     * @return ValidationResults - Encapsulates the information about
     *         the validation of the course submitted to be imported to the Sample RTE.
     * @parma iFilename - A String representation of the name of the file.
     */

    public ValidationResults importMultipleCourses(String iFilename, String iZipFile, File iDirectory,
                                                   String iWebPath, String iSessionID,
                                                   String iValidation) {
        ValidationResults validationResults = new ValidationResults();
        String sessionID = "";
        String uploadDir = "";
        String fileName = "";
        String myFileName = "";
        String courseTitle = "";
        LMSManifestHandler myManifestHandler;

        boolean validation = true;
        if (iValidation.equals("0")) {
            validation = false;
        } else if (iValidation.equals("1")) {
            validation = true;
        }

        try {
            sessionID = iSessionID;
            String fileSeparator = java.io.File.separator;

            String courseIdentifier = Constants.getCourseIdentifier();

            uploadDir = Constants.getTempZipFilesDirectory(courseIdentifier) + fileSeparator + sessionID;
            java.io.File theRTEUploadDir = new java.io.File(uploadDir);

            if (!theRTEUploadDir.isDirectory()) {
                theRTEUploadDir.mkdirs();
            }

            String zipFile = iZipFile;
            myFileName = iFilename;
            File fNew = new File(uploadDir, myFileName);
            courseTitle = fileName;

            String theWebPath = iWebPath;

            File courseFile = new File(iZipFile);
            boolean success = ZipCopy(courseFile, fNew);

            String theXSDPath = theWebPath.substring(0, theWebPath.
                    lastIndexOf(fileSeparator));

            // Create a manifest handler instance
            myManifestHandler = new LMSManifestHandler(theXSDPath);

            myManifestHandler.setCourseName(courseTitle);

            myManifestHandler.setWebPath(theWebPath);

            // Parse the manifest and fill up the object structure
            ADLValidatorOutcome result = myManifestHandler.processPackage(zipFile, validation, courseIdentifier, null, 0);

            if ((result.getDoesIMSManifestExist() && result.getIsWellformed() && validation == false)
                    || (result.getDoesIMSManifestExist() && result.getIsWellformed() && result.getIsValidRoot()
                    && result.getIsValidToSchema() && result.getIsValidToApplicationProfile() && result
                    .getDoRequiredCPFilesExist() && myManifestHandler.getPackageProcessingStatus())) {
                if (validation) {
                    validationResults.setValidation("true");
                } else {
                    validationResults.setValidation("false");
                }

                validationResults.setRedirectView("/import/dsp_confirmImport.jsp");

            } else {
                if (validation) {
                    validationResults.setValidation("true");
                } else {
                    validationResults.setValidation("false");
                }
                if (result.getDoesIMSManifestExist()) {
                    validationResults.setManifestExists("true");
                } else {
                    validationResults.setManifestExists("false");
                }
                if (result.getIsWellformed()) {
                    validationResults.setWellFormed("true");
                } else {
                    validationResults.setWellFormed("false");
                }
                if (result.getIsValidRoot()) {
                    validationResults.setValidRoot("true");
                } else {
                    validationResults.setValidRoot("false");
                }
                if (result.getIsValidToSchema()) {
                    validationResults.setValidToSchema("true");
                } else {
                    validationResults.setValidToSchema("false");
                }
                if (result.getIsValidToApplicationProfile()) {
                    validationResults.setValidToProfile("true");
                } else {
                    validationResults.setValidToProfile("false");
                }
                if (result.getDoRequiredCPFilesExist()) {
                    validationResults.setRequiredFiles("true");
                } else {
                    validationResults.setRequiredFiles("false");
                }

                // If the error occurred during package processing, no error will
                // be set in validationResult, but an invalid import will be reported
                validationResults.setRedirectView("/import/dsp_invalidImport.jsp");
            }

        }
        catch (Exception e) {
            Log.writeLog(e);
        }

        return validationResults;
    }
}
