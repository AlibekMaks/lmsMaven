package arta.tests.servlets;

import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.*;
import arta.tests.test.Test;
import arta.tests.test.list.TestsSearchParams;
import arta.tests.test.save.FailureSaveMainHandler;
import arta.tests.test.save.SuccessSaveMainHandler;
import arta.tests.common.QuestionMainHandler;
import arta.tests.questions.Question;
import arta.tests.questions.sequence.SequenceQuestion;
import arta.tests.questions.sequence.SingleSequenceTag;
import arta.tests.questions.open.OpenQuestion;
import arta.tests.questions.open.SingleAnswer;
import arta.tests.questions.closed.ClosedQuestion;
import arta.tests.questions.associate.AssociateQuestion;
import arta.tests.questions.associate.SingleAssociateTag;
import arta.filecabinet.logic.Person;
import arta.login.logic.Access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;

public class QuestionServlet extends HttpServlet {

    public static final int DO_NITHING_OPTION = 0;
    public static final int SAVE_TEST_OPTION = 1;
    public static final int ADD_QUESTION_OPTION = 2;
    public static final int DELETE_QUESTION_OPTION = 3;
    public static final int GO_TO_FIRST_OPTION = 4;
    public static final int GO_TO_NEXT_OPTION = 6;
    public static final int GO_TO_PREVIOUS_OPTION = 5;
    public static final int GO_TO_LAST_OPTION = 7;
    public static final int DELETE_VARIANT_OPTION = -2;
    public static final int ADD_VARIANT_OPTION = -3;
    public static final int LOAD_OPTION = 2;
    public static final int EXISTS_OPTION = 3;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            MimeParser parser = MimeParser.getInstance();
            ParsedData data = parser.parseOnly(request);
            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(data, extractor);

            Test test = null;
            // 0 - do nothing
            // 1 = save test
            // 2 - add question
            // 3 - delete question
            //4 - first
            // 5 - next
            // 6 - previous
            // 7 - last
            int option = extractor.getInteger(data.getParameter("option"));

            if (session.getAttribute("test")==null){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                return;
            } else {
                try{
                    test = (Test) session.getAttribute("test");
                } catch (Exception e){
                    Log.writeLog(e);
                }
            }

			int curQuestionNumber = extractor.getInteger(data.getParameter("currentQuestionNumber"));
			int questionNumber = -1;

			if (data.getParameter("questionNumber") != null)
				questionNumber = extractor.getInteger(data.getParameter("questionNumber"));
			else
				questionNumber = curQuestionNumber;

			String tmp = data.getParameter("testName");
			if (tmp != null)
				test.testName = new String(tmp.getBytes(Encoding.ISO),Encoding.UTF);
			else
				test.testName = "";

			tmp = data.getParameter("subject");
			if (tmp != null) {

				int subjectID = 0;
				try {
					subjectID = Integer.parseInt(tmp);
				} catch (Exception e) {
                    //Не найден предмет
				}
				if (subjectID > 0) {
					test.testSubjectID = subjectID;
				}
			}

             int questionType = extractor.getInteger(data.getParameter("questionType"));
             if (questionType > 0 && curQuestionNumber >= 0 && curQuestionNumber < test.questions.size() &&
                     questionType != test.questions.get(curQuestionNumber).getQuestionType()){
                int qn = test.questions.get(curQuestionNumber).getNumber();
                if (questionType == Question.ASSOCIATE_QUESTION){
                    AssociateQuestion q =   new AssociateQuestion(getServletContext(), lang, true);
                    q.question = test.questions.get(curQuestionNumber).getQuestion();
                    test.questions.set(curQuestionNumber,  q);
                } else if (questionType == Question.CLOSED_QUESTION){
                    ClosedQuestion q = new ClosedQuestion(getServletContext(), lang);
                    q.question = test.questions.get(curQuestionNumber).getQuestion();
                    test.questions.set(curQuestionNumber, q);
                } else if (questionType == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS){
                    OpenQuestion q  = new OpenQuestion(getServletContext(), Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS, lang, true);
                    q.question = test.questions.get(curQuestionNumber).getQuestion();
                    test.questions.set(curQuestionNumber, q);
                } else if (questionType == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                    OpenQuestion q = new OpenQuestion(getServletContext(), Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS, lang, true);
                    q.question = test.questions.get(curQuestionNumber).getQuestion();
                    test.questions.set(questionNumber, q);
                } else if (questionType == Question.SEQUENCE_QUESTION){
                    SequenceQuestion q = new SequenceQuestion(getServletContext(), lang, true);
                    q.question = test.questions.get(curQuestionNumber).getQuestion();
                    test.questions.set(questionNumber, q);
                }
                 test.questions.get(curQuestionNumber).setNumber(qn);
             }

