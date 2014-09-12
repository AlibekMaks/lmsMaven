package arta.filecabinet.logic.students;

import arta.common.logic.util.SimpleObject;

import java.util.Date;


public class SearchStudent extends SimpleObject {
    public String className = null;

    //RECOMMEND
    public boolean isRecommend = false;
    public Date lastTestingDate = null;
    public Date startdate = null;

    //  testing_status_type
    public int UNKNOWN = 0; // не известно
    public int ERROR_START_DATE = 1; // не правильно указана дата поступления
    public int NEVER_PASSED = 2; //ни разу не проходил
    public int FAILED_TEST = 3; // провалил тестирование
    public int NEED_TO_BE_TESTED = 4;  // нужно пройти тестирование
    public int SUCCUESSFULLY_PASSED_THE_TEST = 5; //тест успешно пройден

    public int testing_status_type = UNKNOWN;


    public int mark = 0;
    public boolean checkMark = false;

    public boolean isPassed = false;
    public int passedTextType = 0;

    public int testingStatus = 0;
    public Date testingDate = null;
    public boolean testingIsEnded = false;

    public int year = 0;
    public int month = 0;
    public int day = 0;
    public int hour = 0;
    public int minute = 0;
    public int week = 0;

}
