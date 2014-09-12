package arta.tests.parser.logic.xls;

import arta.tests.test.Test;
import arta.tests.questions.Question;
import arta.tests.questions.open.OpenQuestion;
import arta.tests.questions.open.SingleAnswer;
import arta.tests.common.TestMessages;
import arta.tests.common.DifficultySelect;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFCell;


public class XLSParser {

    Test test;
    InputStream stream;

    public XLSParser(int tutorID, InputStream test){
        this.test = new Test(0);
        this.test.tutorID = tutorID;
        stream = test;
    }

    public boolean parse(Message message, int lang){
        boolean result = true;
        try{
            this.test.message = message;
            this.test.lang = lang;
            HSSFWorkbook workbook = null;
            try{
                workbook = new HSSFWorkbook(stream);
            } catch (Exception exc){
                Log.writeLog(exc);
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_HAS_NOT_BEEN_IMPORTED, null));
                message.setMessage(MessageManager.getMessage(lang, TestMessages.INVALID_FILE, null));
                return false;
            }
            int sheetsCount = workbook.getNumberOfSheets();

            for (int sheetNumber = 0; sheetNumber < sheetsCount; sheetNumber ++ ){
                HSSFSheet sheet = workbook.getSheetAt(sheetNumber);
                Iterator rows = sheet.rowIterator();
                while (rows.hasNext()){
                    HSSFRow row = (HSSFRow)rows.next();
                    if (row == null) continue;
                    OpenQuestion question = new OpenQuestion(Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS);
                    question.setDifficulty(DifficultySelect.EASY);
                    Iterator cells = row.cellIterator();
                    int k = 0;
                    while (cells.hasNext()){
                        HSSFCell cell = (HSSFCell) cells.next();
                        if (k == 0){
                            question.setQuestion(cell.toString());
                        } else {
                            question.answers.add(new SingleAnswer(cell.toString(), k == 1));
                        }
                        k ++ ;
                    }
                    test.questions.add(question);
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return result;
    }


    public Test getTest() {
        return test;
    }

}
