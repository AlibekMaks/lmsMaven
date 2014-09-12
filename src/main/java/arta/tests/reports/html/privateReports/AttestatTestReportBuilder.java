package arta.tests.reports.html.privateReports;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import kz.arta.plt.common.Person;

import arta.tests.common.TestMessages;
import arta.check.logic.Testing;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.filecabinet.logic.students.Student;
import arta.classes.ClassMessages;
import arta.subjects.logic.SubjectMessages;

public class AttestatTestReportBuilder {

    public StringBuffer build(Testing testing, Student student, int lang){
    	
    	Locale locale = null;
    	if (lang == Languages.ENGLISH) {
    		locale = new Locale("en");
    	} else if (lang == Languages.KAZAKH) {
    		locale = new Locale("kz");
    	} else {
    		locale = new Locale("ru");
    	}


        //String p=AttestatTestReportBuilder.class.getResource("attestat_report_msgs.properties").getPath();
    	ResourceBundle bundle = ResourceBundle.getBundle("attestat_report_msgs", locale); //arta.tests.reports.html.privateReports.
    	
        StringBuffer str = new StringBuffer();
        
        str.append("<center><b>");
        str.append(bundle.getString("attestat.info"));
        str.append("</b></center>");
        str.append("<br/>");
        str.append("1.&nbsp;");
        str.append("<span style=\"display: inline-block;\">");
        str.append(bundle.getString("fio"));
        str.append("</span>");
        str.append("&nbsp;");
        str.append(student.getFullName());
        str.append("<br/>");
        str.append("2.&nbsp;");
        str.append(bundle.getString("birthdate"));
        str.append("&nbsp;");
        str.append(student.getBirthdate().getStringDateValue(lang));
        str.append("<br/>");
        str.append("3.&nbsp;");
    	str.append(bundle.getString("educatioin"));
    	str.append("___________,&nbsp;");
    	str.append(bundle.getString("institution"));
    	str.append("&nbsp;");
    	str.append(student.getEducationUZ());
    	str.append(",&nbsp;");
    	str.append(bundle.getString("profession"));
    	str.append("&nbsp;");
    	str.append(student.getEducationProfession());
    	str.append(",&nbsp;");
    	str.append(bundle.getString("qualification"));
    	str.append("&nbsp;");
    	str.append(student.getEducationQualification());
    	str.append("<br/>");
    	str.append("4.&nbsp;");
    	str.append(bundle.getString("upgrading.info"));
    	str.append("&nbsp;");
    	str.append( student.isHadUpgradedQualification()?"☑":"☒" );
    	str.append("<br/>");
    	str.append("5.&nbsp;");
    	str.append(bundle.getString("position.and.date"));
    	// SELECT FROM positions
    	str.append("&nbsp;");
    	str.append(student.getClassName());
    	str.append("&nbsp;");
    	str.append(student.getStartdate().getStringDateValue(lang));
    	str.append("<br/>");
    	str.append("6.&nbsp;");
    	str.append(bundle.getString("experience.overall"));
    	str.append("&nbsp;");
    	appendStaz(bundle, str, student.getStazOverallStart());
    	str.append("<br/>");
    	str.append("7.&nbsp;");
    	str.append(bundle.getString("experience.post"));
    	str.append("&nbsp;");
    	appendStaz(bundle, str, student.getStazPostStart());
    	str.append("<br/>");
    	str.append("8.&nbsp;");
    	str.append(bundle.getString("results"));
    	str.append("<br/>");
    	
    	str.append("<table style=\"width: 100%;border:1px solid black;border-collapse:collapse;\">");
    	str.append("<tr style=\"border:1px solid black;\">");
    	str.append("<td style=\"border-right:1px solid black;\">№");
    	str.append("</td>");
    	str.append("<td style=\"border-right:1px solid black;\">");
    	str.append(bundle.getString("header.testing"));
    	str.append("</td>");
    	str.append("<td style=\"border-right:1px solid black;\">");
    	str.append(bundle.getString("header.practice"));
    	str.append("</td>");
    	str.append("<td style=\"border-right:1px solid black;\">");
    	str.append(bundle.getString("header.interview"));
    	str.append("</td>");
    	str.append("<td>");
    	str.append(bundle.getString("header.summary"));
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr style=\"border:1px solid black;\">");
    	str.append("<td style=\"border-right:1px solid black;\"></td>" +
    			"<td style=\"border-right:1px solid black;\">" + testing.mark + "</td>" +
    			"<td style=\"border-right:1px solid black;\"></td>" +
    			"<td style=\"border-right:1px solid black;\"></td>" +
    			"<td></td>");
    	str.append("</tr>");
    	str.append("</table>");
    	
