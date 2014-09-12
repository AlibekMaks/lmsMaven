package arta.tests.questions.closed;

import arta.tests.questions.Question;
import arta.tests.common.TestMessages;
import arta.tests.common.ImagesIDParser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Log;
import arta.common.logic.util.Encoding;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.StringTransform;
import arta.common.logic.db.Varchar;
import arta.common.html.util.TinyMce;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import com.bentofw.mime.ParsedData;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;


public class ClosedQuestion implements Question{

    public int classID;
    public int subjectID;
    public int mainTestingID;
    public String subjectName;

    public String question;
    public String answer;
    public int questionID = 0;
    public String errorString = "";
    public int difficulty = 0;
    private int questionNumber;

    public String studentAnswer;

    ServletContext servletContext;
    int lang;

    private boolean isRight = false;

    public ClosedQuestion(ServletContext servletContext, int lang) {
        this.servletContext = servletContext;
        this.lang = lang;
    }

    public ClosedQuestion() {
    }

    public int getQuestionType() {
        return CLOSED_QUESTION;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public boolean saveQuestion(Statement st, ResultSet res, int testID, StringTransform trsf) {
        boolean saved = false;
        try{

            if (question == null || question.length()==0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.QUESTION_NOT_DEFINED, properties);
                return false;
            }

            if (answer==null || answer.length() == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.RIGHT_ANSWER_NOT_DEFINED, properties);
                return false;
            }

            /*
            if (difficulty>settings.maxTestQuestionDifficulty || difficulty <= 0){
                errorString = "invalid difficulty value";
                return false;
            }
            */

            st.execute("INSERT INTO questions (question, questionTypeID, difficulty, testID) " +
                    " VALUES ('"+trsf.getDBString(question, Varchar.QUESTION)+"', "+CLOSED_QUESTION+", "+difficulty+", "+testID+") ",
                    Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();

            if (res.next()){
                questionID = res.getInt(1);
            } else {
                questionID = 0;
            }

            if (questionID <= 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                return false;
            }

            st.execute("INSERT INTO closedanswers (questionID, answer) VALUES " +
                    " ("+questionID+", '"+trsf.getDBString(answer, Varchar.QUESTION)+"')");
            saved = true;
        } catch (Exception exc){
            Log.writeLog(exc);
            saved = false;
        }
        return saved;
    }

    public boolean checkQuestion() {
        return false;
    }

    public String getError() {
        return errorString;
    }

