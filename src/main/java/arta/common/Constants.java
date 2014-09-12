package arta.common;

import arta.common.logic.util.Log;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.server.Server;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: Topa
 * Date: 17.01.2008
 * Time: 16:25:23
 * To change this template use File | Settings | File Templates.
 */
public class Constants {

    private static String coursesdir = null;//"E:\\Development\\Services\\tomcat\\webapps\\books";
    private static String courseunpacktmp = null; //"E:\\ADLTemp\\";
    private static String ziptmp = null; //"E:\\ZIPEMP\\";
    private static String usersdir = null;//"E:\\Users";

    public static void initDirectories(){

        if (coursesdir != null && courseunpacktmp != null && ziptmp != null && usersdir != null)
            return;

        Statement st = null;
        ResultSet res = null;

        Connection con = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT value FROM options WHERE name='coursesdir'");
            if (res.next())
                coursesdir = res.getString(1);

            res = st.executeQuery("SELECT value FROM options WHERE name='courseunpacktmp'");
            if (res.next())
                courseunpacktmp = res.getString(1);

            res = st.executeQuery("SELECT value FROM options WHERE name='ziptmp'");
            if (res.next())
                ziptmp = res.getString(1);

            res = st.executeQuery("SELECT value FROM options WHERE name='usersdir'");
            if (res.next())
                usersdir = res.getString(1);

        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{

                con.close();
                st.close();
                res.close();

            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }


    }

    public static String getReturnToMainPage(HttpServletRequest request){
        return "<script language=\"javascript\">document.top.location=\"http://192.168.1.12:8080/adl/runtime/LMSMain.htm\"</script>";

    }

    public static final Object syncObject = new Object();

    public static final String COURSE_PREFIX = "Course-";

    public static final String HOST = "host";
    public static final String WAR = "war";
    public static final String USER_ID = "userID";
    public static final String ROLE_ID = "roleID";
    public static final String LANG = "lang";

    //public static final String USERS_INFO_DIRECTTORY = "E:\\SCORM3EDSampleRTE102Files";

    public static String getSCORMRuntimeFolder(String host, String war){
        //return host + "/adl/runtime/";
        return Server.MAIN_URL +  "/runtime/";
    }

    public static String getCoursesStoreDirectory(){
        return coursesdir;
    }

    /**
     * Метод возвращает полный путь к директории куда будет распакован
     * zip архив курса.
     * <br><b>Примечаниие.</b> В оригинальной версии программы все курсы распаковываются
     * в директорию <code>[полный путь к приложению]/PackageImport</code>
     * @param courseIdentifier - некоторый уникальный идентификатор
     * данного курса, необходим для того чтобы существовала возможность
     * обновременного импорта нескольких курсов разеыми пользователями
     * @return Возвращает полный путь к временной директории, куда должен быть
     * распакован архив, перед тем как он будет распарсен и импортирован.
     */
    public static String getCousreUnpackTemporaryDirectory(String courseIdentifier){
        return courseunpacktmp + getUnpackDirecoty(courseIdentifier);
    }

    /**
     * Возвращает название временной папки, куда будет импортирован курс.
     * @param courseIdentifier - некоторый уникальный идентификатор курса.
     * @return Название временной папки.
     */
    public static String getUnpackDirecoty(String courseIdentifier){
         return "CourseTemporaryFolder" + courseIdentifier;        
    }

    /**
     * Создает уникальный идентификатор для курса как
     * строку представляющую текущее время в милисекундах.
     * @return уникальный идентификатор для мозжаваемого курса.
     */
    public static String getCourseIdentifier(){
        synchronized(syncObject){
            return new Date().getTime() + "";
        }
    }

    public static String getTempZipFilesDirectory(String sessionID){
        return ziptmp + sessionID;
    }

    public static String getUsersInfoDirectory(){
        return usersdir;
    }
}
