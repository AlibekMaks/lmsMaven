package arta.common.html.notfound;

import arta.common.html.navigation.NoArrowNavigationHandler;

import java.io.PrintWriter;


public class InfoNotFoundNavigationHandler extends NoArrowNavigationHandler {


    int lang;
    String image;
    String header;
    String information;

    public InfoNotFoundNavigationHandler(int lang, String image, String header, String information) {
        this.lang = lang;
        this.image = image;
        this.header = header;
        this.information = information;
    }

    public int getLanguage() {
        return lang;
    }

    public String getImage() {
        return image;
    }

    public String getHeader() {
        return header;
    }

    public String [] getInfo() {
        return new String[]{information};
    }


    public String getSearch() {
        return null;
    }


    public void writeSearch(PrintWriter pw) {
        pw.print("&nbsp;");
    }
}
