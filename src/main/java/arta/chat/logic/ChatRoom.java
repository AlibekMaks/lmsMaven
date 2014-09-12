package arta.chat.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

public class ChatRoom {

    /**
     * in miliseconds
     */
    public static final long MESSAGE_LIFE_TIME = 60000;

    public static final long USER_TIME = 60000;

    public int roomID;
    public String roomName = "";
    ArrayList <ChatPerson> persons;
    ArrayList <ChatMessage> messages;
    ArrayList <BBElement> elements = new ArrayList <BBElement> ();

    public ChatRoom(int roomID) {
        this.roomID = roomID;
        persons = new ArrayList <ChatPerson> () ;
        messages = new ArrayList <ChatMessage> ();
    }

    public void checkRoom(long currentTime){
        for (int i=messages.size()-1; i>=0; i--){
            if (messages.get(i).isDeletePossible(currentTime))
                messages.remove(i);
        }
        for (int i=persons.size()-1; i>=0; i--){
            if (!persons.get(i).isUserPresent(currentTime))
                persons.remove(i);
        }
    }

    public void addMessage(String message, String sender, int senderRoleID, long curTime){
        ChatMessage chatMessage = new ChatMessage(message, sender, senderRoleID, curTime);
        messages.add(chatMessage);
    }

    public int getPersonsCount(){
        if (persons == null)
            return 0;
        return persons.size();
    }

    public void addBBImage(byte[] image, long time, int senderRoleID, int senderPersonID){
        elements.add(new BBImageElement(image, senderPersonID, senderRoleID, time));
    }

    public void addBBElement(String element, int senderRoleID, int senderPersonID, long time){
        elements.add(new BBSimpleElement(element, senderPersonID, senderRoleID, time));
    }

    /**
     *
     * @param person
     * @return
     */
    public byte [] getNextImage(ChatPerson person){

        if (person == null)
            return null;

        for (int i = elements.size() - 1; i >= 0; i --){
            if (elements.get(i).time > person.lastGetElements && elements.get(i).isBinaryData()){
                if (i > 0){
                    if (elements.get(i-1).time > person.lastGetElements){
                        return null;
                    }
                    return ((BBImageElement)elements.get(i)).image;
                }
            }
        }

        return null;

    }

    public ArrayList <String> getBBElements(ChatPerson person, long time){

        int i = elements.size() - 1;

        ArrayList <String> newElements = new ArrayList <String> ();

        if (person.roleID == Constants.STUDENT){
            if (person.canDraw){
                newElements.add("enable");
            } else {
                newElements.add("disable");
            }
        } else {
            newElements.add("enable");
        }

        while (i >= 0 && elements.get(i).time > person.lastGetElements
                && elements.get(i).time < time
                && !elements.get(i).isBinaryData()){
            newElements.add(((BBSimpleElement)elements.get(i)).element);
            i --;
        }        

        return newElements;
    }

    public void changeStudentStatus(int studentID, boolean status){

        for (int i=0; i < persons.size(); i ++){
            if (persons.get(i).roleID == Constants.STUDENT && persons.get(i).personID == studentID){
                persons.get(i).canDraw = status;
            }
        }

    }

}
