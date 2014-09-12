package arta.library.logic;

import arta.filecabinet.logic.SearchParams;
import arta.common.logic.util.Languages;
import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServletRequest;

import com.bentofw.mime.ParsedData;

import java.io.PrintWriter;


public class LibrarySearchParams extends SearchParams {

    public boolean searchInSubjectBooks = false;
    public boolean searchInTutorBooks = false;
    public boolean searchInBookName = false;
    public boolean searchInTutorName = false;
    public int lang = Languages.RUSSIAN;


    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        super.extractParameterValues(request, extractor);
        boolean needAdd = true;
        if (request.getParameter("subjectbook") != null){
            searchInSubjectBooks = true;
            needAdd = false;
        }
        if (request.getParameter("tutorbook") != null){
            searchInTutorBooks = true;
            needAdd = false;
        }
        if(searchInTutorBooks){
            if (request.getParameter("inbookname") != null)
                searchInBookName = true;
            if (request.getParameter("intutorname") != null)
                searchInTutorName = true;
        }
        if (needAdd){
            searchInSubjectBooks = true;
        }
        lang = extractor.getInteger(request.getParameter("lang"));
    }

    public void extractParameterValues(ParsedData data, DataExtractor extractor) {
        super.extractParameterValues(data, extractor);
        if (data.getParameter("subjectbook") != null)
            searchInSubjectBooks = true;
        if (data.getParameter("tutorbook") != null)
            searchInTutorBooks = true;
        if(searchInTutorBooks){
            if (data.getParameter("inbookname") != null)
                searchInBookName = true;
            if (data.getParameter("intutorname") != null)
                searchInTutorName = true;
        }
        lang = extractor.getInteger(data.getParameter("lang"));
    }

    private String getSpecifiedParams(){
        StringBuffer res = new StringBuffer();
        if (searchInSubjectBooks)
            res.append("&subjectbook=on");
        if (searchInTutorBooks){
            res.append("&tutorbook=on");
            if (searchInBookName)
                res.append("&inbookname=on");
            if (searchInTutorName)
                res.append("&intutorname=on");
        }
        res.append("&lang=");
        res.append(lang);
        return res.toString();
    }

    public String getParams() {
        String res = super.getParams();
        res += getSpecifiedParams();
        return  res;
    }

    public String getParamsWithoutRecord() {
        String res = super.getParamsWithoutRecord();
        res += getSpecifiedParams();
        return res;
    }

    public String getFullParams() {
        String res = super.getFullParams();
        res += getSpecifiedParams();
        return res;
    }

    public void writeHiddenInputs(PrintWriter pw) {
        super.writeHiddenInputs(pw);
    }
}
