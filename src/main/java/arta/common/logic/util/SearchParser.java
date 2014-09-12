package arta.common.logic.util;

import java.util.ArrayList;


public class SearchParser {

    private ArrayList <String>  tokens = null;

    public ArrayList <String> parseSearchString(StringBuffer search){
        tokens = new ArrayList <String> ();
        StringTransform trsf = new StringTransform();
        if (search == null)
            return tokens;
        trim(search);
        while (search.indexOf(" ")>0){
            String tmp = search.substring(0, search.indexOf(" "));
            search.delete(0, search.indexOf(" "));
            trim(search);
            tokens.add(tmp);
        }
        if (search.indexOf(" ")==-1){
            if (search.length() >0)
                tokens.add(trsf.getDBString(search.toString()));
        }
        return tokens;
    }

    private void trim(StringBuffer str){
        while (str.length()>0 && str.substring(0, 1).equals(" "))
            str.delete(0,1);
        while (str.length()>0 && str.substring(str.length()-1, str.length()).equals(" "))
            str.delete(str.length()-1, str.length());
    }


    public ArrayList<String> getTokens() {
        return tokens;
    }

    public String getCondition(ArrayList fields){
        StringBuffer condition = new StringBuffer();
        for (int i=0; i<tokens.size(); i++){
            if (i > 0)
                condition.append(" OR ");
            for (int j=0; j<fields.size(); j++){
                if (j > 0)
                    condition.append(" OR ");
                condition.append(fields.get(j));
                condition.append(" LIKE '%");
                condition.append(tokens.get(i));
                condition.append("%' ");
            }
        }
        return condition.toString();
    }

    public String getCondition(String [] fields){
        StringBuffer condition = new StringBuffer();
        for (int i=0; i<tokens.size(); i++){
            if (i > 0)
                condition.append(" OR ");
            for (int j=0; j<fields.length; j++){
                if (j > 0)
                    condition.append(" OR ");
                condition.append(fields[j]);
                condition.append(" LIKE '%");
                condition.append(tokens.get(i));
                condition.append("%' ");
            }
        }
        return condition.toString();
    }
}
