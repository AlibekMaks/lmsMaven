package arta.common.logic.db;

public class Key {
    public static final int PRIMARY_KEY = 1;
    public static final int AUTO_INCREMENT_KEY = 2;
    public static final int NOT_A_KEY = 0;

    public static String getSQLValue(int type){
        if (type == AUTO_INCREMENT_KEY){
            return " AUTO_INCREMENT ";
        }
        return "";
    }
    
}
