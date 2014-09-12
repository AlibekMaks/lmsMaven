package arta.common.logic.sql;

import arta.common.logic.server.Server;
import arta.common.logic.util.Log;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class ConnectionPool {

    private Connection con = null;

    synchronized public Connection getConnection(){
       /* try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/mysql");
            con = ds.getConnection( );
            Statement st = con.createStatement();
            try{
                st.execute("use `"+Server.getSchema()+"`");
            }catch (Exception e){
                st.execute("CREATE DATABASE `"+Server.getSchema()+"` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin");
                st.execute("use `"+Server.getSchema()+"`");
            }
        } catch (Exception exc){
            exc.printStackTrace();
        }
        return con; */
        try {
            return arta.common.db.connection.ConnectionPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
