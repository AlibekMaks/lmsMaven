package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 30.10.2006
 * Time: 15:26:21
 * To change this template use File | Settings | File Templates.
 */
public class RectangleRoundered extends BasicRectangleElement{

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth));

        if (width < 0 && height < 0){
            g.drawRoundRect(left + width, top + height, -width, -height, 10, 10);
        } else if (width < 0){
            g.drawRoundRect(left + width, top , -width, height, 10, 10);
        } else if (height < 0){
            g.drawRoundRect(left, top + height, width, -height, 10, 10);
        } else {
            g.drawRoundRect(left, top, width, height, 10, 10);
        }

    }

}