    public void writeQuestionForEdit(PrintWriter pw, int lang) {
        TinyMce tinyMce = new TinyMce(lang);
        ClosedQuestionHandler handler = new ClosedQuestionHandler(lang, this, tinyMce);
        FileReader fileReader = new FileReader("tests/questions/closed/edit.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void writeQuestionForCheck(PrintWriter pw, int lang, ServletContext servletContext) {
        System.out.println("writeQuestionForCheck  :::  arta.tests.questions.closed ");
        ClosedQuestionCheckHandler handler = new ClosedQuestionCheckHandler(lang, this);
        FileReader fileReader = new FileReader("tests/questions/closed/check.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void setRightAnswerForQuestion(Statement st, boolean _isRight){
        ClosedQuestion q = new ClosedQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
    }

    public boolean checkAnswer(Statement st) {
        ClosedQuestion q = new ClosedQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
        isRight = this.equals(q);
        return isRight;
    }

    public void loadQuestion(Statement st) {
        try{
            ResultSet res =  st.executeQuery("SELECT questions.question as q, " +
                    " questions.difficulty as d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }

            res = st.executeQuery("SELECT closedanswers.answer AS a FROM closedanswers " +
                    " WHERE closedanswers.questionID="+questionID);
            if (res.next()){
                answer = res.getString("a");
            }

        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getNumber() {
        return questionNumber;
    }

    public void setNumber(int number) {
        this.questionNumber = number;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void parseParameters(Object[] names, ParsedData data) {
        try{
            DataExtractor DataExtractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                if (name.equals("question")){
                    question = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                } else if (name.equals("rightAnswer")){
                    answer = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                } else if (name.equals("diff")){
                    difficulty = DataExtractor.getInteger(data.getParameter(name));
                }
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void getScript(PrintWriter pw) { }

    public void parseCheckParameters(Object[] names, ParsedData data) {
        try{
            DataExtractor extractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                if (name.equals("answer")){
                    answer = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void loadForCheck(Statement st, ResultSet res) {
        try{
            res =  st.executeQuery("SELECT questions.question as q, " +
                    " questions.difficulty as d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }
        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public boolean equals(Object obj) {
        ClosedQuestion q = (ClosedQuestion) obj;
        if (this.answer == null)
            return false;
        StringBuffer current = new StringBuffer(this.answer);
        StringBuffer right = new StringBuffer(q.answer);
        studentAnswer = this.answer;
        this.answer = q.answer;
        int index;
        while ((index = current.indexOf(" ")) >= 0){
            current.replace(index, index + 1, "");
        }
        while ((index = right.indexOf(" ")) >= 0){
            right.replace(index, index + 1, "");
        }
        if (right.toString().toLowerCase().equals(current.toString().toLowerCase()))
            return true;
        return false;
    }

    public void writeQuestionForView(PrintWriter pw, int lang, ServletContext servletContext) {
        ClosedQuestionViewHandler handler = new ClosedQuestionViewHandler(this, lang);
        FileReader fileReader = new FileReader("tests/questions/closed/view.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw , handler);
        parser.parse();
    }

    public boolean isRightAnswer() {
        return isRight;
    }

    public String getReportVariants() {
        StringBuffer str = new StringBuffer();
        str.append("<table border=0 width=\"100%\" cellpadding=1px class=\"table\">");
        str.append("<tr><td><b>");
        str.append(MessageManager.getMessage(lang, TestMessages.ANSWER, null));
        str.append("</td></tr><tr><td>");
        str.append("<table border=1 width=\"100%\" class=\"table\"><tr><td>");
        str.append(studentAnswer);
        str.append("</td></tr><tr><td bgcolor=\""+Question.RIGHT_VARIANT_BACKGROUND+"\">");
        str.append(answer);
        str.append("</td></tr></table>");
        str.append("</td></tr></table>");
        return str.toString();
    }


    public void addToXLSReport(HSSFSheet sheet, Object maxRow, DataExtractor extractor, int lang, HSSFCellStyle right, HSSFCellStyle wrong, HSSFCellStyle student, HSSFCellStyle system, int number) {
        int max = extractor.getInteger(maxRow);
        HSSFRow empty = sheet.createRow(++max);
        StringTransform trsf = new StringTransform();

        HSSFRow questionHeaderRow = sheet.createRow(++max);
        HSSFCell signCell = questionHeaderRow.createCell((short)0);
        HSSFCell questionHeaderCell = questionHeaderRow.createCell((short)1);
        if (isRightAnswer()){
            signCell.setCellStyle(right);
            questionHeaderCell.setCellStyle(right);
        } else {
            signCell.setCellStyle(wrong);
            questionHeaderCell.setCellStyle(wrong);
        }
        sheet.addMergedRegion(new Region((short)max, (short)1, (short)max,  (short)5));
        HSSFRichTextString questionHeader = new HSSFRichTextString(
                MessageManager.getMessage(lang, TestMessages.QUESTION, null)+" "+number);
        questionHeaderCell.setCellValue(questionHeader);

        HSSFRow questionRow = sheet.createRow(++max);
        sheet.addMergedRegion(new Region((short)max, (short)0, (short)max,  (short)5));
        HSSFCell questionCell = questionRow.createCell((short)0);
        HSSFRichTextString question = new HSSFRichTextString(this.question);
        questionCell.setCellValue(new HSSFRichTextString(trsf.getXLSString(question.toString())));

        HSSFRow studentAnswerRow = sheet.createRow(++max);
        HSSFCell studentAnswerCell = studentAnswerRow.createCell((short)0);
        sheet.addMergedRegion(new Region((short)max, (short)0, (short)max,  (short)5));
        studentAnswerCell.setCellStyle(student);
        studentAnswerCell.setCellValue(new HSSFRichTextString(trsf.getXLSString(studentAnswer)));
        HSSFCell cell01 = studentAnswerRow.createCell((short)1);
        HSSFCell cell02 = studentAnswerRow.createCell((short)2);
        HSSFCell cell03 = studentAnswerRow.createCell((short)3);
        HSSFCell cell04 = studentAnswerRow.createCell((short)4);
        HSSFCell cell05 = studentAnswerRow.createCell((short)5);
        cell01.setCellStyle(student);
        cell02.setCellStyle(student);
        cell03.setCellStyle(student);
        cell04.setCellStyle(student);
        cell05.setCellStyle(student);

        HSSFRow rightAnswerRow = sheet.createRow(++max);
        HSSFCell rightAnswerCell = rightAnswerRow.createCell((short)0);
        sheet.addMergedRegion(new Region((short)max, (short)0, (short)max,  (short)5));
        rightAnswerCell.setCellStyle(student);
        rightAnswerCell.setCellValue(new HSSFRichTextString(trsf.getXLSString(answer)));

        HSSFCell cell11 = rightAnswerRow.createCell((short)1);
        HSSFCell cell12 = rightAnswerRow.createCell((short)2);
        HSSFCell cell13 = rightAnswerRow.createCell((short)3);
        HSSFCell cell14 = rightAnswerRow.createCell((short)4);
        HSSFCell cell15 = rightAnswerRow.createCell((short)5);
        cell11.setCellStyle(student);
        cell12.setCellStyle(student);
        cell13.setCellStyle(student);
        cell14.setCellStyle(student);
        cell15.setCellStyle(student);

        ((StringBuffer)maxRow).delete(0, ((StringBuffer)maxRow).length());
        ((StringBuffer)maxRow).append(max);

    }

    public ArrayList<Integer> getUsedImagesID() {
        ImagesIDParser parser = new ImagesIDParser();
        ArrayList <Integer> ids = new ArrayList <Integer> ();
        parser.setParserString(question);
        while(parser.hasMoreImages()){
            ids.add(parser.getNextImageID());
        }
        parser.setParserString(answer);
        while(parser.hasMoreImages()){
            ids.add(parser.getNextImageID());
        }
        return ids;
    }

    public int getQuestionID() {
        return questionID;
    }

    public int getClassID(){
        return this.classID;
    }

    public void setClassID(int classID){
        this.classID = classID;
    }

    public int getSubjectID(){
        return this.subjectID;
    }

    public void setSubjectID(int subjectID){
        this.subjectID = subjectID;
    }

    public int getMainTestingID(){
        return this.mainTestingID;
    }

    public void setMainTestingID(int mainTestingID){
        this.mainTestingID = mainTestingID;
    }

    public void setSubjectName(String subjectName){
        this.subjectName = subjectName;
    }

    public String getSubjectName(){
        return this.subjectName;
    }
}
