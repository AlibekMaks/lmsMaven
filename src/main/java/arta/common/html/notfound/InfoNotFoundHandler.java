package arta.common.html.notfound;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.servlet.Messages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


/**
 * common/not.found.txt
 */
public abstract class InfoNotFoundHandler extends TemplateHandler {

    public void replace(String name, PrintWriter pw) {
        if (name.equals("navigation")){
            new Parser(new FileReader("common/navigate.noarrows.txt").read(getServletContext()), pw,
                    new InfoNotFoundNavigationHandler(getLanguage(), getImage(), getHeader(), getInformation())).parse();
        } else if (name.equals("nothing was found")){
            pw.print(getNotFoundHeader());
        } else if (name.equals("reasons")){
            getReasons(pw);
        } else if (name.equals("return link")){
            pw.print(getReturnLink());
        } else if (name.equals("return title")){
            pw.print(getReturnTitle());            
        } else if (name.equals("header")){

        }
    }
        
    public abstract String getImage();

    public abstract String getHeader();

    public abstract int getLanguage();

    public abstract ServletContext getServletContext();

    public String getInformation(){
        return MessageManager.getMessage(getLanguage(), Constants.GO_TO_THE_UPPER_LEVEL_AND_SEARCH_AGAIN, null);
    }

    public abstract String getReturnLink();

    public String getReturnTitle(){
        return MessageManager.getMessage(getLanguage(), Constants.BACK, null);
    }

    public void getReasons(PrintWriter pw){
        pw.print("<tr>");
            pw.print("<td>");
                pw.print(MessageManager.getMessage(getLanguage(), Constants.REASONS, null));
            pw.print("</td>");
        pw.print("<tr>");
        pw.print("<tr>");
            pw.print("<td>");
                pw.print(MessageManager.getMessage(getLanguage(), Constants.INFORMATION_HAS_BEEN_CHANGED, null));
            pw.print("</td>");
        pw.print("<tr>");
        pw.print("<tr>");
            pw.print("<td>");
                pw.print(MessageManager.getMessage(getLanguage(), Constants.INFORMATION_HAS_BEEN_DELETED, null));
            pw.print("</td>");
        pw.print("<tr>");
    }

    public String getNotFoundHeader(){
        return MessageManager.getMessage(getLanguage(), Constants.NO_INFORMATION_FOUND_ON_YOUR_QUEY, null);
    }
}
