<%@ page import="org.adl.util.EnvironmentVariable,org.adl.util.debug.LogConfig,java.io.*,java.util.logging.*" %>
<%@ page import="arta.common.Constants" %>
<%@ page import="arta.common.logic.server.Server" %>
<%@ page import="arta.common.logic.util.DataExtractor" %>
<%@ page import="arta.common.logic.messages.MessageManager" %>
<%@ page import="arta.common.SCORMMessages" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    /***************************************************************************
     **
     ** Filename: LMSFrame.jsp
     **
     ** File Description: This page contains the API Adapter applet.  The API
     **                   Adapter applet has no visual display elements and is
     **                   therefore invisible to the user.  Note that the API
     **                   Adapter object is exposed to SCOs via the LMSMain.htm
     **                   page.  The SCOs communicate with the Run-time
     **                   Environment through this API.  This page also contains
     **                   the Run-time Environment login button and
     **                   the button for Next, Previous, Suspend, and Quit.
     ** Author: ADL Technical Team
     **
     ** Contract Number:
     ** Company Name: CTC
     **
     ** Module/Package Name:
     ** Module/Package Description:
     **
     ** Design Issues:
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
    Logger mLogger = Logger.getLogger("org.adl.util.debug.samplerte");
    try {

        String adlHome = getServletConfig().getServletContext().getRealPath("/");
        adlHome = adlHome.substring(0, adlHome.lastIndexOf(File.separator));

        LogConfig logConfig = new LogConfig();
        logConfig.configure(adlHome, true);

        mLogger.entering("---LMSFrame", "try()");
    } catch (Exception e) {
        mLogger.severe("---Caught exception " + e);
    }
    int lang = new DataExtractor().getInteger(session.getAttribute("lang"));
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="expires" content="Tue, 20 Aug 1999 01:00:00 GMT">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cach-Control" content="no-cache, must-revalidate">
<title>Testing System</title>
<link href="../includes/sampleRTE_style.css" rel="stylesheet" type="text/css">
</head>
<script language=javascript>
// variable to contain string of API and Datamodel calls made by current SCO
var mlog = "API and Datamodel Calls";
var showMessages = false;

/****************************************************************************
 **
 ** Function: LMSIsInitialized()
 ** Input:   none
 ** Output:  boolean
 **
 ** Description:  This function returns a boolean that represents whether or
 **               not LMSInitialize() has been called by the SCO.
 **
 ***************************************************************************/

function LMSIsInitialized() {

    // Determines if the API (LMS) is in an initialized state.
    // There is no direct method for determining if the LMS API is initialized
    // for example an LMSIsInitialized function defined on the API so we'll try
    // a simple LMSGetValue and trap for the LMS Not Initialized Error
    var errCode = API.LMSGetLastError().toString();
    if (errCode == 301) {
        return false;
    } else {
        return true;
    }
}

/****************************************************************************
 **
 ** Function: refreshMenu()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called by the API after an LMSCommit.  It
 **               causes the menu page to load the latest UI state and update
 **               itself.
 **
 ***************************************************************************/

function refreshMenu() {
    window.parent.frames['code'].document.location.href = "/LMS2/runtime/code.jsp";
}

/****************************************************************************
 **
 ** Function: setUIState()
 ** Input:   boolean - state
 ** Output:  none
 **
 ** Description:  This function is called twice during an LMSCommit.  It
 **               disables the navigation buttons while the commit is active
 **               and re-enables the buttons when the commit is finished.
 **
 ***************************************************************************/
function setUIState(state) {
    if (showMessages) {
        alert("setUIState(" + state + ") function has been called.");
    }
    if (! state) {
        document.buttonform.quit.disabled = true;
        document.buttonform.previous.disabled = true;
        document.buttonform.next.disabled = true;
        document.buttonform.suspend.disabled = true;
    } else {
        document.buttonform.quit.disabled = false;
        document.buttonform.previous.disabled = false;
        document.buttonform.next.disabled = false;
        document.buttonform.suspend.disabled = false;
    }
}
/****************************************************************************
 **
 ** Function: nextSCO()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when the user clicks the "next"
 **               button.  The Sequencing Engine is called, and all relevant
 **               controls are affected.
 **
 ***************************************************************************/
