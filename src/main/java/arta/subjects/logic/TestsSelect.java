package arta.subjects.logic;

import java.util.Date;

public class TestsSelect {

    protected int testID = 0;
    protected String testName = "";
    protected int tutorID = 0;
    protected int subjectID = 0;
    protected Date created = null;
    protected Date modified = null;
    protected int easy = 0;
    protected int middle = 0;
    protected int difficult = 0;
    protected boolean selected = false;

    public void setSelected(boolean  val){
        this.selected = val;
    }

    public boolean getSelected(){
        return this.selected;
    }


    // get Methods
    public int getTestID(){
        return this.testID;
    }

    public String getTestName(){
        return this.testName;
    }

    public int getTutorID(){
        return this.tutorID;
    }

    public int getSubjectID(){
        return this.subjectID;
    }

    public Date getCreated(){
        return this.created;
    }

    public Date getModified(){
        return this.modified;
    }

    public int getEasy(){
        return this.easy;
    }

    public int getMiddle(){
        return this.middle;
    }

    public int getDifficult(){
        return this.difficult;
    }

    // set Methods
    public void setTestID(int testID){
        this.testID = testID;
    }

    public void setTestName(String testName){
        this.testName = testName;
    }

    public void setTutorID(int tutorID){
        this.tutorID = tutorID;
    }

    public void setSubjectID(int subjectID){
        this.subjectID = subjectID;
    }

    public void setCreated(Date created){
        this.created = created;
    }

    public void setModified(Date modified){
        this.modified = modified;
    }

    public void setEasy(int easy){
        this.easy = easy;
    }

    public void setMiddle(int middle){
        this.middle = middle;
    }

    public void setDifficult(int difficult){
        this.difficult = difficult;
    }

    // other Methods
    public TestsSelect(){
    }



}
