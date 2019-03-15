package arta.common.db.connection;

import java.util.ArrayList;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionPoolImpl {

    private int maxConnectionsCount = 2000;

    public ArrayList<PooledConnection> connections = new ArrayList<PooledConnection> ();

    boolean log = true;

    public PooledConnection getConnection() throws Exception{

        synchronized(connections){

            if (log)
                System.out.println("Number of connections in pool is " + connections.size());

            checkConnections();
            
            PooledConnection con = chooseConnection();
            if (con != null){
                con.hold();
                return con;
            }

            if (connections.size() >= maxConnectionsCount){
                do{
                    if (log)
                        System.out.println("Connection pool is full. waiting...");
                    connections.wait();
                    con = chooseConnection();
                } while (con == null);
                return con;
            } else {
                 return createConnection();
            }
        }

    }

    private PooledConnection createConnection() throws Exception{
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Properties prop = new Properties();
        prop.setProperty("create", "true");
        prop.setProperty("user", User.getLogin());
        prop.setProperty("password", User.getPassword());
        Connection con = DriverManager.getConnection("jdbc:mysql://"+ Server.getHost()
                + ":"+Server.getPort()+"/"+ Server.getSchema() + "?characterEncoding=utf8", prop);
        PooledConnection poolCon = new PooledConnection(con);
        poolCon.hold();
        connections.add(poolCon);
        return poolCon;

    }

    private PooledConnection chooseConnection(){
        for (int i=connections.size() - 1; i >= 0; i--){
            if (connections.get(i).isEmpty()){
                if (connections.get(i).checkMe())
                    return connections.get(i);
                connections.get(i).close();
                removeConnectionFromList(i);
            }
        }
        return null;
    }
    
    private void removeConnectionFromList(int index) {
    	try{
        	connections.get(index).connection.close();
        } catch (SQLException sqlException) {
        	sqlException.printStackTrace();
        }
        connections.remove(index);
    }
    
    private void removeConnectionFromList(PooledConnection con) {
    	try{
        	con.connection.close();
        } catch (SQLException sqlException) {
        	sqlException.printStackTrace();
        }
        connections.remove(con);
    }

    public void releaseConnection(PooledConnection con){
        try {
            synchronized(connections){
                if (con != null){
                    con.release();
                    connections.notify();
                }
            }
        } catch (Exception exc){
            exc.printStackTrace();
            removeConnectionFromList(con);
        }
    }

    private void checkConnections(){
        for (int i = connections.size() - 1; i >= 0; i--){
            if (!connections.get(i).isAlive()){
                try {
                    connections.get(i).release();
                    connections.get(i).connection.close();
                } catch (Exception exc){
                    exc.printStackTrace();
                }
                connections.remove(i);
            }
        }
    }

    public void releaseAll(){
        synchronized (connections){
            for (int i = 0; i < connections.size(); i++){
                connections.get(i).release();
                connections.get(i).close();
            }
        }
    }

}
