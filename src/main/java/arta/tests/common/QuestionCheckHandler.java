package arta.tests.common;

import arta.tests.questions.Question;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.html.handler.TemplateHandler;
import arta.check.logic.Testing;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class QuestionCheckHandler extends TemplateHandler {

    int lang;
    int questionNumber;
    ServletContext servletContext;
    Testing testing;

    public QuestionCheckHandler(int lang, int questionNumber, ServletContext servletContext, Testing testing) {
        this.lang = lang;
        this.questionNumber = questionNumber;
        this.servletContext = servletContext;
        this.testing = testing;
    }

    public void replace(String name, PrintWriter pw) {

        if (name.equals("go to")){
            pw.print(MessageManager.getMessage(lang, TestMessages.GO_TO, null));
        } else if (name.equals("question number select")){
            NumberSelect  select = new NumberSelect(lang);
            select.writeSimpleNumberSelectForCheck(pw, "question", questionNumber + 1, testing.questions.size(), 100);
        } else if (name.equals("question")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION, null));
            System.out.println("question");
        } else if (name.equals("first")){
            if (questionNumber>0){
                pw.println("<a onClick='send(0); return false;' href=\"#\" class=\"link\"");
                pw.print(" title=\""+ MessageManager.getMessage(lang, TestMessages.TO_FIRST_QUESTION, null)+"\">");
                pw.println("<img src=\"images/button.left.end.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("previous")){
           if (questionNumber>0){
                pw.println("<a  onClick='send("+(questionNumber-1)+"); return false;' href=\"#\" class=\"link\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_PREVIOUS_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/button.left.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
           } else {
                pw.print("&nbsp;");
           }
        } else if (name.equals("question number")){
                    pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_NUMBER, null) + " " + (questionNumber + 1));
        } else if (name.equals("next")){
            if (questionNumber < testing.questions.size()-1){
                pw.println("<a  onClick='send("+(questionNumber+1)+"); return false;' href=\"#\" class=\"link\" ");
                pw.print("title=\""+MessageManager.getMessage(lang, TestMessages.TO_NEXT_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/button.right.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp;");
            }
        } else if (name.equals("last")){
            if (questionNumber<testing.questions.size()-1){
                pw.println("<a   onClick='send("+(testing.questions.size()-1)+"); return false;' href=\"#\" class=\"link\" ");
                pw.print(" title=\""+MessageManager.getMessage(lang, TestMessages.TO_LAST_QUESTION, null)+"\" >");
                pw.println("<img src=\"images/button.right.end.gif\" border=0 width=\"24px\" height=\"24px\">");
                pw.println("</a>");
            } else {
                pw.print("&nbsp");
            }
        } else if (name.equals("question formulation")){
            pw.print(MessageManager.getMessage(lang, TestMessages.QUESTION_FORMULATION, null));
        } else if (name.equals("question formulation value")){
            pw.print(testing.questions.get(questionNumber).getQuestion());
        } else if (name.equals("variants")){
            testing.questions.get(questionNumber).writeQuestionForCheck(pw, lang, servletContext);
        } else if (name.equals("send")){
            pw.print(MessageManager.getMessage(lang, TestMessages.SEND_ANSWER, null));
        } else if (name.equals("subject name")){
            pw.print( testing.questions.get(questionNumber).getSubjectName() );
        } else if (name.equals("are u sure")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DO_U_REALLY_WANT_TO_SEND_ANSWERS_TO_CHECK, null));
        } else if (name.equals("functions")){
            if (testing.questions.get(questionNumber).getQuestionType() == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                pw.print("function clck(n){" +
                        "document.getElementById(n).checked=true;" +
                        "}"+
                        "function chng(n, max){" +
                                "if (document.getElementById(n).checked){" +
                                "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.RIGHT_CHCK_COLOR+"\";" +
                                "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.RIGHT_VAR_COLOR+"\";" +
                                "} else {" +
                                "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";" +
                                "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";" +
                                "}" +
                                "for (var i=0; i<max; i++) {" +
                                "if (i!=n*1){" +
                                    "document.getElementById(\"Q\"+i).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                                    "document.getElementById(\"A\"+i).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                                "}" +
                                "}" +
                        "}");
            }
            if (testing.questions.get(questionNumber).getQuestionType() == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                pw.print("function clck(n){" +
                        "document.getElementById(n).checked=!document.getElementById(n).checked;" +
                "}"+
                "function chng(n, max){" +
                        "if (document.getElementById(n).checked){" +
                        "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.RIGHT_CHCK_COLOR+"\";" +
                        "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.RIGHT_VAR_COLOR+"\";" +
                        "} else {" +
                        "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";" +
                        "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";" +
                        "}" +
                "}");
            }
            if (testing.questions.get(questionNumber).getQuestionType() == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS ||
                    testing.questions.get(questionNumber).getQuestionType() == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                pw.print("\tfunction sel(n){\n" +
                "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.SELCOLOR+"\";\n" +
                "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.SELCOLOR+"\";\n" +
                "}" +
                "function unsel(n){" +
                        "if (document.getElementById(n).checked){" +
                            "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.RIGHT_CHCK_COLOR+"\";" +
                            "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.RIGHT_VAR_COLOR+"\";" +
                        "} else {" +
                            "document.getElementById(\"Q\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                            "document.getElementById(\"A\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                        "}" +
                "}"  );
            }
            if (testing.questions.get(questionNumber).getQuestionType() == Question.SEQUENCE_QUESTION){
                  pw.print("\tfunction sel(n){\n" +
                "document.getElementById(\"T\"+n).style.backgroundColor=\""+Question.SELCOLOR+"\";\n" +
                "document.getElementById(\"N\"+n).style.backgroundColor=\""+Question.SELCOLOR+"\";\n" +
                "}" +
                "function unsel(n){" +
                "document.getElementById(\"T\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                "document.getElementById(\"N\"+n).style.backgroundColor=\""+Question.UNSELCOLOR+"\";\n" +
                "}"  );
            }
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("current question number")){
            pw.print(questionNumber);

        } else if (name.equals("submit button")){
            if (questionNumber == testing.questions.size() - 1){
                pw.print("<tr><td>");
                pw.print("<input type=\"button\" name=\"send\" value=\""+
                        MessageManager.getMessage(lang, Constants.SEND)+
                        "\" onClick='sendAnswers();' class=\"button\"/>");
                pw.print("</td></tr>");
            }
        }

    }      
}
