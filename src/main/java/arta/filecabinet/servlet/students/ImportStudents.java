package arta.filecabinet.servlet.students;

import arta.common.html.handler.PageGenerator;
import arta.common.http.HttpRequestParser;
import arta.common.http.SimpleHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.filecabinet.logic.Person;
import arta.filecabinet.servlet.students.upload.file.MainPageHandler;
import arta.login.logic.Access;
import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;
import org.apache.poi.hssf.usermodel.*;


public class ImportStudents extends HttpServlet {

//    public static final int _CELL_TYPE_FORMULA = 2;
//    public static final int _CELL_TYPE_BLANK = 3;
//    public static final int _CELL_TYPE_ERROR = 5;
    public static final int CELL_TYPE_NONE = 0;
    public static final int CELL_TYPE_INTEGER = 1;
    public static final int CELL_TYPE_DOOUBLE = 2;
    public static final int CELL_TYPE_STRING = 3;
    public static final int CELL_TYPE_BOOLEAN = 4;
    public static final int CELL_TYPE_DATE = 5;

    private Map<Integer, SimpleCell> importTemplate = new HashMap<Integer, SimpleCell>();
    Message message;
    int lang;
    StringTransform trsf = new StringTransform();

    class SimpleCell{
        public int type = CELL_TYPE_NONE;
        public String fieldName = "";
        public Object insertedValue = null;
        public String errorMessage = "";
        public boolean result = false;

        public SimpleCell(String fieldName, int fieldType){
            this.fieldName = fieldName;
            this.type = fieldType;
        }

    }

    public class ImpStudent{
        public int studentid = 0;
        public String lastname = "";
        public String firstname = "";
        public String patronymic = "";
        public String adress = "";
        public String phone = "";
        public String login = "";
        public String password = "";
        public int departmentid = 0;
        public int classID = 0;
        public String startdate = "";
        public String birthdate = "";
        public String parents = "";
        public String staz_overall_startdate = "";
        public String staz_society_startdate = "";
        public String staz_post_startdate = "";
        public String edu_uz = "";
        public String edu_profession = "";
        public String edu_qualification = "";
        public boolean isUpgrade = false;
        public boolean isDirector = false;
        public boolean isInMaternityLeave = false;
        public boolean deleted = false;
    }

