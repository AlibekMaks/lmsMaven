package arta.common.html.util;

import java.io.PrintWriter;


public class HourSelect {
    public void writeSimpleHourSelect(int width, int lang, int value, PrintWriter pw, String name){
        Select select = new Select(Select.SIMPLE_SELECT);
        pw.print(select.startSelect(name));
        for (int i=0; i<24; i++){
            pw.print(select.addOption(i+"", value == i, i+""));
        }
        pw.print(select.endSelect());
    }
}
