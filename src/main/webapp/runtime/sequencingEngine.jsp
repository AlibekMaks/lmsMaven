<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@page import = "java.sql.*,java.util.*,java.io.*, org.adl.util.*, 
  org.adl.sequencer.*, org.adl.samplerte.util.*, arta.common.Constants"%>
<%@ page import="javax.swing.*" %>
<%@ page import="arta.common.logic.sql.ConnectionPool" %>
<%@ page import="arta.scorm.SCORMPages" %>
<%@ page import="arta.common.logic.messages.MessageManager" %>
<%@ page import="arta.common.logic.util.*" %>
<%@ page import="arta.login.logic.Access" %>
<%@ page import="arta.common.SCORMMessages" %>

<%@ include file="sequencingUtil.jsp" %>

<%
   /***************************************************************************
   **
   ** Filename:  sequencingEngine.jsp
   **
   ** File Description:   This file determines which item should be launched in
   **                     the current course.  It responds to the following
   **                     events Next - Launch the next sco or asset
   **                     Previous - Launch the previous sco or asset
   **                     Menu - Launch the selected item
   **
   ** Author: ADL Technical Team
   **
   ** Contract Number:
   ** Company Name: CTC
   **
   ** Module/Package Name:
   ** Module/Package Description:
   **
   ** Design Issues: This is a proprietary solution for a sequencing engine.  
   **                This version will most likely be replaced when the SCORM
   **                adopts the current draft sequencing specification.
   **
   ** Implementation Issues:
   ** Known Problems:
   ** Side Effects:
   ** 
   ** References: ADL SCORM
   **
   /***************************************************************************
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
   ***************************************************************************/
%>

