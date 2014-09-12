package arta.common.logic.messages;

import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Languages;
import arta.common.logic.sql.ConnectionPool;
import kz.arta.plt.common.DataExtractor;
//import kz.arta.plt.common.DataExtractor;

import java.util.Properties;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.io.InputStream;


public class MessageManager {
    public static final int ENABLED_DELETING_SUBJECT_BEING_IN_TYPCURRICULUMS =1;

    private static HashMap messageRU=null;
    private static HashMap messageKZ=null;
    private static HashMap messageENG=null;

    static StringTransform sf=new StringTransform();

    public static synchronized String getMessage(int language, int messageCode){
        return getMessage(language, messageCode, null);
    }

    public static synchronized String getMessageNative(int language, int messageCode, Properties prop){
        if(messageRU==null||messageKZ==null||messageENG==null){
            initializeMessages();
        }
        if(language==Languages.RUSSIAN){
            if(messageRU.get(messageCode)==null){
                readMessage(messageCode);
            }
            return getString(messageRU.get(messageCode), prop, messageCode);
        }
        if(language==Languages.KAZAKH){
            if(messageKZ.get(messageCode)==null){
                readMessage(messageCode);
            }
            return getString(messageKZ.get(messageCode), prop, messageCode);
        }
        if(language==Languages.ENGLISH){
            if(messageENG.get(messageCode)==null){
                readMessage(messageCode);
            }
            return getString(messageENG.get(messageCode), prop, messageCode);
        }
        return "";
    }

    public static synchronized String getMessage(int language, int messageCode, Properties prop){

        if(messageRU==null||messageKZ==null||messageENG==null){
            initializeMessages();
        }
        if(language==Languages.RUSSIAN){
            if(messageRU.get(messageCode)==null){
                readMessage(messageCode);
            }
            return sf.getHTMLString(getString(messageRU.get(messageCode), prop, messageCode));
        }
        if(language==Languages.KAZAKH){
            if(messageKZ.get(messageCode)==null){
                readMessage(messageCode);
            }
            return sf.getHTMLString(getString(messageKZ.get(messageCode), prop, messageCode));
        }
        if(language==Languages.ENGLISH){
            if(messageENG.get(messageCode)==null){
                readMessage(messageCode);
            }
            return sf.getHTMLString(getString(messageENG.get(messageCode), prop, messageCode));
        }
        return "";
    }


    private static void readMessage(int messageID){
        ConnectionPool pool=new ConnectionPool();
        Connection c=pool.getConnection();
        try{
             Statement st=c.createStatement();
            ResultSet res=st.executeQuery("SELECT messageID, messageRU, messageKZ, messageENG FROM messages where messageID="+messageID+"");
            if(res.next()){
                int code=res.getInt(1);
                messageRU.put(code,res.getString(2));
                messageKZ.put(code,res.getString(3));
                messageENG.put(code,res.getString(4));
            }
            c.close();
        }catch(Exception e){
            try{
               c.close();
            }catch(Exception e1){
               Log.writeLog(e1);
            }
            Log.writeLog(e);
        }
    }

    private static String getString(Object message, Properties prop, int messageID){
        if(message==null){
            readMessage(messageID);
        }

        if(message==null){
            return "Message not found "+messageID;
        }
        String text=message.toString();
        while(text.indexOf("{")!=-1){
            int from=text.indexOf("{");
            int till=text.indexOf("}");
            if (till==-1)break;
            String field=text.substring(from+1,till);

            String value="";
            if(prop!=null)
            value=prop.getProperty(field);
            if(value==null)value="";
            String begin=text.substring(0, from);
            String end=text.substring(till+1);
            text=begin+value+end;
        }
        return text;
    }

