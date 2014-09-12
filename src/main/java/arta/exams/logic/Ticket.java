package arta.exams.logic;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.exams.ExamMessages;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.Date;

public class Ticket {

    public int ticketID;
    public int examID;
    public int ticketNumber;
    public Date modified;

    public boolean IsExists = false;
    StringTransform trsf = new StringTransform();

    public ArrayList<TicketQuestion> ticketQuestions = new ArrayList<TicketQuestion>();
    public HashMap<Integer, TicketQuestion> loadQuestions = new HashMap<Integer, TicketQuestion>();

    public Ticket(){

    }

    public int getTicketID(){
        return this.ticketID;
    }

    public int getExamID(){
        return this.examID;
    }

    public int getTicketNumber(){
        return this.ticketNumber;
    }

    public Date getModified(){
        return this.modified;
    }

    public void setTicketID(int ticketID){
        this.ticketID = ticketID;
    }

    public void setExamID(int examID){
        this.examID = examID;
    }

    public void setTicketNumber(int ticketNumber){
        this.ticketNumber = ticketNumber;
    }

    public void setModified(Date modified){
        this.modified = modified;
    }


    public void load(Statement st, ResultSet res, boolean loadTicketQuestions, int lang) throws Exception{
        this.IsExists = false;

        res = st.executeQuery("SELECT t.examID, t.ticketNumber, t.modified \n" +
                              " FROM tickets t \n" +
                              " WHERE t.ticketID = "+this.ticketID+" \n"+
                              " LIMIT 1 ");
        if(res.next()){
            this.setExamID(res.getInt("examID"));
            this.setTicketNumber(res.getInt("ticketNumber"));
            this.setModified(res.getDate("modified"));
            if(loadTicketQuestions){
                this.loadTicketQuestions();
            }
            this.IsExists = true;
        }

    }

