package arta.common.html.handler;

import arta.common.html.menu.MenuGenerator;
import arta.common.html.menu.TimerHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Date;
//import arta.common.messages.Messages;
import arta.common.logic.util.Languages;
import arta.tests.common.TestMessages;

import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;


public abstract class PageContentHandler extends TemplateHandler{

    MenuGenerator generator = new MenuGenerator();
    Date date = new Date();

    public String getBuildNumber(int lang){

        Locale locale = null;
        if (lang == Languages.ENGLISH) {
            locale = new Locale("en");
        } else if (lang == Languages.KAZAKH) {
            locale = new Locale("kz");
        } else {
            locale = new Locale("ru");
        }

        ResourceBundle bundle = ResourceBundle.getBundle("application", locale);
        return bundle.getString("buildNumber");
    }

    public void replace(String name, PrintWriter pw) {
        if(name.equals("hours")){
            new TimerHandler(getLanguage()).write(pw);
        }else if(name.equals("menu")){
            generator.writeUserMenu(pw, getLanguage(), getRole());
        } else if(name.equals("mainPart")){
            getMainPart(pw);
        } else if(name.equals("bodyFunction")){
            getBodyFunction(pw);
        } else if(name.equals("header")){
            getHeader(pw);
        } else if(name.equals("common menu")){
            generator.writeCommonMenu(getLanguage(), pw);
        } else if (name.equals("month")){
            pw.print(Date.getMonthName(date.month, getLanguage()));
        } else if (name.equals("day")){
            pw.print(date.getFullDayValue());
        } else if (name.equals("visit our website")){       //218246
           // pw.print(Messages.getMessage(Constants.VISIT_OUR_WEBSITE, null));
            pw.print(MessageManager.getMessage(getLanguage(), Constants.VISIT_OUR_WEBSITE, null));
        } else if (name.equals("buildNumber")){
            pw.print(getBuildNumber(Languages.KAZAKH));
        }
    }

    public String getLicence(){
        return "free licence";
    }

    public abstract void getHeader(PrintWriter out);

    public abstract void getMainPart(PrintWriter pw);

    public abstract int getLanguage();

    public abstract int getRole();

    public abstract void getBodyFunction(PrintWriter pw);

}
