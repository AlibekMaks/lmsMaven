package arta.tests.reports.logic.privateReports;

import java.io.StringReader;
import java.sql.*;

import arta.check.logic.Testing;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Date;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.tutors.Tutor;
import arta.tests.reports.html.privateReports.AttestatTestReportBuilder;
import arta.tests.reports.html.privateReports.TestReportBuilder;
import arta.tests.reports.html.privateReports.TestReportHeader;

import javax.swing.plaf.synth.Region;

/**
 * Мндивидуальная ведомость тестирования
 */
public class TestReport {

    public static final int REPORT_PART_LENGTH = 1024;

    public int testingID;
    public int studentID;
    public String subject;

    public StringBuffer report;

    public TestReport(int studentID, int testingID) {
        this.studentID = studentID;
        this.testingID = testingID;
    }

    public TestReport() {
    }

    public void load(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT testreports.report " +
                    " FROM testreports WHERE testreports.testingID="+testingID+" AND studentID="+studentID);
            if (res.next()) {
            	report = new StringBuffer(res.getString(1));
            }

        }catch (Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            }catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void makeReport(Testing testing, Student student, Statement st, int lang, Connection con,
                           int easy, int middle, int difficult) throws Exception{
        ResultSet res = null;

        Date date = new Date();
        String tutor = "";

        int tutorID = 0;

        res = st.executeQuery("SELECT "+
                " tutors.lastname as ln, " +
                " tutors.firstname as fn, " +
                " tutors.patronymic as p  " +
                " FROM tutors LEFT JOIN testings ON tutors.tutorID=testings.tutorID " +
                " WHERE testings.testingID="+testing.testingID+" ");
        if (res.next()){
            tutor = Person.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));            
        }

        res = st.executeQuery("SELECT subjects.name"+Languages.getLang(lang)+" " +
                " FROM subjects LEFT JOIN studygroups ON studygroups.subjectID=subjects.subjectID " +
                " JOIN registrar ON registrar.groupID=studygroups.groupID " +
                " WHERE registrar.studentID="+student.getPersonID()+" AND registrar.testingID="+testingID+" ");
        if (res.next()){
            subject = res.getString(1);
        }

        res = st.executeQuery("SELECT classes.classname FROM classes LEFT JOIN students" +
                " ON students.classID=classes.classID " +
                " WHERE students.studentID="+student.getPersonID()+"");
        if (res.next()){
            student.setClassName(res.getString(1));
        }


        report = new StringBuffer("");
        report.append("<div id=\"report\" disabled=\"disabled\" onload=\"disabledCheckboxes()\" >");
        report.append("<table border=0 width=\"100%\" cellpadding=\"20px\"><tr><td>");
        TestReportHeader header = new TestReportHeader();
        report.append(header.getHeader(testing, student, tutor, subject, lang, date));
        TestReportBuilder  reportBuilder = new TestReportBuilder();
        report.append(reportBuilder.getReport(testing, lang));
        report.append("</td></tr></table>");
        report.append("</div>");


        PrivateXLSReport xlsReport = new PrivateXLSReport(lang, student, subject, tutor, date, testing);
        xlsReport.build();

        Timestamp startTime = new Timestamp(testing.startTime.getTime());
        Timestamp finishTime = new Timestamp(testing.finishTime.getTime());

        PreparedStatement prst = con.prepareStatement("INSERT INTO testreports (studentID, mark, testingDate, report, xlsreport, tutorID, testingID, easy, middle, difficult, starttime, finishtime) " +
                " VALUES ("+student.getPersonID()+", "+testing.mark+", '"+date.getDate()+"', ?, ?, "+tutorID+", "+testing.testingID+", "+easy+", "+middle+", "+difficult+",?,?)");
        StringReader reader = new StringReader(report.toString());
        prst.setCharacterStream(1, reader, report.length());
        prst.setBytes(2, xlsReport.getReport());
        prst.setTimestamp(3, startTime);
        prst.setTimestamp(4, finishTime);
        prst.execute();

    }

	public String getReportAttestat(int testingID, int studentID,int tutorid, String chairman,
                                    String vicechairman, String members, String secretary, int lang) {
		Testing testing = new Testing();
        Tutor tutor = new Tutor();
        testing.tutorid= tutorid;
        testing.chairman=chairman;
        testing.vicechairman=vicechairman;
        testing.members=members;
        testing.secretary=secretary;
        testing.studentID = studentID;
        testing.testingID = testingID;
        testing.load();
        tutor.loadName();
        Student student = new Student();
        student.loadById(studentID);
        
		StringBuffer report_attestat = new StringBuffer("<table border=0 width=\"100%\" cellpadding=\"20px\"><tr><td>");
        AttestatTestReportBuilder builder_attestat = new AttestatTestReportBuilder();
        report_attestat.append(builder_attestat.build(testing, student, lang));
       // report_attestat.append(builder_attestat.tutor(testing, tutor, lang));
        report_attestat.append("</td></tr></table>");
        return report_attestat.toString();
	}

}
