package arta.welcome.logic;

import arta.tests.testing.logic.SimpleTesting;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.04.2008
 * Time: 18:22:44
 * To change this template use File | Settings | File Templates.
 */
public class StudentTesting extends SimpleTesting {

    private String subjectName = null;


    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
