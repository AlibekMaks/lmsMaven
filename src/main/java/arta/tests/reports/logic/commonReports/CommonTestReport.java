package arta.tests.reports.logic.commonReports;

import arta.common.logic.util.Date;

/**
 * сводная ведомост тестирования
 */
public class CommonTestReport {

    public Date date ;
    public String name;
    public int testingID;
    public int mainTestingID;
    public int tutorID;
    public String tutor_firstname = "";
    public String tutor_lastname = "";
    public String tutor_patronymic = "";
    public boolean timeIsUp = false;


    public CommonTestReport() {
        date = new Date();
    }

    public String tutorGetFullName() {
        StringBuffer fullName = new StringBuffer();
        if (tutor_lastname != null){
            fullName.append(tutor_lastname);
            fullName.append(" ");
        }
        if (tutor_firstname!=null){
            fullName.append(tutor_firstname);
            fullName.append(" ");
            if (tutor_patronymic!=null){
                fullName.append(tutor_patronymic);
            }
        }
        return fullName.toString();
    }

}
