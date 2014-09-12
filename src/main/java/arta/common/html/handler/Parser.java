package arta.common.html.handler;

import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;

import java.io.PrintWriter;


public class Parser {

    StringBuffer stringBuilder;
    PrintWriter printWriter;
    TemplateHandler templateHandler;

    String startSymbol = "{[";
    String endSymbol = "]}";

    public Parser(StringBuffer stringBuilder, PrintWriter printWriter, TemplateHandler handler) {
        this.stringBuilder = stringBuilder;
        this.printWriter = printWriter;
        templateHandler = handler;
    }

    public Parser() {
    }

    public void parse() {

        while (stringBuilder.indexOf(startSymbol) >= 0) {

            printWriter.print(stringBuilder.substring(0, stringBuilder.indexOf(startSymbol)));
            printWriter.flush();

            stringBuilder.delete(0, stringBuilder.indexOf(startSymbol) + 2);

            if (stringBuilder.indexOf(endSymbol) >= 0) {
                String name = stringBuilder.substring(0, stringBuilder.indexOf(endSymbol));
                stringBuilder.delete(0, stringBuilder.indexOf(endSymbol) + 2);
                templateHandler.replace(name, printWriter);
            }
        }
        printWriter.print(stringBuilder.toString());
        printWriter.flush();
    }

    public void parseForHttpRequest(){
        StringTransform trsf = new StringTransform();
        while (stringBuilder.indexOf(startSymbol) >= 0) {

            printWriter.print(trsf.makeChatSting(stringBuilder.substring(0, stringBuilder.indexOf(startSymbol))));
            printWriter.flush();

            stringBuilder.delete(0, stringBuilder.indexOf(startSymbol) + 2);

            if (stringBuilder.indexOf(endSymbol) >= 0) {
                String name = stringBuilder.substring(0, stringBuilder.indexOf(endSymbol));
                stringBuilder.delete(0, stringBuilder.indexOf(endSymbol) + 2);
                templateHandler.replace(name, printWriter);
            }
        }
        printWriter.print(trsf.makeChatSting(stringBuilder.toString()));
        printWriter.flush();
    }

    public void setStringBuilder(StringBuffer stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public void setTemplateHandler(TemplateHandler templateHandler) {
        this.templateHandler = templateHandler;
    }
}
