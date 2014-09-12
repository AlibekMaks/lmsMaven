package arta.tests.test.list;

import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.DataExtractor;
import arta.common.http.SimpleHandler;

import javax.servlet.http.HttpServletRequest;

import com.bentofw.mime.ParsedData;

import java.io.PrintWriter;


public class TestsSearchParams extends SearchParams {

    public String tutorIdStr = "tutorID";
    public int tutorID;


    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        super.extractParameterValues(request, extractor);
        tutorID = extractor.getInteger(request.getParameter("tutorID"));
    }

    public void extractParameterValues(SimpleHandler handler, DataExtractor extractor) {
        super.extractParameterValues(handler, extractor);
        tutorID = extractor.getInteger(handler.getParameter("tutorID"));
    }

    public void extractParameterValues(ParsedData data, DataExtractor extractor) {
        super.extractParameterValues(data, extractor);
        tutorID = extractor.getInteger(data.getParameter("tutorID"));
    }

    public String getParams() {
        String res = super.getParams();
        res += "&tutorID="+tutorID;
        return res;
    }

    public void writeHiddenInputs(PrintWriter pw) {
        super.writeHiddenInputs(pw);
        pw.print("<input type=\"hidden\" name=\"tutorID\" value=\""+tutorID+"\"/>");
    }
}
