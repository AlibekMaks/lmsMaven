package arta.tests.common;

public class ImagesIDParser {

    public String string = null;

    private String servlet = "getImage?id=";
    int nextID = 0;

    public ImagesIDParser() {}

    public boolean hasMoreImages(){
        if (string == null)
            return false;
        nextID = 0;
        int pos = 0;
        if ((pos = string.indexOf(servlet))>=0){
            try{
                string = string.substring(pos + servlet.length(), string.length());
                nextID = new Integer(string.substring(0, string.indexOf("\"")));
            } catch (Exception exc){
                nextID = 0;
            }
            if (nextID != 0){
                string = string.substring(string.indexOf("\""), string.length());
                return true;
            }
        }
        return false;
    }

    public int getNextImageID(){
        return nextID;
    }

    public void setParserString(String string){
        this.string = string;
    }

}
