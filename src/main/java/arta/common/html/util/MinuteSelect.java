package arta.common.html.util;

import java.io.PrintWriter;


public class MinuteSelect {
    public void writeMinuteSimpleSelect(String name, int value, PrintWriter pw){
        Select select = new Select(Select.SIMPLE_SELECT);
        pw.print(select.startSelect(name));
        for (int i=0; i<60; i+=5){
            pw.print(select.addOption(i+"", i==value, i+""));
        }
        pw.print(select.endSelect());
    }
}
