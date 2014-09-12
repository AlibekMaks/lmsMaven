package arta.tests.testing.servlet;


import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.Person;
import arta.filecabinet.logic.SearchParams;
import arta.login.logic.Access;
import arta.tests.testing.html.*;
import arta.tests.testing.logic.TestForTesting;
import arta.tests.testing.logic.Testing;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class AddTestsForTestingServlet extends HttpServlet {

    public static final int SEARCH_TO_ADD_OPTION = 1;
    public static final int ADD_OPTION = 2;
    public static final int CHANGE_OPTION = 3;
    public static final int DELETE_OPTION = -1;

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            SearchParams params = new SearchParams();
            params.extractParameterValues(request, extractor);

            Testing testing = (Testing) session.getAttribute("testing");
            //testing.setTutorID(person.getPersonID());
            int testingID = extractor.getInteger(request.getParameter("testingID"));
            int option = extractor.getInteger(request.getParameter("option"));

//            if(testing == null && testingID == 0 && option == 3){
//                testing = null;
//                testing = new Testing();
//                if (testingID == 0){
//                    testing.setTutorID(person.getPersonID());
//                } else {
//                    testing.setTestingID(testingID);
//                    testing.setTutorID(person.getPersonID());
//                    testing.load(lang);
//                }
////                new PageGenerator().writeHtmlPage(new TestingCardMainHandler(person, lang, getServletContext(),
////                        testing, null), pw, getServletContext());
//                //session.setAttribute("testing", testing);
//            }



            if (option == -1){
                int testID = extractor.getInteger(request.getParameter("testID"));
                int mainTestID = extractor.getInteger(request.getParameter("mainTestID"));
                testing.deleteTest(testID, testing.mainTestID);

                new PageGenerator().writeHtmlPage(new MyAddedTestsMainHandler(lang, person.getRoleID(),
                        getServletContext(), testing, null), pw, getServletContext());

            } else if (option == ADD_OPTION || option == SEARCH_TO_ADD_OPTION){
                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()){
                    String name = (String) en.nextElement();
                    if (name.length()>6 && name.substring(0, 4).equals("tID_")){
                        name = name.substring(4, name.length());
                        int id = extractor.getInteger(name.substring(0, name.indexOf("_")));
                        name = name.substring(name.length()-1, name.length());
                        if (id>0){
                            TestForTesting test = null;
                            test = testing.getTest(id);
                            if (test == null){
                                test  = new TestForTesting();
                                test.alldifficult = extractor.getInteger(request.getParameter("all_"+id+"_d"));
                                test.alleasy = extractor.getInteger(request.getParameter("all_"+id+"_e"));
                                test.allmiddle = extractor.getInteger(request.getParameter("all_"+id+"_m"));
                                test.testID = id;
                                test.name = extractor.getRequestString(request.getParameter("tn_"+id));
                                testing.tests.add(test);
                            }
                            if (name.equals("e")){
                                test.easy = extractor.getInteger(request.getParameter("tID_"+id+"_e"));
                                if (test.easy > test.alleasy)
                                    test.easy = test.alleasy;
                                if (test.easy < 0)
                                    test.easy = 0;
                            } else if (name.equals("m")){
                                test.middle = extractor.getInteger(request.getParameter("tID_"+id+"_m"));
                                if (test.middle > test.allmiddle)
                                    test.middle = test.allmiddle;
                                if (test.middle < 0)
                                    test.middle = 0;
                            } else if (name.equals("d")){
                                test.difficult = extractor.getInteger(request.getParameter("tID_"+id+"_d"));
                                if (test.difficult > test.alldifficult)
                                    test.difficult = test.alldifficult;
                                if (test.difficult < 0)
                                    test.difficult = 0;
                            }
                        }
                    }
                }

                for (int i=testing.tests.size()-1; i>=0; i--){
                    if (testing.tests.get(i).easy == 0 && testing.tests.get(i).middle == 0 &&
                            testing.tests.get(i).difficult == 0)
                        testing.tests.remove(i);
                }

                if (request.getParameter("add_tests") != null){  // Добавлены тесты в данное тестирование
                    new PageGenerator().writeHtmlPage(new MyAddedTestsMainHandler(lang, person.getRoleID(),
                            getServletContext(), testing, null), pw, getServletContext());
                } else { // Нажатие на кнопку добавление тестов
                    new PageGenerator().writeHtmlPage(new MyTestsListMain(person, lang, params, getServletContext(),
                            testing), pw, getServletContext());
                }


            } else if (option == CHANGE_OPTION){  // Показать все тесты для этого тестирования
                String change = extractor.getRequestString(request.getParameter("change"));
                String isNew = extractor.getRequestString(request.getParameter("param"));

                if(isNew != null && !isNew.equals("")){ //Первый раз
                    testing = null;
                    testing = new Testing();
                    testing.mainTestID = extractor.getInteger(request.getParameter("mainTestID"));
                    session.setAttribute("testing", testing);
                } else {
                    testing = (Testing) session.getAttribute("testing");
                }

                System.out.println("### testingID = " + testingID);

                if (testingID == 0){
                    testing.setTutorID(person.getPersonID());
                    testing.person = person;
                    testing.myload(lang);
                    System.out.println("testing.myload(lang);");
                } else {
                    testing.setTestingID(testingID);
                    testing.setTutorID(person.getPersonID());
                    testing.person = person;
                    testing.load(lang);
                }
                //session.setAttribute("testing", testing);

//                if(testing == null ) {
//                    testing = new Testing();
//                    session.setAttribute("testing", testing);
//                }
//                // ?????????????????
//                if (testingID == 0){
//                    testing.setTutorID(person.getPersonID());
//                } else {
//                    testing.setTestingID(testingID);
//                    testing.setTutorID(person.getPersonID());
//                    testing.load(lang);
//                }

                //session.setAttribute("testing", testing);

                Enumeration en = request.getParameterNames();
                while (en.hasMoreElements()){
                    String name = (String) en.nextElement();
                    if (name.length()>6 && name.substring(0, 4).equals("tID_")){
                        name = name.substring(4, name.length());
                        int id = extractor.getInteger(name.substring(0, name.indexOf("_")));
                        name = name.substring(name.length()-1, name.length());
                        if (id>0){
                            TestForTesting test = testing.getTest(id);
                            if (test == null) continue;
                            if (name.equals("e")){
                                test.easy = extractor.getInteger(request.getParameter("tID_"+id+"_e"));
                                if (test.easy > test.alleasy)
                                    test.easy = test.alleasy;
                                if (test.easy < 0)
                                    test.easy = 0;
                            } else if (name.equals("m")){
                                test.middle = extractor.getInteger(request.getParameter("tID_"+id+"_m"));
                                if (test.middle > test.allmiddle)
                                    test.middle = test.allmiddle;
                                if (test.middle < 0)
                                    test.middle = 0;
                            } else if (name.equals("d")){
                                test.difficult = extractor.getInteger(request.getParameter("tID_"+id+"_d"));
                                if (test.difficult > test.alldifficult)
                                    test.difficult = test.alldifficult;
                                if (test.difficult < 0)
                                    test.difficult = 0;
                            }
                        }
                    }
                }

                for (int i=testing.tests.size()-1; i>=0; i--){
                    if (testing.tests.get(i).easy == 0 && testing.tests.get(i).middle == 0 &&
                            testing.tests.get(i).difficult == 0)
                        testing.tests.remove(i);
                }

                new PageGenerator().writeHtmlPage(new MyAddedTestsMainHandler(lang, person.getRoleID(),
                        getServletContext(), testing, null), pw, getServletContext());


                if(change != null && !change.equals("")){ //Была нажата кнопка "Изменить"
                    Message message = new Message();
                    testing.mysave(message, lang, person.getPersonID());
                }

                session.setAttribute("testing", testing); // ?????????????????
            }


            pw.flush();
            pw.close();

        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

}
