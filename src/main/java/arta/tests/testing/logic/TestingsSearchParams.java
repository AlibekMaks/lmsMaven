package arta.tests.testing.logic;

import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Date;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

public class TestingsSearchParams extends SearchParams {

    public Date startDate;
    public Date finishDate;
    public String fio = "";
    public String fio_str = "fio";
    public int departmentID = 0;
    public String departmentID_str = "department";
    public int examinerID = 0;
    public String examinerID_str = "examiner";

    public TestingsSearchParams(){
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

    public String getParams() {
        return super.getParams() + getAdditionalFields();
    }

    public String getParamsWithoutRecord() {
        return super.getParamsWithoutRecord() + getAdditionalFields();
    }

    private String getAdditionalFields(){
        String str = "&" + examinerID_str + "=" + examinerID +
                     "&" + departmentID_str + "=" + departmentID+
                     "&"+fio_str+"="+new StringTransform().getHTMLString(fio);

        if (startDate!=null && finishDate!=null){
            str += "&dateFrom="+startDate.getInputValue()+"&dateTill="+finishDate.getInputValue();
        }
        return str;
    }

    public void writeHiddenInputs(PrintWriter pw) {
        super.writeHiddenInputs(pw);
        if (startDate!=null && finishDate!=null){
            pw.print("<input type=\"hidden\" name=\"dateFrom\" value=\""+startDate.getInputValue()+"\">");
            pw.print("<input type=\"hidden\" name=\"dateTill\" value=\""+finishDate.getInputValue()+"\">");
        }

        pw.print("<input type=\"hidden\" name=\""+examinerID_str+"\" value=\""+examinerID+"\">");
        pw.print("<input type=\"hidden\" name=\""+departmentID_str+"\" value=\""+departmentID+"\">");

        if (fio!= null && fio.length()>0)
            pw.print("<input type=\"hidden\" name=\""+fio_str+"\" value=\""+new StringTransform().getHTMLString(fio)+"\">");
    }

}
