package arta.chat.logic;

import com.bentofw.mime.ParsedData;
import arta.common.logic.util.Encoding;
import arta.common.logic.util.DataExtractor;
import arta.common.logic.util.Log;

public class MessageExtractor {

    public ChatMessage extract(ParsedData data){
        ChatMessage message = null;
        try{

            DataExtractor extractor = new DataExtractor();
            String str = data.getParameter("message");
            if (str == null)
                return null;
            str = new String(str.getBytes(Encoding.ISO), Encoding.UTF);
            message = new ChatMessage();
            message.message = str;

            String type = data.getParameter("type");
            if (type!=null && type.equals("private")){
                message.messageType = ChatMessage.PRIVATE_MESSAGE;
                Object [] params = data.getParameterNames().toArray();
                for (int i=0; i<params.length; i++){
                    String tmp = (String)params[i];
                    int personID = 0;
                    int roleID = 0;
                    if (tmp.length()>8 && tmp.substring(0, 8).equals("private_")){
                        tmp = tmp.substring(8, tmp.length());
                        if (tmp.length()>7 && tmp.substring(0, 7).equals("roleID_")){
                            tmp = tmp.substring(7, tmp.length());
                            if (tmp.indexOf("_")>0){
                                roleID = extractor.getInteger(tmp.substring(0, tmp.indexOf("_")));
                                tmp = tmp.substring(tmp.indexOf("_")+1, tmp.length());
                                if (tmp.length()>7 && tmp.substring(0, 7).equals("persID_")){
                                    tmp = tmp.substring(7, tmp.length());
                                    personID = extractor.getInteger(tmp);
                                }
                            }
                        }
                        if (personID > 0 && roleID > 0){
                            message.recipients.add(new SimpleChatPerson(personID, roleID));
                        }
                    }
                }
            }
        } catch (Exception exc){
            Log.writeLog(exc);
            return null;
        }
        return message;
    }

}
