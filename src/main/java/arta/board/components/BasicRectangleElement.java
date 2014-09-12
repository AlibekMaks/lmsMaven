package arta.board.components;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 29.10.2006
 * Time: 19:46:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicRectangleElement extends PaintableElement {

    protected int left;
    protected int top;
    protected int width;
    protected int height;
    
    public String toXML() {
        return null;
    }

    public void fromXML() {

    }


    public void setLeft(int left) {
        this.left = left;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
