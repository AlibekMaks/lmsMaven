package arta.board.components;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 30.10.2006
 * Time: 12:20:16
 * To change this template use File | Settings | File Templates.
 */
public class Pencil extends PaintableElement{

    ArrayList<Point> points = new ArrayList <Point> ();

    public void paint(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size()-1; i ++){
            g.drawLine((int)points.get(i).getX(), (int)points.get(i).getY(),
                   (int) points.get(i + 1).getX(), (int) points.get(i + 1).getY());
        }
    }

    public String toXML() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void fromXML() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
