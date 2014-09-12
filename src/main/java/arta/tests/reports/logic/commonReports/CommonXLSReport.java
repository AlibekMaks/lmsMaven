package arta.tests.reports.logic.commonReports;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import arta.common.hssf.HSSFStyles;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.tests.common.TestMessages;
import arta.subjects.logic.SubjectMessages;
import arta.classes.ClassMessages;

import java.io.OutputStream;


public class CommonXLSReport {


    HSSFWorkbook workbook;

    CommonTestReportView report;
    int lang;


    public CommonXLSReport(CommonTestReportView report, int lang) {
        this.report = report;
        this.lang = lang;
        workbook = new HSSFWorkbook();
    }

    public void buildReport(){

        HSSFStyles styles = new HSSFStyles();
        HSSFCellStyle headerStyle = styles.getHeaderCellStyle(workbook);
        HSSFCellStyle tableCellStyle = styles.getTableCellStyle(workbook);
        HSSFCellStyle tableHeaderStyle = styles.getTableHeaderCellStyle(workbook);
        HSSFCellStyle numberCellStyle = styles.getTableCellStyle(workbook); //workbook.createCellStyle();
        numberCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        short maxCols = 8;
        short currentRow = -1;
        HSSFSheet sheet = workbook.createSheet("Report");

        HSSFRow headerRow  = sheet.createRow(++currentRow);
        HSSFCell headerCell = headerRow.createCell((short) 0);
        headerCell.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.COMMON_REPORT_PAPER, null)));
        headerCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new Region((short)0, (short)0, (short)0, maxCols));

        HSSFRow testingNameRow = sheet.createRow(++currentRow);
        HSSFCell testingNameCell = testingNameRow.createCell((short)0);
        testingNameCell.setCellValue(new HSSFRichTextString(report.getTEstinName()));
        testingNameCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new Region(currentRow, (short)0, currentRow, maxCols));

        sheet.createRow(++currentRow);

        HSSFRow dateRow = sheet.createRow(++currentRow);
        HSSFCell dateCell = dateRow.createCell((short)0);
        HSSFCell dateValueCell = dateRow.createCell((short)(1));
        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
        dateCell.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.DATE, null)));
        dateValueCell.setCellValue(new HSSFRichTextString(report.date.getInputValue()));
        

        HSSFRow tutorRow = sheet.createRow(++currentRow);
        HSSFCell tutorCell = tutorRow.createCell((short)0);
        HSSFCell tutorValueCell = tutorRow.createCell((short)1);
        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
        tutorCell.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE, null)));
        tutorValueCell.setCellValue(new HSSFRichTextString(report.tutorName));

