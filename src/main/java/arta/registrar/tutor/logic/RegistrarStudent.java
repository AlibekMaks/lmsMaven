package arta.registrar.tutor.logic;

import arta.common.logic.util.Constants;

import java.util.ArrayList;


public class RegistrarStudent {

    public ArrayList<RegistrarDay> marks = new ArrayList<RegistrarDay>();
    public int studentID;
    public String studentName;

    public void setTestMark(int testingID, int mark, int day){

        int i = 0;

        if (marks.size() < day){
            i = - day + 1;
        }

        while (day - 1 + i < marks.size() && marks.get(day - 1 + i).day <= day){
            i ++;
        }

        marks.add(day - 1 + i, new RegistrarDay(mark, day, Constants.TEST_MARK, testingID));

    }

    public void setMark(int day, int mark){
        marks.add(new RegistrarDay(mark, day));
    }

}
