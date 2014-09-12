package arta.settings.html;

import arta.common.html.navigation.ReturnPanelHandler;
import arta.common.html.menu.MenuMessages;
import arta.common.logic.messages.MessageManager;


public class SettingsNavigationHandler extends ReturnPanelHandler {

    int lang;


    public SettingsNavigationHandler(int lang) {
        this.lang = lang;
    }

    public int getLanguage() {
        return lang;
    }

    public String getImage() {
        return "images/settings.gif";
    }

    public String getHeader() {
        return MessageManager.getMessage(lang, MenuMessages.SETTINGS, null);
    }

    public String [] getInfo() {
        return new String[]{
        };
    }
}
