package arta.adminpanel.savepoint.html;

import arta.adminpanel.savepoint.SavePoint;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Rand;

import java.io.PrintWriter;


public class SingleSavePointHandler extends TemplateHandler {

    SavePoint savePoint;

    public SingleSavePointHandler(SavePoint savePoint) {
        this.savePoint = savePoint;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("name")){
            pw.print(savePoint.savePointName);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        }
    }
}
