<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="arta.common.SCORMMessages" %>
<%@ page import="arta.common.logic.messages.MessageManager" %>
<%@ page import="arta.login.logic.Access" %>
<%@ page import="arta.common.logic.util.Rand" %>
<%@ page import="arta.common.html.handler.Parser" %>
<%@ page import="arta.common.html.handler.FileReader" %>
<%@ page import="arta.scorm.html.ScormMessageHandler" %>
<%--
  Created by IntelliJ IDEA.
  User: Topa
  Date: 24.01.2008
  Time: 15:26:11
  To change this template use File | Settings | File Templates.
--%>
    <head>
        <title>Testing System</title>
        <META HTTP-Equiv="Cache-Control" Content="no-cache">
        <META HTTP-Equiv="Pragma" Content="no-cache">
        <META HTTP-Equiv="Expires" Content="Tue, 01 Jan 1980 1:00:00 GMT">
        <META http-equiv=Content-Type content="text/html; charset=utf-8">
        <LINK TYPE="text/css" REL="stylesheet" HREF="css/common.css">
        <LINK TYPE="text/css" REL="stylesheet" HREF="css/common.page.css">
        <script type="text/javascript" src="jscripts/common.js"></script>
         <script language="JavaScript" src="APIWrapper.js"></script>
    </head>

    <script language="JavaScript">
      var API_1484_11 = null;
      function initFrame(){
          initAPI();
          API_1484_11 = API;
          document.location.href="sequencingEngine.jsp?courseID=<%=request.getParameter("courseID")%>";
      }
    </script>

    <body topmargin=0 bottommargin=0 leftmargin=0 rightmargin=0 onload="initFrame();">
    <input type="button" value="Start" onclick='document.location.href="sequencingEngine.jsp?courseID=<%=request.getParameter("courseID")%>"'>
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