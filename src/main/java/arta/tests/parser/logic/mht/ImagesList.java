package arta.tests.parser.logic.mht;

import java.util.ArrayList;


public class ImagesList {

    private ArrayList<Image> images = new ArrayList<Image> ();

    public void addImage(String name, int id){
        int k=0;
        for (int i=0; i<images.size(); i++){
            if (images.get(i).imageName.equals(name)){
                k = i+1;
                break;
            }
        }
        images.add(k, new Image(name, id));
    }

    public ArrayList <Integer> getImageIds(String name){
        ArrayList <Integer> ids = new ArrayList<Integer> () ;
        for (int i=0; i<images.size(); i++){
            if (images.get(i).imageName.equals(name)){
                ids.add(images.get(i).id);
                i++;
                while (i < images.size() && images.get(i).imageName.equals(name)){
                    ids.add(images.get(i).id);
                    i++;
                }
                break;
            }
        }
        return ids;
    }

    class Image{
        public Image(String name, int id){
            this.imageName = name;
            this.id = id;
        }
        public String imageName = "";
        public int id;
    }
}

