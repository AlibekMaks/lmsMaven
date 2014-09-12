package arta.tests.imageExchange;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 25.03.2008
 * Time: 14:33:11
 * To change this template use File | Settings | File Templates.
 */
public class InsertObjectHandler extends TemplateHandler {


    int lang;
    int testingID;
    boolean uploaded ;
    String mimetype;
    int id;
    int width, height;

    public InsertObjectHandler(int lang, int testingID, boolean uploaded) {
        this.lang = lang;
        this.testingID = testingID;
        this.uploaded = uploaded;
    }


    public InsertObjectHandler(int lang, int testingID, boolean uploaded,
                               String mimetype, int id, int width, int height) {
        this.lang = lang;
        this.testingID = testingID;
        this.uploaded = uploaded;
        this.mimetype = mimetype;
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("testingID")){
            pw.print(testingID);
        } else if(name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if(name.equals("type")){
            pw.print(Constants.TEST_ANY_OBJECT);
        } else if(name.equals("insert object")){
            pw.print(MessageManager.getMessage(lang, TestMessages.INSERT_OBJECT));
        } else if(name.equals("upload")){
            pw.print(MessageManager.getMessage(lang, TestMessages.UPLOAD));
        } else if(name.equals("setTimeout(insertNewObject(), 0);")){
            if (uploaded){
                pw.print("setTimeout('insertNewObject()', 0);");
            }
        } else if (name.equals("new object link")){
            if (uploaded){
                pw.print(new ObjectToHTMLTransformer().transform(mimetype, id, width, height));
            }
        } else if (name.equals("width")){
            pw.print(MessageManager.getMessage(lang, TestMessages.WIDTH));
        } else if (name.equals("height")){
            pw.print(MessageManager.getMessage(lang, TestMessages.HEIGHT));
        } else if (name.equals("description")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FOLLOWING_PARAMTERS_APPLIED_ONLY_TO_IMG_AND_FLASH));
        } else if (name.equals("file")){
            pw.print(MessageManager.getMessage(lang, TestMessages.OBJECT_FILE_PATH));
        }
    }
}
