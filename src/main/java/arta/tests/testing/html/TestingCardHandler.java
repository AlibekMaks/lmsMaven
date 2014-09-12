package arta.tests.testing.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.util.HTMLCalendar;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.tests.common.TestMessages;
import arta.tests.testing.logic.Testing;
import arta.tests.testing.logic.TestingMessages;
import arta.tests.testing.logic.TestingStudent;
import arta.tests.testing.servlet.TestingServlet;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class TestingCardHandler extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    Testing testing;

    Message message;
    HTMLCalendar calendar;

    public TestingCardHandler(ServletContext servletContext, int lang,
                              Testing testing, Message message) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.testing = testing;
        this.message = message;
        calendar = new HTMLCalendar(lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("name value")){
            pw.print(testing.getTestingName(lang));
        } else if (name.equals("maxlength")){
            pw.print(Varchar.NAME/2);
        } else if (name.equals("duration")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_TIME, null));
        } else if (name.equals("duration value")){
            pw.print(testing.getTestingTime());
        } else if (name.equals("to appoint testing")){
            Properties prop = new Properties();
            pw.print(MessageManager.getMessage(lang, TestMessages.TO_APPOINT_TESTING1, prop));
            pw.print("&nbsp;<a href=\"testsfortesting?&sitting="+
                    "; \" onClick='document.getElementById(\"option\").value="+ TestingServlet.TESTS_OPEN_OPTION +"; " +
                    " document.getElementById(\"form\").submit(); return false;' class=\"href\">");
            pw.print(MessageManager.getMessage(lang, TestMessages.TO_APPOINT_TESTING2, prop));
            pw.print("</a>&nbsp;");
            pw.print(MessageManager.getMessage(lang, TestMessages.TO_APPOINT_TESTING3, prop));
        } else if (name.equals("appoint")){
            if (testing.getTestingID() > 0)
                pw.print(MessageManager.getMessage(lang, Constants.CHANGE, null));
            else
                pw.print(MessageManager.getMessage(lang, Constants.APPOINT, null));
        } else if (name.equals("onclick button")){
            pw.print("document.getElementById(\"option\").value="+TestingServlet.SAVE_OPTION+"; " +
                    " document.getElementById(\"form\").submit();");
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_PARAMETERS));
        } else if (name.equals("options")){
            new Parser(new FileReader("testing/testing.card.options.html").read(servletContext), pw,
                    new TestingOptionsHandler(lang, testing, null, null)).parse();
        } else if (name.equals("accept")){
            pw.print(MessageManager.getMessage(lang, Constants.ACCEPT));
        }  else if (name.equals("date")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_DATE));
        } else if (name.equals("date input")){
            calendar.printInput(pw, testing.getTestingDate(), "date", "form");
        } else if (name.equals("time start")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_START_TIME));
        } else if (name.equals("time start inputs")){
            testing.getTestingStartTime().writeTimeSelects(lang, pw, "hour", "minute");
        } else if (name.equals("time finish")){
            pw.print(MessageManager.getMessage(lang, TestingMessages.TESTING_FINISH_TIME));
        } else if (name.equals("time finish inputs")){
            testing.getTestingFinishTime().writeTimeSelects(lang, pw, "hourfinish", "minutefinish");
        } else if (name.equals("students tree data")) {
        	
        	Connection con = null;
            Statement st = null;
            ResultSet res = null;
            
            ArrayList <SimpleObject> classes = new ArrayList <SimpleObject> ();
            Map<Integer, List<SimpleObject>> studentsMap = new HashMap<Integer, List<SimpleObject>>();
            
            try{

                con = new ConnectionPool().getConnection();
                st = con.createStatement();


                res = st.executeQuery("SELECT DISTINCT classes.classID as id, " +
                        " classes.classname as name " +
                        " FROM classes JOIN students ON students.classid = classes.classid "+
                        " WHERE (students.deleted <> 1) "+
                        " ORDER BY classname, classes.classID");

                while (res.next()){
                    SimpleObject classObject = new SimpleObject();
                    classObject.name = res.getString("name");
                    classObject.id = res.getInt("id");
                    classes.add(classObject);
                }
                
                for(SimpleObject eachClass: classes) {
                	List<SimpleObject> classStudents = new ArrayList<SimpleObject>();
                	
                	res = st.executeQuery(
                			" SELECT s.studentid, s.lastname, s.firstname, s.patronymic " +
                            " FROM students s " +
                            " WHERE (s.classid=" + eachClass.id +") AND (s.deleted <> 1)"+
                            " ORDER BY lastname, firstname, patronymic");
                	
                	/*String s = "SELECT s.studentid, s.lastname, s.firstname, s.patronymic, case when cnt is null then 0 else 1 end existence " +
                			"FROM students s LEFT JOIN (" +
                				"select count(*) as cnt, studentid from testingstudents where testingid = 2 group by studentid" +
                				") t ON s.studentid = t.studentID " +
                			"GROUP BY studentid";*/
                    
                    while (res.next()){
                        SimpleObject studentObject = new SimpleObject();
                        String middleName = res.getString("patronymic");
                        if (middleName == null) middleName = "";
                        studentObject.name = res.getString("lastname") + " " + res.getString("firstname") + " " + middleName;
                        studentObject.id = res.getInt("studentid");
                        classStudents.add(studentObject);
                    }
                    studentsMap.put(eachClass.id, classStudents);
                }

            } catch(Exception exc){
                Log.writeLog(exc);
            } finally{
                try{

                    if (con != null) con.close();
                    if (st != null) st.close();
                    if (res != null) res.close();

                } catch(Exception  exc){
                    Log.writeLog(exc);
                }
            }
            
            
            for (int classesIndex = 0; classesIndex < classes.size(); classesIndex++) {
            	SimpleObject eachClass = classes.get(classesIndex);
            	if (classesIndex>0) pw.print(",");
            	pw.print("{");
            	pw.print("\"data\":\"" + eachClass.name + "\",");
            	pw.print("\"attr\": {\"id\":\"" + eachClass.id + "\"},");
            	pw.print("\"children\": [");
            	
            	List<SimpleObject> classStudents = studentsMap.get(eachClass.id);
            	for (int studentIndex = 0; studentIndex < classStudents.size(); studentIndex++) {
            		SimpleObject classStudent = classStudents.get(studentIndex);
            		if (studentIndex > 0) pw.print(",");
            		pw.print("{");
                	pw.print("\"data\":\"" + classStudent.name + "\",");
                	pw.print("\"attr\": {\"id\":\"std_" + classStudent.id + "\"}");
                	pw.print("}");
            	}
            	
            	pw.print("]");
            	pw.print("}");
            }
            
        } else if (name.equals("check students that are in the testing")) {
        	
        	pw.print("var studentsTree = $.jstree._reference('#students-tree');");
        	for (int studentid : testing.students) {
        		pw.print("studentsTree.check_node('#std_" + studentid + "');");
        	}
        }
    }
}
