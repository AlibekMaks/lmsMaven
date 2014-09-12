package arta.exams.logic;



public class SimpleExam {

    public int examID;
    public String examName;
    public int questionCount;
    public boolean isUsed = false;

    public SimpleExam() {
    }


    public SimpleExam(int examID, String examName, int questionCount) {
        this.examID = examID;
        this.examName = examName;
        this.questionCount = questionCount;
    }

    public String getExamName(){
        if (examName == null) return "";
        return examName;
    }

}
