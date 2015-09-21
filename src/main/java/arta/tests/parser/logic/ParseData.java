package arta.tests.parser.logic;

import arta.tests.common.TestMessages;
import arta.tests.parser.logic.xls.XLSParser;
import arta.tests.parser.logic.doc.DOCParser;
import arta.tests.parser.logic.mht.MHTParser;
import arta.tests.test.list.TestsSearchParams;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;

import java.io.InputStream;


public class ParseData {

    public static final int XLS_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int MHT_TYPE = 3;

    InputStream test;
    int type;
    private final String fileName;

    public ParseData(InputStream test, int type, String fileName) {
        this.test = test;
        this.type = type;
        this.fileName = fileName;
    }


    public void setType(int type) {
        this.type = type;
    }

    public void setTest(InputStream test) {
        this.test = test;
    }

    public InputStream getTest() {
        return test;
    }

    public int getType() {
        return type;
    }

    public boolean check(Message message, int lang) {
        if (type != ParseData.DOC_TYPE && type != ParseData.MHT_TYPE && type != ParseData.XLS_TYPE) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.SELECT_IMPORT_TYPE, null));
            return false;
        }
        if (type == ParseData.DOC_TYPE && !fileName.endsWith(".doc")) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_TYPE_AND_EXTENSION_CONFLICT, null));
            return false;
        }
        if (type == ParseData.MHT_TYPE && !fileName.endsWith(".mht")) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_TYPE_AND_EXTENSION_CONFLICT, null));
            return false;
        }
        if (type == ParseData.XLS_TYPE && !fileName.endsWith(".xls")) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.FILE_TYPE_AND_EXTENSION_CONFLICT, null));
            return false;
        }
        if (test == null) {
            message.setMessageType(Message.ERROR_MESSAGE);
            message.setMessageHeader(MessageManager.getMessage(lang, TestMessages.SELECT_FILE, null));
            return false;
        }
        return true;
    }

    public boolean parserAndSave(int tutorID, int eventObjectID, String host, Message message, int lang, String testname, int testSubjectID){
        boolean result = true;
        if (type == ParseData.XLS_TYPE){
            XLSParser parser = new XLSParser(tutorID, test);
            parser.getTest().testName = testname;
            parser.getTest().testSubjectID = testSubjectID;
            result = parser.parse(message, lang);
            if (result){
                result = parser.getTest().saveTest(eventObjectID, host, new TestsSearchParams(), false);
            }
        } else if (type == ParseData.DOC_TYPE){
            DOCParser parser = new DOCParser(tutorID, test);
            parser.getTest().testName = testname;
            parser.getTest().testSubjectID = testSubjectID;
            result = parser.parse(message, lang);
            if (result){
                result = parser.getTest().saveTest(eventObjectID, host, new TestsSearchParams(), false);
            }
        } else if (type == ParseData.MHT_TYPE){
            MHTParser parser = new MHTParser(tutorID, test);
            parser.getTest().testName = testname;
            parser.getTest().testSubjectID = testSubjectID;
            parser.parse(message, lang);
            result = parser.getTest().saveTest(eventObjectID, host, new TestsSearchParams(), false);
        }
        return result;
    }
}
