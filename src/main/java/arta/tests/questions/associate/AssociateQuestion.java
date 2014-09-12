package arta.tests.questions.associate;

import arta.tests.questions.Question;
import arta.tests.common.TestMessages;
import arta.tests.common.ImagesIDParser;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Log;
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

public class AssociateQuestion implements Question{

    public int classID;
    public int subjectID;
    public int mainTestingID;
    public String subjectName;

    private int questionNumber = -1;

    public String question = "";
    public int questionID = 0;
    public int difficulty = 0;
    int lang;
    public ArrayList <SingleAssociateTag> tags = new ArrayList <SingleAssociateTag> ();
    private String errorString  = "";
    ServletContext servletContext;

    private boolean isRightAnswer = false;

    public AssociateQuestion(ServletContext servletContext,  int lang, boolean init) {
        this.servletContext = servletContext;
        this.lang = lang;
        if (init){
            for (int i=0; i<5; i++){
                tags.add(new SingleAssociateTag());
            }
        }
    }

    public AssociateQuestion() {
    }

    public int getQuestionType() {
        return ASSOCIATE_QUESTION;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public boolean saveQuestion(Statement st, ResultSet res, int testID, StringTransform trsf) {
        boolean saved = false;
        try{

            StringTransform stringTransform = new StringTransform();

            if (question == null || question.length()==0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.QUESTION_NOT_DEFINED, properties);
                return false;
            }

            question = stringTransform.getDBString(question);

            st.execute("INSERT INTO questions (question, questionTypeID, difficulty, testID) " +
                    " VALUES ('"+trsf.getDBString(question, Varchar.QUESTION)+"', "+ASSOCIATE_QUESTION+", "+difficulty+", "+testID+")",
                    Statement.RETURN_GENERATED_KEYS);
            res = st.getGeneratedKeys();
            if (res.next())
                questionID = res.getInt(1);
            else
                questionID = 0;

            if (questionID <= 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                return false;
            }

            for (int i=tags.size()-1; i>=0; i--){
                if ((tags.get(i).firstTag==null || tags.get(i).firstTag.length() == 0) &&
                        (tags.get(i).secondTag == null || tags.get(i).secondTag.length() == 0))
                    tags.remove(i);
            }

            if (tags.size() == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.NO_VARIANTS_FOUND, properties);
                return false;
            }

            for (int i=0; i<tags.size(); i++){
                tags.get(i).firstTag = stringTransform.getDBString(tags.get(i).firstTag);
                tags.get(i).secondTag = stringTransform.getDBString(tags.get(i).secondTag);
                st.execute("INSERT INTO associatefirsttag (questionID, tag) " +
                        " VALUES ("+questionID+", '"+trsf.getDBString(tags.get(i).firstTag, Varchar.QUESTION)+"')",
                         Statement.RETURN_GENERATED_KEYS );
                res = st.getGeneratedKeys();
                if (res.next()){
                    tags.get(i).firstTagID = res.getInt(1);
                } else {
                    tags.get(i).firstTagID = 0;
                }
                if (tags.get(i).firstTagID <= 0){
                    Properties properties = new Properties();
                    properties.setProperty("question", (questionNumber+1)+"");
                    errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                    return false;
                }
                st.execute("INSERT INTO associatesecondtag (firstTagID,  tag) " +
                        " VALUES ("+tags.get(i).firstTagID+", '"+trsf.getDBString(tags.get(i).secondTag, Varchar.QUESTION)+"') ",
                        Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    tags.get(i).secondTagID = res.getInt(1);
                } else {
                    tags.get(i).secondTagID = 0;
                }
                if (tags.get(i).secondTagID <= 0){
                    Properties properties = new Properties();
                    properties.setProperty("question", (questionNumber+1)+"");
                    errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                    return false;
                }
            }
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
        AssociateQuestionHandler handler = new AssociateQuestionHandler(this, servletContext, tinyMce, lang);
        FileReader fileReader= new FileReader("tests/questions/associate/edit.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void writeQuestionForCheck(PrintWriter pw, int lang, ServletContext servletContext) {
        System.out.println("writeQuestionForCheck  :::  arta.tests.questions.associate ");
        FileReader fileReader = new FileReader("tests/questions/associate/check.txt");
        AssociateCheckHandler handler = new AssociateCheckHandler(this, lang, servletContext);
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void setRightAnswerForQuestion(Statement st, boolean _isRight){
        AssociateQuestion q = new AssociateQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
    }

    public boolean checkAnswer(Statement st) {
        AssociateQuestion q = new AssociateQuestion();
        q.setQuestionID(questionID);
        q.loadQuestion(st);
        isRightAnswer = this.equals(q);
        for (int i = 0; i < q.tags.size(); i++){
            for (int j = 0; j < tags.size(); j++){
                if (tags.get(j).firstTagID == q.tags.get(i).firstTagID){
                    tags.get(j).firstRightNumber = i + 1;
                }
                if (tags.get(j).secondTagID == q.tags.get(i).secondTagID){
                    tags.get(j).secondRightNumber = i + 1;
                }
            }
        }
        return isRightAnswer;
    }

    public void loadQuestion(Statement st) {
        try{

            ResultSet res = st.executeQuery("SELECT questions.question as q, " +
                    " questions.difficulty as d FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }

            res = st.executeQuery("SELECT associatefirsttag.tagID AS fID, " +
                    " associatefirsttag.tag AS ft, " +
                    " associatesecondtag.tagID AS sID, " +
                    " associatesecondtag.tag AS st " +
                    " FROM associatefirsttag LEFT JOIN associatesecondtag ON" +
                        " associatefirsttag.tagID=associatesecondtag.firsttagID " +
                    " WHERE associatefirsttag.questionID="+questionID);
            while (res.next()){
                SingleAssociateTag tag = new SingleAssociateTag();
                tag.firstTag = res.getString("ft");
                tag.firstTagID = res.getInt("fID");
                tag.secondTag = res.getString("st");
                tag.secondTagID = res.getInt("sID");
                tags.add(tag);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
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

            DataExtractor extractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                if (name.length()>9 && name.substring(0,9).equals("firstTag=")){
                    int number = extractor.getInteger(name.substring(9, name.length()));
                    if (number>=0 && number<tags.size()){
                        tags.get(number).firstTag = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                    }
                } else if (name.length()>10 && name.substring(0, 10).equals("secondTag=")){
                    int number = extractor.getInteger(name.substring(10, name.length()));
                    if (number>=0 && number<tags.size()){
                        tags.get(number).secondTag = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
                    }
                } else if (name.equals("question")){
                    question = new String(data.getParameter(name).getBytes(Encoding.ISO), Encoding.UTF);
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
        pw.println("function removeVariant(name, number){");
        pw.println("if (confirm(\""+MessageManager.getMessage(lang, TestMessages.DO_YOU_REALLY_WANT_TO_DELETE_VARIANT, null)+
                "\"+name+\" ?\")){" +
                "form.option.value=-2;" +
                "form.variant.value=number;" +
                "form.submit();" +
                "}" +
                "return false;");
        pw.println("\t}");
        pw.println("</script>");
    }

    public void parseCheckParameters(Object[] names, ParsedData data) {
        try{
            DataExtractor extractor = new DataExtractor();
            for (int i=0; i<names.length; i++){
                String name = "";
                if (names[i]!=null)
                    name = names[i].toString();
                String value = data.getParameter(name);
                if (name.length()>4 && name.substring(0,4).equals("id1=")){
                    int number = extractor.getInteger(name.substring(4, name.length()));
                    for (int k=0; k<tags.size(); k++){
                        if (tags.get(k).firstTagID==number)
                            tags.get(k).firstTagNumber = extractor.getInteger(value);
                    }
                } else if (name.length()>4 && name.substring(0, 4).equals("id2=")){
                    int number = extractor.getInteger(name.substring(4, name.length()));
                    for (int k=0; k<tags.size(); k++){
                        if (tags.get(k).secondTagID==number)
                            tags.get(k).secondTagNumber = extractor.getInteger(value);
                    }
                }
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public void loadForCheck(Statement st, ResultSet res) {
        try{
            loadQuestion(st);
            for (int i=0; i<(int)tags.size()/2; i++){
                int rand = (int) (Math.random()*tags.size());
                Object tmp = tags.get(i);
                tags.set(i, tags.get(rand));
                tags.set(rand, (SingleAssociateTag) tmp);
            }

            for (int i=0; i<(int)tags.size()/2; i++){
                int rand = (int) (Math.random()*tags.size());
                String tagTmp = tags.get(rand).secondTag;
                int tagID = tags.get(rand).secondTagID;
                tags.get(rand).secondTag = tags.get(i).secondTag;
                tags.get(rand).secondTagID = tags.get(i).secondTagID;
                tags.get(i).secondTag = tagTmp;
                tags.get(i).secondTagID = tagID;
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public boolean equals(Object obj) {
        AssociateQuestion q = (AssociateQuestion) obj;
        for (int i=0; i<q.tags.size(); i++){
            for (int j=0; j<tags.size(); j++){
                if (q.tags.get(i).firstTagID == tags.get(j).firstTagID){
                    for (int k=0; k<tags.size(); k++){
                        if (tags.get(k).secondTagNumber==tags.get(j).firstTagNumber &&
                                tags.get(k).secondTagID!=q.tags.get(i).secondTagID)
                            return false;
                    }
                }
            }
        }
        return true;
    }

    public void writeQuestionForView(PrintWriter pw, int lang, ServletContext servletContext) {
        AssociateQuestionViewHandler handler = new AssociateQuestionViewHandler(this, lang, servletContext);
        FileReader fileReader = new FileReader("tests/questions/associate/view.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public boolean isRightAnswer() {
        return isRightAnswer;
    }

    public String getReportVariants() {
        StringBuffer str = new StringBuffer("<table border=1 width=100% class=\"table\">");
        for (int i=0; i<tags.size(); i++){
            str.append("<tr><td width=\"50%\">");
            str.append("<table border=1 width=\"100%\" class=\"table\"><tr><td width=\"20px\" align=\"center\" " +
                    " valign=\"top\" >");
            str.append(tags.get(i).firstTagNumber);
            str.append("</td><td width=\"20px\" valign=\"top\" align=\"center\" bgcolor=\""+Question.RIGHT_VARIANT_BACKGROUND+"\">");
            str.append(tags.get(i).firstRightNumber);
            str.append("</td><td>");
            str.append(tags.get(i).firstTag);
            str.append("</td></tr></table>");

            str.append("</td><td width=\"*\">");

            str.append("<table border=1 width=\"100%\" class=\"table\"><tr><td width=\"20px\" align=\"center\" " +
                    " valign=\"top\" >");
            str.append( tags.get(i).secondTagNumber);
            str.append("</td><td width=\"20px\" align=\"center\" valign=\"top\" bgcolor=\""+Question.RIGHT_VARIANT_BACKGROUND+"\">");
            str.append(tags.get(i).secondRightNumber);
            str.append("</td><td >");
            str.append(tags.get(i).secondTag);
            str.append("</td></tr></table></td></tr>");
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
            cell0.setCellValue(new HSSFRichTextString(tags.get(i).firstTagNumber + ""));
            cell1.setCellValue(new HSSFRichTextString(tags.get(i).firstRightNumber + ""));
            cell2.setCellValue(new HSSFRichTextString(trsf.getXLSString(tags.get(i).firstTag)));
            cell3.setCellValue(new HSSFRichTextString(tags.get(i).secondTagNumber + ""));
            cell4.setCellValue(new HSSFRichTextString(tags.get(i).secondRightNumber + ""));
            cell5.setCellValue(new HSSFRichTextString(trsf.getXLSString(tags.get(i).secondTag)));
        }

        ((StringBuffer)maxRow).delete(0, ((StringBuffer)maxRow).length());
        ((StringBuffer)maxRow).append(max);        
    }

    public ArrayList <Integer> getUsedImagesID() {
        ImagesIDParser parser = new ImagesIDParser();
        ArrayList <Integer> ids = new ArrayList <Integer> ();
        for (int i=0; i<tags.size(); i++){
            parser.setParserString(tags.get(i).firstTag);
            while (parser.hasMoreImages()){
                ids.add(parser.getNextImageID());
            }
            parser.setParserString(tags.get(i).secondTag);
            while (parser.hasMoreImages()){
                ids.add(parser.getNextImageID());
            }
        }
        parser.setParserString(question);
        while (parser.hasMoreImages()){
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

