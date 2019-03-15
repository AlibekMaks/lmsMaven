package arta.subjects.logic;


public enum SubjectsStatus {
ARCHIVE(1),ACTIVE(0);

public int statusId;

    SubjectsStatus(int statusId) {
        this.statusId = statusId;
    }
}
