package arta.common.logic.server;

import arta.common.Constants;
import arta.common.http.MimeTypes;
import arta.common.logic.util.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.net.URL;

public class Server {

    public static String getSchema(){
        return "arta_demo";
    }


    public static String MAIN_URL = "";
    public static String WAR_NAME = "";

    public static final Object syncObject = new Object();

    public static boolean isURLCorrect = false;

    public static void readMainURL(HttpServletRequest request, ServletContext servletContext){
        String path = request.getContextPath();
        path += "/";

        Constants.initDirectories();

        if(MAIN_URL == null || MAIN_URL.length() == 0 ||
                MAIN_URL.toLowerCase().contains("localhost") || MAIN_URL.toLowerCase().contains("127.0.0.1")){
            MimeTypes.init(servletContext);
            synchronized(syncObject){
                try{

                    URL url = new URL(request.getRequestURL().toString());
                    isURLCorrect = url.getProtocol().toLowerCase().equals("http");
                    if (!isURLCorrect){
                        MAIN_URL = "";
                        return;
                    }
                    MAIN_URL = "" + path;


//
//                    MAIN_URL = url.getProtocol()+"://"+url.getHost() ;
//                    Log.writeLog("step3 " + MAIN_URL);
//                    Log.writeLog("step4 " + path);
//                    if(url.getPort() != -1){
//                        MAIN_URL += ":"+url.getPort();
//                        Log.writeLog("step5 " + MAIN_URL);
//                    }
//                    MAIN_URL +=  path;
                    WAR_NAME = path;
                }catch(Exception e){
                    MAIN_URL = "";
                }
            }
        }
    }


}
