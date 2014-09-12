package arta.tests.questions.open;

import arta.tests.questions.Question;
import arta.tests.common.TestMessages;
import arta.tests.common.ImagesIDParser;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Log;
import arta.common.logic.util.Encoding;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.TinyMce;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import com.bentofw.mime.ParsedData;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;


public class OpenQuestion implements Question, Serializable {

    public int classID;
    public int subjectID;
    public int mainTestingID;
    public String subjectName;

    public String question = "";
    public int questionID = 0;
    private int questionNumber = 0;
    public int difficulty = 0;
    private String errorString = "";    
    public ArrayList <SingleAnswer> answers = new ArrayList <SingleAnswer> ();
    
    ServletContext servletContext;
    private int type;
    int lang;

    private boolean isRight = false;

    public OpenQuestion(ServletContext servletContext, int type, int lang,
                        boolean init) {
        this.servletContext = servletContext;
        this.type = type;
        this.lang = lang;
        if (init){
            for (int i=0; i<5; i++){
                answers.add(new SingleAnswer());
            }
        }
    }

    public OpenQuestion(int type) {
        this.type = type;
    }

    public int getQuestionType() {
        return type;
    }

    public boolean saveQuestion(Statement st, ResultSet res, int testID, StringTransform trsf) {

        boolean saved = false;
        StringTransform stringTransform = new StringTransform();
        try{

            if (question == null || question.length() ==0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.QUESTION_NOT_DEFINED, properties);
                return false;
            }

            question = stringTransform.getDBString(question);

            st.execute("INSERT INTO questions (question, questionTypeID, difficulty, testID) " +
                    " VALUES ('"+trsf.getDBString(question, Varchar.QUESTION)+"', "+type+", "+difficulty+", "+testID+") ",
                    Statement.RETURN_GENERATED_KEYS);

            res = st.getGeneratedKeys();

            if (res.next()){
                questionID = res.getInt(1);
            } else {
                questionID = 0;
            }

            if (questionID<=0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.FAILED_TO_SAVE_QUESTION, properties);
                return false;
            }

            int rightAnswersCount = 0;

            for (int i=answers.size()-1; i>=0; i--){
                if (answers.get(i).answer == null || answers.get(i).answer.length() == 0)
                    answers.remove(i);
            }