//        HSSFRow questionsRow = sheet.createRow(++currentRow);
//        HSSFCell questionsCell = questionsRow.createCell((short)0);
//        HSSFCell questionsValueCell = questionsRow.createCell((short)1);
//        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
//        questionsCell.setCellValue(new HSSFRichTextString(
//                MessageManager.getMessage(lang, TestMessages.QUESTIONS_COUNT, null)
//        ));
//        questionsValueCell.setCellValue(report.questionsCount);
//        questionsValueCell.setCellStyle(numberCellStyle);
//
//
//        HSSFRow easyRow = sheet.createRow(++currentRow);
//        HSSFCell easyCell = easyRow.createCell((short)0);
//        HSSFCell easyValueCell = easyRow.createCell((short)1);
//        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
//        easyCell.setCellValue(new HSSFRichTextString(
//                MessageManager.getMessage(lang, TestMessages.EASY_QUESTIONS_COUNT, null)
//        ));
//        easyValueCell.setCellValue(report.easyCount);
//        easyValueCell.setCellStyle(numberCellStyle);
//
//        HSSFRow middleRow = sheet.createRow(++currentRow);
//        HSSFCell middleCell = middleRow.createCell((short)0);
//        HSSFCell middleValueCell = middleRow.createCell((short)1);
//        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
//        middleCell.setCellValue(new HSSFRichTextString(
//                MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTIONS_COUNT, null)
//        ));
//        middleValueCell.setCellValue(report.middleCount);
//        middleValueCell.setCellStyle(numberCellStyle);
//
//
//        HSSFRow diffRow = sheet.createRow(++currentRow);
//        HSSFCell diffCell = diffRow.createCell((short)0);
//        HSSFCell diffValueCell = diffRow.createCell((short)1);
//        sheet.addMergedRegion(new Region(currentRow, (short)1, currentRow, maxCols));
//        diffCell.setCellValue(new HSSFRichTextString(
//                MessageManager.getMessage(lang, TestMessages.MIDDLE_QUESTIONS_COUNT, null)
//        ));
//        diffValueCell.setCellValue(report.difficultCount);
//        diffValueCell.setCellStyle(numberCellStyle);

        sheet.createRow(++currentRow);

        HSSFRow headerRow0 = sheet.createRow(++currentRow);
        HSSFCell cell00 = headerRow0.createCell((short)0);
        HSSFCell cell01 = headerRow0.createCell((short)1);
        HSSFCell cell02 = headerRow0.createCell((short)2);
        HSSFCell cell03 = headerRow0.createCell((short)3);
        HSSFCell cell04 = headerRow0.createCell((short)4);
        HSSFCell cell05 = headerRow0.createCell((short)5);
        HSSFCell cell06 = headerRow0.createCell((short)6);
        HSSFCell cell07 = headerRow0.createCell((short)7);
        HSSFCell cell08 = headerRow0.createCell((short)8);

        HSSFRow headerRow1 = sheet.createRow(++currentRow);
        HSSFCell cell10 = headerRow1.createCell((short)0);
        HSSFCell cell11 = headerRow1.createCell((short)1);
        HSSFCell cell12 = headerRow1.createCell((short)2);
        HSSFCell cell13 = headerRow1.createCell((short)3);
        HSSFCell cell14 = headerRow1.createCell((short)4);
        HSSFCell cell15 = headerRow1.createCell((short)5);
        HSSFCell cell16 = headerRow1.createCell((short)6);
        HSSFCell cell17 = headerRow1.createCell((short)7);
        HSSFCell cell18 = headerRow1.createCell((short)8);

        cell00.setCellStyle(tableHeaderStyle);
        cell01.setCellStyle(tableHeaderStyle);
        cell02.setCellStyle(tableHeaderStyle);
        cell03.setCellStyle(tableHeaderStyle);
        cell04.setCellStyle(tableHeaderStyle);
        cell05.setCellStyle(tableHeaderStyle);
        cell06.setCellStyle(tableHeaderStyle);
        cell07.setCellStyle(tableHeaderStyle);
        cell08.setCellStyle(tableHeaderStyle);

        cell10.setCellStyle(tableHeaderStyle);
        cell11.setCellStyle(tableHeaderStyle);
        cell12.setCellStyle(tableHeaderStyle);
        cell13.setCellStyle(tableHeaderStyle);
        cell14.setCellStyle(tableHeaderStyle);
        cell15.setCellStyle(tableHeaderStyle);
        cell16.setCellStyle(tableHeaderStyle);
        cell17.setCellStyle(tableHeaderStyle);
        cell18.setCellStyle(tableHeaderStyle);


        sheet.addMergedRegion(new Region(currentRow-1, (short)0, currentRow, (short)1)); // Ф.И.О. учащегося
        sheet.addMergedRegion(new Region(currentRow-1, (short)2, currentRow, (short)2)); // Должность
        sheet.addMergedRegion(new Region(currentRow-1, (short)3, currentRow-1, (short)5)); // Правильных ответов
        sheet.addMergedRegion(new Region(currentRow-1, (short)6, currentRow, (short)6));  // Оценка
        sheet.addMergedRegion(new Region(currentRow-1, (short)7, currentRow, (short)7)); // Язык тестирования
        sheet.addMergedRegion(new Region(currentRow-1, (short)8, currentRow, (short)8)); // Результат тестирования


        cell00.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.STUDENT_FIO, null)));
        cell02.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.STUDENT_POST, null)));
        cell03.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWERS, null)));
        cell13.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.EASY, null)));
        cell14.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.MIDDLE, null)));
        cell15.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.DIFFICULT, null)));
        cell06.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.MARK, null)));
        cell07.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.TEST_LANGUAGE, null)));
        cell08.setCellValue(new HSSFRichTextString(MessageManager.getMessage(lang, TestMessages.TESTING_RESULT, null)));


        for (int i =0; i < report.students.size(); i++){
            HSSFRow row = sheet.createRow(++currentRow);
            HSSFCell studentNameCell = row.createCell((short)0);
            HSSFCell studentNameCell1 = row.createCell((short)1);
            sheet.addMergedRegion(new Region(currentRow, (short)0, currentRow, (short)1));
            HSSFCell clas = row.createCell((short)2);
            HSSFCell easy  = row.createCell((short)3);
            HSSFCell middle  = row.createCell((short)4);
            HSSFCell difficult  = row.createCell((short)5);
            HSSFCell mark  = row.createCell((short)6);
            HSSFCell language = row.createCell((short)7);
            HSSFCell testing_result = row.createCell((short)8);

            studentNameCell.setCellStyle(tableCellStyle);
            studentNameCell1.setCellStyle(tableCellStyle);
            clas.setCellStyle(tableCellStyle);
            easy.setCellStyle(tableCellStyle);
            middle.setCellStyle(tableCellStyle);
            difficult.setCellStyle(tableCellStyle);
            mark.setCellStyle(tableCellStyle);
            language.setCellStyle(tableCellStyle);
            testing_result.setCellStyle(tableCellStyle);

            studentNameCell.setCellValue(new HSSFRichTextString(report.students.get(i).name));
            easy.setCellValue(report.students.get(i).easy);
            easy.setCellStyle(numberCellStyle);

            middle.setCellValue(report.students.get(i).middle);
            middle.setCellStyle(numberCellStyle);

            difficult.setCellValue(report.students.get(i).difficult);
            difficult.setCellStyle(numberCellStyle);

            mark.setCellValue(report.students.get(i).mark);
            mark.setCellStyle(numberCellStyle);

            clas.setCellValue(report.students.get(i).className);
            language.setCellValue(report.students.get(i).getLanguageName());

            if(report.students.get(i).testingIsPassed){
                testing_result.setCellValue(MessageManager.getMessage(lang, TestMessages.TEST_PASSED, null).toLowerCase());
            } else {
                testing_result.setCellValue(MessageManager.getMessage(lang, TestMessages.TEST_NOT_PASSED, null).toLowerCase());
            }

        }

        sheet.setColumnWidth((short)0, (short)4000); // Ф.И.О. учащегося
        sheet.setColumnWidth((short)1, (short)3000);
        sheet.setColumnWidth((short)2, (short)8000);
        sheet.setColumnWidth((short)3, (short)3000);
        sheet.setColumnWidth((short)4, (short)5000);
        sheet.setColumnWidth((short)5, (short)2500);
        sheet.setColumnWidth((short)6, (short)2500);
        sheet.setColumnWidth((short)7, (short)4500);
        sheet.setColumnWidth((short)8, (short)3000);
    }

    public void writeReport(OutputStream out){
        try{
            workbook.write(out);
        } catch (Exception exc){
            Log.writeLog(exc);
        } 
    }
}