function nextSCO()
{
    // Disable the button controls
    if (showMessages) {
        alert("function next SCO has been called!");
    }

    document.forms['buttonform'].next.disabled = true;
    document.forms['buttonform'].previous.disabled = true;
    document.forms['buttonform'].quit.disabled = true;
    document.forms['buttonform'].suspend.disabled = true;

    // This is the launch line for the next SCO...
    // The Sequencing Engine determines which to launch and
    // serves it up into the LMS's content frame or child window - depending
    //on the method that was used to launch the content in the first place.

    var scoWinType = typeof(window.parent.frames['Content'].scoWindow);
    var theURL = "pleaseWait.jsp?button=next&nocache=" + Math.round((Math.random() * 10000000));

    if (showMessages) {
        alert(theURL);
    }

    if (scoWinType != "undefined" && scoWinType != "unknown") {
        if (window.parent.frames['Content'].scoWindow != null) {
            // there is a child content window so display the sco there.
            window.parent.frames['Content'].scoWindow.document.location.href = theURL;
            if (showMessages)
                alert("window.parent.frames['Content'].scoWindow.document.location.href = " + theURL);
        } else {
            window.parent.frames['Content'].document.location.href = theURL;
            if (showMessages)
                alert("window.parent.frames['Content'].document.location.href = " + theURL);
        }
    } else {
        window.parent.frames['Content'].document.location.href = theURL;
        if (showMessages)
            alert("window.parent.frames['Content'].document.location.href = " + theURL);
    }

    if (showMessages) {
        alert("function nextSCO() has been completed!");
    }
}

/****************************************************************************
 **
 ** Function: display_log(String)
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function writes information from API and datamodel calls to
 ** a logging window.  The window is scrolled to the bottom to allow the most
 ** recent log entries to be visible.
 **
 ***************************************************************************/
function display_log(call_string)
{
    mlog += "<br>";
    mlog += call_string;
    //top.frames['log'].document.getElementById('log_span').innerHTML = mlog;

    //top.frames['log'].location.hash = '#bottom';
}
/****************************************************************************
 **
 ** Function: reset_log_string()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function clears the logging information from API
 ** and datamodel calls when a new course is launched.
 **
 **
 **
 ***************************************************************************/
function reset_log_string()
{
    mlog = "API and Datamodel Calls";
    //top.frames['log'].document.getElementById('log_span').innerHTML = mlog;
}
/****************************************************************************
 **
 ** Function: reset_logging()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function clears the logging information from API
 ** and datamodel calls when a new course is launched.
 **
 **
 **
 ***************************************************************************/
function reset_logging()
{
    mlog = "API and Datamodel Calls";
    //top.frames['log'].document.getElementById('log_span').innerHTML = mlog;
    API_1484_11 = this.document.APIAdapter;
    API_1484_11.resetLoggingVariable();
}

/****************************************************************************
 **
 ** Function: doChoiceEvent( navEvent)
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when Terminate has been called by
 ** the SCO after a choice navEvent has been set.
 **
 **
 ***************************************************************************/
function doChoiceEvent(choiceEvent) {
    alert("doChoiceEvent( " + choiceEvent + ")");
    window.top.frames['Content'].location.href = "pleaseWait.jsp?scoID=" + choiceEvent;
}

/****************************************************************************
 **
 ** Function: invokeSuspendAll()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when the learner presses the RTE
 **				 provided Suspend button.
 **
 **
 ***************************************************************************/
function invokeSuspendAll()
{
    API_1484_11 = this.document.APIAdapter;
    API_1484_11.suspendButtonPushed();
    doNavEvent('suspendAll');
    window.top.close();
}

