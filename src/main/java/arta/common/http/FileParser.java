package arta.common.http;

import java.io.File;


public class FileParser {

    String fullFileName = null;

    String simpleFileName;


    public FileParser(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public void parse(){
        File file = new File(fullFileName.toString());
        simpleFileName = file.getName();
    }

    public String getFileName(){
        return simpleFileName;
    }

}
