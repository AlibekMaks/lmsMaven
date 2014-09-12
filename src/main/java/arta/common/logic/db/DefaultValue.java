package arta.common.logic.db;

public class DefaultValue {
    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String CURRENT_TIME = "CURRENT_TIME";
    public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

    public static String getDefaultValue(DBField field){
        if (field.getKey() == Key.AUTO_INCREMENT_KEY)
            return "";
        if (field.getDefaultValue() == null || field.getDefaultValue().equals(""))
            return "";
        if (field.getType() == FieldType.TIMESTAMP){
            if (field.getDefaultValue() == null)
                return  " DEFAULT NULL "; 
            if (field.getDefaultValue().equals(CURRENT_TIMESTAMP))
                return " DEFAULT "+CURRENT_TIMESTAMP;
            return " DEFAULT  '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.DATE  ){
            if (field.getDefaultValue().equals(CURRENT_DATE))
                return " DEFAULT "+CURRENT_DATE;
            return " DEFAULT  '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.TIME ){
            if (field.getDefaultValue().equals(CURRENT_TIME))
                return " DEFAULT "+CURRENT_TIME;
            return " DEFAULT  '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.VARCHAR || field.getType() == FieldType.MEDIUM_TEXT
                        || field.getType() == FieldType.TEXT){
            return " DEFAULT  '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.DOUBLE || field.getType() == FieldType.INTEGER){
            return " DEFAULT  "+field.getDefaultValue();
        }
        if (field.getType() == FieldType.BOOLEAN ){
            if (field.getDefaultValue().toLowerCase().equals("true")){
                return " DEFAULT true ";
            } else {
                return " DEFAULT false ";
            }
        }
        return "";
    }

    public static String getUpdateValue(DBField field){
        if (field.getKey() == Key.AUTO_INCREMENT_KEY)
            return "";
        if (field.getUpdateValue() == null || field.getUpdateValue().equals(""))
            return "";
        if (field.getType() == FieldType.TIMESTAMP){
            if (field.getDefaultValue().equals(CURRENT_TIMESTAMP))
                return " ON UPDATE "+CURRENT_TIMESTAMP;
            return " ON UPDATE '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.DATE  ){
            if (field.getDefaultValue().equals(CURRENT_DATE))
                return " ON UPDATE "+CURRENT_DATE;
            return " ON UPDATE '"+field.getDefaultValue()+"'";
        }
        if (field.getType() == FieldType.TIME ){
            if (field.getDefaultValue().equals(CURRENT_TIME))
                return " ON UPDATE "+CURRENT_TIME;
            return " ON UPDATE  '"+field.getDefaultValue()+"'";
        }
        return "";
    }
}
