package arta.tests.test;

import arta.check.logic.TestingTest;
import arta.tests.questions.Question;
import arta.tests.questions.closed.ClosedQuestion;
import arta.tests.questions.associate.AssociateQuestion;
import arta.tests.questions.sequence.SequenceQuestion;
import arta.tests.questions.open.OpenQuestion;
import arta.tests.common.DifficultySelect;
import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.*;
import arta.common.logic.util.Constants;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Test implements Serializable {

    public  static final int MAX_TEST_NAME_LENGTH = 1024;
    public static final int MAX_QUESTION_LENGTH = 2048;
    public static final int MAX_TAG_LENGTH = 1024;

    public int classID;
    public int subjectID;
    public int mainTestingID;
    public TestingTest test;

    public String testName;
    public int testID;
    public int testSubjectID;
    public int tutorID;
    public String tutorName;
    public Date created = new Date();
    public Date modified = new Date();
    public ArrayList <Question> questions = new ArrayList <Question> ();

    public int easy = 0;
    public int middle = 0;
    public int difficult = 0;
    public int total = 0;

    public Message message = new Message();
    public int lang;

    public String testSignature = Date.getCurrentTime() + "";

    private ArrayList <Integer> usedImgesID = new ArrayList <Integer> ();

    public Test(int testID, int lang) {
        this.testID = testID;
        this.lang = lang;
    }

    public Test(int testID) {
        this.testID = testID;
    }

    public Test(int testID, TestingTest test) {
        this.testID = testID;
        this.test = test;
    }

    public String getTestName(){
        if (testName == null)
            return "";
        return testName;
    }

    public boolean loadTestForEdit(ServletContext servletContext){

        Connection con = null;
        Statement st = null;
        Statement statement = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            statement = con.createStatement();

            res = st.executeQuery("SELECT tests.testName as name, " +
                                " tests.subjectID as subjectID, " +
                                " tutors.tutorID as tID, " +
                                " tutors.lastname as ln, " +
                                " tutors.firstname as fn, " +
                                " tutors.patronymic as p, " +
                                " tests.created as  cr," +
                                " tests.modified as md "+
                                " FROM tests "+
                                " LEFT JOIN tutors on tests.tutorID=tutors.tutorID " +
                                " WHERE tests.testID="+testID);

            if (res.next()){
                testName = res.getString("name");
                testSubjectID = res.getInt("subjectID");
                tutorID = res.getInt("tID");
                tutorName = res.getString("ln");
                String tmp = res.getString("fn");
                if (tmp!=null && tmp.length()>0){
                    tutorName += " "+tmp.substring(0, 1)+".";
                    tmp = res.getString("p");
                    if (tmp!=null && tmp.length()>0)
                        tutorName += tmp.substring(0, 1)+".";
                }
                created.loadDate(res.getString("cr"), Date.FROM_DB_CONVERT);
                modified.loadDate(res.getString("md"), Date.FROM_DB_CONVERT);
            }

            res = st.executeQuery("SELECT questions.questionID as id, " +
                    " questions.questionTypeID as type " +
                    " FROM questions WHERE questions.testID="+testID);
            int i=0;
            while (res.next()){
                int type = res.getInt("type");
                int id = res.getInt("id");
                Question question = null;
                if (type == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                    question = new OpenQuestion(servletContext,
                            Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS, lang, false );
                } else if (type == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                    question = new OpenQuestion(servletContext,
                            Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS, lang, false);
                } else if (type == Question.SEQUENCE_QUESTION){
                    question = new SequenceQuestion(servletContext, lang, false);
                } else if (type == Question.ASSOCIATE_QUESTION){
                    question = new AssociateQuestion(servletContext, lang, false);
                } else if (type == Question.CLOSED_QUESTION){
                    question = new ClosedQuestion(servletContext, lang);
                }
                if (question != null){
                    question.setQuestionID(id);
                    question.setNumber(i);
                    question.loadQuestion(statement);
                    questions.add(question);
                    i++;
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
            if (statement!=null) statement.close();
            if (res!=null) res.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return true;
    }

    public void addQuestion(ServletContext servletContext){
        questions.add(new ClosedQuestion(servletContext, Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS));
        questions.get(questions.size()-1).setNumber(questions.size()-1);
    }

    /**
     *
     * @param eventObjectID
     * @param host
     * @param params
     * @param isFromDesigner defined wether test has been edited with designer or
     * test is imported from file
     * if this parameter is true - test has been edited width designer and error messages should
     * include links to correct test
     * if this parameter is false - test is imported from file and no linka are needed, just text messages
     * @return
     */
    public boolean saveTest(int eventObjectID, String host, TestsSearchParams params, boolean isFromDesigner){

        boolean first = testID == 0;
        boolean saved = true;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform stringTransform = new StringTransform();
        message.refresh();

        boolean create = testID <= 0;

        if (testName == null || testName.length() == 0){
            if (isFromDesigner)
                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_NAME_NOT_DEFINED, null),
                        "testedit?questionNumber="+(questions.size()-1)+"&"+ Rand.getRandString() + "&" + params.getFullParams());
            else
                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_NAME_NOT_DEFINED, null));
            return false;
        }

        if (testName.length()>=MAX_TEST_NAME_LENGTH/2){
            if (isFromDesigner)
                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_NAME_TOO_LONG, null),
                        "testedit?questionNumber="+(questions.size()-1)+"&"+Rand.getRandString() + "&" + params.getFullParams());
            else
                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_NAME_TOO_LONG, null));
            return false;
        }
        
