package arta.tests.questions;

import com.bentofw.mime.ParsedData;

import javax.servlet.ServletContext;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.util.ArrayList;

import arta.common.logic.util.StringTransform;
import arta.common.logic.util.DataExtractor;
import arta.common.hssf.HSSFStyles;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;

public interface Question {

    public static String UNSELCOLOR = "LightSkyBlue"; //"#ffffff";
    public static String SELCOLOR = "#cfcfcf";
    public static String RIGHT_CHCK_COLOR = "#c0c0c0";
    public static String RIGHT_VAR_COLOR = "#cfcfcf";

    public static String RIGHT_VARIANT_BACKGROUND = "#cfcfcf";

    public static int OPEN_WITH_PLURAL_ANSWERS_QUESTIONS = 1;
    public static int OPEN_WITH_SINGLE_ANSWER_QUESTIONS = 2;
    public static int ASSOCIATE_QUESTION = 3;
    public static int SEQUENCE_QUESTION = 4;
    public static int CLOSED_QUESTION = 5;

    public void setQuestionID(int questionID);

    public int getQuestionID();

    public int getQuestionType();

    public boolean saveQuestion(Statement st, ResultSet res, int testID, StringTransform trsf);

    public boolean checkQuestion();

    public String getError();

    public void writeQuestionForEdit(PrintWriter pw, int lang);

    public void writeQuestionForCheck(PrintWriter pw, int lang, ServletContext servletContext);

    public void writeQuestionForView(PrintWriter pw, int lang, ServletContext servletContext);

    public void setRightAnswerForQuestion(Statement st, boolean _isRight);

    public boolean checkAnswer(Statement st);

    public void loadQuestion(Statement st);

    public int getDifficulty();

    public void setDifficulty(int difficulty);

    public String getQuestion();

    public int getNumber();

    public void setNumber(int number);

    public void setQuestion(String question);

    public void parseParameters(Object[] names, ParsedData data);

    public void parseCheckParameters(Object[] names, ParsedData data);

    public void getScript(PrintWriter pw);

    public void loadForCheck(Statement st, ResultSet res);

    public boolean isRightAnswer();

    public String getReportVariants();

    public ArrayList <Integer> getUsedImagesID();

    public void addToXLSReport(HSSFSheet sheet, Object maxRow, DataExtractor extractor, int lang, HSSFCellStyle right, HSSFCellStyle wrong, HSSFCellStyle student, HSSFCellStyle system, int number);

    public int getClassID();

    public void setClassID(int classID);

    public int getSubjectID();

    public String getSubjectName();

    public void setSubjectName(String subjectName);

    public void setSubjectID(int subjectID);

    public int getMainTestingID();

    public void setMainTestingID(int mainTestingID);

}
