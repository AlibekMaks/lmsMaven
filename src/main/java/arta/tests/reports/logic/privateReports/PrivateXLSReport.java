package arta.tests.reports.logic.privateReports;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Date;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.common.hssf.HSSFStyles;
import arta.tests.common.TestMessages;
import arta.filecabinet.logic.students.Student;
import arta.subjects.logic.SubjectMessages;
import arta.classes.ClassMessages;
import arta.check.logic.Testing;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class PrivateXLSReport {


    HSSFWorkbook workbook;
    int lang;

    Student student;
    String subject;
    String tutorName;
    Date date;
    Testing testing;


    public PrivateXLSReport(int lang, Student student, String subject, String tutorName, Date date, Testing testing) {
        this.lang = lang;
        this.student = student;
        this.subject = subject;
        this.tutorName = tutorName;
        this.date = date;
        this.testing = testing;
        workbook = new HSSFWorkbook();
    }

    public void build() throws Exception{

        HSSFStyles styles = new HSSFStyles();
        HSSFCellStyle headerStyle = styles.getHeaderCellStyle(workbook);
        HSSFCellStyle cellStyle = styles.getCommonCellStyle(workbook);
        HSSFCellStyle tableStyle = styles.getTableCellStyle(workbook);
        HSSFCellStyle tableHeaderStyle = styles.getTableHeaderCellStyle(workbook);
        HSSFCellStyle tableHeaderVertBound = styles.getTableHeaderCellStyleWithVertBoundaries(workbook);
        HSSFCellStyle tableHeaderHorBound = styles.getTableHeaderCellStyleWithHorBoundaries(workbook);

        HSSFSheet sheet = workbook.createSheet();

        HSSFRow headerRow = sheet.createRow(0);
        sheet.addMergedRegion(new Region(0, (short)0, 0, (short) 5));
        HSSFCell headerCell = headerRow.createCell((short)0);
        HSSFRichTextString headerString = new HSSFRichTextString(
            MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT, null));
        headerCell.setCellValue(headerString);
        headerCell.setCellStyle(headerStyle);

        HSSFRow studentNameRow = sheet.createRow(1);
        sheet.addMergedRegion(new Region((short)1, (short)0, (short)1, (short)5));
        HSSFCell studentNameCell = studentNameRow.createCell((short)0);
        HSSFRichTextString studentName = new HSSFRichTextString(
                MessageManager.getMessage(lang, Constants.STUDENT_MESSAGE, null) + " " +
                student.getFullName());
        studentNameCell.setCellValue(studentName);

        HSSFRow classNameRow = sheet.createRow(2);
        sheet.addMergedRegion(new Region((short)2, (short)0, (short)2, (short)5));
        HSSFCell classNameCell = classNameRow.createCell((short)0);
        HSSFRichTextString className = new HSSFRichTextString(
                MessageManager.getMessage(lang, ClassMessages.CLASS, null) + " " +
                student.getClassName());
        classNameCell.setCellValue(className);

        HSSFRow subjectNameRow = sheet.createRow(3);
        sheet.addMergedRegion(new Region((short)3, (short)0, (short)3, (short)5));
        HSSFCell subjectNameCell = subjectNameRow.createCell((short)0);
        HSSFRichTextString subjectName = new HSSFRichTextString(
                MessageManager.getMessage(lang, SubjectMessages.SUBJECT, null) + " " +
                subject);
        subjectNameCell.setCellValue(subjectName);

        HSSFRow tutorNameRow = sheet.createRow(4);
        sheet.addMergedRegion(new Region((short)4, (short)0, (short)4, (short)5));
        HSSFCell tutorNameCell = tutorNameRow.createCell((short)0);
        HSSFRichTextString tutorName = new HSSFRichTextString(
                MessageManager.getMessage(lang, TestMessages.TUTOR, null) + " " +
                this.tutorName);
        tutorNameCell.setCellValue(tutorName);

        HSSFRow dateRow = sheet.createRow(5);
        sheet.addMergedRegion(new Region((short)5, (short)0, (short)5, (short)5));
        HSSFCell dateCell = dateRow.createCell((short)0);
        HSSFRichTextString date = new HSSFRichTextString(
                MessageManager.getMessage(lang, TestMessages.DATE, null) + " " +
                this.date.getDate());
        dateCell.setCellValue(date);

        HSSFRow markRow = sheet.createRow(6);
        sheet.addMergedRegion(new Region((short)6, (short)0, (short)6, (short)5));
        HSSFCell markCell = markRow.createCell((short)0);
        HSSFRichTextString mark = new HSSFRichTextString(
                MessageManager.getMessage(lang, TestMessages.NUMBER_OF_RIGHT_ANSWERS, null) + " " +
                testing.getRightAnswersNumber());
        markCell.setCellValue(mark);

        HSSFRow questionsHeaderRow = sheet.createRow(7);
        sheet.addMergedRegion(new Region((short)7, (short)0, (short)7, (short)5));
        HSSFCell questionsHeaderCell = questionsHeaderRow.createCell((short)0);
        HSSFRichTextString questionsHeader = new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.TESTING_QUESTIONS, null));
        questionsHeaderCell.setCellStyle(headerStyle);
        questionsHeaderCell.setCellValue(questionsHeader);

        HSSFCellStyle rightCellStyle = workbook.createCellStyle();
        rightCellStyle.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
        rightCellStyle.setFillForegroundColor( (short)42);

        HSSFCellStyle wrongCellStyle = workbook.createCellStyle();
        wrongCellStyle.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
        wrongCellStyle.setFillForegroundColor( (short)45);

        HSSFCellStyle studentCellStyle = workbook.createCellStyle();
       // studentCellStyle.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
        //studentCellStyle.setFillForegroundColor( (short)22);
        studentCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        studentCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        studentCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        studentCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);

        HSSFCellStyle systemCellStyle = workbook.createCellStyle();
        //systemCellStyle.setFillPattern((short) HSSFCellStyle.SOLID_FOREGROUND);
        //systemCellStyle.setFillForegroundColor( (short)23);
        systemCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        systemCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        systemCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        systemCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);

        DataExtractor extractor = new DataExtractor();
        StringBuffer max = new StringBuffer("8");
        for (int i = 0; i < testing.questions.size(); i++){
            testing.questions.get(i).addToXLSReport(sheet, max, extractor, lang,
                    rightCellStyle, wrongCellStyle, studentCellStyle,  systemCellStyle, i+1);
        }

        sheet.setColumnWidth((short)0, (short)1000);
        sheet.setColumnWidth((short)1, (short)1000);
        sheet.setColumnWidth((short)2, (short)10000);
        sheet.setColumnWidth((short)3, (short)1000);
        sheet.setColumnWidth((short)4, (short)1000);
        sheet.setColumnWidth((short)5, (short)10000);

    }

    public byte[] getReport(){
        byte [] report = null;
        try{
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            workbook.write(bout);
            report = bout.toByteArray();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return report;
    }


    public PrivateXLSReport() {
    }

    public void loadAndWrite(int studentID, int testingID, OutputStream out){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            res = st.executeQuery("SELECT xlsreport FROM testreports WHERE testingID="+testingID +" AND " +
                    " studentID="+studentID+" ");
            if (res.next()){
                byte [] b = res.getBytes(1);
                if (b != null)
                out.write(b);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
