package arta.groups.logic;

import arta.filecabinet.logic.SearchParams;


public class StudyGroupsSearchParams extends SearchParams {

    public static final String RECORD_NUMBER = "recordNumberGr";
    public static final String RECORDS_COUNT = "recordsCountGr";

    public StudyGroupsSearchParams() {
        search = "searchGr";
        countInPartStr = "countInPartGr";
        partNumberStr = "partNumberGr";
        recordsCountStr = RECORDS_COUNT;
        recordNumberStr = RECORD_NUMBER;
    }
}
