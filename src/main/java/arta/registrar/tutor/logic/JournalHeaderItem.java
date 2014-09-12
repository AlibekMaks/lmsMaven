package arta.registrar.tutor.logic;


public class JournalHeaderItem {

    public int day;
    public int type;
    public int testingID;
    public String signature;


    public JournalHeaderItem(int day, int type) {
        this.day = day;
        this.type = type;
    }


    public JournalHeaderItem(int day, int type, int testingID, String signature) {
        this.day = day;
        this.type = type;
        this.testingID = testingID;
        this.signature = signature;
    }
}