             if (option == GO_TO_FIRST_OPTION){
                 questionNumber = 0;
             } else if (option == GO_TO_PREVIOUS_OPTION){
                 questionNumber = curQuestionNumber - 1;
             } else if (option == GO_TO_NEXT_OPTION){
                 questionNumber = curQuestionNumber + 1;
             } else if (option == GO_TO_LAST_OPTION){
                 questionNumber = test.questions.size() - 1;
             }

             Object [] obj = data.getParameterNames().toArray();
             if (curQuestionNumber>=0 && curQuestionNumber<test.questions.size()){
                 test.questions.get(curQuestionNumber).parseParameters(obj, data);
             }

             if (option == DELETE_VARIANT_OPTION){
                 int number = extractor.getInteger(data.getParameter("variant"));
                 if (test.questions.get(curQuestionNumber).getQuestionType() == Question.ASSOCIATE_QUESTION){
                    if (number>=0 && number < ((AssociateQuestion)test.questions.get(curQuestionNumber)).tags.size()){
                        ((AssociateQuestion)test.questions.get(curQuestionNumber)).tags.remove(number);
                    }
                 } else if (test.questions.get(curQuestionNumber).getQuestionType() == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS ||
                         test.questions.get(curQuestionNumber).getQuestionType() == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                    if (number>=0 && number< ((OpenQuestion)test.questions.get(curQuestionNumber)).answers.size()){
                        ((OpenQuestion)test.questions.get(curQuestionNumber)).answers.remove(number);
                    }
                 } else if (test.questions.get(curQuestionNumber).getQuestionType() == Question.SEQUENCE_QUESTION){
                     if (number>=0 && number<((SequenceQuestion)test.questions.get(curQuestionNumber)).tags.size()){
                         ((SequenceQuestion)test.questions.get(curQuestionNumber)).tags.remove(number);
                     }
                 }
             } else if (option == ADD_VARIANT_OPTION){
                 if (test.questions.get(curQuestionNumber).getQuestionType()==Question.ASSOCIATE_QUESTION){
                     ((AssociateQuestion)test.questions.get(curQuestionNumber)).tags.add(new SingleAssociateTag());
                 } else if(test.questions.get(curQuestionNumber).getQuestionType() == Question.OPEN_WITH_PLURAL_ANSWERS_QUESTIONS ||
                         test.questions.get(curQuestionNumber).getQuestionType() == Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS){
                    ((OpenQuestion)test.questions.get(curQuestionNumber)).answers.add(new SingleAnswer());
                 } else if (test.questions.get(curQuestionNumber).getQuestionType() == Question.SEQUENCE_QUESTION){
                    ((SequenceQuestion)test.questions.get(curQuestionNumber)).tags.add(new SingleSequenceTag());
                 }
             }

