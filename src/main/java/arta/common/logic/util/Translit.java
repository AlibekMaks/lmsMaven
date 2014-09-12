package arta.common.logic.util;


public class Translit {

    public String translit(String str){
        StringBuffer translited = new StringBuffer();
        for (int i=0; i<str.length(); i++){
            translited.append(getEquiv(str.substring(i, i+1).toLowerCase()));
        }
        return translited.toString();
    }

    private String getEquiv(String letter){
        if (letter.equals("а")){
            return "a";
        } else if (letter.equals("б")){
            return "b";
        } else if (letter.equals("в")){
            return "v";
        } else if (letter.equals("г")){
            return "g";
        } else if (letter.equals("д")){
            return "d";
        } else if (letter.equals("е")){
            return "e";
        } else if (letter.equals("ё")){
            return "e";
        } else if (letter.equals("ж")){
            return "zh";
        } else if (letter.equals("з")){
            return "z";
        } else if (letter.equals("и")){
            return "i";
        } else if (letter.equals("й")){
            return "i";
        } else if (letter.equals("к")){
            return "k";
        } else if (letter.equals("л")){
            return "l";
        } else if (letter.equals("м")){
            return "m";
        } else if (letter.equals("н")){
            return "n";
        } else if (letter.equals("о")){
            return "o";
        } else if (letter.equals("п")){
            return "p";
        } else if (letter.equals("р")){
            return "r";
        } else if (letter.equals("с")){
            return "s";
        } else if (letter.equals("т")){
            return "t";
        } else if (letter.equals("у")){
            return "u";
        } else if (letter.equals("ф")){
            return "f";
        } else if (letter.equals("х")){
            return "h";
        } else if (letter.equals("ц")){
            return "ts";
        } else if (letter.equals("ч")){
            return "ch";
        } else if (letter.equals("ш")){
            return "sh";
        }  else if (letter.equals("щ")){
            return "sch";
        }  else if (letter.equals("ы")){
            return "y";
        } else if (letter.equals("ъ")){
            return "";
        } else if (letter.equals("ь")){
            return "";
        } else if (letter.equals("э")){
            return "e";
        } else if (letter.equals("ю")){
            return "u";
        } else if (letter.equals("я")){
            return "ya";
        } else if (letter.equals("ә")){
            return "a";
        } else if (letter.equals("і")){
            return "i";
        } else if (letter.equals("ң")){
            return "n";
        } else if (letter.equals("ғ")){
            return "g";
        } else if (letter.equals("ү")){
            return "u";
        } else if (letter.equals("ұ")){
            return "u";
        } else if (letter.equals("қ")){
            return "k";
        } else if (letter.equals("ө")){
            return "o";
        } else if (letter.equals("һ")){
            return "h";
        }
        return letter;
    }
}
