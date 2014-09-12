package arta.registrar.tutor.logic;

import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;


public class RegistrarDay {

    private int mark;
    public int day;
    public int markType = Constants.SIMPLE_MARK;
    public int testingID;

    public RegistrarDay(int mark, int day) {
        this.mark = mark;
        this.day = day;
    }

    public RegistrarDay(int mark, int day, int markType, int testingID) {
        this.mark = mark;
        this.day = day;
        this.markType = markType;
        this.testingID = testingID;
    }

    public void setMark(int mark){
        this.mark = mark;
    }

    public String getMark(int lang){
        if (mark == -1)
            return RegistrarMessages.getAbsent(lang);
        return mark + "";
    }
}
