package arta.chat.logic;

import arta.common.logic.util.Log;
import arta.chat.servlet.ChatServlet;

import java.util.ArrayList;


public class ChatRooms {

    public ArrayList <ChatRoom> rooms = new ArrayList <ChatRoom> ();

    public void performCheck(long time){
        for (int i=rooms.size()-1; i>=0; i--){
            int lockRoom = rooms.get(i).roomID;
            try{
                ChatServlet.chatLocker.execute(lockRoom);
                rooms.get(i).checkRoom(time);
                if (rooms.get(i).persons.size() == 0)
                    rooms.remove(i);
            } catch(Exception exc){
                Log.writeLog(exc);
            } finally{
                ChatServlet.chatLocker.finished(lockRoom);
            }

        }
    }
    
    public void addMessage(String message, String sender, int senderRoleID,
                           long curTime, int roomID){
        for (int i=0; i<rooms.size(); i++){
            try{
                if (rooms.get(i).roomID == roomID){
                    rooms.get(i).addMessage(message, sender, senderRoleID, curTime);
                    break;
                }
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    public void addMessage(ChatMessage message, long curTime, int roomID){
        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID){
                rooms.get(i).messages.add(message);                
            }
        }
    }

    public int enterRoom(int roomID, int userID, int roleID, long time, String userLogin){

        for (int i=0; i<rooms.size(); i++){
            try{
                if (rooms.get(i).roomID == roomID){
                    for (int j=0; j<rooms.get(i).persons.size(); j++){
                        if (rooms.get(i).persons.get(j).roleID == roleID && rooms.get(i).persons.get(j).personID == userID){
                            rooms.get(i).persons.get(j).setLastGetPrivateMessagesTime(-1);
                            rooms.get(i).persons.get(j).setLastGetMessagesTime(-1);
                            rooms.get(i).persons.get(j).lastGetElements = -1;
                            rooms.get(i).persons.get(j).setRequestTime(time);
                            return rooms.get(i).roomID;
                        }
                    }
                    rooms.get(i).persons.add(new ChatPerson(userID, roleID, userLogin, time));
                    return rooms.get(i).roomID;
                }
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        ChatRoom room = new ChatRoom(roomID);
        room.persons.add(new ChatPerson(userID,  roleID, userLogin, time));
        rooms.add(room);
        return roomID;
    }

    public void addRoom(){
        ChatRoom chatRoom = new ChatRoom(0);
        chatRoom.roomName = "common";
        rooms.add(chatRoom);
    }

    public String getUserNewMessages(int personID, int roomID, int personRoleID, long time){
        StringBuffer result = new StringBuffer("");
        for (int i=0; i<rooms.size(); i++){
            try{
                if (rooms.get(i).roomID == roomID){
                    for (int j=0; j<rooms.get(i).persons.size(); j++){
                        if (rooms.get(i).persons.get(j).personID == personID &&
                                rooms.get(i).persons.get(j).roleID == personRoleID){
                            for (int k=0; k<rooms.get(i).messages.size(); k++){
                                if (rooms.get(i).messages.get(k).time > rooms.get(i).persons.get(j).lastGetMessages &&
                                         rooms.get(i).messages.get(k).messageType == ChatMessage.COMMON_MESSAGE){
                                    result.append(rooms.get(i).messages.get(k).toString());
                                }
                            }
                            rooms.get(i).persons.get(j).setLastGetMessagesTime(time);
                            rooms.get(i).persons.get(j).setRequestTime(time);
                        }
                    }
                }
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
        if (result.length() == 0)
            result = null;
        return result!=null ? result.toString() : null ;
    }

    public String getUserNewPrivateMessages(int personID, int roomID, int roleID, long time){
        String result = "";

        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID){
                for (int j=0; j<rooms.get(i).persons.size(); j++){
                    if (rooms.get(i).persons.get(j).personID == personID && rooms.get(i).persons.get(j).roleID == roleID){
                        for (int k=0; k<rooms.get(i).messages.size(); k++){
                            if (rooms.get(i).messages.get(k).messageType == ChatMessage.PRIVATE_MESSAGE &&
                                    rooms.get(i).messages.get(k).time > rooms.get(i).persons.get(j).lastGetPrivateMessages){
                                for (int l=0; l<rooms.get(i).messages.get(k).recipients.size(); l++){
                                    if ((rooms.get(i).messages.get(k).recipients.get(l).personID == personID &&
                                             rooms.get(i).messages.get(k).recipients.get(l).roleID == roleID) ||
                                             (rooms.get(i).messages.get(k).senderPersonID == personID && rooms.get(i).messages.get(k).senderRoleID==roleID) ){
                                        result += rooms.get(i).messages.get(k).toString();
                                    }
                                }
                            }
                        }
                        rooms.get(i).persons.get(j).setLastGetPrivateMessagesTime(time);
                        rooms.get(i).persons.get(j).setRequestTime(time);
                        break;
                    }
                }
                break;
            }
        }

        if (result.equals(""))
                return null;
        return result;
    }

    public ArrayList <ChatPerson> getRoomUsers(int roomID){
        ArrayList <ChatPerson> users = new ArrayList <ChatPerson> ();
        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID){
                for (int j=0; j<rooms.get(i).persons.size(); j++){
                    users.add(new ChatPerson(rooms.get(i).persons.get(j)));
                }
                break;
            }
        }
        return users;
    }

    public ChatPerson getRoomUser(int roomID, int personID, int roleID){
        ChatPerson person = null;
        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID){
                for (int j=0; j<rooms.get(i).persons.size(); j++){
                    if (rooms.get(i).persons.get(j).personID == personID &&
                            rooms.get(i).persons.get(j).roleID == roleID){
                        person = rooms.get(i).persons.get(j);
                    }
                }
                break;
            }
        }
        return person;
    }    

    public int getRoomSubjectID(int roomID){
        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID)
                return rooms.get(i).roomID;
        }
        return 0;
    }

    public ChatRoom getChatRoom(int roomID){
        for (int i=0; i<rooms.size(); i++){
            if (rooms.get(i).roomID == roomID)
                return rooms.get(i);
        }
        return null;
    }

    public void addBBImage(byte[] image, long time, int senderRoleID, int senderPersonID, int roomID){
        for (int i = 0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                rooms.get(i).addBBImage(image, time, senderRoleID, senderPersonID);
                break;
            }
        }
    }

    public void addBBElement(String element, long time, int senderRoleID, int senderPersonID, int roomID){
        for (int i = 0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                rooms.get(i).addBBElement(element, senderRoleID, senderPersonID, time);
                break;
            }
        }
    }

    public ArrayList <String> getElements(ChatPerson person, int roomID, long time){
        for (int i=0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                return rooms.get(i).getBBElements(person, time);
            }
        }
        return null;
    }

    public byte[] getLastImage(ChatPerson person, int roomID){
        for (int i = 0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                return rooms.get(i).getNextImage(person);
            }
        }
        return null;
    }

    public ChatPerson getChatPerson(int roomID, int roleID, int personID){
        for (int i = 0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                for (int j = 0; j < rooms.get(i).persons.size(); j ++){
                    if (rooms.get(i).persons.get(j).personID == personID &&
                            rooms.get(i).persons.get(j).roleID == roleID){
                        return rooms.get(i).persons.get(j);
                    }
                }
            }
        }
        return null;
    }

    public void changeStudentStatus(int studentID, int roomID, boolean status){

        for (int i=0; i < rooms.size(); i ++){
            if (rooms.get(i).roomID == roomID){
                rooms.get(i).changeStudentStatus(studentID, status);
                break;
            }
        }

    }
}