/****************************************************************************
 **
 ** Function: invokeQuit()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when the learner presses the RTE
 **				 provided Quit button.
 **
 **
 ***************************************************************************/
function invokeQuit()
{
    API_1484_11 = this.document.APIAdapter;
    API_1484_11.quitButtonPushed();
    doNavEvent('exitAll');
    window.top.close();
}

/****************************************************************************
 **
 ** Function: doNavEvent( navEvent)
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when an LMSFinish has been called by
 ** the SCO after a navEvent has been set.
 **
 **
 ***************************************************************************/

function doNavEvent(navEvent) {
    try {
        // Disable the button controls

        document.forms['buttonform'].next.disabled = true;
        document.forms['buttonform'].previous.disabled = true;
        document.forms['buttonform'].quit.disabled = true;
        document.forms['buttonform'].suspend.disabled = true;

        // This is the launch line for the next SCO...
        // The Sequencing Engine determines which to launch and
        // serves it up into the LMS's content frame or child window - depending
        //on the method that was used to launch the content in the first place.
        var scoWinType = typeof(window.parent.frames['Content'].scoWindow);

        if (navEvent == "continue") {
            navEvent = "next";
        }
        if (navEvent == "previous") {
            navEvent = "prev";
        }

        var theURL = "sequencingEngine.jsp?button=" + navEvent + "&" + Math.round(Math.random() * 1000000);

        if (showMessages) {
            alert("doNavEvent(); has been called.\r\n scoWinType = " + scoWinType + "\r\n" +
                  " naveEvent is " + navEvent + " \r\n " +
                  " the future url is " + theURL);
            alert("Content frame is " + window.parent.frames['Content'].document);
        }


        if (scoWinType != "undefined" && scoWinType != "unknown") {
            if (window.parent.frames['Content'].scoWindow != null) {
                // there is a child content window so display the sco there.
                window.parent.frames['Content'].scoWindow.document.location.href = theURL;

            } else {

                window.parent.frames['Content'].document.location.href = theURL;
            }
        } else {
            if (window.navigator.appName.indexOf("Netscape") >= 0) {
                window.parent.frames['Content'].document.location =
                '<%=Constants.getSCORMRuntimeFolder(session.getAttribute(Constants.HOST) + "",
                                                                session.getAttribute(Constants.WAR) + "")%>' + theURL;
            } else {
                window.parent.frames['Content'].document.location.href = theURL;
            }
        }

        /* if ( document.layers != null ) {
         alert("swap layers has been called");
         swapLayers();
     } else if ( document.all != null ) {
          window.top.frames[0].document.forms[0].next.disabled = true;
          window.top.frames[0].document.forms[0].previous.disabled = true;
     } else {
         //Neither IE nor Netscape is being used
         alert("(doNavEvent "+theURL+" ) your browser may not be supported");
         alert(window.top.frames["Content"].document);
         window.top.frames["Content"].document.location.href = theURL;
     }   */
    } catch(e) {
        alert(e.message);
    }
}


/****************************************************************************
 **
 ** Function: previousSCO()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is called when the user clicks the "previous"
 **               button.  The Sequencing Engine is called, and all relevant
 **               controls are affected.
 **
 ***************************************************************************/
function previousSCO() {

    // This function is called when the "Previous" button is clicked.
    // The LMSLesson servlet figures out which SCO to launch and
    // serves it up into the LMS's content frame or child window - depending
    //on the method that was used to launch the content in the first place.

    // Disable the button controls
    document.forms['buttonform'].next.disabled = true;
    document.forms['buttonform'].previous.disabled = true;
    document.forms['buttonform'].quit.disabled = true;
    document.forms['buttonform'].suspend.disabled = true;

    var scoWinType = typeof(window.parent.frames['Content'].scoWindow);
    var theURL = "pleaseWait.jsp?button=prev";

    if (scoWinType != "undefined" && scoWinType != "unknown") {

        if (window.parent.frames['Content'].scoWindow != null) {
            // there is a child content window so display the sco there.
            window.parent.frames['Content'].scoWindow.document.location.href = theURL;
        } else {
            window.parent.frames['Content'].document.location.href = theURL;
        }
    } else {
        window.parent.frames['Content'].document.location.href = theURL;

        //  scoWindow is undefined which means that the content frame
        //  does not contain the lesson menu at this time.
    }
    /* if ( document.layers != null )
    {
       swapLayers();
    }
    else if ( document.all != null )
    {
      // window.document.forms[0].next.disabled = true;
      // window.document.forms[0].previous.disabled = true;
    }
    else
    {
      //Neither IE nor Netscape is being used
       alert("(PREVIOUS SCO) your browser may not be supported");
    }*/

}

