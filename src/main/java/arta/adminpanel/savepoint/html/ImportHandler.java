package arta.adminpanel.savepoint.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Message;

import java.io.PrintWriter;


public class ImportHandler extends TemplateHandler {

    String name;
    Message message;

    public ImportHandler(String name, Message message) {
        this.name = name;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("import folder")){
            if (this.name!=null) pw.print(this.name);
        } else if (name.equals("message")){
            if (message!=null) message.writeMessage(pw);
        }
    }

}
