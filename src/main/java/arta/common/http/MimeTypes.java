package arta.common.http;

import arta.common.logic.util.Log;

import javax.servlet.ServletContext;
import java.util.Properties;
import java.io.InputStream;


public class MimeTypes {

    public static Properties prop = null;

    public static String getMimeType(String filename){
        if (filename == null)
            return null;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1)
            return "application/octet-stream";
        String extension = filename.substring(dotIndex + 1, filename.length()).toLowerCase();
        if (prop != null)
            if (prop.getProperty(extension) != null)
                return prop.getProperty(extension);
        return "application/octet-stream";
    }

    public static boolean isImage(String mimetype){
        if (mimetype == null)
            return false;
        if (mimetype.equals("image/png") || mimetype.equals("image/jpeg") ||
                mimetype.equals("image/ief") || mimetype.equals("image/tiff") || mimetype.equals("image/bmp")
                || mimetype.equals("image/gif")){
            return true;
        }
        return false;
    }

    public static void init(ServletContext servletContext){
        InputStream in = servletContext.getResourceAsStream("mime-types.properties");
        prop = new Properties();
        try{
            prop.load(in);
        } catch (Exception exc){
            Log.writeLog(exc);
        }
    }

    public static boolean isFlash(String mimetype){
        if (mimetype == null)
            return false;
        if (mimetype.equals("application/x-shockwave-flash")){
            return true;
        }
        return false;
    }

}