/****************************************************************************
 **
 ** Function: closeSCOContent()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function exits out of the current lesson and presents
 **               the RTE menu.
 **
 ***************************************************************************/
function closeSCOContent()
{
    var scoWinType = typeof(window.parent.frames['Content'].window);

    ctrl = window.document.forms['buttonform'].control.value;

    if (ctrl == "auto")
    {

        window.top.frames['Content'].location.href = "LMSMenu.jsp"
        window.top.contentWindow.close();
    }
    else
    {

        if (scoWinType != "undefined" && scoWinType != "unknown")
        {
            if (window.parent.frames['Content'].scoWindow != null)
            {
                // there is a child content window so close it.
                window.parent.frames['Content'].scoWindow.close();
                window.parent.frames['Content'].scoWindow = null;
            }
            window.parent.frames['Content'].document.location.href = "LMSMenu.jsp";
        }
        else
        {
            //  scoWindow is undefined which means that the content frame
            //  does not contain the lesson menu so do nothing...
        }
    }
}

/****************************************************************************
 **
 ** Function: swapLayers()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function is used to swap the login and logout buttons
 **
 ***************************************************************************/
function swapLayers()
{
    if (document.loginLayer.visibility == "hide")
    {
        document.logoutLayer.visibility = "hide";
        document.loginLayer.visibility = "show";
    }
    else
    {
        document.loginLayer.visibility = "hide";
        document.logoutLayer.visibility = "show";
    }
}

/****************************************************************************
 **
 ** Function: init()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function sets the API variable and hides the
 **               the navigation buttons
 **
 ***************************************************************************/
function init() {

        API_1484_11 = this.document.APIAdapter;
        window.top.frames['LMSFrame'].document.forms['buttonform'].next.style.visibility = "hidden";
        window.top.frames['LMSFrame'].document.forms['buttonform'].previous.style.visibility = "hidden";
    }

/****************************************************************************
 **
 ** Function: doConfirms()
 ** Input:   none
 ** Output:  none
 **
 ** Description:  This function prompts the user that they may lose
 **               data if they exit the course.  If exit is confirmed,
 **               the sequencing engine is called with "ExitAll".
 **
 ***************************************************************************/
function doConfirm()
{
    if (confirm("If you quit now the course information may not be saved.  Do you wish to quit?"))
    {
        document.forms['buttonform'].next.disabled = true;
        document.forms['buttonform'].previous.disabled = true;
        document.forms['buttonform'].quit.disabled = true;
        document.forms['buttonform'].suspend.disabled = true;

        var scoWinType = typeof(window.parent.frames['Content'].scoWindow);
        var theURL = "sequencingEngine.jsp?button=exitAll";

        if (scoWinType != "undefined" && scoWinType != "unknown")
        {
            if (window.parent.frames['Content'].scoWindow != null)
            {
                // there is a child content window so display the sco there.
                window.parent.frames['Content'].scoWindow.document.location.href = theURL;
            }
            else
            {
                window.parent.frames['Content'].document.location.href = theURL;

            }
        }
        else
        {
            window.parent.frames['Content'].document.location.href = theURL;

        }
        if (document.layers != null)
        {
            swapLayers();
        }
        else if (document.all != null)
        {
            // window.top.frames[0].document.forms[0].next.disabled = true;
            // window.top.frames[0].document.forms[0].previous.disabled = true;
        }
        else
        {
            //Neither IE nor Netscape is being used
            alert("doConfirm your browser may not be supported");
        }

    }
    else
    {
    }
}
</script>

