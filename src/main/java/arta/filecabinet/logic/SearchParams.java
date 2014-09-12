package arta.filecabinet.logic;

import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Rand;
import arta.common.http.SimpleHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

import com.bentofw.mime.ParsedData;


public class SearchParams {

    public String search = null;
    public int partNumber = 0;
    public int countInPart = 30;

    public int recordsCount;
    public int recordNumber;

    public String searchStr = "search";
    public String partNumberStr = "partNumber";
    public String countInPartStr = "countInPart";
    public String recordsCountStr = "recordsCount";
    public String recordNumberStr = "recordNumber";

    public void extractParameterValues(HttpServletRequest request, DataExtractor extractor) {
        search = extractor.getRequestString(request.getParameter(searchStr));
        recordsCount = extractor.getInteger(request.getParameter(recordsCountStr));
        recordNumber = extractor.getInteger(request.getParameter(recordNumberStr));
        if (request.getParameter(countInPartStr)!=null)
            countInPart = extractor.getInteger(request.getParameter(countInPartStr));
        partNumber = extractor.getInteger(request.getParameter(partNumberStr));
    }

    public void extractParameterValues(SimpleHandler handler, DataExtractor extractor) {
        search = extractor.getRequestString(handler.getParameter(searchStr));
        recordsCount = extractor.getInteger(handler.getParameter(recordsCountStr));
        recordNumber = extractor.getInteger(handler.getParameter(recordNumberStr));
        if (handler.getParameter(countInPartStr)!=null)
            countInPart = extractor.getInteger(handler.getParameter(countInPartStr));
        partNumber = extractor.getInteger(handler.getParameter(partNumberStr));
    }

    public void extractParameterValues(ParsedData data, DataExtractor extractor) {
        search = extractor.getRequestString(data.getParameter(searchStr));
        recordsCount = extractor.getInteger(data.getParameter(recordsCountStr));
        recordNumber = extractor.getInteger(data.getParameter(recordNumberStr));
        if (data.getParameter(countInPartStr)!=null)
            countInPart = extractor.getInteger(data.getParameter(countInPartStr));
        partNumber = extractor.getInteger(data.getParameter(partNumberStr));
    }

    public String getSearch() {
        if (search != null && search.length() > 0)
            return new StringTransform().getHTMLString(search);
        return "";
    }

    /**
     * Моетод возвращает строку параметров за исключением
     * номера выбранной страницы
     * @return стока параметров
     */
    public String getParams() {
        StringBuffer result = new StringBuffer(countInPartStr+"=" + countInPart + "&nocache="+ Rand.getRandString());
        if (search != null && search.length() > 0) {
            result.append("&"+searchStr+"=");
            result.append(new StringTransform().getHTMLString(search));
        }
        result.append("&"+recordsCountStr+"=");
        result.append(recordsCount);
        result.append("&"+recordNumberStr+"=");
        result.append(recordNumber);
        return result.toString();
    }

    /**
     * Метод возвращает строку параметров за исключением
     * номера текущей записи
     * @return строка параметров
     */
    public String getParamsWithoutRecord() {
        StringBuffer result = new StringBuffer(countInPartStr+"=" + countInPart + "&nocache="+ Rand.getRandString());
        if (search != null && search.length() > 0) {
            result.append("&"+searchStr+"=");
            result.append(new StringTransform().getHTMLString(search));
        }
        result.append("&"+partNumberStr+"=");
        result.append(partNumber);
        result.append("&"+recordsCountStr+"=");
        result.append(recordsCount);        
        return result.toString();
    }

    /**
     * Метод возвращает строку всех параметров
     * @return строка параметров
     */
    public String getFullParams(){
        String result = getParams();
        result += "&"+partNumberStr+"="+partNumber;
        return result;
    }

    public void writeHiddenInputs(PrintWriter pw){
        pw.print("<input type=\"hidden\" name=\""+countInPartStr+"\" value=\""+countInPart+"\">");
        pw.print("<input type=\"hidden\" name=\""+partNumberStr+"\" value=\""+partNumber+"\">");
        pw.print("<input type=\"hidden\" name=\""+recordNumberStr+"\" value=\""+recordNumber+"\">");
        pw.print("<input type=\"hidden\" name=\""+recordsCountStr+"\" value=\""+recordsCount+"\">");
        if (search!= null && search.length()>0)
            pw.print("<input type=\"hidden\" name=\""+searchStr+"\" value=\""+
                    new StringTransform().getHTMLString(search)+"\">");        
    }

    /**
     * Метод возвращает количество страниц выборки
     * @return кол-во страниц
     */
    public int getPartsNumber(){
        if (countInPart == 0) return 0;
        int partsNumber = recordsCount / countInPart;
        if (recordsCount % countInPart != 0)
            partsNumber ++ ;
        return partsNumber;
    }

    /**
     * Меотд возвращает номер последней страницы выборки
     * Если колво сраниц равно 0, возвращает 0
     * @return номер страницы
     */
    public int getLastPartNumber(){
        int number = getPartsNumber();
        if (number > 0)
            return number - 1;
        return  0;
    }

    /**
     * Метод возвращает номер следующей за текущей страницы
     * Если в выборке нет ни одной записи возвращает 0
     * @return номер след страницы
     */
    public int getNextPartNumber(){
        int number = getPartsNumber();
        if ((partNumber + 1) < number)
            return partNumber + 1;
        if (number == 0)
            return 0;
        return number - 1;
    }

    /**
     * Метод возвращает номер предыдущей страницы
     * Если в выборке нет ни одной записи, то возвращает 0
     * @return номер предыдущей страницы
     */
    public int getPrevPartNumber(){
        if (partNumber > 0)
         return partNumber - 1;
        return 0;
    }

    /**
     * Метод возвращает номер следующей за текущей записи из выборки
     * Если в выборке нет ни одной записи, возвращает -1
     * @return номер след записи
     */
    public int getNextRecordNumber(){
        if ((recordNumber + 1) < recordsCount)
            return recordNumber + 1;
        return recordsCount - 1;
    }

    /**
     * Метод возвращает номер последней записи из выборки
     * Если в выборке нет ни одной записи, возвращает -1
     * @return номер последней записи
     */
    public int getLastRecordNumber(){
        return recordsCount - 1;
    }

    /**
     * Метод возвращает номер предыдущей записи
     * Если в выборке нет ни одной записи, возвращает -1
     * @return номер предыдущей записи
     */
    public int getPreviousRecordNumber(){
        if ((recordNumber - 1) >= 0)
            return recordNumber - 1;
        return 0;
    }

    /**
     * Возвращает кол-во страниц выборки для отоборажения на странице,
     * т.е. если кол-во страниц=0, то возвращена будет 1
     * @return кол-во страниц
     */
    public int getPanelPartsNumber(){
        if (countInPart == 0) return 0;
        int partsNumber = recordsCount / countInPart;
        if (recordsCount % countInPart != 0)
            partsNumber ++ ;
        if (partsNumber == 0)
            partsNumber ++;
        return partsNumber;
    }

    /**
     * Возвращает номер текущей страницы для отоюражения
     * на странице
     * @return номер страницы
     */
    public int getPanelPartNumber(){
        return partNumber + 1;
    }

    /**
     * Возвращает номер текущей записи для отображения на странице
     * @return номер текущей записи
     */
    public int getPanelRecordNumber(){
        return recordNumber + 1;
    }
}
