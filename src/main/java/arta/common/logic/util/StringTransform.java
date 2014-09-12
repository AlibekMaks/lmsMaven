package arta.common.logic.util;

public class StringTransform {

    public String getDBString(String str) {
        if (str == null)
            return "";
        str = replaceSlash(str);
        return str.replaceAll("\'", "\'\'");
    }

    public String getDBString(String str, int maxLength){
        if (str == null)
            return "";
        str = str.replaceAll("\'", "\'\'");
        str = replaceSlash(str);
        if (str.length() > maxLength)
            str = str.substring(0 , maxLength);
        return str;
    }

    public String getDBStringSCORM(String str, int maxLength){
        if (str == null)
            return "";
        try{
            str = new String(str.getBytes(), Encoding.UTF);
        } catch(Exception exc){
            Log.writeLog(exc);
        }
        str = str.replaceAll("\'", "\'\'");
        str = replaceSlash(str);
        if (str.length() > maxLength)
            str = str.substring(0 , maxLength);
        return str;
    }

    public static String replaceSlash(String str){
        int index = -1;
        String result = "";
        while ((index = str.indexOf("\\")) >= 0){
            result += str.substring(0, index) + "\\\\" ;
            str = str.substring(index+1, str.length());
        }
        result += str;
        return result;
    }

    public String makeChatSting(String str) {
        if (str == null)
            return "";
        str = str.replaceAll("\'", "&#39;");
        str = str.replaceAll("'", "&#39;");
        str = str.replaceAll("\n", "");
        str = str.replaceAll("\r", "");
        str = str.replaceAll("\t", "");
        return str;
    }

    public String getHTMLString(String str) {
        if (str == null)
            return "";
        str = str.replaceAll("\'\'", "\'");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&#39;");
        str = str.replaceAll("\"", "&#8221;");
        return str;
    } 

    public String getHTMLString(String str, String defaultValue) {
        if (str == null || str.length() == 0) return getHTMLString(defaultValue);
        str = str.replaceAll("\'\'", "\'");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("'", "&#39;");
        str = str.replaceAll("\"", "&#8221;");
        return str;
    }

    public String decodeUnicodeToUTF(String unicode) throws Exception {
        StringBuffer result = new StringBuffer();
        StringBuffer str = new StringBuffer(unicode);
        while (str.length() > 0) {
            if (str.length() > 5) {
                if (str.substring(0, 2).equals("%u")) {
                    char tmp = (char) Integer.decode("0x" + str.substring(2, 6)).intValue();
                    result.append(tmp);
                    str.delete(0, 6);
                    continue;
                }
            }
            if (str.length() > 2) {
                if (str.substring(0, 1).equals("%")) {
                    result.append((char) Integer.decode("0x" + str.substring(1, 3)).intValue());
                    str.delete(0, 3);
                    continue;
                }
            }
            result.append(str.substring(0, 1));
            str.delete(0, 1);
        }
        return result.toString();
    }

    public String getXLSString(String str){
        if (str == null)
            return "";
        StringBuffer buf = new StringBuffer(str);
        int index;
        while ((index = buf.indexOf("\r")) >= 0){
            buf.delete(index, index+1);
        }
        while ((index = buf.indexOf("\n")) >= 0){
            buf.delete(index, index+1);
        }
        while ((index = buf.indexOf("\t")) >= 0){
            buf.delete(index, index+1);
        }
        int tmp = -1;
        while ((index = buf.indexOf("<")) >= 0){
            if (tmp == index)
                break;
            tmp = index;
            int end = buf.indexOf(">");
            if (end >= 0 && end > index){
                buf.delete(index, end+1);
            }
        }

        String s = buf.toString();
        s = s.replaceAll("&nbsp;", " ");
        s = s.replaceAll("&quot;", "\"");
        s = s.replaceAll("&hellip;", "...");
        s = s.replaceAll("&prime;", "'");
        s = s.replaceAll("&lsaquo;", "<");
        s = s.replaceAll("&rsaquo;", ">");
        s = s.replaceAll("&laquo;", "<<");
        s = s.replaceAll("&raquo;", ">>");
        s = s.replaceAll("&rsquo;", "'");
        s = s.replaceAll("&lsquo;", "'");
        s = s.replaceAll("&ldquo;", "\"");
        s = s.replaceAll("&sbquo;", ",");
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&le;", "<=");
        s = s.replaceAll("&ndash;", "-");
        s = s.replaceAll("&macr;", "-");
        s = s.replaceAll("&minus;", "-");
        s = s.replaceAll("&plusmn;", "+-");
        s = s.replaceAll("&divide;", "/");
        s = s.replaceAll("&frasl;", "/");
        s = s.replaceAll("&times;", "*");

        s = s.replaceAll("&amp;", "&");

        return s;
    }


}
