package arta.tests.common;

import arta.common.html.util.Select;
import arta.common.logic.util.Rand;

import java.io.PrintWriter;

public class NumberSelect {

    int lang;

    public NumberSelect(int lang) {
        this.lang = lang;
    }

    public void writeQuestionNumberSelect(PrintWriter pw, String link, String name, int value, int count, String onClick){
        Select select = new Select(Select.SERVLET_SELECT);
        select.change_function = onClick;
        pw.println(select.startSelect(""));
        for (int i=0; i<count; i++){
            pw.println(select.addOption(replace(link, name, i), i == value, (i+1)+""));
        }
        pw.println(select.endSelect());
    }

    public void writeSimpleNumberSelect(PrintWriter pw, String name, int value, int maxValue, int width, String selectFunction){
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        select.select_function = selectFunction;
        pw.print(select.startSelect(name));
        for (int i=1; i<=maxValue; i++){
            pw.print(select.addOption(i+"", i==value, i+"", selectFunction));
        }
        pw.print(select.endSelect());
    }

    public void writeSimpleNumberSelectForCheck(PrintWriter pw, String name, int value, int maxValue, int width){
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        select.change_function = "send(this.options[this.selectedIndex].value)";
        pw.print(select.startSelect(name));
        for (int i=1; i<=maxValue; i++){
            pw.print(select.addOption((i-1)+"", i==value, i+"" ));
        }
        pw.print(select.endSelect());
    }

    public void writeQuestionNumberPostSelect(PrintWriter pw, String name, int value, int maxValue, int width, String changeFunction){
        Select select = new Select(Select.SIMPLE_SELECT);
        select.width = width;
        select.change_function = changeFunction;
        pw.print(select.startSelect(name));
        for (int i=0; i<maxValue; i++){
            pw.print(select.addOption(i+"", i==value, (i+1)+""));
        }
        pw.print(select.endSelect());
    }
    private String replace(String link, String name, int value){
        if (link == null || name== null)
            return "";
        return link += "&"+name+"="+value+"&"+ Rand.getRandString();
    }
}
