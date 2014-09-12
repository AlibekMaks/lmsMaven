package arta.adminpanel.savepoint;


import arta.common.logic.util.Message;
import arta.common.logic.util.Time;
import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.db.DBSchema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;


public class SavePoint implements Comparable{

    private String copiesFolder = "";
    public String savePointName = "";

    public void setFolderName(String name){
        this.copiesFolder = name;
    }

    String delim = "qwertyuiop[asdfghjklzxcvbnm";

    public boolean makeCopy(Message message, boolean full){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        boolean copied = false;

        File f = new File(copiesFolder);
        if (!f.exists()){
            message.setMessage("Директория "+copiesFolder+" не существует.");
            return false;
        }

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            DBSchema schema = new DBSchema();
            schema.init();
            String currentFolder = new Date().getDate() +" "+ new Time().getDBValue();
            currentFolder = currentFolder.replaceAll("-", ".").replaceAll(":", ".");

            File file = new File(copiesFolder+"//"+currentFolder);
            file.mkdir();

            if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0){
                try{
                    FileOutputStream f1 = new FileOutputStream("/home/topa/asd.txt");
                    String command = "chown mysql \"" + (copiesFolder + "/" + currentFolder) + "\"";
                    System.out.println("command = " + command);

                    Process p = Runtime.getRuntime().exec(command);
                    p.waitFor();
                    int r;
                    byte [] b = new byte[1024];
                    while((r = p.getInputStream().read()) > 0){
                        f1.write(b, 0, r);
                    }
                    f1.flush();
                    f1.close();
                } catch(Exception e){
                    Log.writeLog(e);
                }
            }

            for (int i=0; i<schema.tables.size(); i++){
                String fields = "";
                for (int j=0; j<schema.tables.get(i).fields.size(); j++){
                    if (full ){
                        if (fields.length() > 0)
                            fields += ", ";
                        fields += schema.tables.get(i).fields.get(j).getFieldName();
                    }
                }
                //fields += schema.tables.get(i).fields.get(schema.tables.get(i).fields.size()-1).getFieldName()+" ";
                if (fields.length() > 0){
                    st.execute("SELECT "+fields+" INTO OUTFILE \""+copiesFolder+"/"+currentFolder+"/"+schema.tables.get(i).name+"\" " +
                            " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' " +
                            " LINES TERMINATED BY '\n\n' " +
                            " FROM "+schema.tables.get(i).name+" ");
                    FileOutputStream fout = new FileOutputStream(file.getAbsolutePath()+"/" +
                            schema.tables.get(i).name+".fieds.txt");
                    fout.write(fields.getBytes());
                    fout.flush();
                    fout.close();
                }
            }
            con.commit();
            copied = true;
            message.setMessage("Точка восстановления создана.");

        } catch (Exception exc){
            Log.writeLog(exc);
            copied = false;
            try{
                message.setMessage("Exception: "+exc.getMessage());
                con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
        } finally{
            try{
                if (con!=null) con.close();
                if (st!=null) st.close();
                if (res!=null) res.close();
            } catch (Exception e){
                Log.writeLog(e);
            }
        }
        return copied;
    }

    public boolean goToSavePoint(String fullName, Message message){
        boolean returned = false;
        Connection con = null;
        Statement st = null;
        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            con.setAutoCommit(false);

            DBSchema schema = new DBSchema();
            schema.init();
            for (int i=0; i<schema.tables.size(); i++){               
                String fields = "";
                boolean noFile = false;
                try{
                    FileInputStream fin = new FileInputStream(fullName+"/" +
                            schema.tables.get(i).name+".fieds.txt");
                    int n =fin.available();
                    if (n > 0){
                        byte [] b = new byte[n];
                        fin.read(b);
                        fields = new String(b);
                    }
                } catch (Exception exc){
                    noFile = true;
                }
                if (fields.length() == 0 && !noFile) {
                    for (int j=0; j<schema.tables.get(i).fields.size()-1; j++){
                        fields += schema.tables.get(i).fields.get(j).getFieldName()+", ";
                    }
                    fields += schema.tables.get(i).fields.get(schema.tables.get(i).fields.size()-1).getFieldName()+" ";    
                }

                if (!noFile){
                    if (fields.length() > 0){
                        st.execute("DELETE FROM "+schema.tables.get(i).name);
                        st.execute("LOAD DATA INFILE \""+fullName+"/"+schema.tables.get(i).name+"\" REPLACE " +
                                " INTO TABLE "+schema.tables.get(i).name+" " +
                                " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' " +
                                " LINES TERMINATED BY '\n\n'  ("+fields+") ");
                    }
                }
            }

            con.commit();
            returned = true;
            message.setMessage("Данные восстановлены.");

        } catch (Exception exc){
            Log.writeLog(exc);
            returned = false;
            message.setMessage("Exception: "+exc.getMessage());
            try{
                con.rollback();
            } catch (Exception e){
                Log.writeLog(e);
            }
        }
        try{
            if (con!=null) con.close();
            if (st!=null) st.close();
        } catch (Exception exc){
            Log.writeLog(exc);
        }
        return returned;
    }

    public boolean rollback(String foldername, Message message){

        File f = new File(copiesFolder+"/"+foldername);
        if (!f.exists()){
            message.setMessage("Директория "+copiesFolder+"/"+foldername+" не существует.");
            return false;
        }
        return goToSavePoint(copiesFolder+"/"+foldername, message);
    }


    public int compareTo(Object o) {
        if (!(o instanceof SavePoint))
            return 1;
        return savePointName.compareTo(((SavePoint)o).savePointName);
    }
}
