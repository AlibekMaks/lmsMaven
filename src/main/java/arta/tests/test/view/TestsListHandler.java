package arta.tests.test.view;

import arta.tests.common.TestMessages;
import arta.common.logic.util.Date;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.messages.MessageManager;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;

import javax.servlet.ServletContext;
import java.io.PrintWriter;

public class TestsListHandler extends TemplateHandler {

    int lang;

    int sort;
    Date createStart;
    Date createFinish;
    Date modifyStart;
    Date modifyFinish;
    String search;
    HTMLCalendar calendar;
    int partNumber;
    int countInPart;
    
    StringBuffer str;
    StringTransform stringTransform = new StringTransform();
    ViewTestsManager manager = new ViewTestsManager();
    int partsCount;
    String params = "";

    public TestsListHandler(int lang, int sort, Date createStart, Date createFinish, Date modifyStart,
                            Date modifyFinish, String search, 
                            HTMLCalendar calendar, int partNumber, int countInPart,
                            ServletContext servletContext) {
        this.lang = lang;
        this.sort = sort;
        this.createStart = createStart;
        this.createFinish = createFinish;
        this.modifyStart = modifyStart;
        this.modifyFinish = modifyFinish;
        this.search = search;
        this.calendar = calendar;
        this.partNumber = partNumber;
        this.countInPart = countInPart;

        FileReader fileReader = new FileReader("tests/tests.list/single.test.view.txt");
        str = fileReader.read(servletContext);

        manager.search(search, modifyStart, modifyFinish, createStart, createFinish,
                sort, partNumber, countInPart);
        if (countInPart!=0){
            partsCount = manager.recordsCount / countInPart + 1;
        }
        if (search!=null){
            params += "search="+stringTransform.getHTMLString(search);
        }
        if (createStart!=null && createFinish!=null){
            if (params.length()>0)
                params += "&";
            params += "createStart="+createStart.getDate()+"&createFinish="+createFinish.getDate();
        }
        if (modifyStart!=null && modifyFinish!=null){
            if (params.length()>0)
                params += "&";
            params += "modifyStart="+modifyStart.getDate()+"&modifyFinish="+modifyFinish.getDate();
        }
        if (countInPart!=0){
            if (params.length()>0)
                params += "&";
            params += "countInPart="+countInPart+"&partNumber="+partNumber;
        }
    }

    public void replace(String name, PrintWriter pw) {

        if (name.equals("sort")){
            pw.print(sort);
        } else if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("searchValue")){
            if (search!=null)
                pw.print(stringTransform.getHTMLString(search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH, null));
        } else if (name.equals("create date")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DATE_OF_CREATE, null) );
        } else if (name.equals("from")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FROM, null));
        } else if (name.equals("create date start")){
            calendar.printInput(pw, createStart, "createStart", "form", createStart!=null && createFinish!=null);
        } else if (name.equals("till")){
            pw.print(MessageManager.getMessage(lang, TestMessages.TILL, null));
        } else if (name.equals("create date finish")){
            calendar.printInput(pw, createFinish, "createFinish", "form", createStart!=null && createFinish!=null);
        } else if (name.equals("modified date")){
            pw.print(MessageManager.getMessage(lang, TestMessages.DATE_OF_MODIFY, null));
        } else if (name.equals("modified date start")){
            calendar.printInput(pw, modifyStart, "modifyStart", "form", modifyStart!=null && modifyFinish!=null);
        } else if (name.equals("modified date finish")){
            calendar.printInput(pw, modifyFinish, "modifyFinish", "form", modifyFinish!=null && modifyStart!=null);
        } else if (name.equals("found tests")){
            pw.print(MessageManager.getMessage(lang, TestMessages.FOUND_TESTS_NUMBER, null));
        } else if (name.equals("tests count")){
            pw.print(manager.recordsCount);
        } else if (name.equals("params")){
            pw.print(params);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, TestMessages.NAME, null));
        } else if (name.equals("created")){
            pw.print(MessageManager.getMessage(lang, TestMessages.CREATED, null));
        } else if (name.equals("modified")){
            pw.print(MessageManager.getMessage(lang, TestMessages.MODIFIED, null));
        } else if (name.equals("tests")){
            for (int i=0; i<manager.tests.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleTestViewHandler handler = new SingleTestViewHandler(manager.tests.get(i),lang, stringTransform);
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("pages")){
            String link = "testsList?countInPart="+countInPart+"&partNumber=PartNumberValue;";
            if (search!=null){
                link += "&search="+stringTransform.getHTMLString(search);
            }
            if (createFinish!=null && createStart!=null){
                link += "&createStart="+createStart.getDate()+"&createFinish="+createFinish.getDate();
            }
            if (modifyStart!=null && modifyFinish!=null){
                link += "&modifyStart="+modifyStart.getDate()+"&modifyFinish="+modifyFinish.getDate();
            }
            /*PartsNumberHandler handler = new PartsNumberHandler(partsCount, partNumber, lang,
                    link, "PartNumberValue;");
            handler.writeLinks(pw);*/
        }
    }
}
