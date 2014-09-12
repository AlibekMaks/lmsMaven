package arta.tests.reports.html.privateReports;

import arta.tests.questions.Question;
import arta.tests.common.TestMessages;
import arta.common.logic.messages.MessageManager;
import arta.check.logic.Testing;

public class TestReportBuilder {

    public StringBuffer getReport(Testing testing, int lang){
        StringBuffer res = new StringBuffer();
        for (int i=0; i<testing.questions.size(); i++){
            res.append(getQuestionReport(lang, testing.questions.get(i), i+1));
        }
        return res;
    }

    private StringBuffer getQuestionReport(int lang, Question q, int number){
        StringBuffer res = new StringBuffer();
        res.append("<table border=0 width=\"100%\" class=\"table\">");
        res.append("<tr><td width=\"16px\" valign=\"top\">");
        if (q.isRightAnswer()){
            res.append("<img src=\"images/right.gif\" width=\"16px\" height=\"16px\" border=0>");
        } else {
            res.append("<img src=\"images/wrong.gif\" width=\"16px\" height=\"16px\" border=0>");
        }
        res.append("</td><td width=\"100%\"><b>");
        res.append(MessageManager.getMessage(lang, TestMessages.QUESTION, null));
        res.append(" ");
        res.append(number);
        res.append("</td></tr>");
        res.append("<tr><td align=\"left\" colspan=2>");
        res.append(q.getQuestion());
        res.append("</td></tr>");
        res.append("<tr><td colspan=2>");
        res.append(q.getReportVariants());
        res.append("</td></tr>");
        res.append("</table>");
        return res;
    }

}
