<script language="javascript">
    function changeState(subjectID, state){
        if (state){
            document.getElementById("subsel"+subjectID).disabled=false;
            html = '<table border=0 width="100%" class="table">';           

            html += "<tr>";
            html += "<td width=1px>";
            html += "<input  name='sub="+subjectID+"ind=0' id='sub="+subjectID+"ind=0' type='hidden'>";
            html += "</td>";
            html += "<td width=* id='sub"+subjectID+"_ind0'>";
            html += "{[select tutor]}</td>";
            html += "<td width='10px'>";
            html += "<a href='#' onClick='selectTutor(0, "+subjectID+"); return false;' class='href' >";
            html += "...</a></td></tr>";

            html += '</table>';
            document.getElementById("sub"+subjectID+"td").innerHTML = html;
        } else {
            document.getElementById("subsel"+subjectID).disabled=true;    
            document.getElementById("sub"+subjectID+"td").innerHTML = "&nbsp;";
        }
    }
    function set(tdid, count, subjectID){    
        html = "<table border=0 width='100%' class='table'>";
        for (i = 0; i < count; i ++){
            html += "<tr>";
            html += "<td width=1px>";
            html += "<input  name='sub="+subjectID+"ind="+i+"' id='sub="+subjectID+"ind="+i+"' type='hidden'>";
            html += "</td>";
            html += "<td width=* id='sub"+subjectID+"_ind"+i+"'>";
            html += "{[select tutor]}</td>";
            html += "<td width='10px'>";
            html += "<a href='#' onClick='selectTutor("+i+", "+subjectID+"); return false;' class='href' >";
            html += "...</a></td></tr>";
        }
        html += "</table>";
        document.getElementById(tdid).innerHTML = html;
    }
    function selectTutor(index, subjectID){
        width = 600;
        height = 400;
        tmpLeft = (window.screen.availWidth-width)/2;
        tmpTop = (window.screen.availHeight-height)/2;   
        window.open("tutorselect?index="+index+"&subjectID="+subjectID, "", "height="+height+", width="+width+
        ", left="+tmpLeft+", top="+tmpTop+", resizable=1, scrollbars=1 ");
    }
    function setValue(index, subjectID, tutorID, tutorname){
        document.getElementById("sub"+subjectID+"_ind"+index).innerHTML = tutorname;
        name = "sub="+subjectID+"ind="+index;
        document.getElementById(name).value = tutorID;
    }
</script>