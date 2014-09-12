package arta.common.db.connection;


public class ConnectionPool {

    // one hour in miliseconds
    public static final long MAX_ALIVE_TIME = 216000000l;

    public static ConnectionPoolImpl connectionPool = new ConnectionPoolImpl();

    public static PooledConnection getConnection() throws Exception{
        return connectionPool.getConnection();
    }

    public static void releaseConnection(PooledConnection connection){
        connectionPool.releaseConnection(connection);
    }

    public static int getConnectionsCount(){
        return connectionPool.connections.size();
    }

    public static void releaseAll(){
        connectionPool.releaseAll();
    }
}
