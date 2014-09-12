package arta.tests.test.save;

import arta.tests.common.TestMessages;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Rand;
import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;

public class SuccessSaveHandler extends TemplateHandler {

    int lang;
    TestsSearchParams params;

    public SuccessSaveHandler(int lang, TestsSearchParams params) {
        this.lang = lang;
        this.params = params;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("test successfully saved")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TEST_WAS_SUCCESSFULLY_SAVED, null));
        } else if (name.equals("back to tests list href")){
            pw.print("testsedit?"+ params.getFullParams());
        } else if (name.equals("back to tests list")){
            pw.print(MessageManager.getMessage(lang, TestMessages.RETURN_TO_TESTS_LIST, null));
        }
    }

}
