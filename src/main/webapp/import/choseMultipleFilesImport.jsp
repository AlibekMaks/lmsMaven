
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
   ** Filename:  importCourse.jsp
   **
   ** File Description:  This file allows the user to enter a name for a new
   **                    course, and select the zip
   **                    file that contains the manifest and course content.
   **
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

<html>
<head>
   <title>SCORM 2004 3rd Edition Sample Run-Time Environment Version 1.0.2 - 
    Multiple Course Import</title>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

   <link href="../includes/sampleRTE_style.css" rel="stylesheet" type="text/css">

   <script language="JavaScript">

   /****************************************************************************
   **
   ** Function:  checkValues()
   ** Input:   none
   ** Output:  boolean
   **
   ** Description:  This function ensures that there are values in each text
   **               box before submitting
   **
   ***************************************************************************/
   function checkValues()
   { 
      importFolder.theFolder.value = importFolder.importfolder.value;
      return true;
   }

   /****************************************************************************
   **
   ** Function:  newWindow()
   ** Input:   pageName
   ** Output:  none
   **
   ** Description:  This function opens the help window
   **
   ***************************************************************************/
   function newWindow( pageName )
   {
      window.open(pageName, 'Help',
      "toolbar=no,location=no,directories=no,status=no,menubar=no," +
      "scrollbars=no,resizable=yes,width=500,height=500");
   }

   </script>
</head>

<body bgcolor="#FFFFFF">

<p><a href="../runtime/LMSMenu.jsp">Go Back To Main Menu</a></p>



<form method="post" action="/adl/LMSCourseAdmin"
      name="importFolder" onSubmit="return checkValues()">
    <input type=hidden name="theFolder">
    <input type=hidden name="type" value="29">
    <input type=hidden name="sessionId" value="<%=id%>" >
   <p class="font_header">
   <b>
      Multiple Course Import
   </b>
   </p>


   <table width="450" border="0" align="left">
      <tr>
         <td COLSPAN="2">
            <hr>
         </td>
      </tr>
      <tr>
         <td bgcolor="#5E60BD" colspan="2" class="white_text">
            <b>
               &nbsp;Please provide the following course information:
            </b>
         </td>
      </tr>
      <tr>
         <td width="51%">&nbsp;</td>
      </tr>
      <tr>
         <td>
            Enter the path of the folder containing the course
            content that you wish to import:
         </td>
      </tr>
      <tr>
         <td width="49%">
            <label for="importfolder">Import Folder:&nbsp;</label>
            <input id="importfolder" name="importfolder" type=text>
         </td>
      </tr>
      <tr>
         <td width="51%">&nbsp;</td>
      </tr>
      <tr>
         <td>
            Please choose whether or not you would like to validate the courses.
            <br>
            <b>Choosing to validate a package during import in no way
               implies SCORM Conformance.  Packages must be tested in the
               latest SCORM Conformance Test Suite to ensure complete
               SCORM Conformance.</b>
         </td>
      </tr>
      <tr>
         <td width="49%">
            <input id="validateYES" name="validate" type=radio value="1" checked> <label for="validateYES">YES</label>
         </td>
      </tr>
      </tr>
        <td width="49%">
            <input id="validateNO" name="validate" type=radio value="0"> <label FOR="validateNO">NO</label>
        </td>
      </tr>
      <tr>
         <td colspan="2">
            <hr>
         </td>
      </tr>
      <tr>
         <td width="51%">&nbsp;</td>
      </tr>
      <tr>
         <td width="100%" colspan="2" align="center">
            <input type="submit" name="Submit" value="Submit">
         </td>
      </tr>
      <tr>
         <td>
            <br>
            <a href="javascript:newWindow('../help/multipleImportHelp.htm');">Help!</a>
         </td>
      </tr>
   </table>

   
</form>

</body>
</html>

<%
   }
   else
   {
      // Redirect the user to the LMSMenu page.
      response.sendRedirect( "../runtime/LMSMain.htm" );
   }
%>
