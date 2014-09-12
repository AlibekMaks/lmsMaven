package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 30.10.2006
 * Time: 14:36:27
 * To change this template use File | Settings | File Templates.
 */
public class Ellipse extends BasicRectangleElement{


    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth));

        if (width < 0 && height < 0){
            g.drawOval(left + width, top + height, -width, -height);
        } else if (width < 0){
            g.drawOval(left + width, top , -width, height);
        } else if (height < 0){
            g.drawOval(left, top + height, width, -height);
        } else {
            g.drawOval(left, top, width, height);
        }

    }
}
