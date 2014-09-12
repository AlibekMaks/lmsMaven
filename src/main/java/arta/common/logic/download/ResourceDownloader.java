package arta.common.logic.download;

import arta.common.logic.util.Translit;
import arta.common.logic.util.DataExtractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;


public class ResourceDownloader {

    Downloadable resource;


    public ResourceDownloader(Downloadable resource) {
        this.resource = resource;
    }

    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String BYTES = "bytes";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String OCTET_STREAM = "application/octet-stream";
    public static final String CONNECTION = "Connection";
    public static final String CLOSE = "close";
    public static final String CONTENT_DISPOSITON = "Content-Disposition";
    public static final String INLINE = "inline; ";
    public static final String ATTACHMENT = "attachment; ";
    public static final String FILE = "filename=\"";
    public static final String CONTENT_RANGE = "Content-Range";

    public void download(HttpServletRequest request, HttpServletResponse response, boolean inline)
            throws Exception{
        int contentLength = 0;
        String range = request.getHeader("range");
        if (range == null || range.indexOf("=")<0 || range.indexOf("-")<0){
            resource.init();
            contentLength = resource.getLength();
            String filename = new Translit().translit(resource.getFileName());

            if (inline){
                response.addHeader(CONTENT_DISPOSITON, INLINE+FILE + filename+"\"");
            } else {
                response.addHeader(CONTENT_DISPOSITON, ATTACHMENT + FILE + filename +"\"");
            }

            response.addHeader(ACCEPT_RANGES, BYTES);
            response.addHeader(CONTENT_LENGTH, contentLength+"");
            response.addHeader(CONTENT_TYPE, resource.getMimeType());
            response.addHeader(CONNECTION, CLOSE);
            OutputStream out  = response.getOutputStream();
            resource.writeResource(out);
            out.flush();
            out.close();
        } else {
            int start = new DataExtractor().getInteger(range.substring(range.indexOf("=")+1, range.indexOf("-")));
            resource.init();
            contentLength = resource.getLength() - start;
            if (contentLength < 0) contentLength = 0; 
            String contentRange = BYTES+" "+start+"-"+resource.getLength()+"/"+(resource.getLength()+1);
            String filename = new Translit().translit(resource.getFileName());

            if (inline){
                response.addHeader(CONTENT_DISPOSITON, INLINE+FILE + filename +"\"");
            } else {
                response.addHeader(CONTENT_DISPOSITON, ATTACHMENT + FILE + filename +"\"");
            }

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.addHeader(ACCEPT_RANGES, BYTES);
            response.addHeader(CONTENT_LENGTH, contentLength+"");
            response.addHeader(CONTENT_TYPE, resource.getMimeType());
            response.addHeader(CONNECTION, CLOSE);
            response.addHeader(CONTENT_RANGE, contentRange);
            OutputStream out  = response.getOutputStream();
            resource.writeResource(start, out);
            out.flush();
            out.close();
        }
    }

}
