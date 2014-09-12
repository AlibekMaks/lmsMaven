package arta.tests.test.view;

import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageGenerator;
import arta.common.logic.util.Constants;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;
import arta.tests.test.Test;
import arta.tests.test.list.TestsSearchParams;
import arta.tests.common.QuestionViewMainHandler;
import arta.tests.common.QuestionViewHandler;
import arta.login.logic.Access;
import arta.filecabinet.logic.Person;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

public class QuestionViewServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

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

            if (session.getAttribute("test")!=null){
                test = (Test) session.getAttribute("test");
            }

            if (request.getParameter("print")!=null){
                int number = extractor.getInteger(request.getParameter("questionNumber"));
                if(number>=0 && number<test.questions.size()){
                    QuestionViewHandler handler = new QuestionViewHandler(lang, test, number, getServletContext(), params);
                    FileReader fileReader = new FileReader("tests/questions/question.common.preview.template.txt");
                    Parser parser = new Parser(fileReader.read(getServletContext()), pw, handler);
                    parser.parse();
                    return;
                }
            }
            boolean load = false;
            int testID = 0;
            int questionNumber = 0;

            String tmp = request.getParameter("load");
            if (tmp!=null)
                load = true;

            testID = extractor.getInteger(request.getParameter("testID"));
            questionNumber = extractor.getInteger(request.getParameter("questionNumber"));

            if (load){
                test = new Test(testID);
                if (!test.loadTestForEdit(getServletContext()))
                    test = null;
            }

            if (test == null){
//                pw.print("<script language=\"javascript\">top.location=\"testsList\"</script>");
                return;
            }
            session.setAttribute("test", test);

            QuestionViewMainHandler handler = new QuestionViewMainHandler(lang, person.getRoleID(),
                    test, questionNumber, getServletContext(), params);
            PageGenerator pageGenerator = new PageGenerator();
            pageGenerator.writeHtmlPage(handler, pw, getServletContext());

        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
}
