import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

import com.bentofw.mime.MimeParser;
import com.bentofw.mime.ParsedData;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 29.08.2009
 * Time: 17:34:02
 * To change this template use File | Settings | File Templates.
 */

public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection con = null;
        Statement st = null;

        Log.writeLog("doPost:TestServlet");

        try{
             MimeParser parser = MimeParser.getInstance();
             ParsedData data = parser.parseOnly(request);

            InputStream in = data.getInputStream("file");

            File f = new File("C://FILE");
            if (!f.exists())
                f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f);
            byte buf [] = new byte[1024];
            int n = 0;
            while ((n = in.read(buf)) != 0){
                fout.write(buf, 0, n);
            }

            System.out.println("Got input stream");
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
                                 //1 468 948 480
            st.execute("UPDATE resources SET resource=LOAD_FILE('"+f.getAbsolutePath()+"') " +
                    " WHERE resourceID=0");


        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
