package arta.help.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.filecabinet.logic.Person;
import arta.help.HelpMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class HelpCommonPage extends TemplateHandler {

    Person person;
    int lang;
    ServletContext servletContext;


    public HelpCommonPage(Person person, int lang, ServletContext servletContext) {
        this.person = person;
        this.lang = lang;
        this.servletContext = servletContext;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("download full version")){
            pw.print(MessageManager.getMessage(lang, HelpMessages.DOWNLOAD_FULL_VERSION, null));
        } else if (name.equals("download")){
            String langValue = Languages.getManualLanguageValue(lang);
            if (person.getRoleID() == Constants.STUDENT){
                pw.print("manual/student/manual."+langValue+".doc");
            } else {
                pw.print("manual/tutor/manual."+langValue+".doc");
            }
        }
    }
}
