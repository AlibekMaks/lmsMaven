package arta.common.selects;

import arta.common.html.util.Select;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;

import java.io.PrintWriter;


public class RolesSelect {

    public void writeRoleSelect(int lang, int width, PrintWriter pw){
        Select select  = new Select(Select.SIMPLE_SELECT);
        pw.print(select.startSelect("role"));
        pw.print(select.addOption(Constants.STUDENT+"", true, MessageManager.getMessage(lang, Constants.STUDENT_MESSAGE, null)));
        pw.print(select.addOption(Constants.TUTOR+"", false, MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE, null)));
        pw.print(select.endSelect());
    }
}
