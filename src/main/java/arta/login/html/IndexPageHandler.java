package arta.login.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Rand;
import java.io.PrintWriter;


public class IndexPageHandler extends TemplateHandler{



    public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("kazakh")){
            pw.print("ҚАЗАҚША");
        } else if (name.equals("russian")){
            pw.print("РУССКИЙ");
        } else if (name.equals("english")){
            pw.print("ENGLISH");
        }
    }
    
}
