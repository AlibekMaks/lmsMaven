package arta.tests.parser.logic.doc;

import arta.tests.test.Test;
import arta.tests.common.TestMessages;
import arta.tests.parser.logic.TestParser;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.InputStream;



public class DOCParser {

    InputStream stream;
    Test test;


    public DOCParser(int tutorID, InputStream stream) {
        this.stream = stream;
        test = new Test(0);
        test.tutorID = tutorID;
    }

    String QUESTION_START = "<question";
    String VARIANT_START = "<variant>";

    public boolean parse(Message message, int lang){
        boolean result = true;
        try{

            this.test.message = message;
            this.test.lang = lang;

            WordExtractor extractor = null;
            try{
                extractor = new WordExtractor(stream);
            } catch (Exception exc){
                Log.writeLog(exc);
                message.setMessageHeader(MessageManager.getMessage(lang, Constants.NOT_SAVED, null));
                message.setMessageType(Message.ERROR_MESSAGE);
                message.setMessage(MessageManager.getMessage(lang, TestMessages.INVALID_FILE, null));
                return false;
            }
            StringBuffer text = new StringBuffer(extractor.getText());
            TestParser parser = new TestParser(test);
            parser.setDelims(QUESTION_START, VARIANT_START, ">", "1>", "2>", "3>");
            parser.parse(text);

        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return result;
    }


    public Test getTest() {
        return test;
    }

    public InputStream getStream() {
        return stream;
    }


}
