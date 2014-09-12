package arta.filecabinet.servlet.students.upload.file;

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

public class PageHandler extends TemplateHandler  {
    int lang;
    Message message;
    StringTransform trsf = new StringTransform();
    String testName;
    int testSubjectID;
    ServletContext servletContext;
    TestsSearchParams params;

    public PageHandler(int lang, Message message, String testName, int testSubjectID, ServletContext servletContext,
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
            pw.print(MessageManager.getMessage(lang, TestMessages.IMPORT_STUDENTS_FROM_XLS_FILE, null));
        }else if (name.equals("select xls file")){
            pw.print(MessageManager.getMessage(lang, TestMessages.SELECT_XLS_FILE, null));
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
        } else if (name.equals("return link")){
            pw.print("students?" + params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("download link")){
            pw.print("download?download=template.xls");
        } else if (name.equals("download title")){
            pw.print(MessageManager.getMessage(lang, Constants.DOWNLOAD_A_TEMPLATE_FOR_IMPORT, null));
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        }
    }
}
