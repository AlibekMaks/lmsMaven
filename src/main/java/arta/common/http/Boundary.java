package arta.common.http;


public class Boundary {

    String boundary;
    String append = "--";

    public Boundary(String str) throws Exception{
        if (str == null)
            throw new Exception("No boundary found");
        if (str.indexOf("boundary=") < 0){
            throw new Exception("No boundary found");
        }
        boundary = str.substring(str.indexOf("boundary=") + "boundary=".length(), str.length());
    }

    public String getStartBoundary(){
        return append + boundary;
    }

    public String getFinishBoundary(){
        return append + boundary + append;       
    }

}
