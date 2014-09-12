package arta.filecabinet.logic.students;

import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServletRequest;


public class StudentSearchParams extends SearchParams {

    public int classID = 0;
    public String classIDStr = "classID";

    public int examinerID = 0;
    public String examinerID_Str = "examiner";

    public String studentList = "";
    public String studentList_Str = "studentList";


    // timeHasPassed
    // 0 - за все время
    // 1..12 - месяцы
    // 13 - больше года
    public int timeHasPassed = 0;
    public String timeHasPassed_Str = "time";


    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        super.extractParameterValues(request, extractor);
        classID = extractor.getInteger(request.getParameter(classIDStr));
        examinerID = extractor.getInteger(request.getParameter(examinerID_Str));
        timeHasPassed = extractor.getInteger(request.getParameter(timeHasPassed_Str));
        studentList = extractor.getRequestString(request.getParameter(studentList_Str));
    }


    public String getParams() {
        return super.getParams() + getAdditionalFields();
    }


    public String getParamsWithoutRecord() {
        return super.getParamsWithoutRecord() + getAdditionalFields();
    }


    private String getAdditionalFields(){
        return "&" + classIDStr + "=" + classID +
               "&" + examinerID_Str + "=" + examinerID+
               "&" + timeHasPassed_Str + "=" + timeHasPassed+
               "&" + studentList_Str + "=" + studentList;

    }
}
