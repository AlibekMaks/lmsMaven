package arta.tests.test.save;

import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class FailureSaveHandler extends TemplateHandler {

    int lang;
    Message message ;
    TestsSearchParams params;

    public FailureSaveHandler(int lang, Message message, TestsSearchParams params) {
        this.lang = lang;
        this.message = message;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("test was not saved")){
            //pw.print(MessageManager.getMessage(lang, TestMessages.TEST_WAS_NOT_SAVED, null));
        } else if (name.equals("see errors")){
            //pw.print(MessageManager.getMessage(lang, TestMessages.SEE_ERRORS_LIST, null));
        } else if (name.equals("errors")){
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.TEST_WAS_NOT_SAVED, null));
            message.setMessage(MessageManager.getMessage(lang, TestMessages.SEE_ERRORS_LIST, null));
            message.writeMessage(pw);
            /*pw.println("<UL>");
                for (int i=0; i<errors.size(); i++){
                    pw.println("<LI>");
                    pw.print(errors.get(i));
                }
            pw.println("</UL>");*/
        } else if (name.equals("edit href")){

        } else if (name.equals("edit")){

        }
    }

}
