package arta.exams.logic;

import arta.common.lock.LockManager;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.exams.ExamMessages;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class Exam {

    private int examID;
    private String examName;
    private int questionCount;
    private boolean IsExists = false;

    public ArrayList<Ticket> tickets = new ArrayList<Ticket>();

    public static LockManager examsManager = new LockManager();

    public static String getLockString (int examID, int personID){
        return examID + "_" + personID;
    }

    public boolean examIsExists(){
        return this.IsExists;
    }

    public void loadByRecordNumber(SearchParams params, int recordNumber, int lang){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT classID "+new ExamsManager().getCondition(params)+ ExamsManager.ORDER+
                                        "LIMIT "+recordNumber+", 1");
            if (res.next()){
                examID = res.getInt(1);
            }

            load(lang, st, res, true);

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

    }

    public void loadById(int examID, int lang, boolean loadQuestions){

        this.examID = examID;

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            load(lang, st, res, loadQuestions);
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

    private void load(int lang, Statement st, ResultSet res, boolean loadQuestions) throws Exception{

        boolean found = false;
        this.tickets.clear();
        this.IsExists = false;

        res = st.executeQuery("SELECT e.examname as name, e.question_count as qcount \n"+
                              " FROM exams e WHERE e.examID = "+ examID + "\n"+
                              " LIMIT 1");
        if(res.next()){
            this.setExamName(res.getString("name"));
            this.setQuestionCount(res.getInt("qcount"));
            found = true;
            this.IsExists = true;
        }

        if(found & loadQuestions){
            res = st.executeQuery("SELECT t.ticketID, t.examID, t.ticketNumber, t.modified \n" +
                                  " FROM tickets t \n" +
                                  " WHERE t.examID = " + examID);
            while (res.next()){
                Ticket ticket = new Ticket();
                 ticket.setTicketID(res.getInt("ticketID"));
                 ticket.setExamID(res.getInt("examID"));
                 ticket.setTicketNumber(res.getInt("ticketNumber"));
                 ticket.setModified(res.getDate("modified"));
                 ticket.loadTicketQuestions();
                tickets.add(ticket);
            }
        }
    }

    public boolean save(Message message, int lang, SearchParams params, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        String lockString = getLockString(examID, personID);

        examsManager.execute(lockString);

        if (!check(message, lang)){
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            examsManager.finished(lockString);
            return false;
        }

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (examID > 0){
                st.execute("UPDATE exams SET examname='"+trsf.getDBString(examName)+"', " +
                           " question_count = " + questionCount +
                           " WHERE examID="+ examID);
            } else {
                st.execute("INSERT INTO exams (examname, question_count) \n"+
                           " VALUES ('"+trsf.getDBString(examName)+"', "+questionCount+")",
                           Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next()){
                    examID = res.getInt(1);
                }
            }

            load(lang, st, res, true);

            message.setMessageHeader(MessageManager.getMessage(lang, Constants.SAVED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
            return false;
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_SAVED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            examsManager.finished(lockString);
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean delete(Message message, int lang, int personID){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String lockString = getLockString(examID, personID);
        examsManager.execute(lockString);

        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            ExamsManager manager = new ExamsManager();
            manager.search(examID);

            if(manager.exams.size() == 0){
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            } else {
                SimpleExam exam = (SimpleExam)manager.exams.get(0);
                if(exam.isUsed){
                    message.setMessage(MessageManager.getMessage(lang, ExamMessages.THIS_EXAM_IS_USED, null));
                    message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_DELETED, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;
                }
            }

            con.setAutoCommit(false);

            st.execute("DELETE FROM exams WHERE examID = " + examID);

            st.execute("DELETE FROM ticket_questions "+
                       " WHERE ticket_questions.ticketID IN (SELECT t.ticketID FROM tickets t WHERE t.examID = " + examID + ")");

            st.execute("DELETE FROM tickets WHERE examID = " + examID );

            con.commit();

            message.setMessageHeader(MessageManager.getMessage(lang, Constants.DELETD, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
        } catch (Exception exc){
            try{
                if (con!=null && !con.getAutoCommit()) con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            Log.writeLog(exc);
            message.setMessageHeader(MessageManager.getMessage(lang,  Constants.NOT_DELETED, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            examsManager.finished(lockString);
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }

        return true;
    }

    private boolean check(Message message, int lang){
        boolean checked = true;
        if (examName == null || examName.trim().length() == 0){
            message.addReason(MessageManager.getMessage(lang, ExamMessages.EXAM_NAME_CAN_NOT_BE_EMPTY, null));
            checked = false;
        } else {
            if (examName.length()> Varchar.NAME/2){
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, Constants.NAME, null));
                message.addReason(MessageManager.getMessage(lang, FileCabinetMessages.TOO_LONG_VALUE, prop));
                checked = false;
            }
        }

        if(this.questionCount<0 | this.questionCount > 999){
            message.addReason(MessageManager.getMessage(lang, ExamMessages.NUMBER_OF_QUESTIONS_ASKED_INT_THE_WRONG_FORMAT, null));
            checked = false;
        } else if(this.questionCount == 0){
            message.addReason(MessageManager.getMessage(lang, ExamMessages.NUMBER_OF_QUESTIONS_IN_A_SINGLE_TICKET_CAN_NOT_BE_IESS_THAN_ONE, null));
            checked = false;
        }

        return checked;
    }

    public int getExamID() {
        return examID;
    }

    public String getExamName() {
        if (examName == null) return "";
        return examName;
    }

    public int getQuestionCount(){
        if(questionCount<0) return 0;
        return questionCount;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public void setQuestionCount(int questionCount){
        this.questionCount = questionCount;
    }

}
