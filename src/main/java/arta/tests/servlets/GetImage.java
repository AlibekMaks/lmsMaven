package arta.tests.servlets;

import arta.common.logic.util.Log;
import arta.common.logic.util.DataExtractor;
import arta.tests.imageExchange.ImageDownload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;


public class GetImage extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{

            DataExtractor extractor = new DataExtractor();
            int imageID = extractor.getInteger(request.getParameter("id"));
            ImageDownload imageDownload = new ImageDownload();
            byte [] img = imageDownload.getImge(imageID);

            response.setContentType(imageDownload.getMimeType());
            response.addHeader("Content-Disposition", encodeContentDispositionForDownload(request, "image", true));

            
            OutputStream out = response.getOutputStream();
            out.write(img);                        
        } catch (Exception exc){
           // Log.writeLog(exc);
        }
    }

  private String encodeContentDispositionForDownload(HttpServletRequest request, String fileName, boolean isInline)
                throws UnsupportedEncodingException {
            if ( fileName == null ) throw new IllegalArgumentException("Value of the \"filename\" parameter cannot be null!");
            String contentDisposition = isInline ? "inline; " : "attachment; ";
            String agent = request.getHeader("USER-AGENT").toLowerCase();
        if(agent!=null){
                if (agent != null && agent.indexOf("opera") == -1 && agent.indexOf("msie") != -1)
                    // IE

                    contentDisposition += "filename=\"" + toUTF8String(fileName) + "\"";
                else if (agent.indexOf("opera") != -1)
                    // Opera
                    contentDisposition += "filename=" + toUTF8String(fileName);
                else
                    // Firefox and others
                    contentDisposition += "filename=\"" + MimeUtility.encodeText(fileName, "UTF8", "B")+ "\"";
            }
            return contentDisposition;
        }

    private String toUTF8String(String s) throws UnsupportedEncodingException {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 0 && c <= 255 && !Character.isWhitespace(c)) {
                    sb.append(c);
                } else {
                    byte[] b;
                    b = Character.toString(c).getBytes("utf-8");
                    for (int j = 0; j < b.length; j++) {
                        int k = b[j];
                        if (k < 0) k += 256;
                        sb.append("%" + Integer.toHexString(k).toUpperCase());
                    }
                }
            }
            return sb.toString();
        }
}
