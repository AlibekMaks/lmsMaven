package arta.tests.reports.logic.commonReports;

import arta.common.logic.util.Date;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.http.HttpServletRequest;

import com.bentofw.mime.ParsedData;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CommonTestSearchParams extends SearchParams {

    public Date startDate;
    public Date finishDate;
    public int departmentID = 0;
    public String departmentID_str = "department";
    public int examinerID = 0;
    public String examinerID_str = "examiner";
    public String fio = "";
    public String fio_str = "fio";

    public CommonTestSearchParams() {
        super();
        startDate = new Date();
        finishDate = new Date();
    }

    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        super.extractParameterValues(request, extractor);
        
        String dateFromParam = request.getParameter("dateFrom");
        startDate.loadDate(dateFromParam, Date.FROM_INPUT);
        
        String dateToParam = request.getParameter("dateTill");
        finishDate.loadDate(dateToParam, Date.FROM_INPUT);
        
        boolean paramsAreNull = (dateFromParam == null && dateToParam == null);
        if (finishDate.compareTo(startDate) == 0 && paramsAreNull) {
        	startDate.addMonths(-1);
        }

        examinerID = extractor.getInteger(request.getParameter(examinerID_str));
        departmentID = extractor.getInteger(request.getParameter(departmentID_str));
        fio = extractor.getRequestString(request.getParameter(fio_str));
    }

    public void extractParameterValues(ParsedData data, DataExtractor extractor) {
        super.extractParameterValues(data, extractor);
        
        String dateFromParam = data.getParameter("dateFrom");
        startDate.loadDate(dateFromParam, Date.FROM_INPUT);
        
        String dateToParam = data.getParameter("dateTill");
        finishDate.loadDate(dateToParam, Date.FROM_INPUT);
        
        boolean paramsAreNull = (dateFromParam == null && dateToParam == null);
        if (finishDate.compareTo(startDate) == 0 && paramsAreNull) {
        	startDate.addMonths(-1);
        }

        examinerID = extractor.getInteger(data.getParameter(examinerID_str));
        departmentID = extractor.getInteger(data.getParameter(departmentID_str));
        fio = extractor.getRequestString(data.getParameter(fio_str));
    }


    public String getParams() {
        String str = super.getParams();
        if (startDate!=null && finishDate!=null)
            str += "&dateFrom="+startDate.getInputValue()+"&dateTill="+finishDate.getInputValue();

        str += "&"+examinerID_str+"="+examinerID+
               "&"+departmentID_str+"="+departmentID+
               "&"+fio_str+"="+new StringTransform().getHTMLString(fio);
        return str;
    }

    public String getParamsWithoutRecord() {
        String str = super.getParamsWithoutRecord();
        if (startDate!=null && finishDate!=null)
            str += "&dateFrom="+startDate.getInputValue()+"&dateTill="+finishDate.getInputValue();

        str += "&"+examinerID_str+"="+examinerID+
               "&"+departmentID_str+"="+departmentID+
               "&"+fio_str+"="+new StringTransform().getHTMLString(fio);
        return str;
    }

    public String getFullParams() {
        String str = super.getFullParams();
        if (startDate!=null && finishDate!=null)
            str += "&dateFrom="+startDate.getInputValue()+"&dateTill="+finishDate.getInputValue();

        str += "&"+examinerID_str+"="+examinerID+
               "&"+departmentID_str+"="+departmentID+
               "&"+fio_str+"="+new StringTransform().getHTMLString(fio);
        return str;                
    }

    public String getFullParamsWithoutRecord() {
        String str = super.getFullParams();
        if (startDate!=null && finishDate!=null) str += finishDate.getInputValue();

        str += "&"+examinerID_str+"="+examinerID+"&"+departmentID_str+"="+departmentID+
               "&"+fio_str+"="+new StringTransform().getHTMLString(fio)  ;
        return str;
    }

    public void writeHiddenInputs(PrintWriter pw) {
        super.writeHiddenInputs(pw);
        if (startDate!=null && finishDate!=null){
            pw.print("<input type=\"hidden\" name=\"dateFrom\" value=\""+startDate.getInputValue()+"\">");
            pw.print("<input type=\"hidden\" name=\"dateTill\" value=\""+finishDate.getInputValue()+"\">");
        }

        pw.print("<input type=\"hidden\" name=\""+departmentID_str+"\" value=\""+departmentID+"\">");
        pw.print("<input type=\"hidden\" name=\""+examinerID_str+"\" value=\""+examinerID_str+"\">");

        if (fio!= null && fio.length()>0)
            pw.print("<input type=\"hidden\" name=\""+fio_str+"\" value=\""+new StringTransform().getHTMLString(fio)+"\">");
    }
}
