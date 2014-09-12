package arta.adminpanel.savepoint.html;


import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class ImportMainHandler extends PageContentHandler {

    int lang;
    int role;
    String name;
    Message message;
    ServletContext servletContext;

    public ImportMainHandler(int lang, int role, String name, Message message, ServletContext servletContext) {
        this.lang = lang;
        this.role = role;
        this.name = name;
        this.message = message;
        this.servletContext = servletContext;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        ImportHandler handler = new ImportHandler(name, message);
        FileReader fileOpener = new FileReader("savepoint/import.txt");
        Parser parser = new Parser(fileOpener.read(servletContext), pw, handler);
        parser.parse();
    }

    public void getScript(PrintWriter pw) { }

    public int getLanguage() { return lang; }

    public int getRole() { return role; }

}
