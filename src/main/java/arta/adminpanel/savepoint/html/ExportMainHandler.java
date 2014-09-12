package arta.adminpanel.savepoint.html;


import arta.common.html.handler.Parser;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ExportMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    String name;
    Message message;
    ServletContext servletContext;

    public ExportMainHandler(int lang, int roleID, String name, Message message, ServletContext servletContext) {
        this.lang = lang;
        this.roleID = roleID;
        this.name = name;
        this.message = message;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        ExportHandler handler = new ExportHandler(name, message);
        FileReader fileOpener = new FileReader("savepoint/export.txt");
        Parser parser = new Parser(fileOpener.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() { return lang; }

    public int getRole() { return roleID; }

}
