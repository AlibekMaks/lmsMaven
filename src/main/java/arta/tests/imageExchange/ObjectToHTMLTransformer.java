package arta.tests.imageExchange;

import arta.common.http.MimeTypes;
import arta.common.logic.server.Server;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 25.03.2008
 * Time: 15:39:38
 * To change this template use File | Settings | File Templates.
 */
public class ObjectToHTMLTransformer {

    public String transform(String mimetype, int id, int width, int height){

        if (mimetype == null)
            return "Object id = " + id;

        if (MimeTypes.isImage(mimetype)){
            if (width == 0)
                width = 24;
            if (height == 0)
                height = 24;
            return "<img align=\"middle\" src=\"getImage?id="+id+"\" width=\""+width+"\" height=\""+height+"\">";
        } else if (MimeTypes.isFlash(mimetype)){
            if (width == 0)
                width = 24;
            if (height == 0)
                height = 24;
            return "<object width=\""+width+"\" height=\""+height+"\" type=\"application/x-shockwave-flash\">"
                    + "<param name=\"movie\" value=\""+ Server.MAIN_URL+"getImage?id="+id+"\" >"
                    + "</object>";
        }

        return "Object id = " + id;
    }

}