            if (answers.size() == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.NO_VARIANTS_FOUND, properties);
                return false;
            }

            for (int i=0; i<answers.size(); i++){
                answers.get(i).answer = stringTransform.getDBString(answers.get(i).answer);
                if (answers.get(i).isRight)
                    rightAnswersCount ++ ;
                if (type == OPEN_WITH_SINGLE_ANSWER_QUESTIONS && rightAnswersCount > 1){
                    Properties properties = new Properties();
                    properties.setProperty("question", (questionNumber+1)+"");
                    errorString = MessageManager.getMessage(lang, TestMessages.ONLY_ONE_RIGHT_ANSWER_POSSIBLE, properties);
                    return false;
                }
                int right = 0;
                if (answers.get(i).isRight)
                    right = 1;
                st.execute("INSERT INTO openanswers (questionID, answer, isright) " +
                        " VALUES ("+questionID+", '"+trsf.getDBString(answers.get(i).answer, Varchar.QUESTION)+"', "+right+") ");
            }
            if (rightAnswersCount == 0){
                Properties properties = new Properties();
                properties.setProperty("question", (questionNumber+1)+"");
                errorString = MessageManager.getMessage(lang, TestMessages.NO_RIGHT_ANSWER_FOUND, properties);
                return false;
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
        OpenQuestionHandler handler = new OpenQuestionHandler(tinyMce, lang, this, servletContext);
        FileReader fileReader = new FileReader("tests/questions/open/edit.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public void writeQuestionForCheck(PrintWriter pw, int lang, ServletContext servletContext) {
        OpenQuestionCheckHandler handler = new OpenQuestionCheckHandler(lang, this, servletContext);
        FileReader fileReader = new FileReader("tests/questions/open/check.txt");
        Parser parser = new Parser(fileReader.read(servletContext), pw, handler);
        parser.parse();
    }

    public void setRightAnswerForQuestion(Statement st, boolean _isRight){
        OpenQuestion q = new OpenQuestion(type);
        q.setQuestionID(questionID);
        q.loadQuestion(st);

        for (int i = 0; i < answers.size(); i++){
            answers.get(i).wasSelected = false;
            answers.get(i).isRight = false;
        }

        for (int i = 0; i < q.answers.size(); i++){
            if(_isRight){
                if(q.answers.get(i).isRight){
                    for (int j = 0; j < answers.size(); j++){
                        if (answers.get(j).answerID == q.answers.get(i).answerID){
                            answers.get(j).wasSelected = true;
                            answers.get(j).answerID = q.answers.get(i).answerID;
                            answers.get(j).isRight = q.answers.get(i).isRight;
                            break;
                        }
                    }
                }
            } else {
                if(!q.answers.get(i).isRight){
                    for (int j = 0; j < answers.size(); j++){
                        if (answers.get(j).answerID == q.answers.get(i).answerID){
                            answers.get(j).wasSelected = true;
                            answers.get(j).answerID = q.answers.get(i).answerID;
                            answers.get(j).isRight = q.answers.get(i).isRight;
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean checkAnswer(Statement st) {

        OpenQuestion q = new OpenQuestion(type);
        q.setQuestionID(questionID);
        q.loadQuestion(st);
        isRight = this.equals(q);

        for (int i = 0; i < answers.size(); i++){
            answers.get(i).wasSelected = answers.get(i).isRight;
            answers.get(i).isRight = false;
        }

        for (int i = 0; i < answers.size(); i++){
            for (int j = 0; j < q.answers.size(); j++){
                if (answers.get(i).answerID == q.answers.get(j).answerID){
                    answers.get(i).isRight = q.answers.get(j).isRight;
                    break;
                }
            }            
        }
        return isRight;
    }

    public void loadQuestion(Statement st) {
        try{

            ResultSet res = st.executeQuery("SELECT questions.question as q, " +
                    " questions.difficulty as d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }
            res = st.executeQuery("SELECT openanswers.answer as a," +
                    " openanswers.answerID as id, " +
                    " openanswers.isRight as r " +
                    " FROM openanswers WHERE openanswers.questionID="+questionID);
            while (res.next()){
                SingleAnswer answer = new SingleAnswer();
                answer.answer = res.getString("a");
                answer.answerID = res.getInt("id");
                answer.isRight = res.getInt("r")==1;
                answers.add(answer);
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
        DataExtractor extractor = new DataExtractor();
        for (int i=0; i<answers.size(); i++){
            answers.get(i).isRight = false;
        }
        for (int i=0; i<names.length; i++){
            String name = "";
            if (names[i] != null)
                name = names[i].toString();
            if (name.length()>6 && name.substring(0, 6).equals("right=")){
                int id = extractor.getInteger(name.substring(6, name.length()));
                if (id>=0 && id<answers.size())
                    answers.get(id).isRight  = true;
            } else if (name.length()>8 && name.substring(0, 8).equals("variant=")){
                try{
                    int id = extractor.getInteger(name.substring(8, name.length()));
                    if (id>=0 && id<answers.size()){
                        String tmp = data.getParameter(name);
                        if (tmp == null)
                            continue;
                        tmp = new String(tmp.getBytes(Encoding.ISO), Encoding.UTF);
                        answers.get(id).answer = tmp;
                    }
                }catch(Exception exc){
                    Log.writeLog(exc);
                }
            } else if (name.equals("diff")){
                difficulty = extractor.getInteger(data.getParameter(name));
            } else if (name.equals("question")){
                try{
                    String tmp = data.getParameter(name);
                    if (tmp == null)
                        continue;
                    tmp = new String(tmp.getBytes(Encoding.ISO), Encoding.UTF);
                    question = tmp;
                } catch (Exception exc){
                    Log.writeLog(exc);
                }
            } else if (name.equals("right")){
                if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                    int tmp = extractor.getInteger(data.getParameter(name));
                    if (tmp>=0 && tmp<answers.size())
                        answers.get(tmp).isRight = true;
                }
            }
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
        DataExtractor extractor = new DataExtractor();
        for (int i=0; i<answers.size(); i++){
            answers.get(i).isRight = false;
        }
        for (int i=0; i<names.length; i++){
            String name = "";
            if (names[i] != null)
                name = names[i].toString();
            if (name.length()>4 && name.substring(0, 4).equals("var=") && type == OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                int id = extractor.getInteger(name.substring(4, name.length()));
                if (id>=0 && id<answers.size()) answers.get(id).isRight  = true;
            } else if (name.equals("var") && type == OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                int id = extractor.getInteger(data.getParameter(name));
                if (id>=0 && id<answers.size()) answers.get(id).isRight = true;
            }
        }
    }

    public void loadForCheck(Statement st, ResultSet res) {
        try{
            res = st.executeQuery("SELECT questions.question as q, " +
                    " questions.difficulty as d " +
                    " FROM questions WHERE questions.questionID="+questionID);
            if (res.next()){
                question = res.getString("q");
                difficulty = res.getInt("d");
            }
            res = st.executeQuery("SELECT openanswers.answer as a," +
                    " openanswers.answerID as id " +
                    " FROM openanswers WHERE openanswers.questionID="+questionID);
            while (res.next()){
                SingleAnswer answer = new SingleAnswer();
                answer.answer = res.getString("a");
                answer.answerID = res.getInt("id");
                answers.add(answer);
            }

            for (int i=0; i<(int)answers.size()/2; i++){
                int rand = (int) (Math.random()*answers.size());
                SingleAnswer tmp = answers.get(rand);
                answers.set(rand, answers.get(i));
                answers.set(i, tmp);
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public boolean equals(Object obj) {
        OpenQuestion q = (OpenQuestion) obj;
        for (int i=0; i<q.answers.size(); i++){
            for (int j=0; j<answers.size(); j++){
                if (q.answers.get(i).answerID==answers.get(j).answerID)
                    if (answers.get(j).isRight != q.answers.get(i).isRight)
                        return false;
            }
        }
        return true;
    }

    public void writeQuestionForView(PrintWriter pw, int lang, ServletContext servletContext) {
        OpenQuestionViewHandler handler = new OpenQuestionViewHandler(this, lang, servletContext);
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
        for (int i=0; i<answers.size(); i++){
            str.append("<tr>");
            str.append("<td width=\"10px\" align=\"center\" valign=\"middle\" ");
            if (answers.get(i).isRight){
                str.append(" bgcolor=\"");
                str.append(Question.RIGHT_VARIANT_BACKGROUND);
                str.append("\" ");
            }
            str.append(">");
            if (answers.get(i).isRight){
                str.append("<img src=\"images/plus.gif\" width=\"10px\" height=\"10px\">");
            }

            str.append("<td width=\"10px\" ");

            if (answers.get(i).isRight){
                str.append(" bgcolor=\"");
                str.append(Question.RIGHT_VARIANT_BACKGROUND);
                str.append("\" ");
            }

            str.append(">");
            String chck = "";
            if (answers.get(i).wasSelected){
                chck = " checked ";
            }
            if (type == OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                str.append("<input type=\"checkbox\" "+chck+">");
            } else {
                str.append("<input type=\"radio\" "+chck+">");
            }
            str.append("</td><td");
            if (answers.get(i).isRight){
                str.append(" bgcolor=\"");
                str.append(Question.RIGHT_VARIANT_BACKGROUND);
                str.append("\" ");
            }
            str.append(">");
            str.append(answers.get(i).answer);
            str.append("</td></tr>");
        }
        str.append("</table>");
        return str.toString();
    }


    public void addToXLSReport(HSSFSheet sheet, Object maxRow,
                               DataExtractor extractor, int lang, HSSFCellStyle right, HSSFCellStyle wrong,
                               HSSFCellStyle student, HSSFCellStyle system, int number) {
        StringTransform trsf = new StringTransform();
        int max = extractor.getInteger(maxRow);
        HSSFRow empty = sheet.createRow(++max);

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

        for (int i = 0; i < answers.size(); i++){
            HSSFRow row = sheet.createRow(++max);
            sheet.addMergedRegion(new Region((short)max, (short)2, (short)max,  (short)5));
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

            if (answers.get(i).isRight){
                cell0.setCellValue(new HSSFRichTextString("+"));
            }

            if (answers.get(i).wasSelected){
                cell1.setCellValue(new HSSFRichTextString("+"));
            }

            cell2.setCellValue(new HSSFRichTextString(trsf.getXLSString(answers.get(i).answer)));
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
        for (int i=0; i<answers.size(); i++){
            parser.setParserString(answers.get(i).answer);
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
