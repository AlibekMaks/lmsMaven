package arta.exams.logic;

import arta.common.logic.util.Languages;

public class TicketQuestion {

    public int ticketQuestionID;
    public String questionkz = "";
    public String questionru = "";

    public boolean isNew = false;

    public TicketQuestion(){

    }

    public int getTicketQuestionID(){
        return this.ticketQuestionID;
    }

    public String getQuestionkz(){
        return this.questionkz;
    }

    public String getQuestionru(){
        return this.questionru;
    }

    public String getQuestion(int lang){
        if(lang == Languages.KAZAKH){
            return this.questionkz;
        } else if(lang == Languages.RUSSIAN){
            return this.questionru;
        } else return "";
    }

    public void setTicketQuestionID(int ticketQuestionID){
        this.ticketQuestionID = ticketQuestionID;
    }

    public void setQuestionkz(String questionkz){
        this.questionkz = questionkz;
    }

    public void setQuestionru(String questionru){
        this.questionru = questionru;
    }
}
