package arta.common.logic.util;


public class SimpleObjectKZ {

    public int id = 0;
    public String namekz = "";

    public SimpleObjectKZ() {
    }


    public SimpleObjectKZ(int id, String namekz) {

        this.namekz = namekz;
        this.id = id;
    }

        public String getNamekz(){
        if (namekz == null) return "";
        return namekz;
    }


}
