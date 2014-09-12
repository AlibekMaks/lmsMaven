package arta.registrar.student.logic;

import arta.registrar.tutor.logic.SimpleMark;
import arta.registrar.tutor.logic.RegistrarDay;
import arta.common.logic.util.Constants;

import java.util.ArrayList;


public class StudentRegistrarSubject {

    public int studygroupID;
    public int subgroupID;
    public String subjectName;
    public ArrayList<RegistrarDay> marks = new ArrayList<RegistrarDay>();


    public void setMark(int day, int mark, int testingID, int markType){
        if (markType == Constants.TEST_MARK){
            setMark(day, mark,testingID);
        } else {
            setMark(day, mark);
        }
    }

    public void setMark(int day, int mark){
        marks.add(new RegistrarDay(mark, day));
    }


    public void setMark(int day, int mark, int testingID){

        int i = 0;

        if (marks.size() < day){
            i = - day + 1;
        }

        while (day - 1 + i < marks.size() && marks.get(day - 1 + i).day <= day ){
            i ++;
        }

        marks.add(day - 1 + i, new RegistrarDay(mark, day, Constants.TEST_MARK, testingID));
    }

}
