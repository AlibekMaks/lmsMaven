package arta.adminpanel.savepoint.html;

import arta.common.html.handler.TemplateHandler;

import java.io.PrintWriter;


public class ChangeFolderHandler extends TemplateHandler {

    String currentFolderName;

    public ChangeFolderHandler(String currentFolderName) {
        this.currentFolderName = currentFolderName;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("current folder")){
            if (currentFolderName!=null) pw.print(currentFolderName);
        } else if (name.equals("change")){
            pw.print("Изменить");    
        }
    }

}
