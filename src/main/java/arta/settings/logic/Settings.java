package arta.settings.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.SimpleObject;
import arta.subjects.logic.SubjectsStatus;
import arta.timetable.designer.logic.LessonConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;


public class Settings {

    public int excellent, good, satisfactory, maxMarkValue, attestatThresholdForDirectors, attestatThresholdForEmployee;
    public boolean usesubjectball,usetotalball, show_report, show_answers, recommend_candidates, student_test_access;
    public int recommend_candidates_month;



    public HashMap<Integer, Integer> subjects = new HashMap<Integer, Integer>();


    public void save(Message message, int lang){

        Connection con = null;
        Statement st = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            if (excellent <= good || excellent <= satisfactory || good <= satisfactory
                     || excellent < 0 || good < 0 || satisfactory < 0
                    || excellent > maxMarkValue || good > maxMarkValue ||
                    satisfactory > maxMarkValue
                    || maxMarkValue <= 0 || attestatThresholdForDirectors <= 0 || attestatThresholdForEmployee <= 0 ||
                    attestatThresholdForDirectors > maxMarkValue || attestatThresholdForEmployee > maxMarkValue ){
                message.setMessageHeader(MessageManager.getMessage(lang, SettingsMessages.SETTINGS_HAS_NOT_BEEN_SAVED));
                message.setMessage(MessageManager.getMessage(lang, SettingsMessages.WRONG_MARKS_VALUES));
                message.setMessageType(Message.ERROR_MESSAGE);
                return;
            }

            con.setAutoCommit(false);

            if (Constants.MAX_MARK_VALUE != maxMarkValue && Constants.MAX_MARK_VALUE != 0){
                st.execute("UPDATE registrar SET mark=ROUND((mark*"+maxMarkValue+")/"+Constants.MAX_MARK_VALUE+") " +
                        " WHERE mark>0 ");
            }

            st.execute("DELETE FROM settings");


            st.execute("INSERT INTO  settings (excellent, good, satisfactory, maxmark, attestat_threshold_director, "+
                       " attestat_threshold_employee, usesubjectball,usetotalball, show_report, show_answers, recommend_candidates, student_test_access, recommend_candidates_month) " +
                       " VALUES " +
                       " ("+excellent+", "+good+", "+satisfactory+","+maxMarkValue+","+attestatThresholdForDirectors+","+
                        attestatThresholdForEmployee+", "+usesubjectball+", "+usetotalball+","+show_report+", "+show_answers+", "+recommend_candidates+", "+student_test_access+", "+recommend_candidates_month+")");

            Constants.EXCELLENT_MARK = excellent;
            Constants.GOOD_MARK = good;
            Constants.SATISFACTORY_MARK = satisfactory;
            Constants.MAX_MARK_VALUE = maxMarkValue;
            Constants.USE_SUBJECT_BALL = usesubjectball;
            Constants.USE_TOTAL_BALL = usetotalball;
            Constants.SHOW_REPORT = show_report;
            Constants.RECOMMEND_CANDIDATES = recommend_candidates;

            con.commit();

            message.setMessageType(Message.INFORMATION_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, SettingsMessages.SETTINGS_HAS_BEEN_SAVED));


        } catch (Exception exc){
            Log.writeLog(exc);

            message.setMessageHeader(MessageManager.getMessage(lang, SettingsMessages.SETTINGS_HAS_NOT_BEEN_SAVED));
            message.setMessageType(Message.ERROR_MESSAGE);

            try{
                con.rollback();
            } catch(Exception e){
                Log.writeLog(e);
            }

        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void load(){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT excellent, good, satisfactory, maxmark, attestat_threshold_director as att_d, "+
                                  " attestat_threshold_employee as att_e, usesubjectball as usb,usetotalball as usb_total, show_report as sr, "+
                                  " show_answers as sa, recommend_candidates as rc, student_test_access as sta, recommend_candidates_month as rcm "+
                                  " FROM settings ");
            if (res.next()){
                Constants.EXCELLENT_MARK = res.getInt("excellent");
                Constants.GOOD_MARK = res.getInt("good");
                Constants.SATISFACTORY_MARK = res.getInt("satisfactory");
                Constants.MAX_MARK_VALUE = res.getInt("maxmark");
                Constants.USE_SUBJECT_BALL = res.getBoolean("usb");
                Constants.USE_TOTAL_BALL = res.getBoolean("usb_total");
                Constants.SHOW_REPORT = res.getBoolean("sr");
                Constants.SHOW_ANSWER = res.getBoolean("sa");
                Constants.RECOMMEND_CANDIDATES = res.getBoolean("rc");

                this.attestatThresholdForDirectors = res.getInt("att_d");
                this.attestatThresholdForEmployee = res.getInt("att_e");
                this.excellent = Constants.EXCELLENT_MARK;
                this.good = Constants.GOOD_MARK;
                this.satisfactory = Constants.SATISFACTORY_MARK;
                this.maxMarkValue = Constants.MAX_MARK_VALUE;
                this.usesubjectball = Constants.USE_SUBJECT_BALL;
                this.usetotalball = Constants.USE_TOTAL_BALL;
                this.show_report = Constants.SHOW_REPORT;
                this.show_answers = Constants.SHOW_ANSWER;
                this.recommend_candidates = Constants.RECOMMEND_CANDIDATES;
                this.student_test_access = res.getBoolean("sta");
                this.recommend_candidates_month = res.getInt("rcm");
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con!=null && !con.isClosed()) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void loadSubjectsMark(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{
            subjects.clear();

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT s.subjectID AS sid, s.preferredMark as mark FROM subjects s where (s.isArchive = "+
                    SubjectsStatus.ACTIVE.statusId + ")");
            while (res.next()){
                subjects.put(res.getInt("sid"), res.getInt("mark"));
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null && !con.isClosed()) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public boolean isUsetotalball() {
        return usetotalball;
    }

    public void setUsetotalball(boolean usetotalball) {
        this.usetotalball = usetotalball;
    }

}
