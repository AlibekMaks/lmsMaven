package arta.tests.questions.sequence;

import arta.tests.questions.Question;
import arta.tests.common.TestMessages;
import arta.tests.common.ImagesIDParser;
import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Encoding;
import arta.common.logic.messages.MessageManager;
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


public class SequenceQuestion implements Question{

    public int classID;
    public int subjectID;
    public int mainTestingID;
    public String subjectName;

    public String question;
    public int questionID;
    public ArrayList <SingleSequenceTag> tags = new ArrayList <SingleSequenceTag> ();
    public int difficulty ;
    public String errorString = "";

    private int number;


    ServletContext servletContext;
    int lang;

    private boolean isRight = false;

    public SequenceQuestion(ServletContext servletContext, int lang,
                            boolean init) {
        this.servletContext = servletContext;
        this.lang = lang;
        if (init){
            for (int i=0; i<5; i++){
                tags.add(new SingleSequenceTag());
            }
        }
    }

    public SequenceQuestion() {
    }

    public int getQuestionType() {
        return SEQUENCE_QUESTION;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public boolean saveQuestion(Statement st, ResultSet res, int testID, StringTransform trsf) {
        boolean saved = false;
        try{

            if (question == null || question.length() == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (number+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.QUESTION_NOT_DEFINED, properties);
                return false;
            }

            st.execute("INSERT INTO questions (question, questionTypeID, difficulty, testID) " +
                    " VALUES ('"+trsf.getDBString(question, Varchar.QUESTION)+"', "+SEQUENCE_QUESTION+", "+difficulty+", "+testID+")",
                     Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next()){
                questionID = res.getInt(1);
            } else {
                questionID = 0;
            }

            if (questionID <=0 ){
                Properties properties = new Properties();
                properties.setProperty("question", (number+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                return false;
            }

            for (int i=tags.size()-1; i>=0; i--){
                if (tags.get(i).tag == null || tags.get(i).tag.length()==0)
                    tags.remove(i);
            }

            if (tags.size() == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (number+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.NO_VARIANTS_FOUND, properties);
                return false;
            }

            for (int i=0; i<tags.size(); i++){
                st.execute("INSERT INTO sequencetags (questionID, tag, number) " +
                        " VALUES ("+questionID+", '"+trsf.getDBString(tags.get(i).tag, Varchar.QUESTION)+"', " 
                        +tags.get(i).number+")");
            }
            saved = true;
        } catch (Exception exc){
            Log.writeLog(exc);
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
        for (int i=0; i<tags.size()-1; i++){
            for (int j=i+1; j<tags.size(); j++){
                if (tags.get(j).number < tags.get(i).number){
                    SingleSequenceTag tmp = tags.get(i);
                    tags.set(i, tags.get(j));
                    tags.set(j, tmp);
                }
            }
        }
        SequenceQuestionHandler handler = new SequenceQuestionHandler(lang, servletContext, tinyMce, this);
        FileReader fileReader = new FileReader("tests/questions/sequence/edit.txt");
        Parser parser = new Parser(fileReader.read(servletContext) ,pw, handler);
        parser.parse();
    }

    public void writeQuestionForCheck(PrintWriter pw, int lang, ServletContext servletContext) {
        System.out.println("writeQuestionForCheck  :::  arta.tests.questions.sequence ");
        SequenceQuestionCheckHandler handler = new SequenceQuestionCheckHandler(lang, this, servletContext);
        FileReader fileReader = new FileReader("tests/questions/sequence/check.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void setRightAnswerForQuestion(Statement st, boolean _isRight){
        SequenceQuestion q = new SequenceQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
    }

    public boolean checkAnswer(Statement st) {
        SequenceQuestion q = new SequenceQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
        for (int i = 0; i < q.tags.size(); i++){
            for (int j = 0; j < tags.size(); j++){
                if (tags.get(j).tagID == q.tags.get(i).tagID){
                    tags.get(j).rightNumber = q.tags.get(i).number;
                    break;
                }
            }
        }
        isRight = this.equals(q);
        return isRight;
    }

    public void loadQuestion(Statement st) {
        try{
            ResultSet res = st.executeQuery("SELECT questions.question AS q, " +
                    " questions.difficulty AS d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }

            res = st.executeQuery("SELECT sequencetags.tagID AS id, " +
                    " sequencetags.number as n, " +
                    " sequencetags.tag as t  " +
                    " FROM sequencetags WHERE sequencetags.questionID="+questionID+" " +
                    " ORDER BY sequencetags.number");
            while (res.next()){
                SingleSequenceTag tag = new SingleSequenceTag();
                tag.tagID = res.getInt("id");
                tag.number = res.getInt("n");
                tag.tag = res.getString("t");
                tags.add(tag);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void parseParameters(Object[] names, ParsedData data) {
        try{
            DataExtractor extractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                if (name.length()>8 && name.substring(0,8).equals("variant=")){
                    int tmp = extractor.getInteger(name.substring(8, name.length()));
                    if (tmp>=0 && tmp<tags.size()){
                        tags.get(tmp).tag = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                    }
                } else if (name.length()>7 && name.substring(0, 7).equals("number=")){
                    int tmp = extractor.getInteger(name.substring(7, name.length()));
                    if (tmp>=0 && tmp<tags.size()){
                        tags.get(tmp).number = extractor.getInteger(data.getParameter(name));
                    }
                } else if (name.equals("question")){
                    question = new String (data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                } else if (name.equals("diff")){
                    difficulty = extractor.getInteger(data.getParameter(name));
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void getScript(PrintWriter pw) {
        pw.println("<script language=\"javascript\">");
        pw.println("\tfunction removeVariant(name, number){");
        pw.println("if (confirm(\""+MessageManager.getMessage(lang, TestMessages.DO_YOU_REALLY_WANT_TO_DELETE_VARIANT, null)+
                "\"+name+\" ?\")){" +
                "form.option.value=-2;" +
                "form.variant.value=number;" +
                "form.submit();" +
                "}");
        pw.println("return false;}");
        pw.println("</script>");
    }

    public void parseCheckParameters(Object[] names, ParsedData data) {
        try{
            DataExtractor extractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                if (name.length()>4 && name.substring(0,4).equals("var=")){
                    int tmp = extractor.getInteger(name.substring(4, name.length()));
                    if (tmp>=0 && tmp<tags.size()){
                        tags.get(tmp).number = extractor.getInteger(data.getParameter(name));
                    }
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void loadForCheck(Statement st, ResultSet res) {
        try{
            res = st.executeQuery("SELECT questions.question AS q, " +
                    " questions.difficulty AS d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }

            res = st.executeQuery("SELECT sequencetags.tagID AS id, " +
                    " sequencetags.tag as t  " +
                    " FROM sequencetags WHERE sequencetags.questionID="+questionID);
            while (res.next()){
                SingleSequenceTag tag = new SingleSequenceTag();
                tag.tagID = res.getInt("id");
                tag.tag = res.getString("t");
                tags.add(tag);
            }
            for (int i=0; i<(int)tags.size()/2; i++){
                int rand = (int) (Math.random()*tags.size());
                SingleSequenceTag tmp = tags.get(rand);
                tags.set(rand, tags.get(i));
                tags.set(i, tmp);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public boolean equals(Object obj) {
        SequenceQuestion q = (SequenceQuestion) obj;
        for (int i=0; i<q.tags.size(); i++){
            for (int j=0; j<tags.size(); j++){
                if (q.tags.get(i).tagID == tags.get(j).tagID){
                    if (q.tags.get(i).number != tags.get(j).number)
                        return false;
                    else
                        break;
                }
            }
        }
        return true;
    }

    public void writeQuestionForView(PrintWriter pw, int lang, ServletContext servletContext) {
        SequenceQuestionViewHandler  handler = new SequenceQuestionViewHandler(this, lang, servletContext);
        FileReader fileReader = new FileReader("tests/questions/sequence/view.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public boolean isRightAnswer() {
        return isRight;
    }

    public String getReportVariants() {
        StringBuffer str = new StringBuffer();
        str.append("<table border=1 width=\"100%\" class=\"table\">");
        for (int i=0; i<tags.size(); i++){
            str.append("<tr><td width=\"20px\" align=\"center\" valign=\"top\" >"+
                    tags.get(i).number+"</td>");
            str.append("<td width=\"20px\" align=\"center\" valign=\"top\" bgcolor=\""+Question.RIGHT_VARIANT_BACKGROUND+"\">");
            str.append(tags.get(i).rightNumber);
            str.append("</td>");
            str.append("<td class=\"TDtext\">"+tags.get(i).tag+"</td></tr>");
        }
        str.append("</table>");
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

        for (int i = 0; i < tags.size(); i++){
            HSSFRow row = sheet.createRow(++max);
            HSSFCell cell0 = row.createCell((short)0);
            HSSFCell cell1 = row.createCell((short)1);
            HSSFCell cell2 = row.createCell((short)2);
            HSSFCell cell3 = row.createCell((short)3);
            HSSFCell cell4 = row.createCell((short)4);
            HSSFCell cell5 = row.createCell((short)5);
            cell0.setCellStyle(student);
            cell1.setCellStyle(student);
            cell2.setCellStyle(student);
            cell3.setCellStyle(student);
            cell4.setCellStyle(student);
            cell5.setCellStyle(student);
            sheet.addMergedRegion(new Region((short)max, (short)2, (short)max,  (short)5));
            cell0.setCellValue(new HSSFRichTextString(tags.get(i).number + ""));
            cell1.setCellValue(new HSSFRichTextString(tags.get(i).rightNumber + ""));
            cell2.setCellValue(new HSSFRichTextString(trsf.getXLSString(tags.get(i).tag)));
        }

        ((StringBuffer)maxRow).delete(0, ((StringBuffer)maxRow).length());
        ((StringBuffer)maxRow).append(max);
    }

    public ArrayList<Integer> getUsedImagesID() {
        ArrayList <Integer> ids = new ArrayList <Integer> ();
        ImagesIDParser parser = new ImagesIDParser();
        parser.setParserString(question);
        while (parser.hasMoreImages()){
            ids.add(parser.getNextImageID());
        }
        for (int i=0; i<tags.size(); i++){
            parser.setParserString(tags.get(i).tag);
            while (parser.hasMoreImages()){
                ids.add(parser.getNextImageID());
            }
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
