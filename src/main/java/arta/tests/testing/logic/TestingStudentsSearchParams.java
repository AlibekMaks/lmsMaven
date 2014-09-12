package arta.tests.testing.logic;

import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 16:55:12
 * To change this template use File | Settings | File Templates.
 */
public class TestingStudentsSearchParams extends SearchParams {

    public int classID = 0;
    public String classIDStr = "classID";
    
    public int subjectID = 0;
    public String subjectIDStr = "subjectID";


    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        super.extractParameterValues(request, extractor);
        classID = extractor.getInteger(request.getParameter(classIDStr));
        subjectID = extractor.getInteger(request.getParameter(subjectIDStr));
    }


    public String getParams() {
        return super.getParams() + "&" + classIDStr + "=" + classID + "&" + subjectIDStr + "=" + subjectID;
    }
}
