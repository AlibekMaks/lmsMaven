package arta.common.logic.util;


public class SimpleObject {
    public String nameru = "";
    public int id = 0;


    public SimpleObject() {
    }


    public SimpleObject(int id, String nameru) {
        this.nameru = nameru;
        this.id = id;
    }

    public String getName(){
        if (nameru == null) return "";
        return nameru;
    }
}