    public void initTemplate(){
        int startpos = 0;

        importTemplate.clear();
        importTemplate.put(startpos, new SimpleCell("firstname", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("lastname", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("patronymic", CELL_TYPE_STRING));

        importTemplate.put(++startpos, new SimpleCell("departmentid", CELL_TYPE_INTEGER));
        importTemplate.put(++startpos, new SimpleCell("classID", CELL_TYPE_INTEGER));

        importTemplate.put(++startpos, new SimpleCell("adress", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("phone", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("parents", CELL_TYPE_STRING));

        importTemplate.put(++startpos, new SimpleCell("birthdate", CELL_TYPE_DATE));
        importTemplate.put(++startpos, new SimpleCell("startdate", CELL_TYPE_DATE));
        importTemplate.put(++startpos, new SimpleCell("staz_overall_startdate", CELL_TYPE_DATE));
        importTemplate.put(++startpos, new SimpleCell("staz_society_startdate", CELL_TYPE_DATE));
        importTemplate.put(++startpos, new SimpleCell("staz_post_startdate", CELL_TYPE_DATE));

        importTemplate.put(++startpos, new SimpleCell("edu_uz", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("edu_profession", CELL_TYPE_STRING));
        importTemplate.put(++startpos, new SimpleCell("edu_qualification", CELL_TYPE_STRING));

        importTemplate.put(++startpos, new SimpleCell("isUpgrade", CELL_TYPE_BOOLEAN));
        importTemplate.put(++startpos, new SimpleCell("isDirector", CELL_TYPE_BOOLEAN));
        importTemplate.put(++startpos, new SimpleCell("isInMaternityLeave", CELL_TYPE_BOOLEAN));
        importTemplate.put(++startpos, new SimpleCell("departmentid", CELL_TYPE_BOOLEAN));
        importTemplate.put(++startpos, new SimpleCell("deleted", CELL_TYPE_BOOLEAN));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();


        try{
            if (!Access.isUserInRole(Constants.TUTOR, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            int contentLength = extractor.getInteger(request.getHeader("Content-Length"));
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Person person = (Person) session.getAttribute("person");
            this.message = new Message();
            this.lang = lang;
            String testName = "";
            int testSubjectID;
            int type = 0;

            TestsSearchParams params = new TestsSearchParams();

            if (contentLength > 14680064){ //14680064
                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_HAS_NOT_BEEN_IMPORTED, null));
                message.setMessage(MessageManager.getMessage(lang, TestMessages.TOO_LARGE_SIZE, null));
                message.messageType = Message.ERROR_MESSAGE;
                SimpleHandler handler = new SimpleHandler();
                HttpRequestParser parser = new HttpRequestParser(handler, request);
                parser.parse();
                params.extractParameterValues(handler, extractor);
                testName = extractor.getRequestString(handler.getParameter("name"));
                testSubjectID = extractor.getInteger(handler.getParameter("subject"));
                type = extractor.getInteger(handler.getParameter("type"));
            } else {
                MimeParser mimeParser = MimeParser.getInstance();
                ParsedData data = mimeParser.parseOnly(request);
                params.extractParameterValues(data, extractor);
                type = extractor.getInteger(data.getParameter("type"));
                testName = extractor.getRequestString(data.getParameter("name"));
                testSubjectID = extractor.getInteger(data.getParameter("subject"));

                boolean res = false;
                try{
                    initTemplate();
                    res = ImportFromXLSFile(data.getInputStream("file"));
                } catch (Exception exc){
                    Log.writeLog(exc);
                }

                if(!res){  // Импортирование закончилось ошибкой
                    message.setMessageType(Message.ERROR_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.STUDENTS_HAS_NOT_BEEN_IMPORTED, null));
                } else { // Импортирование прошло успешно
                    message.setMessageType(Message.INFORMATION_MESSAGE);
                    message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.STUDENTS_HAS_BEEN_IMPORTED, null));
                }
            }

            new PageGenerator().writeHtmlPage(new MainPageHandler(person, lang, message, getServletContext(),
                    testName, testSubjectID, params), pw, getServletContext());
            pw.flush();
            pw.close();
            return;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("text/html; charset=utf-8");
        PrintWriter pw = response.getWriter();
        DataExtractor extractor = new DataExtractor();

        try{
            if (!Access.isUserInRole(Constants.TUTOR, session)){
                session.invalidate();
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }
            int lang = extractor.getInteger(session.getAttribute("lang"));
            Person person = (Person) session.getAttribute("person");

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);

            new PageGenerator().writeHtmlPage(new MainPageHandler(person, lang, null, getServletContext(), "", 0, params),
                    pw, getServletContext());
            pw.flush();
            pw.close();
            return;
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public SimpleCell cellToData (HSSFCell cell, int rowNum, int colNum){
//
//        System.out.println("\n"+rowNum+",   "+colNum);
//
//        if(cell==null){
//            System.out.println("cell="+cell+",  "+rowNum+",   "+colNum);
//            System.out.println("getCellType=<unknown>");
//        } else
//        {
//            System.out.println("cell="+cell+",  "+rowNum+",   "+colNum);
//            System.out.println("getCellType="+cell.getCellType());
//        }
//


        SimpleCell res = new SimpleCell("", -1);

        try{
            if(importTemplate.containsKey(colNum)){
                SimpleCell sc = (SimpleCell)importTemplate.get(colNum);
                res.fieldName = sc.fieldName;
                res.type = sc.type;

                if(sc.type == CELL_TYPE_DATE){ // Date
                    if(cell == null || cell.equals("")){
                        res.insertedValue = "null";
                        res.result = true;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date date = new java.util.Date();
                        try{
                            date = df.parse(cell.getRichStringCellValue().toString());
                        } catch (Exception e) {
                            res.errorMessage = "yyyy-MM-dd";
                            return res;
                        }
                        res.insertedValue = "\'" + df.format(date) + "\'";
                        res.result = true;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                        java.util.Date date = new java.util.Date();
                        try{
                            if(HSSFDateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                                date = cell.getDateCellValue();
                            }
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            res.insertedValue = "\'" + df.format(date) + "\'";
                            res.result = true;
                        } catch (Exception e) {
                            res.errorMessage = "";
                            return res;
                        }
                    }
                  // -----------------------------------------------
                } else if (sc.type == CELL_TYPE_STRING){ // String
                    if(cell == null || cell.equals("")){
                        res.insertedValue = "\'\'";
                        res.result = true;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                        res.insertedValue = "\'" + cell.getRichStringCellValue().toString() + "\'";
                        res.result = true;
                    }
                  // ------------------------------------------------
                } else if (sc.type == CELL_TYPE_INTEGER){ // Integer
                    if(cell == null || cell.equals("")){
                        res.errorMessage = "";
                        return res;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                        Double d = (Double)cell.getNumericCellValue();
                        res.insertedValue = d.intValue();
                        res.result = true;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                        Object int_value = null;
                        String string_value = cell.getRichStringCellValue().toString();
                        try {
                            int_value = (Integer)Integer.parseInt(string_value);
                        } catch (Exception e) {
                            res.errorMessage = "";
                            return res;
                        }
                        res.insertedValue = int_value;
                        res.result = true;
                    }
                  // ------------------------------------------------
                } else if (sc.type == CELL_TYPE_BOOLEAN){ // Boolean
                    if(cell == null || cell.equals("")){
                        res.errorMessage = "";
                        return res;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                        Double d = (Double)cell.getNumericCellValue();
                        res.insertedValue = ((Integer)d.intValue()>0) ? 1 : 0;
                        res.result = true;
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
                        Object int_value = null;
                        String string_value = cell.getRichStringCellValue().toString();

                        if(string_value.equalsIgnoreCase("true") || string_value.equalsIgnoreCase("false")){
                            res.insertedValue = string_value.equalsIgnoreCase("true") ? 1 : 0;
                            res.result = true;
                        } else {
                            try {
                                int_value = (Integer)Integer.parseInt(string_value);
                                res.insertedValue = ((Integer)int_value>0) ? 1 : 0;
                                res.result = true;
                            } catch (Exception e) {
                                res.errorMessage = "";
                                return res;
                            }
                        }
                    } else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN){
                        Object boolean_value = cell.getBooleanCellValue();

                        System.out.println("boolean_value = " + boolean_value);
                        res.insertedValue = boolean_value;
                        res.result = true;
                    }
                }

            } else {
                // Не соответствие с шаблоном
                res.fieldName = "<unknown>";
                res.type = CELL_TYPE_NONE;
                res.errorMessage = "Не соответствие с шаблоном";
                return res;
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }

        return res;
    }

    public boolean ImportFromXLSFile(InputStream in) throws Exception {

        Connection con = null;
        Statement st = null;
        int notCorrectRowCount = 0;
        int insertErrorCount = 0;

        try{
            con = new ConnectionPool().getConnection();
            con.setAutoCommit(false);
            st = con.createStatement();

            HSSFWorkbook wb = new HSSFWorkbook(in); //fis
            if(wb == null) return false;

            HSSFSheet ws = wb.getSheetAt(0);
            if(ws == null) return false;

            int rowNum = ws.getLastRowNum() + 1;
            int colNum = ws.getRow(0).getLastCellNum();


            for (int i=1; i<rowNum; i++){
                HSSFRow row = ws.getRow(i);
                if(row==null) continue;

                String insertFields = "";
                String insertValues = "";
                boolean rowDatasIsCorrect = true;
                int notCorrectCellCount = 0;
                for (int j=0; j<colNum; j++){
                    HSSFCell cell = row.getCell((short)j);
                    SimpleCell cellValue = cellToData(cell, i, j);
                    if(cellValue == null){
                        notCorrectCellCount++;
                        message.addReason("", null);
                    } else if(!cellValue.result){
                        notCorrectCellCount++;

                        Properties prop = new Properties();
                        prop.setProperty("0", Integer.toString(i));
                        prop.setProperty("1", Integer.toString(j+1));
                        prop.setProperty("errorText", cellValue.errorMessage);

                        message.addReason(MessageManager.getMessage(lang, TestMessages.INCORRECT_DATA_FORMAT, prop), null);
                    } else {
                        if(insertFields.length() > 0) insertFields += ", ";
                        if(insertValues.length() > 0) insertValues += ", ";
                        insertFields += cellValue.fieldName;
                        insertValues += cellValue.insertedValue;
                    }
                }

                rowDatasIsCorrect = !(notCorrectCellCount > 0);
                if(rowDatasIsCorrect){
                    String sql = "INSERT INTO students (" + insertFields + ") VALUES (" + insertValues + ");";
                    Boolean sql_res = InsertIntoBase(st, sql);
                    if(!sql_res){
                        insertErrorCount ++ ;
                        Log.writeLog("ERROR ON IMPORT STUDENT  "+i+"\n"+sql);
                    }
                } else {
                    notCorrectRowCount++;
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        try{
            if(notCorrectRowCount == 0){ con.commit(); }
            if (st != null) st.close();
            if (con != null && !con.isClosed()) con.close();
        } catch(Exception exc){
            Log.writeLog(exc);
        }

        return (notCorrectRowCount == 0) && (insertErrorCount == 0);
    }

    public boolean InsertIntoBase(Statement st, String sql){
        try{
            st.execute(sql);
        } catch(Exception exc){
            Log.writeLog(exc);
            return false;
        }
        return true;
    }

}
