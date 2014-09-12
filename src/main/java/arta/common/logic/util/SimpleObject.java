package arta.common.logic.util;


public class SimpleObject {
    public String name = "";
    public int id = 0;


    public SimpleObject() {
    }


    public SimpleObject(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName(){
        if (name == null) return "";
        return name;
    }
}