             if (option == SAVE_TEST_OPTION){
                 //System.out.println("SAVE_TEST_OPTION = " + SAVE_TEST_OPTION);
                test.tutorID = person.getPersonID();
                if (!test.saveTest(person.getPersonID(), request.getRemoteHost(), params, true)){
                    PageGenerator pageGenerator = new PageGenerator();
                    FailureSaveMainHandler handler = new FailureSaveMainHandler(lang, person.getRoleID(), test.message,
                            getServletContext(), params);
                    pageGenerator.writeHtmlPage(handler,  pw,  getServletContext());
                } else {
                    PageGenerator pageGenerator = new PageGenerator();
                    SuccessSaveMainHandler handler = new SuccessSaveMainHandler(lang, person.getRoleID(), getServletContext(), params);
                    pageGenerator.writeHtmlPage(handler, pw, getServletContext());
                }
             } else if (option == ADD_QUESTION_OPTION){
                 questionNumber = test.questions.size();
                 OpenQuestion q = new OpenQuestion(getServletContext(), Question.OPEN_WITH_SINGLE_ANSWER_QUESTIONS, lang, true);
                 q.answers.get(0).isRight = true;
                 q.setNumber(test.questions.size());
                 test.questions.add(q);
                 QuestionMainHandler handler = new QuestionMainHandler(lang, person.getRoleID(),
                         test.questions.get(questionNumber), test.questions.size(), questionNumber ,
                         getServletContext(), test.testName, test.testSubjectID, test.testID, params, test.getSignature(), person.getPersonID());
                 PageGenerator pageGenerator = new PageGenerator();
                 pageGenerator.writeHtmlPage(handler, pw, getServletContext());
             } else if (option == DELETE_QUESTION_OPTION){
                 test.deleteQuestion(questionNumber);
                 if (test.questions.size()>0){
                     if (questionNumber>0){
                         questionNumber -- ;
                     } else {
                         questionNumber ++ ;
                     }
                 } else {
                    questionNumber = -1;
                 }

                 if (questionNumber>=0 && questionNumber<test.questions.size()){
                     QuestionMainHandler handler = new QuestionMainHandler(lang, person.getRoleID(),
                             test.questions.get(questionNumber), test.questions.size(), questionNumber ,
                             getServletContext(), test.testName, test.testSubjectID, test.testID, params, test.getSignature(), person.getPersonID());
                     PageGenerator pageGenerator = new PageGenerator();
                     pageGenerator.writeHtmlPage(handler, pw, getServletContext());
                 } else {
                     /*
                    TutorTestsListMain handler = new TutorTestsListMain(lang, 0, null, null,
                    null, null, null,
                    0, 30, getServletContext(), person.getRoleID(), person.getPersonID(),
                    null);
                    PageGenerator pageGenerator = new PageGenerator();
                    pageGenerator.writeHtmlPage(handler, pw, getServletContext());
                    */
                 }

             } else {
                 if (questionNumber>=0 && questionNumber<test.questions.size()){
                     QuestionMainHandler handler = new QuestionMainHandler(lang, person.getRoleID(),
                             test.questions.get(questionNumber), test.questions.size(), questionNumber ,
                             getServletContext(), test.testName, test.testSubjectID, test.testID, params, test.getSignature(), person.getPersonID());
                     PageGenerator pageGenerator = new PageGenerator();
                     pageGenerator.writeHtmlPage(handler, pw, getServletContext());
                 } else {
                  /*  TutorTestsListMain handler = new TutorTestsListMain(lang, 0, null, null,
                    null, null, null,
                    0, 30, getServletContext(), person.getRoleID(), person.getPersonID(),
                    null);
                    PageGenerator pageGenerator = new PageGenerator();
                    pageGenerator.writeHtmlPage(handler, pw, getServletContext());
                    */
                 }
             }

             pw.flush();
             pw.close();

         }catch(Exception exc){
             Log.writeLog(exc);
         }

     }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

         try{
            HttpSession session = request.getSession();
            response.setContentType("text/html; charset=utf-8");
            PrintWriter pw = response.getWriter();
            DataExtractor extractor = new DataExtractor();

            if (!Access.isUserInRole(Constants.TUTOR, session)){
                pw.print(Constants.RETURN_TO_MAIN_PAGE);
                pw.flush();
                pw.close();
                return;
            }

            Person person = (Person) session.getAttribute("person");
            int lang = extractor.getInteger(session.getAttribute("lang"));

            TestsSearchParams params = new TestsSearchParams();
            params.extractParameterValues(request, extractor);


             Test test = null;

             if (session.getAttribute("test") != null){
                 test = (Test) session.getAttribute("test");
             }

             int testID = extractor.getInteger(request.getParameter("testID"));
             int option = extractor.getInteger(request.getParameter("option"));
             if (option == EXISTS_OPTION){
                 test = new Test(0, lang);
                 test.tutorID = person.getPersonID();
                 session.setAttribute("test", test);
             } else if (option == LOAD_OPTION){
                 test = new Test(testID, lang);
                 test.loadTestForEdit(getServletContext());
                 session.setAttribute("test", test);
             }

             int questionNumber = extractor.getInteger(request.getParameter("questionNumber"));

             if (test.questions.size() == 0){
                 OpenQuestion q = new OpenQuestion(getServletContext(), OpenQuestion.OPEN_WITH_SINGLE_ANSWER_QUESTIONS,
                         lang, true);
                 q.answers.get(0).isRight = true;
                 test.questions.add(q);
             }

             if (questionNumber <0 || questionNumber >= test.questions.size())
                questionNumber = 0;

             QuestionMainHandler handler = new QuestionMainHandler(lang, person.getRoleID(),
                     test.questions.get(questionNumber), test.questions.size(), questionNumber,
                     getServletContext(), test.testName, test.testSubjectID, test.testID, params, test.getSignature(), person.getPersonID());
             PageGenerator pageGenerator = new PageGenerator();
             pageGenerator.writeHtmlPage(handler, pw, getServletContext());

             pw.flush();
             pw.close();

         }catch(Exception exc){
             Log.writeLog(exc);
         }
    }


}
