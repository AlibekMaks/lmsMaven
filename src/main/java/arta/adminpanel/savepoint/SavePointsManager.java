package arta.adminpanel.savepoint;

import arta.common.logic.util.Message;
import arta.common.logic.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class SavePointsManager {

    private String copiesFolder = "";
    public ArrayList <SavePoint> copies = new ArrayList <SavePoint> ();

    public void setCopiesFolder(String copiesFolder) {
        this.copiesFolder = copiesFolder;
    }

    public void getCopiesList(Message message){
        try{
            File file = new File(copiesFolder);
            if (file.exists()){
                String[] list = file.list();
                for (int i=0; i<list.length; i++){
                    File tmp = new File(copiesFolder+"//"+list[i]);
                    if (tmp.isDirectory()){
                        SavePoint savePoint = new SavePoint();
                        savePoint.savePointName = tmp.getName();
                        copies.add(savePoint);
                    }
                }
            }
            Collections.sort(copies);
        } catch (Exception exc){
            Log.writeLog(exc);
            message.setMessage("Exception: "+exc);
        }
    }

    public boolean deleteCopy(String copyName, Message message){
        boolean deleted = false;
        try{
            File file = new File(copiesFolder+"//"+copyName);

            String[] list = file.list();
            for (int i=0; i<list.length; i++){
                File tmp = new File(copiesFolder+"//"+copyName+"//"+list[i]);
                tmp.delete();
            }

            if (file.delete()){
                message.setMessage("Copy "+copyName+" deleted.");
            } else {
                message.setMessage("Copy "+copyName+" not deleted.");
            }
        } catch (Exception e){
            Log.writeLog(e);
            message.setMessage("Exception: "+message.getMessage());
        }
        return deleted;
    }

}
