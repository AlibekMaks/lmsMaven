package arta.filecabinet.html.tutors;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.notfound.InfoNotFoundHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorNotFoundHandler extends InfoNotFoundHandler {

    int lang;
    ServletContext servletContext;
    SearchParams params;

    public TutorNotFoundHandler(int lang, ServletContext servletContext, SearchParams params) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.params = params;
    }

    public String getImage() {
        return "images/menu.tutors.gif";
    }

    public String getHeader() {
        return MessageManager.getMessage(lang, FileCabinetMessages.TUTORS, null);
    }

    public int getLanguage() {
        return lang;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }


    public String getReturnLink() {
        return "tutors?" + params.getFullParams(); 
    }


    public void getReasons(PrintWriter pw) {
        pw.print(MessageManager.getMessage(lang, Constants.INFORMATION_HAS_BEEN_CHANGED, null));
    }


    public String getNotFoundHeader() {
        return MessageManager.getMessage(lang, FileCabinetMessages.COULD_NOT_FIND_USER_INFO, null);     
    }


    public String getInformation() {
        return MessageManager.getMessage(lang, FileCabinetMessages.RETURN_TO_LIST_AND_SEARCH_AGAIN, null);
    }
}

