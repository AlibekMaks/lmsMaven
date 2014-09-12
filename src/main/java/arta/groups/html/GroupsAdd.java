package arta.groups.html;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.FileReader;
import arta.common.html.navigation.PartsHandler;
import arta.common.html.menu.MenuMessages;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.groups.logic.SubjectsSearchParams;
import arta.classes.ClassMessages;
import arta.subjects.logic.SubjectsManager;

import javax.servlet.ServletContext;
import java.io.PrintWriter;
import java.util.Properties;


public class GroupsAdd extends TemplateHandler {

    private ServletContext servletContext;
    private int lang;
    private SearchParams params; //refer to classes search
    private SubjectsSearchParams sbParams; //refer to subjects search
    private SimpleObject studyClass;

    private SubjectsManager manager;

    StringTransform trsf = new StringTransform();


    public GroupsAdd(ServletContext servletContext, int lang, SearchParams params,
                     SubjectsSearchParams sbParams, SimpleObject studyClass) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.params = params;
        this.sbParams = sbParams;
        this.studyClass = studyClass;
        manager = new SubjectsManager();
        manager.search(sbParams, lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("found")){
            pw.print(MessageManager.getMessage(lang, Constants.FOUND, null));
        } else if (name.equals("number of found records")){
            pw.print(sbParams.recordsCount);
        } else if (name.equals("rand")){
            pw.print(Rand.getRandString());
        } else if (name.equals("classID")){
            pw.print(studyClass.id);
        } else if (name.equals("name")){
            pw.print(MessageManager.getMessage(lang, Constants.NAME, null));
//        } else if (name.equals("subgroups")){
//            pw.print(MessageManager.getMessage(lang, ClassMessages.NUMBER_OF_SUBGROUPS, null));
//        } else if (name.equals("teachers")){
//            pw.print(MessageManager.getMessage(lang, MenuMessages.TUTORS, null));
        } else if (name.equals("records")){
            GroupAddSingle handler = new GroupAddSingle();
            Parser parser = new Parser();
            parser.setPrintWriter(pw);
            parser.setTemplateHandler(handler);
            StringBuffer str = new FileReader("groups/single.add.txt").read(servletContext);
            for (int i=0; i < manager.subjects.size(); i++){
                handler.setSubject(manager.subjects.get(i));
                parser.setStringBuilder(new StringBuffer(str));
                parser.parse();
            }
        } else if (name.equals("add")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if(name.equals("return link")){
            pw.print("class?classID="+studyClass.id+"&"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));            
        } else if (name.equals("page header")){
            Properties prop = new Properties();
            prop.setProperty("name", studyClass.name);
            StringBuffer str = new StringBuffer(MessageManager.getMessage(lang, ClassMessages.ADD_SUBJECTS, null));
            str.append("<br>");
            str.append(MessageManager.getMessage(lang, ClassMessages.CLASS, prop));
            pw.print(str);
        } else if (name.equals("search value")){
            pw.print(trsf.getHTMLString(sbParams.search));
        } else if (name.equals("search")){
            pw.print(MessageManager.getMessage(lang, Constants.SEARCH));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("parts")){
            new PartsHandler(sbParams.getPartsNumber(), sbParams.partNumber, lang,
                    "studygroups?" + params.getFullParams()+"&classID=" + studyClass.id   
                            + "&"+sbParams.getParams(), sbParams.partNumberStr).writeLinks(pw);
        }
    }
}