//        if (testSubjectID == 0){
//            if (isFromDesigner)
//                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_SUBJECT_NOT_SPECIFIED, null),
//                        "testedit?questionNumber="+(questions.size()-1)+"&"+Rand.getRandString() + "&" + params.getFullParams());
//            else
//                message.addReason(MessageManager.getMessage(lang, TestMessages.TEST_SUBJECT_NOT_SPECIFIED, null));
//            return false;
//        }

        extractImagesID();

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);
            
            st.execute("DELETE FROM openanswers WHERE openanswers.questionID IN (SELECT questions.questionID FROM " +
                    " questions WHERE questions.testID="+testID+") ");
            st.execute("DELETE FROM closedanswers WHERE closedanswers.questionID IN (SELECT questions.questionID FROM " +
                    " questions WHERE questions.testID="+testID+")");

            st.execute("DELETE FROM associatesecondtag WHERE associatesecondtag.firsttagID IN ( SELECT associatefirsttag.tagID " +
                    " FROM associatefirsttag WHERE associatefirsttag.questionID IN (SELECT questions.questionID FROM " +
                    " questions WHERE questions.testID="+testID+"))");
            st.execute("DELETE FROM associatefirsttag WHERE associatefirsttag.questionID IN (SELECT questions.questionID FROM " +
                    " questions WHERE questions.testID="+testID+")");

            st.execute("DELETE FROM sequencetags WHERE questionID IN (SELECT questions.questionID FROM " +
                    " questions WHERE questions.testID="+testID+")");
            st.execute("DELETE FROM questions WHERE testID="+testID);

            if (testID <= 0){
                st.execute("INSERT INTO tests(testname, subjectID, tutorID, created, modified) " +
                        " VALUES " +
                        " ('"+stringTransform.getDBString(testName, Varchar.QUESTION)+"', "+testSubjectID+", "+tutorID+", curdate(), curdate())",
                        Statement.RETURN_GENERATED_KEYS);
               res = st.getGeneratedKeys();
                if (res.next()){
                    testID = res.getInt(1);
                }

                StringBuffer str = new StringBuffer();
                for (int i=0; i<usedImgesID.size(); i++){
                    if (i>0)
                        str.append(" , ");
                    str.append(" "+usedImgesID.get(i)+" ");
                }

                if (str.length() > 0){
                    st.execute("UPDATE testimages SET testID=" + testID + " WHERE imageID IN ("+str+")");
                }

            } else {
                st.execute("UPDATE tests SET testname='"+stringTransform.getDBString(testName, Varchar.QUESTION)+"', " +
                        " modified=curdate() WHERE tests.testID="+testID);

                StringBuffer str = new StringBuffer();
                for (int i=0; i<usedImgesID.size(); i++){
                    if (i>0)
                        str.append(" , ");
                    str.append(" "+usedImgesID.get(i)+" ");
                }


                if (str.length()>0){
                    st.execute("DELETE FROM testimages WHERE imageid NOT IN ("+str.toString()+") AND testID="+testID+" " +
                            " AND (type="+Constants.TEST_CLIPBOARD_IMAGE_OBJECT+" OR type="+Constants.TEST_ANY_OBJECT+") ");
                } else {
                    st.execute("DELETE FROM testimages WHERE testID="+testID+" " +
                            " AND (type="+Constants.TEST_CLIPBOARD_IMAGE_OBJECT+" OR type="+Constants.TEST_ANY_OBJECT+") ");
                }
            }

            st.execute("DELETE FROM testimages WHERE tutorID="+tutorID+" " +
                    " AND abs(signature-"+Date.getCurrentTime()+") > "+Constants.MAX_IMAGE_NOT_SAVED_LIFE_TIME+" " +
                    " AND testID=0 AND " +
                    " (type="+ Constants.TEST_CLIPBOARD_IMAGE_OBJECT +" OR type="+Constants.TEST_ANY_OBJECT +")");

            easy = 0;
            middle = 0;
            difficult = 0;

            for (int i=0; i<questions.size(); i++){
                if (!questions.get(i).saveQuestion(st, res, testID, stringTransform)){
                    if (isFromDesigner)
                        message.addReason(questions.get(i).getError(),
                                "testedit?questionNumber="+questions.get(i).getNumber()+"&"+Rand.getRandString() + "&" + params.getFullParams());
                    else
                        message.addReason(questions.get(i).getError());

                    saved = false;
                } else {
                    if (questions.get(i).getDifficulty() == DifficultySelect.EASY){
                        easy ++;
                    } else if (questions.get(i).getDifficulty() == DifficultySelect.MIDDLE){
                        middle++;
                    } else if (questions.get(i).getDifficulty() == DifficultySelect.DIFFICULT){
                        difficult ++;
                    }
                }
            }

            if (saved){
                st.execute("UPDATE tests SET easy="+easy+", " +
                        " middle="+middle+", " +
                        " difficult="+difficult+" " +
                        " WHERE testID="+testID+"");

                int count = 0;
                res = st.executeQuery("SELECT COUNT(*) FROM testing_all_quetions t WHERE t.mainTestID = "+testID);
                if(res.next()){
                    count = res.getInt(1);
                }

                if(count==0){
                    st.execute("INSERT INTO testing_all_quetions (mainTestID, testID, easy, middle, difficult) " +
                               " VALUES ("+testID+", "+testID+", "+easy+", "+middle+", "+difficult+")");
                }
            }

            if (!saved){
                try{
                    con.rollback();
                    if (first)
                        testID = 0;
                    if (con!= null)con.close();
                    if (st!=null)st.close();
                    if (res!=null) res.close();
                    return saved;
                } catch (Exception e){
                    Log.writeLog(e);
                }
            }
            if (con!=null && !con.isClosed())
                con.commit();
        } catch (Exception exc){
            Log.writeLog(exc);
            saved = false;
            try{
                con.rollback();
            } catch (Exception e){
                Log.writeLog(exc);
            }
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
            if (res!=null) res.close();
        }catch (Exception exc){
            Log.writeLog(exc);
        }

        return saved;
    }

    public boolean checkTest(){
        boolean valid = true;
        return valid;
    }

    public boolean deleteTest(int eventObjectID, String host){
        message.refresh();
        boolean deleted = false;
        boolean possible = true;
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Properties properties = new Properties();
        try{
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            String testname = "";
            res = st.executeQuery("SELECT testname FROM tests WHERE testID="+testID+" ");
            if (res.next()){
                testname = res.getString(1);
            }
            properties.setProperty("test", testname);

            res = st.executeQuery("SELECT COUNT(*) FROM " +
                    " (testsfortesting LEFT JOIN testings ON testsfortesting.testingID=testings.testingID) " +
                    " WHERE (testID="+testID+" AND " +
                    " (testings.testingDate>curdate() OR testings.testingdate=curdate() ))");
            if (res.next() && res.getInt(1)>0){
                message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.TEST_WAS_NOT_DELETED, null));
                message.setMessage(MessageManager.getMessage(lang, TestMessages.TEST_IS_IN_USE_AND_WAS_NOT_DELETED, properties));
                message.setMessageType(Message.ERROR_MESSAGE);
                return false;
            }

            if (possible){
                con.setAutoCommit(false);
                st.execute("DELETE FROM openanswers WHERE openanswers.questionID IN (SELECT questions.questionID FROM " +
                        " questions WHERE questions.testID="+testID+") ");
                st.execute("DELETE FROM closedanswers WHERE closedanswers.questionID IN (SELECT questions.questionID FROM " +
                        " questions WHERE questions.testID="+testID+")");

                st.execute("DELETE FROM associatesecondtag WHERE associatesecondtag.firsttagID IN ( SELECT associatefirsttag.tagID " +
                        " FROM associatefirsttag WHERE associatefirsttag.questionID IN (SELECT questions.questionID FROM " +
                        " questions WHERE questions.testID="+testID+"))");
                st.execute("DELETE FROM associatefirsttag WHERE associatefirsttag.questionID IN (SELECT questions.questionID FROM " +
                        " questions WHERE questions.testID="+testID+")");

                st.execute("DELETE FROM sequencetags WHERE questionID IN (SELECT questions.questionID FROM " +
                        " questions WHERE questions.testID="+testID+")");

                st.execute("DELETE FROM questions WHERE testID="+testID);

                st.execute("DELETE FROM tests WHERE testID="+testID);

                st.execute("DELETE FROM testimages WHERE testID="+testID);

                con.commit();
                deleted = true;
            }
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.TEST_WAS_DELETED, null));
            message.setMessageType(Message.INFORMATION_MESSAGE);
        } catch (Exception exc){
            Log.writeLog(exc);
            deleted = false;
            try{
                if (!con.getAutoCommit())
                    con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.TEST_WAS_NOT_DELETED, properties));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        } finally {
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        return deleted;
    }

    public void deleteQuestion(int questionNumber){
        questions.remove(questionNumber);
        for (int i=0; i<questions.size(); i++){
            questions.get(i).setNumber(i);
        }
    }

    public void loadForCheck(int easy, int middle, int diffiult, Statement st, ResultSet res, int needed){

        try{
            ArrayList <Integer> ids = new ArrayList <Integer> ();
            ArrayList <Integer> types = new ArrayList <Integer> ();

            //load easy questions
            res = st.executeQuery("SELECT questions.questionID, questions.questionTypeID " +
                    " FROM questions WHERE " +
                    " questions.testID="+testID+" AND questions.difficulty="+DifficultySelect.EASY);
            while (res.next()){
                ids.add(res.getInt(1));
                types.add(res.getInt(2));
            }

            for (int i=0; i<easy; i++){
                if (ids.size() > 0){
                    int id = (int) (Math.random()*ids.size());
                    if (! (id>=0 && id<ids.size())){
                        id = 0;
                    }
                    Question q = null;
                    if (id>=0 && id<ids.size()){
                        if (types.get(id) == Question.ASSOCIATE_QUESTION){
                            q = new AssociateQuestion();
                        } else if (types.get(id) == Question.CLOSED_QUESTION){
                            q = new ClosedQuestion();
                        } else if (types.get(id) == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.SEQUENCE_QUESTION){
                            q = new SequenceQuestion();
                        }
                    }
                    if (q != null){
                        q.setClassID(test.classID);
                        q.setSubjectID(test.subjectID);
                        q.setSubjectName(test.subjectName);
                        q.setMainTestingID(test.mainTestingID);
                        q.setQuestionID(ids.get(id));
                        q.loadForCheck(st, res);
                        questions.add(q);
                        ids.remove(id);
                        types.remove(id);
                    }
                } else
                    break;
            }
            
            //load middle questions
            ids.clear();
            types.clear();
            res = st.executeQuery("SELECT questions.questionID, questions.questionTypeID " +
                    " FROM questions WHERE " +
                    " questions.testID="+testID+" AND questions.difficulty="+DifficultySelect.MIDDLE);
            while (res.next()){
                ids.add(res.getInt(1));
                types.add(res.getInt(2));
            }

            for (int i=0; i<middle; i++){
                if (ids.size() > 0){
                    int id = (int) (Math.random()*ids.size());
                    if (! (id>=0 && id<ids.size())){
                        id = 0;
                    }
                    Question q = null;
                    if (id>=0 && id<ids.size()){
                        if (types.get(id) == Question.ASSOCIATE_QUESTION){
                            q = new AssociateQuestion();
                        } else if (types.get(id) == Question.CLOSED_QUESTION){
                            q = new ClosedQuestion();
                        } else if (types.get(id) == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.SEQUENCE_QUESTION){
                            q = new SequenceQuestion();
                        }
                    }
                    if (q != null){
                        q.setClassID(test.classID);
                        q.setSubjectID(test.subjectID);
                        q.setSubjectName(test.subjectName);
                        q.setMainTestingID(test.mainTestingID);
                        q.setQuestionID(ids.get(id));
                        q.loadForCheck(st, res);
                        questions.add(q);
                        ids.remove(id);
                        types.remove(id);
                    }
                } else
                    break;
            }            

            //load difficult questions
            ids.clear();
            types.clear();
            res = st.executeQuery("SELECT questions.questionID, questions.questionTypeID " +
                    " FROM questions WHERE " +
                    " questions.testID="+testID+" AND questions.difficulty="+DifficultySelect.DIFFICULT);
            while (res.next()){
                ids.add(res.getInt(1));
                types.add(res.getInt(2));
            }

            for (int i=0; i<diffiult; i++){
                if (ids.size() > 0){
                    int id = (int) (Math.random()*ids.size());
                    if (! (id>=0 && id<ids.size())){
                        id = 0;
                    }
                    Question q = null;
                    if (id>=0 && id<ids.size()){
                        if (types.get(id) == Question.ASSOCIATE_QUESTION){
                            q = new AssociateQuestion();
                        } else if (types.get(id) == Question.CLOSED_QUESTION){
                            q = new ClosedQuestion();
                        } else if (types.get(id) == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                            q = new OpenQuestion(types.get(id));
                        } else if (types.get(id) == Question.SEQUENCE_QUESTION){
                            q = new SequenceQuestion();
                        }
                    }
                    if (q != null){
                        q.setClassID(test.classID);
                        q.setSubjectID(test.subjectID);
                        q.setSubjectName(test.subjectName);
                        q.setMainTestingID(test.mainTestingID);
                        q.setQuestionID(ids.get(id));
                        q.loadForCheck(st, res);
                        questions.add(q);
                        ids.remove(id);
                        types.remove(id);
                    }
                } else
                    break;
            }


            if (needed > 0){
                String cond = "";
                for (int i=0; i<questions.size(); i++){
                    if (i > 0)
                        cond += " OR ";
                    cond += " questionID="+questions.get(i).getQuestionID()+" ";
                }
                ids.clear();
                types.clear();
                res = st.executeQuery("SELECT questions.questionID, questions.questionTypeID " +
                        " FROM questions WHERE " +
                        " questions.testID="+testID+" AND NOT ("+cond+")");
                while (res.next()){
                    ids.add(res.getInt(1));
                    types.add(res.getInt(2));
                }
                for (int i=0; i<needed; i++){
                    if (ids.size() > 0){
                        int id = (int) (Math.random()*ids.size());
                        if (! (id>=0 && id<ids.size())){
                            id = 0;
                        }
                        Question q = null;
                        if (id>=0 && id<ids.size()){
                            if (types.get(id) == Question.ASSOCIATE_QUESTION){
                                q = new AssociateQuestion();
                            } else if (types.get(id) == Question.CLOSED_QUESTION){
                                q = new ClosedQuestion();
                            } else if (types.get(id) == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                                q = new OpenQuestion(types.get(id));
                            } else if (types.get(id) == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                                q = new OpenQuestion(types.get(id));
                            } else if (types.get(id) == Question.SEQUENCE_QUESTION){
                                q = new SequenceQuestion();
                            }
                        }
                        if (q != null){
                            q.setClassID(test.classID);
                            q.setSubjectID(test.subjectID);
                            q.setSubjectName(test.subjectName);
                            q.setMainTestingID(test.mainTestingID);
                            q.setQuestionID(ids.get(id));
                            q.loadForCheck(st, res);
                            questions.add(q);
                            ids.remove(id);
                            types.remove(id);
                        }
                    } else
                        break;
                }       
            }                                    
        } catch (Exception exc){
            Log.writeLog(exc);
        }        
    }

    private void extractImagesID(){
        usedImgesID.clear();
        for (int i=0; i<questions.size(); i++){
            ArrayList <Integer> tmp = new ArrayList <Integer> ();
            tmp = questions.get(i).getUsedImagesID();
            for (int j=0; j<tmp.size(); j++){
                usedImgesID.add(tmp.get(j));
            }
        }
    }

    public String getSignature(){
        return testSignature;
    }
}
