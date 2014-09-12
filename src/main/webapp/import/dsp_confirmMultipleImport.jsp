<%
   String is_admin = (String)session.getAttribute( "AdminCheck" );
   String id = session.getId();
   // Condition statement to determine if the Session Variable has been
   // properly set.
   if ( (! (is_admin == null)) && ( is_admin.equals("true")) )
   {
%>

<% 
   /***************************************************************************
   **
   ** Filename:  dsp_confirmMultipleImport.jsp
   **
   ** File Description: This page displays a message saying that the courses  
   **                   have been imported and a link to return to the  
   **                   main menu.
   **
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
<%@page import = "org.adl.logging.*, org.adl.util.*, java.util.*,
                  org.adl.samplerte.server.*"%>
<%@ include file="importUtil.jsp" %>
<%
      String validate = "";
   
      ValidationResults vr = (ValidationResults)request.getAttribute("result");      
      validate = vr.getValidation();
      
      boolean warningExists = false;
      
      String bodyString = new String();
      bodyString = "<p><a href='/adl/runtime/LMSMenu.jsp'>Go Back To Main Menu</a>" +
                   "</p><div class='font_header'>Courses Have Been Imported</div><table>";
   
      if(!(validate == null) && validate.equals("true"))
      {
         DetailedLogMessageCollection MC = DetailedLogMessageCollection.getInstance();
         LogMessage currentMessage;
         int count = MC.getSize();
         for ( int i=0; i < count; i++ )
         {
            currentMessage = MC.getMessage();
            if( currentMessage.getMessageType() == MessageType.WARNING )
            {
               warningExists = true;
            }            
         }
         
         if ( warningExists )
         {
         	bodyString = bodyString + "<tr><td class='orange_text'>" +
                               "A WARNING resulted from the Imported Course(s)<br>"
                               + "Please import them individually for a detailed validation report."                               
                               + "</td></tr>";
         }
      }
%>

<html>
<head>
    <title>SCORM 2004 3rd Edition Sample Run-Time Environment Version 1.0.2 - 
    Import Multiple Courses</title>
    <link href="/adl/includes/sampleRTE_style.css" rel="stylesheet" type="text/css">
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#FFFFFF">
<%=bodyString%>
</table>
</body>
</html>

<%
   }
   else
   {
      // Redirect the user to the LMSMenu page.
      response.sendRedirect( "/adl/runtime/LMSMain.htm" ); 
   }   
%>
