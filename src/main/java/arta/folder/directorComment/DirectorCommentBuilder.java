package arta.folder.directorComment;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import arta.common.logic.util.Date;
import arta.common.logic.util.Languages;
import arta.filecabinet.logic.students.Student;
import arta.folder.department.Department;

public class DirectorCommentBuilder {
	public String build(int studentID, int lang) {
		Student student = new Student();
        student.loadById(studentID);
        
        Department dep = Department.loadById(student.getDepartmentID(), lang);
        
		Locale locale = null;
    	if (lang == Languages.ENGLISH) {
    		locale = new Locale("en");
    	} else if (lang == Languages.KAZAKH) {
    		locale = new Locale("kz");
    	} else {
    		locale = new Locale("ru");
    	}
    	
    	ResourceBundle bundle = ResourceBundle.getBundle("comment_msgs", locale); //arta.folder.directorComment.comment_msgs
    	
        StringBuffer str = new StringBuffer();
        
        str.append("<center><b>");
        str.append(bundle.getString("director.comment"));
        str.append("</b></center>");
        
        str.append(bundle.getString("fio"));
        str.append("&nbsp;");
        str.append(student.getFullName());
        str.append("<br/>");
        
        str.append(bundle.getString("post.and.department"));
        str.append("&nbsp;");
        str.append(student.getClassName());
    	str.append(",&nbsp;");
    	if (dep != null) {
    		str.append(dep.getName());
    	} else {
    		str.append("______________");
    	}
        str.append("<br/>");
        
        str.append(bundle.getString("subordination.structure"));
        str.append("<br/>");
        
        str.append(bundle.getString("who.is.his.director"));
        str.append("&nbsp;");
        if (dep != null) {
        	//str.append(dep.getDirectorName());
            str.append("______________________");
    	} else {
    		str.append("______________________");
    	}
        str.append("<br/>");
        
        str.append(bundle.getString("subordinate.count"));
        str.append("&nbsp;");
        str.append("___");
        str.append("<br/>");
        
        str.append(bundle.getString("birthdate"));
        str.append("&nbsp;");
        str.append(student.getBirthdate().getDate());
        str.append("<br/>");
        
        int stazOverallDays = new Date().getDifference( student.getStazOverallStart() );
        int stazOverallYears = stazOverallDays / 365;
        
        str.append(bundle.getString("staz.overall"));
        str.append("&nbsp;");
        str.append( stazOverallYears );
        str.append("<br/>");
        
        int stazSocietyDays = new Date().getDifference( student.getStazSocietyStart() );
        int stazSocietyYears = stazSocietyDays / 365;
        
        str.append(bundle.getString("staz.society"));
        str.append("&nbsp;");
        str.append( stazSocietyYears );
        str.append("<br/>");
        
        str.append("<table style=\"width:100%;border:1px solid black;border-collapse:collapse;\">");
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td rowspan=2 width=\"5%\" style=\"border-right: 1px solid black;\">№");
        str.append("</td>");
        str.append("<td rowspan=2 width=\"25%\" style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("header.function") );
        str.append("</td>");
        str.append("<td colspan=5 width=\"25%\" style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("header.execution") );
        str.append("</td>");
        str.append("<td rowspan=2 width=\"*\">");
        str.append( bundle.getString("header.note") );
        str.append("</td>");
        str.append("</tr>");
        
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">1</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">2</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">3</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">4</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">5</td>");
        str.append("</tr>");
        
        for (int i=0; i<6; i++) {
        	str.append("<tr style=\"border-bottom: 1px solid black;\">");
        	str.append("<td style=\"border-right: 1px solid black;\">&nbsp;</td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td></td>");
        	str.append("</tr>");
        }
        str.append("</table>");
        
        str.append(bundle.getString("average.ball"));
        str.append("______");
        str.append("<br/>");
        
        str.append("<table style=\"width:100%;border:1px solid black;border-collapse:collapse;\">");
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td rowspan=2 width=\"5%\" style=\"border-right: 1px solid black;\">№");
        str.append("</td>");
        str.append("<td rowspan=2 style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("projects") );
        str.append("</td>");
        str.append("<td colspan=5 style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("header.execution") );
        str.append("</td>");
        str.append("<td rowspan=2>");
        str.append( bundle.getString("header.note") );
        str.append("</td>");
        str.append("</tr>");
        
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">1</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">2</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">3</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">4</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">5</td>");
        str.append("</tr>");
        
        for (int i=0; i<2; i++) {
        	str.append("<tr style=\"border-bottom: 1px solid black;\">");
        	str.append("<td style=\"border-right: 1px solid black;\">&nbsp;</td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td></td>");
        	str.append("</tr>");
        }
        str.append("</table>");
        
        str.append(bundle.getString("average.ball"));
        str.append("______");
        str.append("<br/>");
        
        str.append("<table style=\"width:100%;border:1px solid black;border-collapse:collapse;\">");
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td rowspan=2 width=\"5%\" style=\"border-right: 1px solid black;\">№");
        str.append("</td>");
        str.append("<td rowspan=2 width=\"*\" style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("header.merit") );
        str.append("</td>");
        str.append("<td colspan=5 width=\"25%\" style=\"border-right: 1px solid black;\">");
        str.append( bundle.getString("header.mark") );
        str.append("</td>");
        str.append("</tr>");
        
        str.append("<tr style=\"border-bottom: 1px solid black;\">");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">1</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">2</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">3</td>");
        str.append("<td width=\"5%\" style=\"border-right: 1px solid black;\">4</td>");
        str.append("<td width=\"5%\" >5</td>");
        str.append("</tr>");
        
        for (int i=1; i<=9; i++) {
        	str.append("<tr style=\"border-bottom: 1px solid black;\">");
        	str.append("<td style=\"border-right: 1px solid black;\">" + i + ".</td>");
        	str.append("<td style=\"border-right: 1px solid black;\">" + bundle.getString("merit." + i) + "</td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td style=\"border-right: 1px solid black;\"></td>");
        	str.append("<td></td>");
        	str.append("</tr>");
        }
    	
        str.append("</table>");
        
        str.append(bundle.getString("average.ball"));
        str.append("______");
        str.append("<br/>");
        
        str.append(bundle.getString("director.sign"));
        str.append("___________________/______________________");
        str.append("<br/>");
        
        str.append(bundle.getString("employee.sign"));
        str.append("___________________/______________________");
        str.append("<br/>");
        
        str.append("<br/>");
        
        str.append("\"___\"____________20___" + bundle.getString("year.letter") + ".");
        
        return str.toString();
	}
}
