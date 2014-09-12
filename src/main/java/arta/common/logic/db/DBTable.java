package arta.common.logic.db;

import arta.common.logic.util.Log;

import java.util.ArrayList;
import java.sql.Statement;
import java.sql.ResultSet;


public class DBTable {

            public ArrayList<DBField> fields = new ArrayList <DBField> ();
            public ArrayList <DBIndex> indexes = new ArrayList <DBIndex> ();
            public String name;
            public String defaultRecords;


            public DBTable(String name) {
                this.name = name;
            }

            public boolean compare(DBTable table, Statement st, ArrayList<StringBuffer>description){
                if (this.name.toLowerCase().equals(table.name.toLowerCase())){

                    int commonNames = 0;
                    for (int i = 0; i < fields.size(); i ++ ){
                        for (int j = table.fields.size()-1 ; j>=0 ; j-- ){
                            if (fields.get(i).getFieldName().toLowerCase().equals(table.fields.get(j).getFieldName().toLowerCase())){
                                commonNames ++ ;
                            }
                        }
                    }

                    if (commonNames > 0){
                        for (int i = 0; i < fields.size(); i ++ ){
                            boolean met = false;
                            for (int j = table.fields.size()-1 ; j>=0 ; j-- ){
                                if (fields.get(i).getFieldName().toLowerCase().equals(table.fields.get(j).getFieldName().toLowerCase())){
                                    met = true;
                                    if (!fields.get(i).compare(name, table.fields.get(j), st, description)){
                                        return false;
                                    }
                                    table.fields.remove(j);
                                    break;
                                }
                            }

                            if (!met){
                               if (!fields.get(i).dropField(name, st, description))
                                   return false;
                            }
                        }

                        for (int i=0; i<table.fields.size(); i++){
                            if (!table.fields.get(i).addField(name, st, description))
                                return  false;
                        }
                    } else {
                        dropTable(st, description);
                        table.create(st, description);
                    }


                }
                return true;
            }

            public boolean create(Statement st, ArrayList<StringBuffer>description){
                String sql = " CREATE TABLE "+name+" (";
                String pri = "";
                for (int i=0; i<fields.size()-1; i++){
                    sql += fields.get(i).getCreateValue() + " , ";
                    if (fields.get(i).getKey() == Key.AUTO_INCREMENT_KEY || fields.get(i).getKey() == Key.PRIMARY_KEY){
                        if (pri.length()>0){
                            pri += ", ";
                        }
                        pri += fields.get(i).getFieldName();
                    }
                }
                sql += fields.get(fields.size()-1).getCreateValue();
                if (fields.get(fields.size()-1).getKey() == Key.AUTO_INCREMENT_KEY || fields.get(fields.size()-1).getKey() == Key.PRIMARY_KEY){
                    if (pri.length()>0){
                        pri += ", ";
                    }
                    pri += fields.get(fields.size()-1).getFieldName();
                }
                if (pri.length()>0){
                    pri  = " PRIMARY KEY ("+pri+")";
                }
                if (pri.length()>0)
                    sql += " , "+pri;
                sql +=  ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
                try{
                    st.execute(sql);
                } catch (Exception exc){
                    description.add(new StringBuffer(" FAILED ["+sql+"] "));
                    Log.writeLog(exc);
                    return false;
                }
                try{
                    ResultSet res = null;
                    checkIndexes(st, res, description);
                } catch (Exception exc){
                    Log.writeLog(exc);
                    return false;
                }
                description.add(new StringBuffer(sql));
                return true;
            }

            public boolean dropTable(Statement st, ArrayList<StringBuffer>description){
                String sql = "DROP TABLE "+name;
                try{
                    st.execute(sql);
                    description.add(new StringBuffer(sql));
                } catch (Exception exc){
                    description.add(new StringBuffer("FAILED ["+sql+"]"));
                    Log.writeLog(exc);
                    return true;
                }
                return true;
            }

            public void checkIndexes(Statement st, ResultSet res, ArrayList <StringBuffer> description) throws Exception{
                ArrayList <DBIndex> current = new ArrayList<DBIndex>();
                res = st.executeQuery("SHOW INDEXES FROM "+name);
                while (res.next()){
                    String keyName = res.getString("Key_name");
                    String columnName = res.getString("Column_name");
                    if (keyName.toLowerCase().equals("primary"))
                        continue;
                    boolean met = false;
                    for (int i=0; i<current.size(); i++){
                        if (current.get(i).indexName.equals(keyName)){
                            current.get(i).columnNames.add(columnName);
                            met = true;
                            break;
                        }
                    }
                    if (!met){
                            current.add(new DBIndex(keyName, columnName));
                    }
                }
                for (int i=0; i<current.size(); i++){
                    boolean met = false;
                    for (int j=0; j<indexes.size(); j++){
                        if (indexes.get(j).indexName.equals(current.get(i).indexName)){
                            indexes.get(j).compare(current.get(i), st, name, description);
                            indexes.remove(j);
                            met = true;
                            break;
                        }
                    }
                    if (!met){
                        StringBuffer sql = new StringBuffer("DROP INDEX "+current.get(i).indexName+" ON "+name);
                        try{
                            st.execute(sql.toString());
                            description.add(sql);
                        } catch (Exception exc){
                            StringBuffer str = new StringBuffer("FAILED [");
                            str.append(sql);
                            str.append("]");
                            description.add(str);
                        }
                    }
                }
                for (int i=0; i<indexes.size(); i++){
                    description.add( indexes.get(i).create(st, name));
                }
            }
}
