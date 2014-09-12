package arta.common.logic.db;

import java.sql.Statement;
import java.util.ArrayList;


public class  DBIndex {

    public String indexName;
    public ArrayList<String> columnNames = new ArrayList<String>();


    public DBIndex() {
    }


    public DBIndex(String indexName, String columnName) {
        this.indexName = indexName;
        columnNames.add(columnName);
    }

    public DBIndex(String indexName, String [] columnNames) {
        this.indexName = indexName;
        for(int i=0; i < columnNames.length; i ++){
            this.columnNames.add(columnNames[i]);
        }
    }

    public String compare(DBIndex oldIndex, Statement st, String tableName, ArrayList<StringBuffer> description){
        StringBuffer str = new StringBuffer();
        for (int i=0; i<columnNames.size(); i++){
            boolean met = false;
            for ( int j=0; j<oldIndex.columnNames.size(); j++ ){
                if (columnNames.get(i).toLowerCase().equals(oldIndex.columnNames.get(j).toLowerCase())){
                    met = true;
                    break;
                }
            }
            if (!met){
                String sql1 = "DROP INDEX " + indexName + " ON " + tableName;
                String sql2 = "CREATE INDEX "+indexName + " ON "+tableName+ "(";
                for (int k=0; k<columnNames.size(); k++){
                    if (k>0)
                        sql2 += ",";
                    sql2 += columnNames.get(k);
                }
                sql2 += ")";
                try{
                    str.append(sql1);
                    st.execute(sql1);
                }catch (Exception exc){
                    str.insert(0, "FAILED (");
                    str.append(")");
                }
                description.add(str);
                try{
                    str = new StringBuffer(sql2);
                    st.execute(sql2);
                } catch (Exception exc){
                    str.insert(0, "FAILED (");
                    str.append(")");
                }
                description.add(str);
            }
        }
        return str.toString();
    }

    public StringBuffer create(Statement st, String tableName){
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE INDEX "+indexName+" ON "+tableName+"(");
        for (int i=0; i<columnNames.size(); i++){
            if (i > 0)
                sql.append(", ");
            sql.append(columnNames.get(i));
        }
        sql.append(")");
        try{
            st.execute(sql.toString());
        }catch (Exception exc){
            sql.insert(0, "FAILED (");
            sql.append(")");
        }
        return sql;
    }

    public String getCreateSQL(String tableName){
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE INDEX "+indexName+" ON "+tableName+"(");
        for (int i=0; i<columnNames.size(); i++){
            if (i > 0)
                sql.append(", ");
            sql.append(columnNames.get(i));
        }
        sql.append(")");
        return sql.toString();
    }

}
