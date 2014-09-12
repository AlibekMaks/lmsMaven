package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 29.10.2006
 * Time: 19:40:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class PaintableElement {

    public static final int NO_ELEMENT = -1;
    public static final int RECTANGLE = 1;
    public static final int RECTANGLE_FILLED = 11;
    public static final int ELLIPSE = 2;
    public static final int ELLIPSE_FILLED = 21;
    public static final int RECTANGLE_ROUNDERED = 3;
    public static final int RECTANGLE_ROUNDERED_FILLED = 31;
    public static final int LINE = 4;
    public static final int PENCIL = 5;
    public static final int ERASER = 6;


    protected Color color = Color.BLACK;
    protected int lineWidth = 1;

    public abstract void paint(Graphics2D g);

    public abstract String toXML();

    public abstract void fromXML();

}