<body onload="init();" id="topNav" topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0>

<!--  For MS IE Use the Java 1.4 JRE Plug-in instead of the Browser's JVM
      Netscape 4.x can't use the plug-in because it's liveconnect doesn't
	  work with the Plug-in
-->
<form name="buttonform" id="buttonform">
    <table border=0 width="100%" height="100%" bgcolor="#4aa2d4" cellpadding="0" cellspacing="0"
            background="<%=Server.MAIN_URL%>images/banner1.jpg" style="background-repeat:no-repeat;">
        <tr>
            <td width="838px" style="border-style:none" >
                <div style="visibility:hidden;" height="0" width="0">
                    <applet code="org/adl/samplerte/client/ClientRTS.class"
                            archive="util.jar,cmidatamodel.jar,lmsclient.jar,debug.jar,sequencer.jar,joda-time-1.1.jar,sspserver.jar"
                            codebase="<%=Server.WAR_NAME%>"
                            src="<%=Server.WAR_NAME%>"
                            height="0"
                            id="APIAdapter"
                            name="APIAdapter"
                            width="0"
                            mayscript="true">
                        <param name="URL" value="<%=Server.MAIN_URL%>" >
                    </applet>
                </div>
            </td>
            <td width=* align   ="right" valign="bottom" style="border-style:none">
                <table border=0 >
                    <tr>
                        <td>
                            <input type="button" align="left" value="<%=MessageManager.getMessage(lang, SCORMMessages.GLOSSARY_BUTTON)%>" id="glossary"
                                   name="glossary" language="javascript"
                                   onclick="return nextSco();" style="visibility: hidden" disabled>&nbsp;
                        </td>
                        <td>
                            <input type="button" align="right" value="<%=MessageManager.getMessage(lang, SCORMMessages.PREVIOUS_BUTTON)%>" id="previous"
                                   name="previous" language="javascript"
                                   onclick="return previousSCO();" style="visibility: hidden">
                        </td>
                        <td>
                            <input type="button" align="right" value="<%=MessageManager.getMessage(lang, SCORMMessages.NEXT_BUTTON)%>" id="next"
                                   name="next" language="javascript"
                                   onclick="return nextSCO();" style="visibility: hidden">
                        </td>
                        <td>
                            <input type="button" value="<%=MessageManager.getMessage(lang, SCORMMessages.SUSPEND_BUTTON)%>" id="suspend"
                                   name="suspend" style="visibility: hidden"
                                   language="javascript" onclick="return invokeSuspendAll();">
                        </td>
                        <td>
                            <INPUT type="button" ALIGN="right" VALUE="<%=MessageManager.getMessage(lang, SCORMMessages.QUITE_BUTTON)%>"
                                   name="quit" id="quit" language="javascript"
                                   ONCLICK="return invokeQuit();" STYLE="visibility: hidden">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr height="1px" width="100%" bgcolor="#ffffff" style="border-style:none">
            <td valign="top" style="border-style:none" colspan=2>
            </td>
        </tr>
        <tr height="8px" width="100%" bgcolor="#c2c2c2" style="border-style:none">
            <td valign="top" style="border-style:none" colspan=2>
            </td>
        </tr>
    </table>

    <input type="hidden" id="control" name="control" value=""/>
    <input type="hidden" id="isNextAvailable" name="isNextAvailable" value=""/>
    <input type="hidden" id="isPrevAvailable" name="isPrevAvailable" value=""/>
    <input type="hidden" id="isTOCAvailable" name="isTOCAvailable" value=""/>
    <input type="hidden" id="isSuspendAvailable" name="isSuspendAvailable" value=""/>

    <!--/NOLAYER-->
</form>

</body>
</html>
