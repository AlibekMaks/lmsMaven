package arta.common.http;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Enumeration;


public class HttpRequestParser {

    HttpHandler handler;
    InputStream in;
    HashMap requestHeaders;

    Boundary boundary;
    FileParser fileParser = new FileParser("");
    ParameterNameParser parameterParser = new ParameterNameParser();

    int partLength = 1024;

    /**
     * @param handler - Обработчик парсера
     * @param request - Http запрос
     * @throws Exception
     */
    public HttpRequestParser(HttpHandler handler, HttpServletRequest request)
                                                                        throws Exception{
        this.handler = handler;
        this.in = request.getInputStream();
        Enumeration en = request.getHeaderNames();
        requestHeaders = new HashMap();
        while(en.hasMoreElements()){
            String name = (String) en.nextElement();
            requestHeaders.put(name, request.getHeader(name));
        }        
        boundary = new Boundary((String)requestHeaders.get("content-type"));
    }

    String currentParameterName = null;
    ByteBuffer buffer = new ByteBuffer(partLength * 2 + 1024);

    /**
     * Метод непосредственно парсит запрос
     * @throws Exception
     */
    public void parse() throws Exception{
        byte [] b = new byte[partLength];
        int read = -1;
        out: while ((read = in.read(b)) != -1){
            buffer.append(b, read);
            while (buffer.indexOf(boundary.getStartBoundary().getBytes()) >= 0){
                if (currentParameterName != null){
                    if (buffer.indexOf(boundary.getStartBoundary().getBytes()) >= 2)
                        handler.appendData(currentParameterName, buffer.get(0, buffer.indexOf(boundary.getStartBoundary().getBytes()) - 2));
                    else
                        handler.appendData(currentParameterName, buffer.get(0, buffer.indexOf(boundary.getStartBoundary().getBytes())));
                    handler.endData(currentParameterName);
                    currentParameterName = null;
                    buffer.delete(0, buffer.indexOf(boundary.getStartBoundary().getBytes()));
                }
                int rnrn = buffer.indexOf("\r\n\r\n".getBytes());
                if (rnrn  == -1)
                    continue out;

                parameterParser.setParseString(new StringBuffer(new String(buffer.get(0, rnrn + "\r\n\r\n".length()), "utf8")));
                parameterParser.parse();
                currentParameterName = parameterParser.getParameterName();
                buffer.delete(0, rnrn + "\r\n\r\n".length());
                int nextBoundary = buffer.indexOf(boundary.getStartBoundary().getBytes());

                if (nextBoundary == -1){
                    handler.startData(currentParameterName, parameterParser.getFileName(), parameterParser.mimeType, new byte[0]);
                    continue out;
                    /*buffer.delete(0, buffer.getSize());*/
                } else {
                    if (nextBoundary >=2){
                        handler.startData(currentParameterName, parameterParser.getFileName(),parameterParser.mimeType, buffer.get(0, nextBoundary - 2));
                        buffer.delete(0, nextBoundary);   
                    } else {
                        handler.startData(currentParameterName, parameterParser.getFileName(), parameterParser.mimeType, new byte[0]);
                        continue out;
                    }
                }
            }
            if (buffer.getSize() > 0 && currentParameterName != null){
                int k = -1;
                if ((k=buffer.endsWith(("\r\n" + boundary.getStartBoundary()).getBytes())) !=-1){
                    handler.appendData(currentParameterName, buffer.get(0, k-1));
                    buffer.delete(0, k-1);
                } else {
                    handler.appendData(currentParameterName, buffer.get(0, buffer.getSize()));
                    buffer.delete(0, buffer.getSize());
                }
            }
        }
        
    }

}
