package arta.common.logic.util;


public class Rand {

    public static String getRandString(int length){
        String str = "";
        for (int i=0; i<length; i++){
            str += (int)(Math.random()*10);
        }
        return str;
    }

    public static String getRandString(){
        return getRandString(5);
    }

}
