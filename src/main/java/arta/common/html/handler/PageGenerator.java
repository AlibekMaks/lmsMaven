package arta.common.html.handler;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class PageGenerator {

    public String filename = "common/common.page.html";

    public void writeHtmlPage(TemplateHandler handler, PrintWriter out, ServletContext cnxt){
        FileReader f = new FileReader(filename);

        Parser parser = new Parser(f.read(cnxt), out, handler);
        parser.parse();
    }
}
