package arta.common.http;

import com.bentofw.mime.ParsedData;

/**
 * Created by IntelliJ IDEA.
 * User: Natasha
 * Date: 22.07.2007
 * Time: 21:33:01
 * To change this template use File | Settings | File Templates.
 */
public class ParameterNameParser {



    StringBuffer parseString = new StringBuffer();

    String parameterName;
    String fileName;
    String mimeType;

    public void setParseString(StringBuffer parseString) {
        this.parseString = parseString;
        parameterName = null;
        fileName = null;
        mimeType = null;
    }

    public void parse(){
        if (parseString.indexOf("name=\"") >= 0){
            parseString.delete(0, parseString.indexOf("name=\"") + "name=\"".length());
            if (parseString.length() <= 1)
                return;
            int endQuot = parseString.indexOf("\"");
            if (endQuot == -1)
                return;
            parameterName = parseString.substring(0, endQuot);
            parseString.delete(0, endQuot);
        }
        if (parseString.indexOf("filename=\"") >= 0){
            parseString.delete(0, parseString.indexOf("filename=\"") + "filename=\"".length());
            if (parseString.length() <= 1)
                return;
            int endQuot = parseString.indexOf("\"");
            if (endQuot == -1)
                return;
            fileName = parseString.substring(0, endQuot);
            parseString.delete(0, endQuot);

            mimeType = MimeTypes.getMimeType(fileName);            

        }
      /*  if (parseString.toString().toLowerCase().indexOf("content-type") >= 0){
            parseString.delete(0, parseString.toString().toLowerCase().indexOf("content-type") +"content-type".length());
            if (parseString.indexOf(": ") == 0){
                parseString.delete(0, 2);
                int index = parseString.indexOf("\r\n");
                if (index >=0){
                    mimeType = parseString.substring(0, index);
                }
            }
        }     */
        if (fileName != null){
            FileParser parser = new FileParser(fileName);
            parser.parse();
            fileName = parser.getFileName();
        }
    }

    public String getParameterName(){
        return parameterName;
    }

    public String getFileName(){
        return fileName;
    }


    public String toString() {
        return "name="+parameterName + "; \r\nfilename="+fileName+"; \r\nmimetype="+mimeType+".";
    }

    public static void main(String[] args) {
        ParameterNameParser parser = new ParameterNameParser();
        parser.setParseString(new StringBuffer("-----------------------------7d71862a10020c\n" +
                "Content-Disposition: form-data; name=\"sentFile\"; filename=\"D:\\shared\\derbyclient.jar\"\n" +
                "Content-Type: application/x-zip-compressed\n" +
                ""));
        parser.parse();
        System.out.println(parser.toString());
    }
}
