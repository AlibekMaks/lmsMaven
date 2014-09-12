package arta.tests.parser.html;

import arta.subjects.logic.SubjectsSelect;
import arta.tests.common.TestMessages;
import arta.tests.parser.logic.ParseData;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Message;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class ImportPageHandler extends TemplateHandler {

    int lang;
    Message message;
    StringTransform trsf = new StringTransform();
    String testName;
    int testSubjectID;
    ServletContext servletContext;
    TestsSearchParams params;

    public ImportPageHandler(int lang, Message message, String testName, int testSubjectID, ServletContext servletContext, 
                             TestsSearchParams params) {
        this.lang = lang;
        this.message = message;
        this.testName = testName;
        this.testSubjectID = testSubjectID;
        this.servletContext = servletContext;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("select file")){
            pw.print(MessageManager.getMessage(lang, TestMessages.SELECT_FILE, null));
        } else if (name.equals("import header")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TESTS_IMPORT, null));
        } else if (name.equals("import from")){
            pw.print(MessageManager.getMessage(lang, TestMessages.IMPORT_FROM, null));
        } else if (name.equals("xls")){
            pw.print(ParseData.XLS_TYPE);
        } else if (name.equals("from xls")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MSEXCEL_BOOK, null));
        } else if (name.equals("doc")){
            pw.print(ParseData.DOC_TYPE);
        } else if (name.equals("from doc")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MSWORD_DOCUMENT, null));
        } else if (name.equals("mht")){
            pw.print(ParseData.MHT_TYPE);
        } else if (name.equals("from mht")){
            pw.print(MessageManager.getMessage(lang, TestMessages.WEB_PAGE, null));
        } else if (name.equals("import")){
            pw.print(MessageManager.getMessage(lang, TestMessages.IMPORT, null));
        } else if (name.equals("file: ")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FILE_PATH, null));
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                    message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("test name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_NAME, null));
        } else if (name.equals("test name value")){
            pw.print(trsf.getHTMLString(testName));
        }
//        else if (name.equals("test subject")){
//        	pw.print(MessageManager.getMessage(lang, TestMessages.TEST_SUBJECT, null));
//        } else if (name.equals("test subject select")){
//        	SubjectsSelect select = new SubjectsSelect(lang);
//        	select.writePostSelect("subject", 60, testSubjectID, pw, false);
//        }
//        <tr>
//        <td><b>
//                {[test subject]}
//        </td>
//        <td>
//                {[test subject select]}
//        </td>
//        </tr>
        else if (name.equals("return link")){
            pw.print("testsedit?" + params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
