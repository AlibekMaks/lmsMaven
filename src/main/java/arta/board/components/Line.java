package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 30.10.2006
 * Time: 15:29:01
 * To change this template use File | Settings | File Templates.
 */
public class Line extends BasicRectangleElement{


    public void paint(Graphics2D g) {

        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(left, top, left + width, top + height);

    }
}
