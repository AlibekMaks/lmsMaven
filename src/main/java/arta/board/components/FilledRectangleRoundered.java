package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 30.10.2006
 * Time: 15:27:47
 * To change this template use File | Settings | File Templates.
 */
public class FilledRectangleRoundered extends BasicRectangleElement{

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth));

        if (width < 0 && height < 0){
            g.fillRoundRect(left + width, top + height, -width, -height, 10, 10);
        } else if (width < 0){
            g.fillRoundRect(left + width, top , -width, height, 10, 10);
        } else if (height < 0){
            g.fillRoundRect(left, top + height, width, -height, 10, 10);
        } else {
            g.fillRoundRect(left, top, width, height, 10, 10);
        }

    }

}
