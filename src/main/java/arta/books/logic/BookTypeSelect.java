package arta.books.logic;

import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.subjects.logic.SubjectMessages;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 14.03.2008
 * Time: 14:55:04
 * To change this template use File | Settings | File Templates.
 */
public class BookTypeSelect {

    public void writeSimpleSelect(String name, int value, int width, PrintWriter pw,
                                  int lang, String id, String onchange, boolean disabled, boolean includeAll){


        Select select = new Select(Select.SIMPLE_SELECT);
        select.select_function = onchange;
        if (disabled)
            select.select_function += " disabled ";
        pw.print(select.startSelect(name, id));

        if (includeAll){
            pw.print(select.addOption(-1, value == -1, ""));
        }

        pw.print(select.addOption(Book.FILE_TYPE, value == Book.FILE_TYPE,
                MessageManager.getMessage(lang, SubjectMessages.FILE_BOOK_TYPE)));

        pw.print(select.addOption(Book.SCORM_TYPE, value == Book.SCORM_TYPE,
                MessageManager.getMessage(lang, SubjectMessages.SCORM_BOOK_TYPE)));
        
        pw.print(select.endSelect());

    }

}
