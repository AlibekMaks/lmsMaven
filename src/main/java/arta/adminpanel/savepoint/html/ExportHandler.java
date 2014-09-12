package arta.adminpanel.savepoint.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Message;

import java.io.PrintWriter;


public class ExportHandler extends TemplateHandler {

    String name;
    Message message;

    public ExportHandler(String name, Message message) {
        this.name = name;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("message")){
            if (message!=null) message.writeMessage(pw);
        } else if (name.equals("export folder")){
            if (name!=null) pw.print(name);
        }
    }

}
