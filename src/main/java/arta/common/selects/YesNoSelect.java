package arta.common.selects;

import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;

import java.io.PrintWriter;


public class YesNoSelect {

    public static boolean YES = true;
    public static boolean NO = false;

    public void writeSelect(boolean value, int width, PrintWriter pw, String name, int lang){
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        pw.print(select.getStartSelect(name, name));
        pw.print(select.addOption("1", YES == value, MessageManager.getMessage(lang, Constants.YES, null)));
        pw.print(select.addOption("0", NO == value, MessageManager.getMessage(lang, Constants.NO, null)));
        pw.print(select.endSelect());
    }

}
