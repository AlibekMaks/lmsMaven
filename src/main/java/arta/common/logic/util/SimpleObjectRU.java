package arta.common.logic.util;


public class SimpleObjectRU extends SimpleObject {

    public int id = 0;
    public String nameru = "";

    public SimpleObjectRU() {
    }


    public SimpleObjectRU(int id, String nameru) {

        this.nameru = nameru;
        this.id = id;
    }

    public String getNameru(){
        if (nameru == null) return "";
        return nameru;
    }

}
