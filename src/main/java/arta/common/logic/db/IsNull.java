package arta.common.logic.db;


public class IsNull {
    public static String getSQLValue(boolean isNull){
        if (!isNull)
            return " NOT NULL ";
        return "";
    }
}
