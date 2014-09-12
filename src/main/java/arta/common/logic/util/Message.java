package arta.common.logic.util;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;


public class Message implements Serializable {
    public static final int ERROR_MESSAGE = 1;
    public static final int INFORMATION_MESSAGE = 2;

    private String messageHeader = null;
    private String message = null;
    public ArrayList <String> reasons = new ArrayList  <String> ();
    public ArrayList <String> links = new ArrayList  <String> ();
    public int messageType = 0;

    public Message(){}

    public static void writeDescription(String str, PrintWriter out){
        StringTransform sf=new StringTransform();
        if(str.length()>0){
            out.print("<table width=\"100%\" cellpadding=0 cellspacing=0 border=1 borderColor=\"#000000\" " +
                    " style=\"border-collapse:collapse\">");
            out.print("<tr><td class=\"warning\"><i>");
            out.print(sf.getHTMLString(str));
            out.print("</i></td></tr>");
            out.print("</table>");
        }
    }

    public Message(String messageHeader, String message, int messageType) {
        this.messageHeader = messageHeader;
        this.message = message;
        this.messageType = messageType;
    }

    public Message(String messageHeader, int messageType) {
        this.messageHeader = messageHeader;
        this.messageType = messageType;
    }


    public Message(String messageHeader, String message,ArrayList<String> reasons, int messageType) {
        this.messageHeader = messageHeader;
        this.message = message;
        this.reasons = reasons;
        this.messageType = messageType;
    }

    public boolean isEmpty(){
        return (message==null && messageHeader == null && (reasons == null || reasons.size()==0));
    }
    public void writeMessage(PrintWriter pw){
        if (message==null && messageHeader == null && reasons == null)
                return;

        String style = "description";
        if(messageType==ERROR_MESSAGE){
            style = "errordescription";
        } else if(messageType==INFORMATION_MESSAGE){
            style = "informationdescription";
        }

        pw.print("<table border=0 class=\""+style+"\">");
        pw.println("<tr><td align=\"center\" style=\"padding-left:30px; padding-right:30px; padding-top:5px; padding-bottom:5px\"  >");
        //pw.println("<table border=0 class=\"description\">");
        pw.println("<table border=0 >");
        if (messageHeader!=null){
            pw.println("<tr>");
            pw.println("<td ");
            pw.println(" class=\"messageHeader\" ");
            pw.print(">");
            pw.println(messageHeader);
            pw.print("</td>");
            pw.print("</tr>");
        }
        if (message!=null){
            pw.println("<tr>");
            pw.println("<td ");
            pw.println(" class=\"message\" ");
            pw.print(">");
            pw.println(message);
            pw.print("</td>");
            pw.print("</tr>");
        }
        if (reasons!=null && reasons.size()>0){
            pw.println("<tr>");
            pw.println("<td ");
            pw.println(" class=\"message\" ");
            pw.print(">");
            for (int i=0; i<reasons.size(); i++){ 
                if (reasons.get(i)!=null){
                    if (links.get(i)!=null)
                        pw.print("<a class=\"href\" href=\""+links.get(i)+"\">");
                    pw.print(reasons.get(i));
                    if (links.get(i)!=null)
                        pw.print("</a>");
                    if (i<reasons.size()-1) pw.print("<br>");
                }
            }
            pw.print("</td>");
            pw.print("</tr>");
        }
        pw.print("</table>");
        pw.print("</td></tr></table>");
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReasons(ArrayList<String> reasons) {
        this.reasons = reasons;
    }

    public void addReason(String str){
        if (reasons == null)
            reasons = new ArrayList <String> ();
        reasons.add(str);
        links.add(null);
    }

    public void addReason(String str, String link){
        if (reasons == null)
            reasons = new ArrayList <String> ();
        reasons.add(str);
        links.add(link);
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void refresh(){
        message = null;
        messageHeader = null;
        reasons.clear();
    }

    public String getMessage(){
        return  message;
    }


    public String getMessageHeader() {
        return messageHeader;
    }
}