    public synchronized static void initializeMessages(){
        if(messageRU!=null){
            messageRU.clear();
        }else{
            messageRU=new HashMap();
        }
        if(messageKZ!=null){
            messageKZ.clear();
        }else{
            messageKZ=new HashMap();
        }
        if(messageENG!=null){
            messageENG.clear();
        }else{
            messageENG=new HashMap();
        }
        ConnectionPool pool=new ConnectionPool();
        Connection c=pool.getConnection();
        try{
            Statement st=c.createStatement();
            ResultSet res=st.executeQuery("SELECT messageID, messageRU, messageKZ, messageENG FROM messages");
            while(res.next()){
                int code=res.getInt(1);
                messageRU.put(code,res.getString(2));
                messageKZ.put(code,res.getString(3));
                messageENG.put(code,res.getString(4));
            }
            c.close();
        }catch(Exception e){
            try{
                c.close();
            }catch(Exception e1){
                Log.writeLog(e1);
            }
            Log.writeLog(e);
        }
    }
    public synchronized boolean checkMessages(InputStream fileReader) {
        StringTransform trsf = new StringTransform();
        DataExtractor extractor =new DataExtractor();
        ConnectionPool pool = new ConnectionPool();
        Connection c = pool.getConnection();
        if (c == null) return false;
        try {
            Statement st = c.createStatement();
            byte[] b = new byte[fileReader.available()];
            fileReader.read(b);
            String str = new String(b, "utf8");
            int tmp = 0;
            ResultSet res;
            while (str.indexOf("_") > 0) {
                if (str.indexOf("\r") == 0)
                    str = str.substring(2, str.length());

                if (extractor.getInteger(str.substring(0, str.indexOf("|"))) == 0) {
                    tmp = 1;
                }
                String number = str.substring(tmp, str.indexOf("|"));

                tmp = 0;
                int index = 0;
                try {
                    index = new Integer(number).intValue();
                } catch (Exception e) {
                    index = new Integer(number.substring(1));
                }
                boolean exist = false;

                st.executeQuery("SET NAMES 'utf8'");
                st.executeQuery("SET CHARACTER SET 'utf8'");

                res = st.executeQuery("SELECT messageID FROM messages WHERE messageID=" + index);
                if (res.next()) {
                    exist = true;
                } else {
                    exist = false;
                }
                if (exist) {
                    st.execute("DELETE FROM messages WHERE messageid=" + index + "");
                }
                str = str.substring(str.indexOf("|") + 1, str.length());
                String russian = str.substring(0, str.indexOf("|"));
                str = str.substring(str.indexOf("|") + 1, str.length());
                String kazakh = str.substring(0, str.indexOf("|"));
                str = str.substring(str.indexOf("|") + 1, str.length());
                String english = str.substring(0, str.indexOf("_"));

                st.execute("INSERT INTO messages (messageID, messageRu, messageKZ, messageEng) VALUES (" + index + ", '" + trsf.getDBString(russian) + "'," +
                        "'" + trsf.getDBString(kazakh) + "', '" + trsf.getDBString(english) + "')");
                str = str.substring(str.indexOf("_") + 1, str.length());
            }
            MessageManager.initializeMessages();
            c.close();
            return true;
        } catch (Exception e) {
            Log.writeLog(e);
            try {
                c.close();
            } catch (Exception e1) {
                Log.writeLog(e1);
            }
        }
        return false;
    }


    public static String readSimpleMessage(int language, int messageID){

        String messageKZ = "";
        String messageRU = "";
        String messageEN = "";

        ConnectionPool pool=new ConnectionPool();
        Connection c=pool.getConnection();
        try{
            Statement st=c.createStatement();
            ResultSet res=st.executeQuery("SELECT messageID, messageRU, messageKZ, messageENG FROM messages where messageID="+messageID+"");
            if(res.next()){
                messageRU = res.getString(2);
                messageKZ = res.getString(3);
                messageEN = res.getString(4);
            }
            c.close();

            if(language==Languages.RUSSIAN){
               return messageRU;
            } else if(language==Languages.KAZAKH){
                return messageKZ;
            } else if(language==Languages.ENGLISH){
                return messageEN;
            }
        }catch(Exception e){
            try{
                c.close();
            }catch(Exception e1){
                Log.writeLog(e1);
            }
            Log.writeLog(e);
        }

        return "";
    }


}
