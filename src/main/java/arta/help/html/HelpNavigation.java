package arta.help.html;

import arta.common.html.navigation.NoArrowNavigationHandler;
import arta.common.html.menu.MenuMessages;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.Person;
import arta.help.HelpMessages;

import java.io.PrintWriter;


public class HelpNavigation extends NoArrowNavigationHandler {

    int lang;
    Person person;

    public HelpNavigation(int lang, Person person) {
        this.lang = lang;
        this.person = person;
    }

    public int getLanguage() {
        return lang;
    }

    public String getImage() {
        return "images/menu.help.gif";
    }

    public String getHeader() {
        return MessageManager.getMessage(lang, MenuMessages.HELP, null);
    }

    public String [] getInfo() {
        return new String[]{
               
        };
    }


    public void writeSearch(PrintWriter pw) {
        SingleManual singleManual = new SingleManual();
        if (person.getRoleID() == Constants.STUDENT){
            singleManual.writeStudentManual(pw, lang);
        } else {
            singleManual.writeTutorManual(pw, lang);            
        }
    }
}
