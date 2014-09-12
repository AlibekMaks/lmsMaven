package arta.common.logic.util;


public class DataExtractor {

    public boolean isInteger(Object obj){
        if(obj==null){
            return false;
        }
        try{
            new Integer(obj.toString());
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public Integer getInteger(Object obj){
        if(obj==null){
            return 0;
        }
        try{
            return new Integer(obj.toString());
        }catch(Exception e){
            return 0;
        }
    }

    public double getDouble(Object obj){
        if(obj==null){
            return 0;
        }
        try{
            return new Double(obj.toString()).doubleValue();
        }catch(Exception e){
            return 0;
        }
    }

    public long getLong(Object obj){
        if(obj==null){
            return 0;
        }
        try{
            return new Long(obj.toString()).longValue();
        }catch(Exception e){
            return 0;
        }
    }

    public int getYear(Object obj){
        if(obj==null){
            return 2003;
        }
        try{
            return new Integer(obj.toString());
        }catch(Exception e){
            return 2003;
        }
    }

    public int getDateMonth(Object obj){
        if(obj==null){
            return 1;
        }
        try{
            return new Integer(obj.toString());
        }catch(Exception e){
            return 1;
        }
    }

    public boolean getBoolean(Object obj){
        if(obj==null)return false;
        if(obj.toString().toLowerCase().equals("true")){
            return true;
        }else{
            return false;
        }
    }

    public String getRequestString(String str){
        if (str != null)
            try{
                return new String(str.getBytes(Encoding.ISO), Encoding.UTF);
            }catch (Exception exc){
                Log.writeLog(exc);
            }
        return "";
    }

}
