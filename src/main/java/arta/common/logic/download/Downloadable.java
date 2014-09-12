package arta.common.logic.download;

import java.io.OutputStream;


public interface Downloadable {

    public void writeResource(OutputStream out);

    public void writeResource(int start, OutputStream out);

    public void init();
    
    public int getLength();

    public String getFileName();

    public String getMimeType();

}
