package arta.tests.common;

import arta.tests.test.Test;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class QuestionViewHandler extends TemplateHandler {

    int lang;
    Test test;
    int questionNumber;
    ServletContext servletContext;
    TestsSearchParams params;

    StringTransform stringTransform = new StringTransform();

    public QuestionViewHandler(int lang, Test test, int questionNumber, ServletContext servletContext,
                               TestsSearchParams params) {
        this.lang = lang;
        this.test = test;
        this.questionNumber = questionNumber;
        this.servletContext = servletContext;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (questionNumber<0 || questionNumber>test.questions.size()-1)
            return;
        if (name.equals("go to")){
            pw.print(MessageManager.getMessage(lang, TestMessages.GO_TO, null));
        } else if (name.equals("question numberselect")){
            NumberSelect select = new NumberSelect(lang);
            select.writeQuestionNumberSelect(pw, "testView?"+ Rand.getRandString(), "questionNumber",
                    questionNumber, test.questions.size(), null);
        } else if (name.equals("question")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION, null));
        } else if (name.equals("first")){
            if (questionNumber>0){
                pw.println("<a href=\"testView?questionNumber=0&"+Rand.getRandString()+"\" class=\"href\"");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_FIRST_QUESTION, null)+"\">");
                pw.println("<img src=\"images/test.first.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("previous")){
           if (questionNumber>0){
                pw.println("<a href=\"testView?questionNumber="+(questionNumber-1)+"&"+Rand.getRandString()+"\" class=\"href\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_PREVIOUS_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.prev.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
           } else {
                pw.print("&nbsp;");
           }
        } else if (name.equals("question number")){
           pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_NUMBER, null)+" "+(questionNumber+1));
        } else if (name.equals("next")){
            if (questionNumber < test.questions.size()-1){
                pw.println("<a href=\"testView?questionNumber="+(questionNumber+1) + "&" +Rand.getRandString()+"\" class=\"href\" ");
                pw.print("title=\""+MessageManager.getMessage(lang, TestMessages.TO_NEXT_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.next.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("last")){
            if (questionNumber < test.questions.size()-1){
                pw.println("<a href=\"testView?questionNumber="+(test.questions.size()-1)+ "&" +Rand.getRandString()+
                        "\" class=\"href\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_LAST_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/test.last.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp");
            }
        } else if (name.equals("question type")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_TYPE, null));
        } else if (name.equals("question difficulty")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_DIFFICULTY, null));
        } else if (name.equals("question formulation")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_FORMULATION, null));
        } else if (name.equals("formulation")){
            pw.print(test.questions.get(questionNumber).getQuestion());
        } else if (name.equals("variants")){
            test.questions.get(questionNumber).writeQuestionForView(pw, lang, servletContext);
        } else if (name.equals("question type value")){
            QuestionTypeSelect select = new QuestionTypeSelect(lang);
            pw.print(select.getQuestionType(test.questions.get(questionNumber).getQuestionType(), lang));
        } else if (name.equals("question difficulty value")){
            DifficultySelect select = new DifficultySelect(lang);
            pw.print(select.getDifficultyName(test.questions.get(questionNumber).getDifficulty()));            
        } else if (name.equals("return link")){
            pw.print("testsedit?"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("page header")){
            Properties prop = new Properties();
            prop.setProperty("name", test.getTestName());            
            pw.print(MessageManager.getMessage(lang, Constants.TEST, prop));
        }
    }
}
