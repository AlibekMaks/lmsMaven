package arta.common.logic.db;

import java.util.ArrayList;
import java.sql.Statement;

public class DBField {

    private String fieldName;
    private int type;
    private boolean isNull = true;
    private int key = Key.NOT_A_KEY;
    private String defaultValue = null;
    private String updateValue = null;
    private int size = 0;

    public DBField() {
    }

    public DBField(String fieldName, int type, int size) {
        this.fieldName = fieldName;
        this.type = type;
        this.size = size;
    }

    public DBField(String fieldName, int type, String defaultValue, int size) {
        this.fieldName = fieldName;
        this.type = type;
        this.defaultValue = defaultValue;
        this.size = size;
    }

    public DBField(String fieldName, int type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public DBField(String fieldName, int type, String defaultValue) {
        this.fieldName = fieldName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public DBField(String fieldName, int type, boolean aNull, int key) {
        this.fieldName = fieldName;
        this.type = type;
        isNull = aNull;
        this.key = key;
    }

    public DBField(String fieldName, int type, boolean aNull, int key, String defaultValue) {
        this.fieldName = fieldName;
        this.type = type;
        isNull = aNull;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public DBField(String fieldName, int type, boolean aNull, int key, String defaultValue, String updateValue) {
        this.fieldName = fieldName;
        this.type = type;
        isNull = aNull;
        this.key = key;
        this.defaultValue = defaultValue;
        this.updateValue = updateValue;
    }

    public DBField(String fieldName, int type, boolean aNull, int key, String defaultValue, int size) {
        this.fieldName = fieldName;
        this.type = type;
        isNull = aNull;
        this.key = key;
        this.defaultValue = defaultValue;
        this.size = size;
    }

    public boolean compare(String tableName, DBField field, Statement st, ArrayList<StringBuffer>description){
        String sql = "ALTER TABLE "+tableName+" CHANGE "+getFieldName()+" "+getFieldName()+" " +
                            " "+FieldType.getSQLValue(field.getType(), field.getSize())+" "+Key.getSQLValue(field.getKey())+" " +
                            " "+DefaultValue.getDefaultValue(field)+" "+IsNull.getSQLValue(field.isNull())+" "+DefaultValue.getUpdateValue(field)+" ";
        if (field.getFieldName().toLowerCase().equals(getFieldName().toLowerCase())){
            if (field.getType() != type || field.getKey() != getKey() ||
                    ( field.getType()!=FieldType.TIMESTAMP && field.isNull()!=isNull() )||
                    (isNull() && field.getDefaultValue()!=null && getDefaultValue()!=null && !field.getDefaultValue().equals(getDefaultValue())) ||
                    (field.getType() == FieldType.VARCHAR & field.getSize() != getSize())){
                try{
                    st.execute(sql);
                    description.add(new StringBuffer(sql));
                } catch (Exception exc){
                    description.add(new StringBuffer(" FAILED ["+sql+"]"));
                    exc.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean addField(String tableName, Statement st, ArrayList<StringBuffer>description){
        String sql = "ALTER TABLE "+tableName+" ADD COLUMN "+getFieldName()+" "+FieldType.getSQLValue(getType(), getSize())+" " +
                    " "+Key.getSQLValue(getKey())+"  "+DefaultValue.getDefaultValue(this)+" "+IsNull.getSQLValue(isNull())+" " +
                    " "+DefaultValue.getUpdateValue(this)+" ";
        try{
            st.execute(sql);
            description.add(new StringBuffer(sql));
        } catch (Exception exc){
            description.add(new StringBuffer("FAILED ["+sql+"]"));
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean dropField(String tableName, Statement st, ArrayList<StringBuffer>description){
        String sql = " ALTER TABLE "+tableName+" DROP COLUMN "+getFieldName()+" ";
        try{
            st.execute(sql);
            description.add(new StringBuffer(sql));
        } catch (Exception exc){
            description.add(new StringBuffer("FAILED ["+sql+"]"));
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    public String getCreateValue(){
        return " "+getFieldName()+" "+FieldType.getSQLValue(getType(), getSize())+" "+Key.getSQLValue(getKey())+" " +
                " "+DefaultValue.getDefaultValue(this)+" "+IsNull.getSQLValue(isNull())+" "+DefaultValue.getUpdateValue(this)+" ";
    }

    public void setType(String str){
        size = extractInt(str);
        if (str.length()>9){
            if (str.substring(0, 7).equals("varchar")){
                type = FieldType.VARCHAR;
            }
        }
        if (str.length()>5){
            if (str.substring(0, 3).equals("int")){
                type = FieldType.INTEGER;
            }
        }
        if (str.length()>7){
            if (str.substring(0, 7).equals("tinyint")){
                type = FieldType.BOOLEAN;
            }
        }
        if (str.equals("date")){
            type = FieldType.DATE;
        }
        if (str.equals("time")){
            type = FieldType.TIME;
        }
        if (str.equals("timestamp")){
            type = FieldType.TIMESTAMP;
        }
        if (str.equals("text")){
            type = FieldType.TEXT;
        }
        if (str.equals("mediumtext")){
            type = FieldType.MEDIUM_TEXT;
        }
        if (str.equals("double")){
            type = FieldType.DOUBLE;
        }
        if (str.equals("longblob")){
            type = FieldType.LONG_BLOB;
        }
        if (str.equals("mediumblob")){
            type = FieldType.MEDIUM_BLOB;
        }

    }

    private int extractInt(String str){
        int start = -1;
        int finish = -1;
        int t = 0;
        if ((start = str.indexOf("(")) >= 0){
            if ((finish = str.indexOf(")")) > start){
                try{
                    t = new Integer(str.substring(start+1, finish)).intValue();
                } catch (Exception e){}
            }
        }
        return t;
    }
    public String getFieldName() { return fieldName; }

    public int getType() { return type; }

    public boolean isNull() { return isNull; }

    public int getKey() { return key; }

    public String getDefaultValue() { return defaultValue; }

    public int getSize() { return size; }

    public String getUpdateValue() { return updateValue; }

    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public void setType(int type) { this.type = type; }

    public void setNull(boolean aNull) { isNull = aNull; }

    public void setKey(int key) { this.key = key; }

    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public void setSize(int size) { this.size = size; }

    public void setUpdateValue(String updateValue) { this.updateValue = updateValue; }
}