<%

    int lang = Access.hasLanguage(session);
    //  Booleans for a completed course and request type
    boolean courseComplete = true;
    boolean wasAMenuRequest = false;
    boolean wasANextRequest = false;
    boolean wasAPrevRequest = false;
    boolean wasFirstSession = false;
    boolean wasANullRequest = false;
    boolean wasAnExitRequest = false;
    boolean wasAnExitAllRequest = false;
    boolean wasAnAbandonRequest = false;
    boolean wasAnAbandonAllRequest = false;
    boolean empty_block = false;
    boolean wasASuspendAllRequest = false;
    boolean wasSuspended = false;
    boolean endSession = false;

    // variable used as flag for clearing the log string
    // on launch of a new course
    boolean newCourse = false;

    // Create sequencer, launch, UIState, Activity Tree
    // and nav event objects
    ADLSequencer msequencer = new ADLSequencer();
    ADLLaunch mlaunch = new ADLLaunch();
    SeqNavRequests mnavRequest = new SeqNavRequests();
    ADLValidRequests mValidRequests = new ADLValidRequests();
    SeqActivityTree mactivityTree = null;
    SeqActivity mactivity = new SeqActivity();

    // The type of controls shown
    boolean isNextAvailable = false;
    boolean isPrevAvailable = false;
    boolean isSuspendAvailable = false;
    boolean isQuitAvailable = false;
    boolean isTOCAvailable = false;
    boolean isCourseAvailable = true;
    boolean displayQuit = true;

    // Lists used during menu construction
    Vector TOCState = new Vector();

    // The next item that will be launched
    String nextItemToLaunch = new String();

    // The type of button request if its a button request
    String buttonType = "";

    // The courseID and course title are passed as parameters on initial
    // launch of a course

    String courseID = request.getParameter("courseID");
    String courseTitle = request.getParameter("courseTitle");

    String viewTOC = "false";

    if (request.getParameter("viewTOC") != null) {
        viewTOC = request.getParameter("viewTOC");
    }

    //  Get the requested sco if its a menu request
    //  Encode to UTF-8 to allow for correct sequencing of
    //  non-Latin characters
    request.setCharacterEncoding("UTF-8");
    String requestedSCO = request.getParameter("scoID");

    //  Get the button that was pushed if its a button request
    buttonType = request.getParameter("button");

    // if first time for a course, set the course title session variable
    if ((!(courseTitle == null)) && (!courseTitle.equals(""))) {
        session.setAttribute("COURSETITLE", courseTitle);
        newCourse = true;
    }

    String mIsExit = (String) session.getAttribute("EXIT");
    if ((mIsExit == null) || mIsExit.equals("false")) {
        mIsExit = "false";
    }

    session.setAttribute("EXIT", "false");
    // Set boolean for the type of navigation request
    if ((!(requestedSCO == null)) && (!requestedSCO.equals(""))) {
        wasAMenuRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("exitAll"))) {
        wasAnExitAllRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("suspendAll"))) {
        wasASuspendAllRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("abandon"))) {
        wasAnAbandonRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("abandonAll"))) {
        wasAnAbandonAllRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("prev"))) {
        wasAPrevRequest = true;
    } else if (mIsExit.equals("true")) {
        wasAnExitRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("next"))) {
        wasANextRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("nul"))) {
        wasANullRequest = true;
    } else if ((!(buttonType == null)) && (buttonType.equals("exit"))) {
        wasAnExitRequest = true;
    } else {
        // First launch of the course in this session.
        wasFirstSession = true;
    }

    //  If the course has not been launched
    if (courseID != null) {
        //  set the course ID
        session.setAttribute("COURSEID", courseID);
    } else {
        // Not the initial launch of course, use session data
        courseID = (String) session.getAttribute("COURSEID");
    }

    //  Get the user's id
    String userID = (String) session.getAttribute("userid");
    String exitFlag = (String) session.getAttribute("EXITFLAG");

    Connection con = null;
    Statement st = null;
    ResultSet res = null;

    try {

        con = new ConnectionPool().getConnection();
        st = con.createStatement();

        String specialState = "";

        if ((userID == null) || (courseID == null)) {
            specialState = SCORMPages.getPageURL(SCORMPages.LAUNCH_ENDSESSION);
            endSession = true;
        } else {
            //  Get the users record of the course items

//-------------------------------------------------------------------------------------------------------------

            FileInputStream in =
                    new FileInputStream
                            (Constants.getUsersInfoDirectory() + File.separator + userID +
                                    File.separator + courseID + File.separator + "serialize.obj");

            System.out.println(Constants.getUsersInfoDirectory() + File.separator + userID +
                    File.separator + courseID + File.separator + "serialize.obj");

            ObjectInputStream i = new ObjectInputStream(in);
            mactivityTree = (SeqActivityTree) i.readObject();
            i.close();
            in.close();

            // Set the student id in the activity tree if it has not been set yet
            String studentID = new String();
            studentID = mactivityTree.getLearnerID();
            if (studentID == null) {
                mactivityTree.setLearnerID(userID);
            }
            mactivity = mactivityTree.getSuspendAll();
            if (mactivity != null) {
                wasSuspended = true;
                wasFirstSession = false;
            }

            // Set the Activity Tree
            msequencer.setActivityTree(mactivityTree);

//-------------------------------------------------------------------------------------------------------------

            // Initialize variables that help with sequencing
            String scoID = new String();
            String lessonStatus = new String();
            boolean filePersisted = false;

            //  If the user selected a menu option, handle appropriately
            if (wasAMenuRequest) {
                mlaunch = msequencer.navigate(requestedSCO);
            } else if (viewTOC.equals("true")) {
                mlaunch = msequencer.navigate(SeqNavRequests.NAV_NONE);
            } else {
                // It was a next request, previous request, or first launch of
                // session (or auto) or resume

                //  If its first session
                if (wasFirstSession) {
                    mlaunch = msequencer.navigate(mnavRequest.NAV_START);

                }  //  Ends if it was the first time in for the session
                else if (wasSuspended)// Its a resume request
                {
                    mlaunch = msequencer.navigate(mnavRequest.NAV_RESUMEALL);

                    st.execute("UPDATE usercourseinfo SET SuspendAll = false " +
                            " WHERE CourseID = " + courseID);


                }  //  Ends if its a resume request
                else if (wasANextRequest)// Its a next request
                {
                    mlaunch = msequencer.navigate(mnavRequest.NAV_CONTINUE);

                }  //  Ends if its a next request
                else if (wasAPrevRequest)// Its a previous request
                {
                    // Handle the previous request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_PREVIOUS);
                }//end previous
                else if (wasAnExitRequest)// Its an exit request
                {
                    // Handle an exit request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_EXIT);
                }//end exit

                else if (wasAnExitAllRequest)// Its an exitAll request
                {
                    // Handle an exitAll request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_EXITALL);


                }//end exitAll

                else if (wasASuspendAllRequest)// Its a suspendAll request
                {
                    // Handle an exitAll request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_SUSPENDALL);

                    st.execute("UPDATE usercourseinfo " +
                            " SET SuspendAll = true " +
                            " WHERE UserID = '" + userID + "' AND " +
                            " CourseID = " + courseID);

                }//end suspendAll

                else if (wasAnAbandonRequest)// Its an abandon request
                {
                    // Handle an abandon request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_ABANDON);
                }//end abandon

                else if (wasAnAbandonAllRequest)// Its an abandonAll request
                {
                    // Handle an abandon request
                    mlaunch = msequencer.navigate(mnavRequest.NAV_ABANDONALL);
                }//end abandonAll
            }

            // Set the session variables returned by the sequencer
            // that are used by the RTE during launch and execution
            session.setAttribute("SCOID", mlaunch.mStateID);

            Long longObj = new Long(mlaunch.mNumAttempt);
            session.setAttribute("NUMATTEMPTS", longObj.toString());

            session.setAttribute("ACTIVITYID", mlaunch.mActivityID);

            // If its an END_SESSION, clear the active activity
            if ((mlaunch.mSeqNonContent != null) &&
                    ((mlaunch.mSeqNonContent).equals("_ENDSESSION_") ||
                            (mlaunch.mSeqNonContent).equals("_COURSECOMPLETE_") ||
                            (mlaunch.mSeqNonContent).equals("_SEQABANDONALL_"))) {
                msequencer.clearSeqState();
                endSession = true;

            }
            // Save the activity tree
            filePersisted = persistActivityTree(msequencer.getActivityTree(),
                    userID, courseID);

            // Get the RTE's User Interface state
            mValidRequests = mlaunch.mNavState;
            if (mValidRequests == null) {
                isNextAvailable = false;
                isPrevAvailable = false;
                TOCState = null;
                isSuspendAvailable = false;
                isQuitAvailable = false;
            } else {
                if (mValidRequests.mContinue && mValidRequests.mContinueExit) {
%>
        <script language="JAVASCRIPT">
                 alert("continue and continueExit are both true");
        </script>
        
        <%
                    }
                    if (mValidRequests.mContinueExit) {
                        session.setAttribute("EXIT", "true");


                    } else {
                        session.setAttribute("EXIT", "false");


                    }
                    if (mValidRequests.mContinue || mValidRequests.mContinueExit) {
                        isNextAvailable = true;
                    }
                    isPrevAvailable = mValidRequests.mPrevious;
                    TOCState = mValidRequests.mTOC;
                    isSuspendAvailable = mValidRequests.mSuspend;
                }

                // Look for a special state and redirect if appropriate
                if (mlaunch.mSeqNonContent != null) {
                    specialState = getSpecialState(mlaunch.mSeqNonContent);
                    isSuspendAvailable = false;
                    if (specialState.equals(SCORMPages.getPageURL(SCORMPages.LAUNCH_ENDSESSION))) {
                        isQuitAvailable = false;
                        displayQuit = false;
                    }
                }

                StringTransform trsf = new StringTransform();

                res = st.executeQuery("SELECT " +
                        " launch, " +
                        " next, " +
                        " previous, " +
                        " isexit, " +
                        " exitall, " +
                        " suspend " +
                        " FROM iteminfo WHERE CourseID = '" + courseID + "' " +
                        " AND ItemIdentifier = '" + trsf.getDBString(mlaunch.mActivityID) + "'");


                String itemID = mlaunch.mActivityID;
                boolean matched = false;

                if (res.next()) {

                    matched = true;
                    nextItemToLaunch = res.getString("launch");
                    isNextAvailable = !res.getBoolean("next");
                    isPrevAvailable = !res.getBoolean("previous");
                    displayQuit = !(res.getBoolean("isexit") || res.getBoolean("exitall"));
                    isSuspendAvailable = !res.getBoolean("suspend");

                }
                //File testFile = new File(nextItemToLaunch);
                if ((!matched) || (nextItemToLaunch == null) ||
                        (nextItemToLaunch.equals(""))) {
                    nextItemToLaunch = SCORMPages.getPageURL(SCORMPages.ERROR_PAGE);
                }

                // set up the table of contents information if choice = true
                if (TOCState != null) {
                    isTOCAvailable = true;
                    session.setAttribute("TOC", "true");
                } else {
                    session.setAttribute("TOC", "false");
                }
            }

            // Clearing Objectives
            if (endSession) {
                SeqActivity rootActivity = new SeqActivity();
                rootActivity = msequencer.getRoot();
                if ((mactivityTree.getScopeID() != null) && (!rootActivity.getIsSuspended())) {
                    Vector objectives = mactivityTree.getGlobalObjectives();
                    if (objectives != null) {
                        ADLSeqUtilities.clearGlobalObjs(mactivityTree.getLearnerID(),
                                mactivityTree.getScopeID(), objectives);
                    }
                }
            }

            //  If the course is complete redirect to the course
            //  complete page
            if (specialState.equals(SCORMPages.getPageURL(SCORMPages.LAUNCH_ENDSESSION))) {
                session.removeAttribute("COURSEID");
                session.removeAttribute("TOC");
                session.removeAttribute("COURSETITLE");


            }
            if (specialState.equals(SCORMPages.getPageURL(SCORMPages.LAUNCH_COURSECOMPLETE))) {
                session.removeAttribute("COURSEID");
                session.removeAttribute("TOC");
                session.removeAttribute("COURSETITLE");
                response.sendRedirect(SCORMPages.getPageURL(SCORMPages.LAUNCH_COURSECOMPLETE));
            } else {
                // Build the client side controls and redirect
        %>

<!-- ****************************************************************
**   Build the html 'please wait' page that sets the client side 
**   variables and refreshes to the appropriate course page
*******************************************************************-->  
<html>
   <head>
   <title>SCORM 2004 3rd Edition Sample Run-Time Environment Version 1.0.2 - 
       Sequencing Engine</title>
   <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
       <meta http-equiv="Cach-Control" content="no-cache, must-revalidate">
       <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
       <meta http-equiv="Expires" content="Mon, 23 May 1995 02:00:00 GMT">
   <!-- **********************************************************
   **   
   **  This value is determined by the JSP database queries
   **  that are located above in this file.  The queries are keyed
   **  by the ADLLaunch object returned by the ADLSequencer object
   **
   **  Refresh the html page to the next item to launch  
   **
   ***************************************************************-->
<% 
   if ( ( mlaunch.mSeqNonContent != null) || ( endSession ) )
   {    
           nextItemToLaunch = specialState;
%>


<%
   }

%> 
   
      <script language="JavaScript" src="APIWrapper.js"></script>
      <script language="JAVASCRIPT">

         var TOCctrl = <%= isTOCAvailable %>;
         var nextCtrl = <%= isNextAvailable %>;
         var prevCtrl = <%= isPrevAvailable %>;
         var isCourse = <%= isCourseAvailable %>;
         var showQuit = <%= displayQuit %>;
         var suspendCtrl = <%= isSuspendAvailable %>;
         var clearLog = <%= newCourse %>;

         function initLMSFrame()
         {       
            // Set the type of control for the course in the LMS Frame 
            if ( window.opener == null )
            {
               window.top.frames['LMSFrame'].document
                   .forms['buttonform'].control.value 
			   		= <%= isTOCAvailable %>;
               window.top.frames['LMSFrame'].document
                   .forms['buttonform'].isNextAvailable.value 
			   		= <%= isNextAvailable %>;
               window.top.frames['LMSFrame'].document
                   .forms['buttonform'].isPrevAvailable.value
			  		= <%= isPrevAvailable %>;
               window.top.frames['LMSFrame'].document
                   .forms['buttonform'].isTOCAvailable.value
			   		= <%= isTOCAvailable %>;
               window.top.frames['LMSFrame'].document
                   .forms['buttonform'].isSuspendAvailable.value
			   		= <%= isSuspendAvailable %>;
             }
            // find the API and set the user, course, state and activity ids
             //alert("try to init API");
             try{
                 //alert("window.API_1484_11 " + window.API_1484_11);

                initAPI();
                window.API_1484_11 = API;
                 //alert("window.API_1484_11 " + window.API_1484_11);
                 } catch (e){
                 alert(e.message);
             }


                 if ( nextCtrl ) {
                    window.top.frames['LMSFrame'].document.forms['buttonform'].next.style.visibility = "visible";
                    window.top.frames['LMSFrame'].document.forms['buttonform'].next.disabled = false;
                 } else {
                    window.top.frames['LMSFrame'].document.forms['buttonform'].next.style.visibility = "hidden";
                 }
                 if ( prevCtrl ) {
                    window.top.frames['LMSFrame'].document.forms['buttonform'].previous.style.visibility =
                       "visible";
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].previous.disabled = false;
                 }
                 else
                 {
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].previous.style.visibility =
                       "hidden";
                 }

                 if ( suspendCtrl )
                 {
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].suspend.style.visibility =
                       "visible";
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].suspend.disabled = false;
                 }
                 else
                 {
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].suspend.style.visibility =
                       "hidden";
                 }



                 if ( ( isCourse ) && (showQuit) )
                 {
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].quit.style.visibility =
                       "visible";
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].quit.disabled = false;
                 }

                 if ( ! showQuit )
                 {
                    window.top.frames['LMSFrame'].document
                        .forms['buttonform'].quit.style.visibility =
                       "hidden";
                 }
                 if ( clearLog )
                 {
                    window.top.frames['LMSFrame'].reset_log_string();
                 }
                window.parent.frames['code'].document.location.href = "code.jsp?nocache=" + Math.round(Math.random()*100000000);                
                document.location.href="<%=nextItemToLaunch%>";
         }
      </script>
      <script language="javascript">








      </script>
   </head>
         
   <body bgcolor="#FFFFFF" onload="initLMSFrame();">    

    
      <p><font size="4">
         <%=MessageManager.getMessage(lang, SCORMMessages.PLEASE_WAIT)%>
      </font></p>
      
      <form>
         <input type="hidden" name="courseID" 
          value="<%= (String)session.getAttribute( "COURSEID" ) %>" />
         <input type="hidden" name="stateID" 
          value="<%= (String)session.getAttribute( "SCOID" ) %>" />
         <input type="hidden" name="activityID" 
          value="<%= (String)session.getAttribute( "ACTIVITYID" ) %>" />
         <input type="hidden" name="userID" 
          value="<%= (String)session.getAttribute( "USERID" ) %>" />
         <input type="hidden" name="numAttempts" 
          value="<%= (String)session.getAttribute( "NUMATTEMPTS" ) %>" />
         <input type="hidden" name="userName" 
          value="<%= (String)session.getAttribute( "USERNAME" ) %>" />
      </form>             
            
   </body>
</html> 

<%
        }

    } catch (Exception e) {
        Log.writeLog(e);
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
%>

