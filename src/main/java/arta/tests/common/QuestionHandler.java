package arta.tests.common;

import arta.subjects.logic.SubjectsSelect;
import arta.tests.questions.Question;
import arta.tests.test.list.TestsSearchParams;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.TinyMce;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class QuestionHandler extends TemplateHandler {

    Question question;
    int questionNumber;
    String testName;
    int testSubjectID;
    TinyMce tinyMce;
    int lang;
    int questionsCount;
    int testID;
    ServletContext servletContext;
    TestsSearchParams params;
    String testSignature;

    public QuestionHandler(Question question, int questionNumber,
                           TinyMce tinyMce, int lang, int questionsCount, String testName, int testSubjectID,
                           int testID, ServletContext servletContext, TestsSearchParams params,
                           String testSignature) {
        this.question = question;
        this.questionNumber = questionNumber;
        this.tinyMce = tinyMce;
        this.lang = lang;
        this.questionsCount = questionsCount;
        this.testName = testName;
        this.testSubjectID = testSubjectID;
        this.testID = testID;
        this.servletContext = servletContext;
        this.params = params;
        this.testSignature = testSignature;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("questionNumber")){
            pw.print(questionNumber);
        } else if (name.equals("go to")){
            pw.print(MessageManager.getMessage(lang, TestMessages.GO_TO, null));
        } else if (name.equals("question numberselect")){
            NumberSelect select = new NumberSelect(lang);
            select.writeQuestionNumberPostSelect(pw,  "questionNumber", questionNumber, questionsCount, 100, "form.submit();");
        } else if (name.equals("question")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION, null));
        } else if (name.equals("add question")){
            pw.print(MessageManager.getMessage(lang, TestMessages.ADD_QUESTION, null));
        } else if (name.equals("delete question")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DELETE_QUESTION, null));
        } else if (name.equals("question type")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_TYPE, null));
        } else if (name.equals("question type select")){
//            QuestionTypeSelect select = new QuestionTypeSelect(lang);
//            select.writeSelect(pw,  "questionType", question.getQuestionType(), 60);

            QuestionTypeSelect select = new QuestionTypeSelect(lang);
            select.writeSelect(pw,  "questionType", question.getQuestionType(), 60);
        } else if (name.equals("question difficulty")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_DIFFICULTY, null));
        } else if (name.equals("question difficulty select")){            
            DifficultySelect select = new DifficultySelect(lang);
            select.writeSelect("diff", question.getDifficulty(), 20, pw);
        } else if (name.equals("first")){
            if (questionNumber>0){
                pw.println("<a onClick='form.option.value=4; form.submit(); return false;' href=\"#\" class=\"href\"");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_FIRST_QUESTION, null)+"\">");
                pw.println("<img src=\"images/test.first.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("previous")){
            if (questionNumber>0){
                pw.println("<a onClick='form.option.value=5; form.submit(); return false;' href=\"#\" class=\"href\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_PREVIOUS_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.prev.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("next")){
            if (questionNumber<questionsCount-1){
                pw.println("<a onClick='form.option.value=6; form.submit(); return false;' href=\"#\" class=\"href\" ");
                pw.print("title=\""+MessageManager.getMessage(lang, TestMessages.TO_NEXT_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.next.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("last")){
            if (questionNumber<questionsCount-1){
                pw.println("<a onClick='form.option.value=7; form.submit(); return false;' href=\"#\" class=\"href\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_LAST_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.last.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("question number")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_NUMBER, null)+" "+(questionNumber+1));
        } else if (name.equals("question formulation")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_FORMULATION, null));
        } else if (name.equals("question formulation input")){
            tinyMce.writeTinyMceInput(pw, "question", 100, 10, TinyMce.DIMENSION_TYPE__PERCENT_ROW, question.getQuestion());
        } else if (name.equals("variants")){
            question.writeQuestionForEdit(pw, lang);
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT, null));
        } else if (name.equals("script")){
            question.getScript(pw);
        } else if (name.equals("test name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_, null));
        }
//        else if (name.equals("test subject")){
//            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_SUBJECT, null));
//        } else if (name.equals("test subject select")){
//        	SubjectsSelect select = new SubjectsSelect(lang);
//        	select.writePostSelect("subject", 60, testSubjectID, pw, false);
//        }
//        <tr>
//        <td width="20%" align="center">
//        <b>{[test subject]}</b>
//        </td>
//        <td width="*">
//                {[test subject select]}
//        </td>
//        </tr>
        else if (name.equals("save test")){
            pw.print(MessageManager.getMessage(lang, TestMessages.SAVE_TEST, null));
        }else if (name.equals("title")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DOWNLOAD_JRE_TITLE, null));
        } else if (name.equals("testNameValue")){
            if (testName != null)
                pw.print(testName);
        } else if (name.equals("preview title")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_PREVIEW, null));
        } else if (name.equals("navigation")){
            pw.print(MessageManager.getMessage(lang, TestMessages.NAVIGATION, null));
        } else if(name.equals("add")){
            if(question.getQuestionType() != Question.CLOSED_QUESTION){
                pw.print("<tr>\n" +
                        "<td colspan=3>\n" +
                        "<table border=0 width=\"100%\">\n" +
                        "<tr>\n" +
                        "<td width=\"16px\">\n" +
                        "<a onClick='form.option.value=-3; form.submit(); return false;' href=\"#\" >\n" +
                        "<img src=\"images/button.add.gif\" width=\"16px\" height=\"16px\" border=0>\n" +
                        "</a>\n" +
                        "</td>\n" +
                        "<td align=\"left\">\n" +
                        "<a onClick='form.option.value=-3; form.submit(); return false;'  href=\"#\" " +
                        " class=\"href\" >\n" +
                        MessageManager.getMessage(lang, TestMessages.ADD_VARIANT, null) +
                        "</a>\n" +
                        "</td>\n" +
                        "</tr>\n" +
                        "</table>\n" +
                        "</td>\n" +
                        "</tr>");
            }
        } else if (name.equals("testID")){
            pw.print(testID);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("return href")){
            pw.print("testsedit?" + params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("signature")){
            pw.print(testSignature);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
