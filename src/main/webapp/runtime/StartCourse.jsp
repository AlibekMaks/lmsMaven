<%@ page import="arta.common.logic.server.Server" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><html>
<!--
/*******************************************************************************
**
** Filename:  LMSMain.htm
**
** File Description:    This is the main page that contains a frameset which
**                      in turn contains three frames.  The top frame contains
**                      login, logout, Quit, Previous, and Next buttons. The
**                      lower left frame contains a menu constructed from the
**                      items in the currently selected course.  The content
**                      frame (right-side) initially contains the start page.
**                      When the user logs in and selects a course, lesson,
**                      etc., the right-side frame displays this content.
**                      This page exposes the DOM API Object so that a
**                      launched SCO is able to find it.  This page will
**                      be the parent, or the parent of the opener window, of
**                      launched SCOs.
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
-->
<head>
<meta http-equiv="expires" content="Tue, 20 Aug 1999 01:00:00 GMT">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Cach-Control" content="no-cache, must-revalidate">
<title>Testing System</title>
<script language ="JAVASCRIPT" >

var API_1484_11 = null;

/****************************************************************************
**
** Function:  initAPI()
** Input:   none
** Output:  none
**
** Description:  This function sets an "API" variable equal to the API
**               Applet.
**
***************************************************************************/


function initAPI(){
        
    try{
        API_1484_11 = null;

        try{
            API_1484_11 = window.LMSFrame.document.APIAdapter;

        } catch (e1){
            alert(e1);
        }



        link = "<%=Server.MAIN_URL%>scormmsg?startcontent=true&courseID=<%=request.getParameter("courseID")%>&nocache=<%=Math.round(
             Math.random()*100000000)%>";
        frames.Content.location.href = link;
    }catch(e){
    //    alert("asd");
        alert(e);
    }
}

</script>
 <frameset rows="99px,*" onload="initAPI();" >
        <frame id="LMSFrame" name="LMSFrame"  title="Applet Frame"
         src="LMSFrame1.jsp"  marginwidth=0 marginheight=0 scrolling="no" frameborder=0 noresize />
        <frameset cols="275,*">
            <frameset rows="100%, 0%">               
               <frame src="menu_empty.htm" name="menu"
                    title="Navigation Menu Frame"  marginwidth=0 marginheight=0 scrolling="yes" frameborder=0/>
                <frame id="code" src="code.jsp" name="code"
                    title="code.jsp Frame"  marginwidth=0 marginheight=0 scrolling="no" frameborder=0 noresize/>
            </frameset>
            <frame id="Content" name="Content" title="Content Frame"
             src="menu_empty.htm"  marginwidth=0 marginheight=0 scrolling="yes" frameborder=0/>
        </frameset>
</frameset>

</head>

<body>

</body>
</html>
