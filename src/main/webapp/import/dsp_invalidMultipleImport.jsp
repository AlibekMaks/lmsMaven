<% 
   String is_admin = (String)session.getAttribute( "AdminCheck" );
   
   // Condition statement to determine if the Session Variable has been
   // properly set.
   if ( (! (is_admin == null)) && ( is_admin.equals("true")) )
   {
      String errorMsg = (String)request.getAttribute("errorMsg");
      String someFailed = (String)request.getAttribute("someFailed");
      Vector validationFailures = (Vector)request.getAttribute("validationFailures");
      Vector coursenames  = (Vector)request.getAttribute("coursenames");



%>
<% 
   /***************************************************************************
   **
   ** Filename:  invalidImport.jsp
   **
   ** File Description: This page displays a message saying that the course  
   **                   intended for import was invalid and gives a  
   **                   summary of the validator results.  It also contains a 
   **                   link to the main menu.
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

<%@page import = "org.adl.util.*, java.util.*,
    org.adl.logging.*, org.adl.samplerte.server.*"%>
<%@ include file="importUtil.jsp" %>
<%
	
  String bodyString = "";
  String filename ="";
  ValidationResults vr = null;
  String validate = "";
  String manifestExists = "";
  String wellFormed = "";
  String validRoot = "";
  String validToSchema = "";
  String validToProfile = "";
  String requiredFiles = "";
  
 
  bodyString = "<p><a href='/adl/runtime/LMSMenu.jsp'>Go Back To Main Menu</a></p><div class=font_header>";

  if ( someFailed.equals("true"))
  {
    bodyString = bodyString + "The following courses failed validation during import,"
        + " and thus were not imported.  Please import them individually for a " +
        "detailed validation report." ;
    bodyString = bodyString + "</div><table><tr>";
      for (int j = 0; j < validationFailures.size(); j++ )
      {

          filename = (String)coursenames.elementAt(j);
          vr = (ValidationResults)validationFailures.elementAt(j);
          
          bodyString = bodyString + "<td class='red_text'><b>Course:" + filename + "</br></td></tr>";
         
          validate = vr.getValidation();
          manifestExists = vr.getManifestExists();
          wellFormed = vr.getWellFormed();
          validRoot = vr.getValidRoot();
          validToSchema = vr.getValidToSchema();
          validToProfile = vr.getValidToProfile();
          requiredFiles = vr.getRequiredFiles();
          
          bodyString = bodyString + "</div><p>&nbsp;</p><tr>" +
                "<td class='red_text'><b>Validation Trouble:</br></td></tr>";
           if ( manifestExists.equals("false") )
           {
              bodyString = bodyString + "<tr><td class='red_text'><b>" +
                           "* imsmanifest.xml file is not located at the root of the " +
                           "package.</b></td></tr>";
           }
           else if ( wellFormed.equals("false") )
           {
              bodyString = bodyString + "<tr><td class='red_text'><b>" +
                           "*  The imsmanifest.xml file is not well-formed." +
                           "</b></td></tr>";
           }
           else if ( validRoot.equals("false") )
           {
              bodyString = bodyString + "<tr><td class='red_text'><b>" +
                           "*  The root element does not belong to the expected " +
                           "namespace.</b></td></tr>";
           }
           else if ( validate.equals("true") )
           {
              if ( requiredFiles.equals("false") )
              {
                 bodyString = bodyString + "<tr><td class='red_text'>*  Control documents are not " +
                              "located at the root of the package.</b></td></tr>";
              }
              if ( validToSchema.equals("false") )
              {
                 bodyString = bodyString + "<tr><td class='red_text'><b>" +
                            "*  The imsmanifest.xml file is not valid against the " +
                            "schemas.</b></td></tr>";
              }
              if ( validToProfile.equals("false") )
              {
                 bodyString = bodyString + "<tr><td class='red_text'><b>" +
                            "*  The imsmanifest.xml file is not valid to the " +
                            "requirements defined in SCORM 2004 3rd Edition.</b></td></tr>";
              }
              // An Error occurred during package processing
              else
   		      {
                 bodyString = bodyString + "<tr><td class='red_text'><b>" +
                    "*  An error occurred during the processing of the package." + 
                    "</b></td></tr>";         	  
           	  }   

              if ( (manifestExists.equals("false") || wellFormed.equals("false") || 
                 validRoot.equals("false")) || 
                   (validate.equals("true") && (requiredFiles.equals("false") || 
                    validToSchema.equals("false") || validToProfile.equals("false"))) )
              {
              
        		DetailedLogMessageCollection dlmc = DetailedLogMessageCollection.getInstance();
                LogMessage currentMessage;
                int collSize = dlmc.getSize();
                for(int i = 0; i < collSize; i++)
                {
        			currentMessage = dlmc.getMessage();
                }
              }                                   
           }     
      }
  
           bodyString = bodyString + "</table>";
      }
      else 
      {   
          bodyString = bodyString +  "<div class=font_header><p>" +errorMsg +
                "</p></div><p>&nbsp;</p>"; 
      }  
%>


<html>
<head>
   <title>SCORM 2004 3rd Edition Sample Run-Time Environment Version 1.0.2 - 
    Invalid Course</title>
   <link href="/adl/includes/sampleRTE_style.css" rel="stylesheet" 
                                                type="text/css">
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
         
   

<%= bodyString %>


</html>

<%
   }
   else
   {
      // Redirect the user to the LMSMenu page.
      response.sendRedirect( "/adl/runtime/LMSMain.htm" ); 
   }   
%>
