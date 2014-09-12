package arta.tests.parser.logic;

import arta.tests.test.Test;
import arta.tests.questions.open.OpenQuestion;
import arta.tests.questions.open.SingleAnswer;
import arta.tests.questions.Question;


public class TestParser {

    Test test;


    public TestParser(Test test) {
        this.test = test;
        if (test == null) throw new NullPointerException("Test can not be equal null!");
        if (test.tutorID <= 0) throw new IllegalArgumentException("Tutor not specified!");
    }

    String QUESTION_START = "<question";
    String VARIANT_START = "<variant>";

    String QUESTION_DEFAULT = ">";
    String QUESTION_EASY = "1>";
    String QUESTION_MIDDLE = "2>";
    String QUESTION_DIFFICULT = "3>";

    int questionType = 1;
    int variantType = 2;
    int currentQuestion = -1;
    int currentVaraint =-1;
    int currentType = 0;

    StringBuffer inDelim = new StringBuffer();

    public void parse(StringBuffer str){
        trim(str);
        int nextQuestion = str.indexOf(QUESTION_START);
        int nextVariant = str.indexOf(VARIANT_START);

        if ((nextQuestion > 0 && nextVariant > 0) || (nextQuestion == -1 && nextVariant == -1)){
            int border = min(nextQuestion, nextVariant);

            if (currentType == questionType){
                if (currentQuestion >=0  && currentQuestion <test.questions.size()){
                    if (border >=0)
                        test.questions.get(currentQuestion).setQuestion(
                                test.questions.get(currentQuestion).getQuestion() +
                                        str.substring(0, border));
                    else
                        test.questions.get(currentQuestion).setQuestion(
                                test.questions.get(currentQuestion).getQuestion() + str.toString());
                }
            } else if (currentType == variantType ){
                if (currentQuestion >=0 && currentQuestion < test.questions.size()){
                    if (currentVaraint >=0 && currentVaraint < ((OpenQuestion)test.questions.get(currentQuestion)).answers.size()){
                        if (border >=0)
                            ((OpenQuestion)test.questions.get(currentQuestion)).answers.get(currentVaraint).answer +=
                                        str.substring(0, border);
                        else
                            ((OpenQuestion)test.questions.get(currentQuestion)).answers.get(currentVaraint).answer += str.toString();
                    }
                }
            }
            if (border >=0 )
                str.delete(0, border);
        }

        while (str.length() > 0){
            nextQuestion = str.indexOf(QUESTION_START);
            nextVariant = str.indexOf(VARIANT_START);
            int min = min(nextQuestion, nextVariant);
            if (min == -1) break;
            if (min == nextQuestion){

                int difficulty = 1;

                if (str.substring(nextQuestion + QUESTION_START.length(), nextQuestion + QUESTION_START.length() + QUESTION_DEFAULT.length()).equals(QUESTION_DEFAULT)){
                    str.delete(0, nextQuestion + QUESTION_START.length() + QUESTION_DEFAULT.length());
                    difficulty = 1;
                } else if (str.substring(nextQuestion + QUESTION_START.length(), nextQuestion + QUESTION_START.length() + QUESTION_EASY.length()).equals(QUESTION_EASY)) {
                    str.delete(0, nextQuestion + QUESTION_START.length() + QUESTION_EASY.length());
                    difficulty = 1;
                } else if (str.substring(nextQuestion + QUESTION_START.length(), nextQuestion + QUESTION_START.length() + QUESTION_MIDDLE.length()).equals(QUESTION_MIDDLE)) {
                    str.delete(0, nextQuestion + QUESTION_START.length() + QUESTION_MIDDLE.length());
                    difficulty = 2;
                } else if (str.substring(nextQuestion + QUESTION_START.length(), nextQuestion + QUESTION_START.length() + QUESTION_DIFFICULT.length()).equals(QUESTION_DIFFICULT)) {
                    str.delete(0, nextQuestion + QUESTION_START.length() + QUESTION_DIFFICULT.length());
                    difficulty = 3;
                }

                OpenQuestion question = new OpenQuestion(Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS);
                question.setDifficulty(difficulty);

                nextVariant = str.indexOf(VARIANT_START);
                if (nextVariant >=0 ){
                    question.setQuestion(str.substring(0, nextVariant));
                    str.delete(0, nextVariant);
                    test.questions.add(question);
                    currentQuestion = test.questions.size()-1;
                    currentVaraint = -1;
                    currentType = questionType;
                } else {
                    question.setQuestion(str.toString());
                    test.questions.add(question);
                    currentQuestion = test.questions.size()-1;
                    currentVaraint = -1;
                    currentType = questionType;
                    break;
                }
            } else if (min == nextVariant){
                str.delete(0, nextVariant + VARIANT_START.length());
                nextVariant = str.indexOf(VARIANT_START);
                nextQuestion = str.indexOf(QUESTION_START);
                int min1 = min(nextVariant, nextQuestion);
                currentVaraint ++ ;
                if (currentQuestion < 0 || currentQuestion >= test.questions.size())
                    continue;
                if (min1 == -1){
                    ((OpenQuestion)test.questions.get(currentQuestion)).answers.add(new SingleAnswer(
                            str.toString(), currentVaraint == 0));
                    currentType = variantType;
                    break;
                } else if (min1 == nextQuestion){
                    ((OpenQuestion)test.questions.get(currentQuestion)).answers.add(new SingleAnswer(
                            str.substring(0, nextQuestion), currentVaraint == 0));
                    str.delete(0, nextQuestion);
                } else if (min1 == nextVariant){
                    ((OpenQuestion)test.questions.get(currentQuestion)).answers.add(new SingleAnswer(
                            str.substring(0, nextVariant), currentVaraint == 0));
                    str.delete(0, nextVariant);
                }
                currentType = variantType;
            }
        }
    }

    public Test getTest(){
        return test;
    }

    public void setDelims(String question, String variant, String questionDefault,
                          String questionEasy, String questionMiddle, String quetionDiffiult){
        this.QUESTION_START = question;
        this.VARIANT_START = variant;
        this.QUESTION_DEFAULT = questionDefault;
        this.QUESTION_EASY = questionEasy;
        this.QUESTION_MIDDLE = questionMiddle;
        this.QUESTION_DIFFICULT = quetionDiffiult;
    }

    private int min(int n1, int n2){
        if (n1 >= 0 && n2 >= 0){
            return Math.min(n1, n2);
        }
        if (n1 != -1)
            return n1;
        if (n2 != -1)
            return n2;
        return -1;
    }

    private String trim(StringBuffer str){
        int n = -1;
        while ((n = str.indexOf("=\r\n")) >= 0){
            str.delete(n, n + 3);
        }
        while ((n = str.indexOf("\r\n\r\n")) >= 0){
            str.delete(n , n+2);
        }
        return str.toString();
    }

}
