package arta.common.logic.db;


public class FieldType {
    public static final int VARCHAR = 1;
    public static final int INTEGER = 0;
    public static final int DOUBLE = 2;
    public static final int DATE = 3;
    public static final int TIME = 4;
    public static final int TIMESTAMP = 10;
    public static final int MEDIUM_BLOB = 5;
    public static final int LONG_BLOB = 9;
    public static final int BOOLEAN = 6;
    public static final int MEDIUM_TEXT = 7;
    public static final int TEXT = 8;

    public static String getSQLValue(int type, int length){
        if (type == INTEGER) {
            return " INT ";
        } else if (type == VARCHAR){
            return " VARCHAR ("+length+") ";
        } else if (type == DOUBLE){
            return " DOUBLE ";
        } else if (type == DATE){
            return " DATE ";
        } else if (type == TIME){
            return " TIME ";
        } else if (type == MEDIUM_BLOB){
            return " MEDIUMBLOB ";
        } else if (type == BOOLEAN){
            return " BOOLEAN ";
        } else if (type == MEDIUM_TEXT){
            return " MEDIUMTEXT ";
        } else if (type == TEXT){
            return " TEXT ";
        } else if (type == LONG_BLOB){
            return " LONGBLOB ";
        } else if (type == TIMESTAMP){
            return " TIMESTAMP ";
        }

        return null;
    }
}
