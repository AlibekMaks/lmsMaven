package arta.tests.parser.logic.mht;

import java.io.InputStream;
import java.util.ArrayList;

import arta.tests.test.Test;
import arta.tests.parser.logic.TestParser;
import arta.tests.imageExchange.ImageUpload;
import arta.common.logic.util.Message;
import arta.common.logic.util.Log;
import arta.common.logic.util.Base64;

public class MHTParser {

    InputStream stream;
    Test test;

    TestParser parser = null;
    String QUESTION_START = "&lt;question";
    String VARIANT_START = "&lt;variant&gt;";
    String boundary = null;

    ImagesList imagesList = new ImagesList();

    StringBuffer parseData = new StringBuffer();
    boolean bodyStarted = false;
    boolean bodyEnded = false;

    public MHTParser(int tutorID, InputStream stream){
        this.stream = stream;
        test = new Test(0);
        test.tutorID = tutorID;
        parser = new TestParser(test);
        parser.setDelims(QUESTION_START, VARIANT_START, "&gt;", "1&gt;", "2&gt;", "3&gt;");
    }

    public void parse(Message message, int lang) {
        test.message = message;
        test.lang = lang;
        try {
            byte[] b = new byte[4096];
            while ((stream.read(b)) != -1){
                parse(b);
            }            
        }catch (Exception exc){
            Log.writeLog(exc);
        }
    }
    StringBuffer text = new StringBuffer();

    public void parse(byte [] b) {

        StringBuffer append = new StringBuffer(new String(b));
        correct(append);
        parseData.append(append);

        if (!bodyStarted){
            if (boundary == null){
                int boundaryStart = parseData.indexOf("boundary=\"");
                if (boundaryStart >=0 ){
                    parseData.delete(0, boundaryStart + "boundary=\"".length());
                    int quot = parseData.indexOf("\"");
                    if (quot > 0){
                        boundary = "--" + parseData.substring(0, quot);
                    }
                }
            }
            int body = parseData.indexOf("<body");
            if (body >= 0){
                parseData.delete(0, body + 6);
                bodyStarted = true;
            }
        }
        if (! bodyStarted ) return;

        if (bodyEnded){
            parseImages();
        }

        int bodyEnd = -1;

        while (parseData.length() > 0){

            int span = parseData.indexOf(">");
            int img = parseData.indexOf("<img");
            bodyEnd = parseData.indexOf("</body>");

            int min = min(img, span);
            min = min(min, bodyEnd);

            if (min == -1) break;

            if (min == bodyEnd){
                bodyEnded = true;
                parseImages();
                break;
            }
            if (min == span){
                StringBuffer tmp = new StringBuffer(parseData.substring(0, span));
                parseData.delete(0, span);
                span = parseData.indexOf(">");
                if (parseData.length() > span + 2){
                    if (!parseData.substring(span +1, span + 2).equals("<")){
                        tmp.append(parseData.substring(0, span + 1));
                        parseData.delete(0, span + 1);
                        int nextSpan = parseData.indexOf("<");
                        if (nextSpan > 0){
                            StringBuffer tmpText = new StringBuffer(parseData.substring(0, nextSpan));
                            trim(tmpText);
                            if (QUESTION_START.indexOf(tmpText.toString()) >= 0 ||
                                    VARIANT_START.indexOf(tmpText.toString()) >= 0){
                                text.append(tmpText);
                                parseData.delete(0, nextSpan);
                                continue;
                            }
                            text.append(tmpText);
                            parseData.delete(0, nextSpan);
                            parser.parse(text);
                            text.delete(0, text.length());
                        } else{
                            parseData.insert(0, tmp);
                            break;
                        }
                    } else {
                        parseData.delete(0, span + 1);
                    }
                } else {
                    break;
                }
                continue;
            }
            if (min == img){
                StringBuffer tmp = new StringBuffer();
                int src = parseData.indexOf("src");
                if (src >= 0){
                    tmp.append(parseData.substring(0, src));
                    parseData.delete(0, src);
                    int quot = parseData.indexOf("\"");
                    if (quot > 0){
                        tmp.append(parseData.substring(0, quot + 1));
                        parseData.delete(0, quot+1);
                        quot = parseData.indexOf("\"");
                        if (quot >=0){
                            String fileName = parseData.substring(0, quot);
                            if (!isValidImage(fileName)) continue;
                            int n = -1;
                            while ((n = fileName.indexOf("/"))>=0){
                                fileName = fileName.substring(n+1, fileName.length());
                            }
                            int imgID = getImage();
                            parser.parse(new StringBuffer("<img align=\"middle\" src=\"getImage?id="+imgID+"\">"));
                            imagesList.addImage(fileName, imgID);
                        } else{
                            parseData.insert(0, tmp);
                            break;
                        }
                    } else {
                        parseData.insert(0, tmp);
                        break;
                    }
                } else break;
            }
        }
    }


    private void parseImages(){
        while (parseData.length() > 0){
            int nextImage = parseData.indexOf("Content-Location:");
            if (nextImage == -1) break;
            parseData.delete(0, nextImage);
            int k = parseData.indexOf("\r\n");
            if (k == -1) break;
            StringBuffer fileName  = new StringBuffer(parseData.substring(0, k));
            while (fileName.length() > 0 ){
                int n = -1;
                if ((n = fileName.indexOf("/")) >= 0){
                    fileName.delete(0, n + 1);
                }  else {
                    break;
                }
            }
            ArrayList<Integer> ids = imagesList.getImageIds(fileName.toString());
            if (ids.size() == 0) {
                parseData.delete(0, k+1);
                continue;
            }
            int dataStart = parseData.indexOf("\r\n\r\n");
            StringBuffer  tmp = new StringBuffer(parseData.substring(0, dataStart + 4));
            parseData.delete(0, dataStart + 4);
            int dataEnd = parseData.indexOf("\r\n\r\n");
            if (dataEnd == -1){
                parseData.insert(0, tmp);
                break;
            }
            String data = parseData.substring(0, dataEnd);
            try {
                byte [] image = Base64.decode(data);
                ImageUpload imageUpload = new ImageUpload();
                imageUpload.updateImages(ids, image);
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

    private void correct(StringBuffer str){
        int n = -1;
        while ((n = str.indexOf("&nbsp;&nbsp;")) >= 0){
            str.delete(n, n+6);            
        }
        while ((n = str.indexOf("=3D")) >= 0){
            str.delete(n+1, n+3);
        }
    }

    String [] validTags = new String[]{"sup"};

    private int getImage(){

        int id = 0;
        byte [] b = new byte[]{0};
        ImageUpload upload = new ImageUpload();        
        id = upload.uploadImage(b, test.tutorID, test.testID, 0, 24, 24, test.getSignature());
        return id;
    }

    private boolean isValidImage(String str){
        if (str == null)
            return false;
        if (str.toLowerCase().indexOf("gif")>0 || str.toLowerCase().indexOf("jpg")>0 ||
                str.toLowerCase().indexOf("jpeg")>0)
            return true;
        return false;
    }


    public Test getTest() {
        return test;
    }

    private int min(int n1, int n2){
        if (n1 >= 0 && n2 >= 0){
            return Math.min(n1, n2);
        }
        if (n1 != -1)
            return n1;
        if (n2 != -1)
            return n2;
        return -1;
    }
    private void trim(StringBuffer str){
        int n = -1;
        while ((n = str.indexOf("=\r\n")) >= 0){
            str.delete(n, n+3);
        }
    }
}
