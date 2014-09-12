package arta.log.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Message;
import arta.common.logic.messages.MessageManager;
import arta.log.LogMessages;

import java.io.PrintWriter;


public class LogPageHandler extends TemplateHandler {

    int lang;
    Message message;


    public LogPageHandler(int lang, Message message) {
        this.lang = lang;
        this.message = message;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("header")){
            pw.print(MessageManager.getMessage(lang, LogMessages.LOGS_REGSTER, null));
        } else if (name.equals("message")){
            if (message != null){
                pw.print("<tr>");
                    pw.print("<td style=\"padding-left:10px; padding-right:10px; padding-top:10px; " +
                            " padding-bottom:10px;\">");
                        message.writeMessage(pw);
                    pw.print("</td>");
                pw.print("</tr>");
            }
        } else if (name.equals("get file")){
            pw.print(MessageManager.getMessage(lang, LogMessages.FORM_LOG_FILE, null));
        } else if (name.equals("clear logs")){
            pw.print(MessageManager.getMessage(lang, LogMessages.CLEAR_LOGS, null));
        } else if (name.equals("confirm")){
            pw.print(MessageManager.getMessage(lang, LogMessages.CONFIRM_DELETE_LOGS, null));            
        } else if (name.equals("info")){
            pw.print(MessageManager.getMessage(lang, LogMessages.TO_FORM_LOG_FILE, null));
        }
    }
}
