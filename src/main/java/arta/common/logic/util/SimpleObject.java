package arta.common.logic.util;


public class SimpleObject {

    public String name = "";
    public int id = 0;
//    public String namekz = "";
//    public String nameru = "";

    public SimpleObject() {
    }


    public SimpleObject(int id, String name) {
        this.name = name;
//        this.namekz = namekz;
//        this.nameru = nameru;
        this.id = id;
    }

//    public String getNamekz(){
//        if (namekz == null) return "";
//        return namekz;
//    }
//    public String getNameru(){
//        if (nameru == null) return "";
//        return nameru;
//    }
    public String getName(){
        if (name == null) return "";
        return name;
    }
}
