package arta.common.http;

import arta.common.html.handler.TemplateHandler;
import arta.common.logic.util.Log;

import java.util.ArrayList;


public class SimpleHandler extends HttpHandler {

    ArrayList<Parameter> parameters = new ArrayList <Parameter>  ();

    protected StringBuffer currentParameterName = new StringBuffer();
    protected StringBuffer currentParameterValue = new StringBuffer();
    protected boolean isFile = false;

    public void startData(String name, String fileName, String mimeType, byte[] b) {
        currentParameterName.delete(0, currentParameterName.length());
        currentParameterValue.delete(0, currentParameterValue.length());
        isFile = false;
        if (fileName != null)
            isFile = true;
        currentParameterName.append(name);
        try{
            currentParameterValue.append(new String(b, "utf8"));
        } catch (Exception exc){
            Log.writeLog(exc);                    
        }
    }

    public void appendData(String name, byte[] b) {
        if (!isFile){
            if (currentParameterName.length() > 0){
                try{
                    currentParameterValue.append(new String(b, "utf8"));
                } catch (Exception exc){
                    Log.writeLog(exc);
                }
            }
        }
    }

    public void endData(String name) {
        if (!isFile){
            if (currentParameterName.length() > 0){
                parameters.add(new Parameter(currentParameterName.toString(), currentParameterValue.toString()));
            }
        }
    }

    public String getParameter(String name){
        for (int i = 0; i < parameters.size(); i++){
            if(parameters.get(i).name.equals(name))
                return parameters.get(i).value;
        }
        return null;
    }


    public void requestParsed() {}
}
