package arta.common.html.util;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;
import java.util.ArrayList;


public class Select {

    public static final int SIMPLE_SELECT = 1;

    public static final int SERVLET_SELECT = 2;

    public static final int POST_SELECT = 3;


    public int width = 0;
    public String change_function = null;
    public String select_function = null;
    int type;
    public String formName = "form";

    StringTransform trsf = new StringTransform();


    public Select(int type){
        this.type=type;
    }

    public String getStartSelect(String name, String id){
        return getStartSelect(name, id, false);
    }

    public String getStartSelect(String name, String id, boolean disabled){
        StringBuffer start = new StringBuffer();
        String disabledStr = "";
        if (disabled) disabledStr = " disabled ";
        if (width == 0)
            width = 100;
        if (select_function == null) select_function = "";
        if (type == SIMPLE_SELECT){
            if (change_function == null){
                change_function = "";
            }else {
                change_function = " onChange='"+change_function+"' ";
            }
            start.append("<SELECT class=\"select\" "+select_function+" "+change_function+" style=\"width:"+width+"%;\" " +
                    "  name=\""+name+"\" ");
            if (id != null) start.append(" id=\""+id+"\" ");
            start.append(disabledStr);
            start.append(">");
            return start.toString();
        }else if (type == SERVLET_SELECT){
            if (change_function == null)
                change_function = "";
            else
                change_function = change_function+"; ";
            start.append("<SELECT class=\"select\" "+select_function+" style=\"width:"+width+"%;\"  name=\""+name+"\" " +
                    " onChange=\""+change_function+"location=this.options[this.selectedIndex].value\"");
            if (id!=null){
                start.append(" id=\""+id+"\" ");
            }
            start.append(disabledStr);
            start.append(">");
            return start.toString();
        } else if (type == POST_SELECT){
            start.append("<SELECT class=\"select\" "+select_function+" style=\"width:"+width+"%;\"  name=\""+name+"\" " +
                    " onChange=\""+formName+".submit()\" ");
            if (id != null) start.append(" id=\""+id+"\" ");
            start.append(disabledStr);
            start.append(">");
            return start.toString();
        }
        return "<SELECT>";
    }

    public String startSelect(String name, String id){
        return getStartSelect(name, id);
    }

    public String startSelect(String name){
        return getStartSelect(name, null);
    }

    public String addOption(Object value, boolean selected, String text){
        String str = "";
        str += "<OPTION class=\"option\" VALUE=\""+value+"\" ";
        if (selected)
            str += " selected ";
        str += ">"+trsf.getHTMLString(text)+"</OPTION>";
        return str;
    }

    public String addOption(String value, boolean selected, String text, String function){
        String str = "";
        if (function == null) function = "";
        str += "<OPTION class=\"option\" "+function+" VALUE=\""+value+"\" ";
        if (selected)
            str += " selected ";
        str += ">"+text+"</OPTION>";
        return str;
    }

    public String endSelect(){
        return "</SELECT>";
    }

    public static void writeAddScript(PrintWriter pw){
        pw.print("function add(added, notAdded){" +
                "index = document.getElementById(notAdded).selectedIndex;\n" +
                "if (index>=0){\n" +
                "op =document.getElementById(notAdded).options[index];\n" +
                "document.getElementById(notAdded).remove(index);\n" +
                "document.getElementById(added).add(op);\n" +
                "}" +
                "}");
    }
    public static void writeRemoveScript(PrintWriter pw){
        pw.print("function remove(added, notAdded){" +
                "index = document.getElementById(added).selectedIndex;\n" +
                "if (index>=0){\n" +
                "op =document.getElementById(added).options[index];\n" +
                "document.getElementById(added).remove(index);\n" +
                "document.getElementById(notAdded).add(op);\n" +
                "}" +
                "}");
    }

    public void printWidthSelects(int width, int size, ArrayList<SimpleObject> options, String id, PrintWriter pw,
                                  boolean disabled){
        pw.print("<SELECT class=\"select\" style=\"width:"+width+"%\" size="+size+" id=\""+id+"\" ");
        if (disabled)
            pw.print(" disabled ");
        pw.print(">");
            for (int i=0; i<options.size(); i++){
                pw.print("<OPTION class=\"option\" VALUE=\""+options.get(i).id+"\">"+options.get(i).name+"</OPTION>");
            }
        pw.print("</SELECT>");
    }

}
