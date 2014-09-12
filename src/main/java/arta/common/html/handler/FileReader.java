package arta.common.html.handler;

import arta.common.logic.util.Log;

import javax.servlet.ServletContext;
import java.io.InputStream;


public class FileReader {

    String name;
    String folder="templates/";

    public FileReader (String fileName){
        name = fileName;
    }

    public StringBuffer read(ServletContext cnxt){
        StringBuffer result = new StringBuffer("");
        try{
            InputStream inputStream = cnxt.getResourceAsStream(folder+name);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            result = new StringBuffer(new String(bytes, "utf8"));
            inputStream.close();
        } catch(Exception exc){
            Log.writeLog(exc);
        }
        return result;
    }

    public void setFileName(String name){
        this.name = name;
    }

}
