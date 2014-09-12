package arta.adminpanel.savepoint.html;


import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SavePointsListMainHandler extends PageContentHandler {

    String folderName;
    ServletContext servletContext;
    Message message;
    int lang;
    int roleID;
    int option;

    public SavePointsListMainHandler(String folderName, ServletContext servletContext, Message message,
                                     int lang, int roleID, int option) {
        this.folderName = folderName;
        this.servletContext = servletContext;
        this.message = message;
        this.lang = lang;
        this.roleID = roleID;
        this.option = option;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        SavePointsListHandler handler = new SavePointsListHandler(folderName, message, option, servletContext);
        FileReader fileOpener = new FileReader("savepoint/savepoints.list.txt");
        Parser parser = new Parser(fileOpener.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() { return lang; }

    public int getRole() { return roleID; }
}
