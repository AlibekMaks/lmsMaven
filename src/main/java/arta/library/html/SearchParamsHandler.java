package arta.library.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.library.logic.LibrarySearchParams;
import arta.library.logic.LibraryMessages;

import java.io.PrintWriter;


public class SearchParamsHandler extends TemplateHandler {

    LibrarySearchParams params;
    int lang;


    public SearchParamsHandler(LibrarySearchParams params, int lang) {
        this.params = params;
        this.lang = lang;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("Language")){
            pw.print(MessageManager.getMessage(lang, Constants.LANGUAGE, null));
        } else if (name.equals("language select")){
            Languages.writeLanguageSelect(lang, 100, "lang", pw, true, params.lang);
        } else if (name.equals("search in")){
            pw.print(MessageManager.getMessage(lang,  Constants.SEARCH_IN, null));
        } else if (name.equals("subject book checked")){
            if (params.searchInSubjectBooks)
                pw.print("checked");
        } else if (name.equals("subject book")){
            pw.print(MessageManager.getMessage(lang,  LibraryMessages.TEXT_BOOKS, null));
        } else if (name.equals("tutor book checked")){
            if (params.searchInTutorBooks)
                pw.print(" checked ");
        } else if (name.equals("tutor book")){
            pw.print(MessageManager.getMessage(lang,  LibraryMessages.TUTOR_BOOKS, null));
        } else if (name.equals("in book name disabled")){
            if (!params.searchInTutorBooks)
                 pw.print(" disabled ");
        } else if (name.equals("in book name checked")){
                if (params.searchInBookName)
                    pw.print(" checked ");
        } else if (name.equals("in book name")){
            pw.print(MessageManager.getMessage(lang, LibraryMessages.IN_BOOK_NAME, null));
        } else if (name.equals("in tutor name disabled")){
            if (!params.searchInTutorBooks)
                 pw.print(" disabled ");
        } else if (name.equals("in tutor name checked")){
            if (params.searchInTutorName)
                pw.print(" checked ");
        } else if (name.equals("in tutor name")){
            pw.print(MessageManager.getMessage(lang, LibraryMessages.IN_TUTOR_NAME, null));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("search value")){
            pw.print(params.getSearch());            
        } else if (name.equals("language sel")){
           Languages.writeLanguageSelectAny(lang, 100, "lang", pw, true, params.lang);
        }
    }
}
