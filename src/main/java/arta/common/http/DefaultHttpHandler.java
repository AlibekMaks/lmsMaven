package arta.common.http;

import java.util.ArrayList;


public abstract class DefaultHttpHandler extends SimpleHandler{


    private boolean fileStarted = false;


    private String fileName;
    private String mimeType;

    public static final int bufferCapacity = 4194304;
    ByteBuffer buffer = new ByteBuffer(4194304*2);

    public void startData(String name, String fileName, String mimeType, byte[] b) {
        if (fileName == null)
            isFile = false;
        else
            isFile = true;
        if (!isFile)
            super.startData(name, fileName, mimeType, b);
        else {
            this.fileName = fileName;
            this.mimeType = mimeType;
            fileStarted = false;
            buffer.delete(0, buffer.getSize());
            buffer.append(b, b.length);
            if (buffer.getSize() >= bufferCapacity){
                startFile(name, fileName, mimeType, b);
                buffer.delete(0, bufferCapacity);
                fileStarted = true;
            }
        }
    }

    public void appendData(String name, byte[] b) {
        if (!isFile){
            super.appendData(name, b);
        } else {
            buffer.append(b, b.length);
            if (buffer.getSize() >= bufferCapacity){
                if (!fileStarted){
                    fileStarted = true;
                    startFile(currentParameterName.toString(), fileName, mimeType, buffer.get(0, bufferCapacity));
                    buffer.delete(0, bufferCapacity);
                } else {
                    appendFile(currentParameterName.toString(), buffer.get(0, bufferCapacity));
                    buffer.delete(0, bufferCapacity);
                }
            }
        }
    }

    public void endData(String name) {
        if (!isFile){
            super.endData(name);
        } else {
            if (!fileStarted){
                fileStarted = true;
                startFile(currentParameterName.toString(), fileName, mimeType, buffer.get(0, buffer.getSize()));
                buffer.delete(0, buffer.getSize());
            } else {
                appendFile(currentParameterName.toString(), buffer.get(0, buffer.getSize()));
            }
            buffer.delete(0, buffer.getSize());
        }
    }


    public void requestParsed() {
        dataParsed();
    }

    public abstract void startFile(String name, String fileName, String mimeType, byte[] b);

    public abstract void appendFile(String name, byte[] b);

    public abstract void dataParsed();

}
