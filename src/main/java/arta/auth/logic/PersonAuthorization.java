package arta.auth.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.*;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.filecabinet.logic.FileCabinetMessages;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;


public class PersonAuthorization {

    public boolean changeParams(String login, String password, String confirm, int personID,
                                int roleID, Message message, int lang){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        StringTransform trsf = new StringTransform();

        if (login == null || login.length() == 0 || password == null || password.length() == 0 ||
                confirm == null || personID <= 0 ||
                (roleID != Constants.TUTOR && roleID != Constants.STUDENT)){
            message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_NOT_BEEN_CHANGED, null ));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }

        if(login.length() > Varchar.LOGIN/2){
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, AuthorizationMessages.LOGIN, prop));
            message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_NOT_BEEN_CHANGED, null ));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirm)){
            message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_NOT_BEEN_CHANGED, null ));
            message.setMessage(MessageManager.getMessage(lang, AuthorizationMessages.PASSWORD_AND_ITS_CONFIRM_ARE_NOT_EQUAL, null));
            message.setMessageType(Message.ERROR_MESSAGE);
            return false;
        }

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (roleID == Constants.TUTOR){

                res = st.executeQuery("SELECT COUNT(*) FROM tutors WHERE login LIKE '"+trsf.getDBString(login)+"' " +
                        " AND tutorID<>"+personID);
                if (res.next() && res.getInt(1)>0){
                    message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_NOT_BEEN_CHANGED, null ));
                    message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.LOGIN_IS_IN_USE, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;
                }

                st.execute("UPDATE tutors SET login='"+trsf.getDBString(login)+"', " +
                        " password='"+ MD5.crypt(password) +"' WHERE tutorID="+personID+" ");

            } else if (roleID == Constants.STUDENT){

                res = st.executeQuery("SELECT COUNT(*) FROM students WHERE login LIKE '"+trsf.getDBString(login)+"' " +
                        " AND studentID<>"+personID);
                if (res.next() && res.getInt(1)>0){
                    message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_NOT_BEEN_CHANGED, null ));
                    message.setMessage(MessageManager.getMessage(lang, FileCabinetMessages.LOGIN_IS_IN_USE, null));
                    message.setMessageType(Message.ERROR_MESSAGE);
                    return false;
                }

                st.execute("UPDATE students SET login='"+trsf.getDBString(login)+"', " +
                        " password='"+ MD5.crypt(password) +"' WHERE studentID="+personID+" ");

            }

            message.setMessageHeader(MessageManager.getMessage(lang, FileCabinetMessages.AUTH_PARAMS_HAS_BEEN_CHANGED, null));
            return true;
        } catch (Exception exc){
            Log.writeLog(exc);
            return false;
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
