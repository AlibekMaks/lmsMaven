package arta.library.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Languages;
import arta.common.logic.messages.MessageManager;
import arta.library.logic.LibrarySearchParams;
import arta.library.logic.LibraryManager;
import arta.library.logic.LibraryMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class LibraryCardHandler extends TemplateHandler {

    LibrarySearchParams params;
    ServletContext servletContext;
    int lang;

    LibraryManager manager;
    StringTransform trsf = new StringTransform();


    public LibraryCardHandler(LibrarySearchParams params, ServletContext servletContext, int lang) {
        this.params = params;
        this.servletContext = servletContext;
        this.lang = lang;
        manager = new LibraryManager();
        manager.search(params);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("library")){
            pw.print(MessageManager.getMessage(lang, LibraryMessages.LIBRARY, null));
        } else if (name.equals("search value")){
            pw.print(params.getSearch());
        }  else if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(params.recordsCount);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
        } else if (name.equals("records")){
            StringBuffer str = new FileReader("library/single.txt").read(servletContext);
            SingkeBookHandler handler = new SingkeBookHandler(trsf, lang);
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            for (int i=0; i<manager.books.size(); i++){
                handler.setBook(manager.books.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        } else if (name.equals("open")){
            pw.print(MessageManager.getMessage(lang, Constants.OPEN, null));
        } else if (name.equals("download")){
            pw.print(MessageManager.getMessage(lang, Constants.DOWNLOAD, null));
        } else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, LibraryMessages.LIBRARY));
        } else if (name.equals("Language")){
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
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(params.getPartsNumber(), params.partNumber, lang,
                    "library?" + params.getParams(), params.partNumberStr).writeLinks(pw);
        }

    }
}
