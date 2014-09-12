package arta.subjects.logic;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.SearchParams;
import java.io.PrintWriter;

public class SubjectTestsSingle extends TemplateHandler {

    TestsSelect test;
    int lang;
    SearchParams params;
    StringTransform trsf = new StringTransform();

    public SubjectTestsSingle(int lang, SearchParams params){
        this.lang = lang;
        this.params = params;
    }

    public void set(TestsSelect test){
        this.test = test;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("value")){
            pw.print(test.getTestID());
        } else if (name.equals("selected")){
            if(test.getSelected()){
                pw.print("selected");
            }
        } else if (name.equals("text")){
            pw.print(trsf.getHTMLString(test.getTestName()));
        }
    }
}