    	str.append("<br/>");
    	str.append("9.&nbsp;");
    	str.append(bundle.getString("present.committee"));
    	str.append("<br/>");
    	str.append(bundle.getString("votes.count") + "___");
    	str.append("<br/>");
    	str.append(bundle.getString("votes.positive") + "___");
    	str.append("<br/>");
    	str.append(bundle.getString("votes.negative") + "___");
    	str.append("<br/>");
    	str.append(bundle.getString("votes.indifferent") + "___");
    	str.append("<br/>");
    	str.append("10.&nbsp;");
    	str.append(bundle.getString("comission.decision"));
    	str.append("<br/>");
    	str.append("<div style=\"width:100%;border-bottom:1px solid black;\">&nbsp;</div>");
    	str.append("<div style=\"width:100%;border-bottom:1px solid black;\">&nbsp;</div>");
    	str.append("<div style=\"width:100%;border-bottom:1px solid black;\">&nbsp;</div>");
    	str.append("<br/>");
    	str.append(bundle.getString("protocol"));
    	str.append("<br/>");
    	str.append("<br/>");
    	
    	String signature = bundle.getString("signature");
    	String decipherment = bundle.getString("decipherment");
    	
    	str.append("<table style=\"width: 100%;border-collapse:collapse;\">");
    	str.append("<tr>");
    	str.append("<td width=\"40%\">");
    	str.append(bundle.getString("committee.chairman"));
    	str.append("</td>");
    	str.append("<td width=\"15%\">");
    	str.append("</td>");
    	str.append("<td width=\"1%\">");
    	str.append("</td>");
    	str.append("<td width=\"15%\">");
    	str.append("</td>");
    	str.append("<td width=*>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ signature +"</span>");
    	str.append("</td width=\"5%\">");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ decipherment +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append(bundle.getString("committee.vice.chairman"));
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td >");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ signature +"</span>");
    	str.append("</td>");
    	str.append("</td width=\"5%\">");
    	str.append("<td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ decipherment +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append(bundle.getString("committee.secretary"));
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ signature +"</span>");
    	str.append("</td>");
    	str.append("</td width=\"5%\">");
    	str.append("<td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ decipherment +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append(bundle.getString("committee.members"));
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ signature +"</span>");
    	str.append("</td>");
    	str.append("</td width=\"5%\">");
    	str.append("<td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ decipherment +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ signature +"</span>");
    	str.append("</td>");
    	str.append("</td width=\"5%\">");
    	str.append("<td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ decipherment +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("</table>");
    	
    	str.append("<br/>");
    	str.append("<br/>");
    	str.append(bundle.getString("committee.date"));
    	str.append("<br/>");
    	
    	str.append("<table style=\"width: 80%;border-collapse:collapse;\">");
    	str.append("<tr>");
    	str.append("<td width=\"40%\">");
    	str.append(bundle.getString("had.read.attestation.info"));
    	str.append("</td>");
    	str.append("<td width=\"30%\">");
    	str.append("</td>");
    	str.append("<td width=*>");
    	str.append("</td>");
    	str.append("</tr>");
    	
    	str.append("<tr>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("<td style=\"border-top: 1px solid black;\"><span style=\"font-size:0.75em;\">"+ bundle.getString("signature.and.date") +"</span>");
    	str.append("</td>");
    	str.append("<td>");
    	str.append("</td>");
    	str.append("</tr>");
    	str.append("</table>");

        return str;
    }

	private void appendStaz(ResourceBundle bundle, StringBuffer str, Date dade) {
		int days = new Date().getDifference(dade);
    	int years = days / 365;
    	int months = (days % 365) / 30;
    	str.append(MessageFormat.format(bundle.getString("roditeln.padez.year"), years) + ((years==0)?"":","));
    	str.append(MessageFormat.format(bundle.getString("roditeln.padez.month"), months));
	}
	
	public static void main(String[] args) {
		ResourceBundle bundle = ResourceBundle.getBundle("arta.tests.reports.html.privateReports.attestat_report_msgs", new Locale("ru"));
        StringBuffer str = new StringBuffer();
        AttestatTestReportBuilder b = new AttestatTestReportBuilder();
        b.appendStaz(bundle, str, new Date());
        System.out.println(str);
        System.out.println("----------y---------------");
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 0));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 1));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 2));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 6));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 40));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 41));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 42));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.year"), 47));
        System.out.println("----------m---------------");
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.month"), 0));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.month"), 1));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.month"), 2));
        System.out.println(MessageFormat.format(bundle.getString("roditeln.padez.month"), 6));
	}

}
