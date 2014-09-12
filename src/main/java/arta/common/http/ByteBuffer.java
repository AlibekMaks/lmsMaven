package arta.common.http;

import java.util.ArrayList;
import java.util.List;


public class ByteBuffer {


    private byte[] bytes;
    private int maxCapacity;
    private int currentSize = 0;

    public ByteBuffer(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        bytes = new byte[maxCapacity];
    }

    public void append(byte [] b, int size){
        for (int i = 0; i < size; i++){
            bytes[currentSize + i] = b[i];
        }
        currentSize += size;
    }

    public void delete(int off, int len){
        for (int i = off + len ; i < currentSize; i++){
            bytes[i - len ] = bytes[i];
        }
        currentSize -= len;
    }

    public byte[] get(int off, int len){
        byte [] b = new byte[len];
        for (int i = off; i < off + len; i++){
            b[i - off] = bytes[i];
        }
        return b;
    }

    public int getSize(){
        return currentSize;
    }

    public int indexOf(byte [] b){
        int i = 0;
        out: while(i < currentSize){
            if (bytes[i] == b[0]){
                for (int j = 1; j < b.length; j++){
                    if (bytes[i+j] != b[j]){
                        i ++ ;
                        continue out;
                    }
                }
                return i;
            }
            i ++;
        }
        return -1;
    }

    public int endsWith(byte [] b){

        int result = -1;

        out: for (int i = 1 ; i <= b.length; i++){
            if (i < currentSize){
                for (int j = 0; j < i; j++){
                    if (bytes[currentSize - i + j] != b[j]){
                        continue out;
                    }
                }
                System.out.println(new String(get(currentSize - i, i)));
                result = currentSize - i;
            }
        }
        return result;
    }


    public String toString() {
        StringBuffer str = new StringBuffer();
        for (int i= 0; i < currentSize; i++)
            str.append(bytes[i] + " ");
        return str.toString();
    }

    public static void main(String[] args) {

        byte [] a = "asdasdasdasd ------".getBytes();
        byte [] b = "--------------".getBytes();

        ByteBuffer aBuffer = new ByteBuffer(1024);
        aBuffer.append(a, a.length);

        System.out.println(aBuffer.endsWith(b));

    }

}