    public void loadById(int ticketID, int lang, boolean loadTicketQuestions){

        this.ticketID = ticketID;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            load(st, res, loadTicketQuestions, lang);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void loadTicketQuestions(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ticketQuestions.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT tq.ticketQuestionID as id, tq.questionkz, tq.questionru FROM tickets t \n" +
                                  " INNER JOIN ticket_questions tq ON t.ticketID = tq.ticketID \n" +
                                  " WHERE t.ticketID = " + this.ticketID);
            while(res.next()){
                TicketQuestion question = new TicketQuestion();
                 question.setTicketQuestionID(res.getInt("id"));
                 question.setQuestionkz(res.getString("questionkz"));
                 question.setQuestionru(res.getString("questionru"));
                ticketQuestions.add(question);
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void extractQuestionsWithParams(DataExtractor extractor, HttpServletRequest request, boolean isNew){

        try{
            loadQuestions.clear();

            Enumeration<String> parameterNames = request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String originalParamName = new String();
                originalParamName = paramName;

                if(paramName.startsWith("question") & paramName.length()>=12){   // questionkz_{[ticketQuestionID]}
                    paramName = paramName.substring(8); //  _{[ticketQuestionID]}
                    String type = paramName.substring(0, 2);
                    if(type.equals("kz") || type.equals("ru")){
                        int questionID = Integer.parseInt(paramName.substring(3));
                        String value = extractor.getRequestString(request.getParameter(originalParamName));

                        if(loadQuestions.containsKey(questionID)){
                            TicketQuestion question = (TicketQuestion)loadQuestions.get(questionID);
                            if(type.equals("kz")){
                                question.setQuestionkz(value);
                            } else {
                                question.setQuestionru(value);
                            }
                        } else {
                            TicketQuestion question = new TicketQuestion();
                            question.isNew = isNew;
                            question.setTicketQuestionID( (isNew)?0:questionID );
                            if(type.equals("kz")){
                                question.setQuestionkz(value);
                            } else {
                                question.setQuestionru(value);
                            }
                            loadQuestions.put(questionID, question);
                        }
                    }

                }

            }

            ticketQuestions.clear();
            for(int questionID : loadQuestions.keySet()){
                TicketQuestion question = new TicketQuestion();
                question = (TicketQuestion)loadQuestions.get(questionID);
                ticketQuestions.add(question);
            }

            loadQuestions.clear();

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
        }

    }

    public void save(int examID, int ticketNumber, boolean isNew, Message message, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if(isNew){
                st.execute("INSERT INTO tickets(examID, ticketNumber) VALUES("+examID+", "+ticketNumber+")", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    ticketID = res.getInt(1);
                }

                for(int i=0; i<ticketQuestions.size(); i++){
                    TicketQuestion question = (TicketQuestion)ticketQuestions.get(i);
                    st.execute("INSERT INTO ticket_questions(ticketID, questionkz, questionru) "+
                               " VALUES("+ticketID+", '"+trsf.getDBString(question.questionkz)+"', '"+trsf.getDBString(question.questionru)+"')", Statement.RETURN_GENERATED_KEYS);
                    res = st.getGeneratedKeys();
                    if (res.next()){
                        question.setTicketQuestionID(res.getInt(1));
                    }
                }
            } else {
                st.execute("UPDATE tickets SET ticketNumber = " + ticketNumber +
                           " WHERE ticketID = " + ticketID);

                st.execute("DELETE FROM ticket_questions WHERE ticketID = "+ ticketID);

                for(int i=0; i<ticketQuestions.size(); i++){
                    TicketQuestion question = (TicketQuestion)ticketQuestions.get(i);
                    st.execute("INSERT INTO ticket_questions(ticketID, questionkz, questionru) "+
                               " VALUES("+ticketID+", '"+trsf.getDBString(question.questionkz)+"', '"+trsf.getDBString(question.questionru)+"')", Statement.RETURN_GENERATED_KEYS);
                    res = st.getGeneratedKeys();
                    if (res.next()){
                        question.setTicketQuestionID(res.getInt(1));
                    }
                }
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }


    public boolean delete(int examID, Message message, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT r.studentid \n" +
                                  " FROM registrar r \n" +
                                  " INNER JOIN students s ON r.studentid = s.studentid \n" +
                                  " WHERE (s.deleted <> 1) AND (r.ticketID = "+ticketID+") \n" +
                                  " LIMIT 1");
            if (res.next()){
                message.setMessage(MessageManager.getMessage(lang, ExamMessages.THIS_TICKET_IS_USED, null));
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }

            con.setAutoCommit(false);

            st.execute("DELETE FROM tickets WHERE ticketID = "+ ticketID);

            st.execute("DELETE FROM ticket_questions WHERE ticketID = "+ ticketID);

            con.commit();

            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return false;
        } catch (Exception exc){
            try{
                if (con!=null && !con.getAutoCommit()) con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void getTestingTicketForStudent(int studentID, int mainTestingID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ticketID = 0;

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT r.ticketID \n" +
                    " FROM registrar r \n" +
                    " WHERE (r.studentid = "+studentID+") AND (r.mainTestingID = "+mainTestingID+") \n" +
                    " LIMIT 1");
            if (res.next()){
                ticketID = res.getInt("ticketID");
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void writeTicket(PrintWriter pw, int lang){

        try{

            Properties prop = new Properties();
            prop.setProperty("1", Integer.toString(getTicketNumber()));

            pw.print("<table border=1 width=\"100%\" class=\"table\" bordercolor=\"#000000\">\n" +
                    "   <tr class=\"coloredheader\">\n" +
                    "       <td colspan=2>\n" +
                    "           " +MessageManager.getMessage(lang, ExamMessages.TICKET_NUMBER_X, prop)+ "\n" +
                    "       </td>\n" +
                    "   </tr>\n" +
                    "  <tr>\n" +
                    "       <td width=\"30px\" align=\"center\">\n" + "<b>№</b>" +
                    "       </td>\n" +
                    "       <td width=\"*\" align=\"left\">\n" +
                                "<b>"+MessageManager.getMessage(lang, ExamMessages.WORDINF_OF_THE_QUESTION, null) + "</b>" +
                    "       </td>\n" +
                    "  </tr>\n");


                    for(int i=0; i<ticketQuestions.size(); i++){
                        pw.print("<tr>\n");
                            pw.print("<td align=\"center\">" + (i+1)+ ") </td>\n");

                            pw.print("<td  align=\"left\">\n");
                                pw.print(ticketQuestions.get(i).getQuestion(lang));
                            pw.print("</td>\n");
                        pw.print("</tr>\n");
                    }

            pw.print("</table>\n");

        } catch (Exception exc){
            Log.writeLog(exc);
        }

    }

}
