package arta.tests.common;

import arta.tests.questions.Question;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;

import java.io.PrintWriter;

public class QuestionTypeSelect {

    int lang;

    public QuestionTypeSelect(int lang) {
        this.lang = lang;
    }

    public void writeSelect(PrintWriter pw,  String name, int value, int width){
        Select select = new Select(Select.POST_SELECT);
        select.width = width;
        pw.println(select.startSelect(name));
        pw.println(select.addOption(Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS+"",
                value == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS,
                MessageManager.getMessage(lang, TestMessages.OPEN_WITH_SIGLE_RIGHT_VARIANT, null)));
//
//        pw.println(select.addOption(Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS+"",
//                value == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS,
//                MessageManager.getMessage(lang, TestMessages.OPEN_WITH_PLURAL_RIGHT_VARIANTS, null)));
//
//        pw.println(select.addOption(Question.ASSOCIATE_QUESTION + "",
//                value == Question.ASSOCIATE_QUESTION,
//                MessageManager.getMessage(lang, TestMessages.ASSOCIATE_QUESTION, null)));
//
//        pw.println(select.addOption(Question.SEQUENCE_QUESTION + "",
//                value == Question.SEQUENCE_QUESTION,
//                MessageManager.getMessage(lang, TestMessages.SEQUENCE_QUESTION, null)));
//
//        pw.println(select.addOption(Question.CLOSED_QUESTION + "",
//                value == Question.CLOSED_QUESTION,
//                MessageManager.getMessage(lang, TestMessages.CLOSED_QUESTION, null)));

        pw.println(select.endSelect());
    }

    public String getQuestionType(int type, int lang){
        if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
            return MessageManager.getMessage(lang, TestMessages.OPEN_WITH_SIGLE_RIGHT_VARIANT, null);
        } else if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
            return MessageManager.getMessage(lang, TestMessages.OPEN_WITH_PLURAL_RIGHT_VARIANTS, null);
        } else if (type == Question.SEQUENCE_QUESTION){
            return MessageManager.getMessage(lang, TestMessages.SEQUENCE_QUESTION, null);
        } else if (type == Question.ASSOCIATE_QUESTION){
            return MessageManager.getMessage(lang, TestMessages.ASSOCIATE_QUESTION, null);
        } else if (type == Question.CLOSED_QUESTION){
            return MessageManager.getMessage(lang, TestMessages.CLOSED_QUESTION, null);            
        }
        return "";
    }

}
