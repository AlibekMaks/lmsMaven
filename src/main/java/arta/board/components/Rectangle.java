package arta.board.components;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 29.10.2006
 * Time: 19:49:38
 * To change this template use File | Settings | File Templates.
 */
public class Rectangle extends BasicRectangleElement{

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth));

        if (width < 0 && height < 0){
            g.drawRect(left + width, top + height, -width, -height);
        } else if (width < 0){
            g.drawRect(left + width, top , -width, height);
        } else if (height < 0){
            g.drawRect(left, top + height, width, -height);
        } else {
            g.drawRect(left, top, width, height);
        }

    }
}
