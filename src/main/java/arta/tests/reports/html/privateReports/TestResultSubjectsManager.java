package arta.tests.reports.html.privateReports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arta.check.logic.Testing;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.settings.logic.Settings;

public class TestResultSubjectsManager {

	public static boolean save(Map<Integer, TestResultSubject> subjectsByTests, int testingId, int studentId, boolean isDirector, Settings settings) {

        System.out.println("TestResultSubjectsManager: save:  testingId = "+testingId +" studentId="+ studentId);
        Connection con = null;
        PreparedStatement st = null;
        ResultSet res = null;

        boolean result_TestingIsPassed = true;
        
        try {
            con = new ConnectionPool().getConnection();
            st = con.prepareStatement("INSERT INTO test_result (testing_id, subject_id, student_id, questions_count, right_answers_count, persentage, isPassed) SELECT ?, ?, ?, ?, ?, ?, ? FROM tests WHERE testid = ?");
            
//            for (int testId : subjectsByTests.keySet()) {
//            	TestResultSubject subject = subjectsByTests.get(testId);
//            	st.setInt(1, testingId);
//                st.setInt(2, subject.subjectId);
//            	st.setInt(3, studentId);
//            	st.setInt(4, subject.questionsCount);
//            	st.setInt(5, subject.rightAnswersCount);
//            	st.setInt(6, testId);
//            	st.execute();
//    		}

            for (int id : subjectsByTests.keySet()) {
                TestResultSubject subject = subjectsByTests.get(id);
                subject.rightAnswersPersentage = (int)Math.round((double)subject.rightAnswersCount * 100 / subject.questionsCount);

                if(settings.usesubjectball){
                    if(subject.rightAnswersPersentage < settings.subjects.get(subject.subjectId)){
                        subject.isPassed = false;
                        result_TestingIsPassed = false;
                    }
                } else {
                    if(isDirector & subject.rightAnswersPersentage < settings.attestatThresholdForDirectors){
                        result_TestingIsPassed = false;
                    } else if(!isDirector & subject.rightAnswersPersentage < settings.attestatThresholdForEmployee){
                        result_TestingIsPassed = false;
                    }
                }

                st.setInt(1, testingId);
                st.setInt(2, subject.subjectId);
                st.setInt(3, studentId);
                st.setInt(4, subject.questionsCount);
                st.setInt(5, subject.rightAnswersCount);
                st.setInt(6, subject.rightAnswersPersentage);
                st.setBoolean(7, subject.isPassed);
                st.setInt(8, subject.testID);
                st.execute();
            }
            
        } catch (Exception exc) {
        	Log.writeLog(exc);
            result_TestingIsPassed = false;
		} finally {
            try{
                if (res!=null) res.close();
                if (st!=null) st.close();
                if (con!=null) con.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
		return result_TestingIsPassed;
	}
	
	public static List<TestResultSubject> load(int testingId, int mainTestingID, int studentId, int lang) {
		Connection con = null;
        Statement st = null;
        ResultSet res = null;


        
        List<TestResultSubject> result = new ArrayList<TestResultSubject>();
        
        try {
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            
            res = st.executeQuery(
            		"SELECT subject_id, name" + Languages.getLang(lang) + " as nim, questions_count, right_answers_count " +
            		"FROM test_result " +
            		"JOIN subjects ON test_result.subject_id = subjects.subjectid " +
            		"WHERE testing_id = " + testingId + " AND student_id = " + studentId);
            
            while (res.next()) {
            	TestResultSubject subject = new TestResultSubject();
            	subject.studentId = studentId;
            	subject.subjectName = res.getString("nim");
            	subject.subjectId = res.getInt("subject_id");
            	subject.questionsCount = res.getInt("questions_count");
            	subject.rightAnswersCount = res.getInt("right_answers_count");
                subject.ball = 78978901;
            	subject.rightAnswersPersentage = (int)Math.round((double)subject.rightAnswersCount * 100 / subject.questionsCount);
            	result.add(subject);
            }
            
        } catch (Exception exc) {
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
        
        return result;
		
	}

    public static List<TestResultSubject> myload(int testingId, int mainTestingID, int studentId, int lang) {
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Statement st1 = null;
        ResultSet res1 = null;



        List<TestResultSubject> result = new ArrayList<TestResultSubject>();

        try {
            con = new ConnectionPool().getConnection();
            st = con.createStatement();
            st1 = con.createStatement();

            res = st.executeQuery("SELECT t.subject_id, s.name"+Languages.getLang(lang)+" as nim, t.questions_count, t.right_answers_count, t.isPassed, t.persentage " +
                                  " FROM test_result t " +
                                  " JOIN subjects s ON t.subject_id = s.subjectid " +
                                  " WHERE t.testing_id = "+testingId+" AND t.student_id = " + studentId + " "+
                                  " GROUP BY t.subject_id " +
                                  " ORDER BY s.nameru ASC ");

            while (res.next()) {
                TestResultSubject subject = new TestResultSubject();
                subject.studentId = studentId;
                subject.subjectName = res.getString("nim");
                subject.subjectId = res.getInt("subject_id");
                subject.questionsCount = res.getInt("questions_count");
                subject.rightAnswersCount = res.getInt("right_answers_count");
                subject.isPassed = res.getBoolean("isPassed");
                //subject.rightAnswersPersentage = res.getInt("persentage");
//
//
//                res1 = st1.executeQuery("SELECT questions_count, right_answers_count " +
//                                        " FROM test_result t " +
//                                        " WHERE (t.subject_id = "+subject.subjectId+") AND (t.testing_id = "+testingId+") AND (t.student_id = " + studentId+")");
//                while (res1.next()){
//                    subject.questionsCount += res1.getInt("questions_count");
//                    subject.rightAnswersCount += res1.getInt("right_answers_count");
//                }
//                subject.ball = (int)Math.round((double)subject.rightAnswersCount * 100 / subject.questionsCount);
                subject.rightAnswersPersentage = (int)Math.round((double)subject.rightAnswersCount * 100 / subject.questionsCount);
                result.add(subject);
            }

        } catch (Exception exc) {
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

        return result;

    }
}
