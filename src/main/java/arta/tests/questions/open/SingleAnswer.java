package arta.tests.questions.open;

import java.io.Serializable;

public class SingleAnswer implements Serializable {
    
    public String answer = "";
    public int answerID = 0;
    public boolean isRight = false;

    public boolean wasSelected = false;

    public SingleAnswer(String answer, boolean right) {
        this.answer = answer;
        isRight = right;
    }


    public SingleAnswer() {
    }
}
