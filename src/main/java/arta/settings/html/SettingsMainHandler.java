package arta.settings.html;

import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.util.Message;
import arta.settings.logic.Settings;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SettingsMainHandler extends PageContentHandler {

    int lang;
    int roleID;
    ServletContext servletContext;
    Message message;
    Settings settings;


    public SettingsMainHandler(int lang, int roleID, ServletContext servletContext, 
                               Message message, Settings settings) {
        this.lang = lang;
        this.roleID = roleID;
        this.servletContext = servletContext;
        this.message = message;
        this.settings = settings;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("settings/settings.txt").read(servletContext), pw,
                new SettingsHandler(lang, servletContext, message, settings)).parse();
    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
}
