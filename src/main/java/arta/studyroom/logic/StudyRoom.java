package arta.studyroom.logic;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 18.03.2008
 * Time: 16:07:54
 * To change this template use File | Settings | File Templates.
 */
public class StudyRoom {

    private int subGroupID = 0;
    private String subjectName = "";
    private int subGroupNumber = 0;

    private int personsCount = 0;

    private String additionalField = "";


    public int getSubGroupID() {
        return subGroupID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getSubGroupNumber() {
        return subGroupNumber;
    }

    public int getPersonsCount() {
        return personsCount;
    }

    public void setSubGroupID(int subGroupID) {
        this.subGroupID = subGroupID;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubGroupNumber(int subGroupNumber) {
        this.subGroupNumber = subGroupNumber;
    }

    public void setPersonsCount(int personsCount) {
        this.personsCount = personsCount;
    }


    public void setAdditionalField(String additionalField) {
        this.additionalField = additionalField;
    }

    public String getAdditionalField() {
        return additionalField;
    }
}
