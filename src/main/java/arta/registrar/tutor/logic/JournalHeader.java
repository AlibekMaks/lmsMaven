package arta.registrar.tutor.logic;

import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.timetable.designer.logic.LessonConstants;

import java.util.ArrayList;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 28.03.2008
 * Time: 9:09:08
 * To change this template use File | Settings | File Templates.
 */
public class JournalHeader {

    public ArrayList<JournalHeaderItem> items = new ArrayList <JournalHeaderItem>();


    public void fill(int daysCount){
        for (int i = 0; i < daysCount; i ++){
            items.add(new JournalHeaderItem(i + 1, Constants.SIMPLE_MARK));
        }
    }

    /**
     *
     * @param dayNumber c 1
     * @param name
     * @param testingID
     */
    public void insert(int dayNumber, String name, int testingID){

        int i = 0;

        while (items.get(dayNumber - 1 + i).day <= dayNumber){
            i ++;
        }

        items.add(dayNumber - 1 + i, new JournalHeaderItem(dayNumber, Constants.TEST_MARK, testingID, name));

    }


    /**
     * Метод печатает шапку таблицы журанала с дополнительными ячейками для тестирований
     * List header contains information about lessons appointed for this studygroup according timetable
     * and information about testings if there are any of them appointed
     */
     public void writeHeader(PrintWriter pw, int lang, StringTransform trsf, int roleID, int startDayNumber){        

        pw.print("<tr>");
        pw.print("<td rowspan=2 >");
        if (roleID != Constants.STUDENT){
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.REGISTRAR_HEADER, null));
        } else {
            pw.print(MessageManager.getMessage(lang, RegistrarMessages.STUDENT_JOURNAL_HEADER, null));
        }

        pw.print("</td>");

        StringBuffer tmp = new StringBuffer("<tr>");


        for (int i = 0; i < items.size(); i ++){

            pw.print("<td ");
            if (items.get(i).type == Constants.TEST_MARK){
                pw.print(" class=\"journal_testing\" title=\"");
                pw.print(trsf.getHTMLString(items.get(i).signature));
                pw.print("\" ");
            } else {
                pw.print(" class=\"journal_header\" ");
            }
            pw.print(">");
            pw.print(LessonConstants.getDayOfWeeShortkName(startDayNumber, lang));
            pw.print("</td>");

            tmp.append("<td align=\"center\"");
            if (items.get(i).type == Constants.TEST_MARK){
                tmp.append(" class=\"journal_testing\" title=\"");
                tmp.append(trsf.getHTMLString(items.get(i).signature));
                tmp.append("\" ");
            } else {
                tmp.append(" class=\"journal_header\" ");
            }
            tmp.append(">");
                tmp.append(items.get(i).day);
            tmp.append("</td>");


            if (i < items.size() - 1 && items.get(i).day != items.get(i + 1).day)
                startDayNumber ++ ;
            if (startDayNumber > 7)
                startDayNumber = 1;

        }
        pw.print("</tr>");
        pw.print(tmp);
        pw.print("</tr>");
    }

}
